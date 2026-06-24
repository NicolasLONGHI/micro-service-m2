package dauphine.projetmicroservices.gameserver.service;

import dauphine.projetmicroservices.gameserver.client.PlayerClient;
import dauphine.projetmicroservices.gameserver.client.ScoreClient;
import dauphine.projetmicroservices.gameserver.dto.GameResponse;
import dauphine.projetmicroservices.gameserver.dto.GameResultRequest;
import dauphine.projetmicroservices.gameserver.dto.GuessResponse;
import dauphine.projetmicroservices.gameserver.dto.LetterFeedbackResponse;
import dauphine.projetmicroservices.gameserver.exception.PlayerNotFoundException;
import dauphine.projetmicroservices.gameserver.model.Game;
import dauphine.projetmicroservices.gameserver.model.GameStatus;
import dauphine.projetmicroservices.gameserver.model.Guess;
import dauphine.projetmicroservices.gameserver.model.LetterFeedback;
import dauphine.projetmicroservices.gameserver.model.LetterFeedbackStatus;
import dauphine.projetmicroservices.gameserver.model.Word;
import dauphine.projetmicroservices.gameserver.repository.GameRepository;
import dauphine.projetmicroservices.gameserver.repository.WordRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final WordRepository wordRepository;
    private final PlayerClient playerClient;
    private final ScoreClient scoreClient;

    @Transactional
    public GameResponse startNewGame(String playerId, String difficulty) {
        try {
            playerClient.getPlayer(Long.parseLong(playerId));
        } catch (FeignException.NotFound ex) {
            throw new PlayerNotFoundException("Player not found with ID: " + playerId);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid player ID format: " + playerId);
        }

        String normalizedDifficulty = normalizeDifficulty(difficulty);
        Word targetWord = pickTargetWord(normalizedDifficulty);

        Game game = Game.builder()
                .playerId(playerId)
                .status(GameStatus.IN_PROGRESS)
                .startAt(LocalDateTime.now())
                .attemptsRemaining(6)
                .maxAttempts(6)
                .targetWord(targetWord)
                .build();

        Game savedGame = gameRepository.save(game);
        return toGameResponse(savedGame);
    }

    @Transactional
    public GameResponse submitGuess(Long gameId, String guessWord) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is already finished");
        }

        String normalizedGuess = guessWord.trim().toUpperCase();
        String target = game.getTargetWord().getValue().toUpperCase();

        if (normalizedGuess.length() != target.length()) {
            throw new IllegalArgumentException("Guess word must be exactly " + target.length() + " letters");
        }

        Guess guess = Guess.builder()
                .guessWord(normalizedGuess)
                .attemptNumber(game.getGuesses().size() + 1)
                .createdAt(LocalDateTime.now())
                .build();

        List<LetterFeedback> feedbackList = computeFeedback(normalizedGuess, target);
        feedbackList.forEach(guess::addFeedback);
        game.addGuess(guess);

        game.setAttemptsRemaining(game.getAttemptsRemaining() - 1);
        if (normalizedGuess.equals(target)) {
            game.setStatus(GameStatus.WON);
            game.setEndAt(LocalDateTime.now());
        } else if (game.getAttemptsRemaining() <= 0) {
            game.setStatus(GameStatus.LOST);
            game.setEndAt(LocalDateTime.now());
        }

        Game savedGame = gameRepository.save(game);
        
        if (savedGame.getStatus() == GameStatus.WON || savedGame.getStatus() == GameStatus.LOST) {
            notifyScoreService(savedGame);
        }
        
        return toGameResponse(savedGame);
    }

    private void notifyScoreService(Game game) {
        try {
            boolean isWinner = game.getStatus() == GameStatus.WON;
            String difficulty = game.getTargetWord().getDifficulty();
            GameResultRequest resultRequest = GameResultRequest.builder()
                    .gameId(game.getId())
                    .playerId(Long.parseLong(game.getPlayerId()))
                    .targetWord(game.getTargetWord().getValue())
                    .attemptsCount(game.getGuesses().size())
                    .isWinner(isWinner)
                    .playedAt(game.getEndAt())
                    .difficulty(difficulty)
                    .pointsEarned(computePoints(difficulty, isWinner))
                    .build();
            scoreClient.recordGameResult(resultRequest);
        } catch (Exception ex) {
            // Log the error but don't fail the game response
            System.err.println("Failed to notify score service: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public GameResponse getGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));
        return toGameResponse(game);
    }

    private Word pickTargetWord(String difficulty) {
        return wordRepository.findRandomWordByDifficulty(difficulty)
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun mot disponible pour la difficulté " + difficulty
                                + ". Vérifiez le dictionnaire ou choisissez une autre difficulté."));
    }

    private String normalizeDifficulty(String difficulty) {
        if (difficulty == null || difficulty.isBlank()) {
            return "EASY";
        }
        String normalized = difficulty.trim().toUpperCase();
        if (!normalized.equals("EASY") && !normalized.equals("MEDIUM") && !normalized.equals("HARD")) {
            throw new IllegalArgumentException("Difficulté invalide. Valeurs acceptées : EASY, MEDIUM, HARD");
        }
        return normalized;
    }

    private int computePoints(String difficulty, boolean isWinner) {
        if (!isWinner) {
            return 0;
        }
        return switch (difficulty != null ? difficulty.toUpperCase() : "EASY") {
            case "MEDIUM" -> 2;
            case "HARD" -> 3;
            default -> 1;
        };
    }

    private List<LetterFeedback> computeFeedback(String guessWord, String targetWord) {
        return java.util.stream.IntStream.range(0, guessWord.length())
                .mapToObj(position -> {
                    char letter = guessWord.charAt(position);
                    LetterFeedbackStatus status;

                    if (letter == targetWord.charAt(position)) {
                        status = LetterFeedbackStatus.CORRECT;
                    } else if (targetWord.indexOf(letter) >= 0) {
                        status = LetterFeedbackStatus.MISPLACED;
                    } else {
                        status = LetterFeedbackStatus.ABSENT;
                    }

                    return LetterFeedback.builder()
                            .position(position)
                            .letter(String.valueOf(letter))
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private GameResponse toGameResponse(Game game) {
        List<GuessResponse> guessResponses = game.getGuesses().stream()
                .map(guess -> GuessResponse.builder()
                        .guessId(guess.getId())
                        .attemptNumber(guess.getAttemptNumber())
                        .guessWord(guess.getGuessWord())
                        .createdAt(guess.getCreatedAt())
                        .feedback(guess.getFeedback().stream()
                                .map(f -> LetterFeedbackResponse.builder()
                                        .position(f.getPosition())
                                        .letter(f.getLetter())
                                        .status(f.getStatus().name())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        String target = game.getTargetWord().getValue().toUpperCase();
        String difficulty = game.getTargetWord().getDifficulty();
        boolean isWinner = game.getStatus() == GameStatus.WON;

        return GameResponse.builder()
                .gameId(game.getId())
                .playerId(game.getPlayerId())
                .status(game.getStatus().name())
                .attemptsRemaining(game.getAttemptsRemaining())
                .maxAttempts(game.getMaxAttempts())
                .startAt(game.getStartAt())
                .endAt(game.getEndAt())
                .guesses(guessResponses)
                .wordLength(target.length())
                .firstLetter(game.getStatus() == GameStatus.IN_PROGRESS ? String.valueOf(target.charAt(0)) : null)
                .targetWord(game.getStatus() == GameStatus.LOST ? target : null)
                .difficulty(difficulty)
                .pointsEarned(isWinner ? computePoints(difficulty, true) : 0)
                .build();
    }
}
