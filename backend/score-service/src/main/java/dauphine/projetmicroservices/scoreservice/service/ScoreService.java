package dauphine.projetmicroservices.scoreservice.service;

import dauphine.projetmicroservices.scoreservice.dto.GameResultRequest;
import dauphine.projetmicroservices.scoreservice.dto.LeaderboardEntry;
import dauphine.projetmicroservices.scoreservice.dto.PlayerStatsResponse;
import dauphine.projetmicroservices.scoreservice.client.PlayerClient;
import dauphine.projetmicroservices.scoreservice.dto.PlayerDTO;
import dauphine.projetmicroservices.scoreservice.model.GameResult;
import dauphine.projetmicroservices.scoreservice.repository.GameResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final GameResultRepository gameResultRepository;
    private final PlayerClient playerClient;

    @Transactional
    public void recordGameResult(GameResultRequest request) {
        GameResult result = GameResult.builder()
                .gameId(request.getGameId())
                .playerId(request.getPlayerId())
                .targetWord(request.getTargetWord())
                .attemptsCount(request.getAttemptsCount())
                .isWinner(request.getIsWinner())
                .playedAt(request.getPlayedAt() != null ? request.getPlayedAt() : LocalDateTime.now())
                .difficulty(request.getDifficulty())
                .pointsEarned(request.getPointsEarned() != null ? request.getPointsEarned() : 0)
                .build();
        gameResultRepository.save(result);
    }

    @Transactional(readOnly = true)
    public PlayerStatsResponse getPlayerStats(Long playerId) {
        List<GameResult> playerResults = gameResultRepository.findByPlayerId(playerId);

        if (playerResults.isEmpty()) {
            return PlayerStatsResponse.builder()
                    .playerId(playerId)
                    .totalGames(0L)
                    .totalWins(0L)
                    .winRate(0.0)
                    .averageAttempts(0.0)
                    .totalScore(0L)
                    .build();
        }

        long totalGames = playerResults.size();
        long totalWins = playerResults.stream()
                .filter(gr -> Boolean.TRUE.equals(gr.getIsWinner()))
                .count();
        double winRate = (double) totalWins / totalGames * 100;
        double averageAttempts = playerResults.stream()
                .mapToInt(GameResult::getAttemptsCount)
                .average()
                .orElse(0.0);
        long totalScore = playerResults.stream()
                .mapToLong(gr -> gr.getPointsEarned() != null ? gr.getPointsEarned() : 0)
                .sum();

        return PlayerStatsResponse.builder()
                .playerId(playerId)
                .totalGames(totalGames)
                .totalWins(totalWins)
                .winRate(winRate)
                .averageAttempts(averageAttempts)
                .totalScore(totalScore)
                .build();
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getLeaderboard() {
        List<GameResult> allResults = gameResultRepository.findAll();

        return allResults.stream()
                .collect(Collectors.groupingBy(GameResult::getPlayerId))
                .entrySet().stream()
                .map(entry -> {
                    Long playerId = entry.getKey();
                    List<GameResult> playerResults = entry.getValue();
                    long totalGames = playerResults.size();
                    long totalWins = playerResults.stream()
                            .filter(gr -> Boolean.TRUE.equals(gr.getIsWinner()))
                            .count();
                    double winRate = totalGames > 0 ? (double) totalWins / totalGames * 100 : 0.0;
                    double averageAttempts = playerResults.stream()
                            .mapToInt(GameResult::getAttemptsCount)
                            .average()
                            .orElse(0.0);
                    long totalScore = playerResults.stream()
                            .mapToLong(gr -> gr.getPointsEarned() != null ? gr.getPointsEarned() : 0)
                            .sum();

                    return LeaderboardEntry.builder()
                            .playerId(playerId)
                            .username(resolveUsername(playerId))
                            .totalWins(totalWins)
                            .totalScore(totalScore)
                            .winRate(winRate)
                            .averageAttempts(averageAttempts)
                            .build();
                })
                .sorted(Comparator.comparingLong(LeaderboardEntry::getTotalScore).reversed()
                        .thenComparingLong(LeaderboardEntry::getTotalWins).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GameResult> getGameHistory(Long playerId, Boolean isWinner, LocalDateTime startDate, LocalDateTime endDate) {
        return gameResultRepository.findByFilters(playerId, isWinner, startDate, endDate);
    }

    private String resolveUsername(Long playerId) {
        try {
            PlayerDTO player = playerClient.getPlayer(playerId);
            if (player != null && player.getUsername() != null && !player.getUsername().isBlank()) {
                return player.getUsername();
            }
        } catch (Exception ex) {
            // Resilience: leaderboard still works if player-service is unavailable
        }
        return "Joueur inconnu";
    }
}
