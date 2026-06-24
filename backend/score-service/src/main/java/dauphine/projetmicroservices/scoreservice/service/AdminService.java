package dauphine.projetmicroservices.scoreservice.service;

import dauphine.projetmicroservices.scoreservice.client.PlayerClient;
import dauphine.projetmicroservices.scoreservice.dto.AdminGameResultDTO;
import dauphine.projetmicroservices.scoreservice.dto.AdminGameResultUpdateRequest;
import dauphine.projetmicroservices.scoreservice.dto.PlayerDTO;
import dauphine.projetmicroservices.scoreservice.model.GameResult;
import dauphine.projetmicroservices.scoreservice.repository.GameResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final GameResultRepository gameResultRepository;
    private final PlayerClient playerClient;

    @Transactional(readOnly = true)
    public List<AdminGameResultDTO> searchResults(
            String username,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Boolean isWinner,
            Double minWinRate,
            Double maxWinRate,
            Double minAvgAttempts,
            Double maxAvgAttempts,
            Integer leaderboardPosition,
            Integer minLeaderboardPosition,
            Integer maxLeaderboardPosition) {

        Map<Long, PlayerContext> playerContexts = buildPlayerContexts();
        Set<Long> usernameMatchedIds = resolvePlayerIdsByUsername(username);

        return gameResultRepository.findAll().stream()
                .filter(result -> matchesUsername(result, usernameMatchedIds, username))
                .filter(result -> matchesDate(result, startDate, endDate))
                .filter(result -> isWinner == null || Boolean.TRUE.equals(result.getIsWinner()) == isWinner)
                .filter(result -> matchesPlayerStats(
                        result.getPlayerId(),
                        playerContexts,
                        minWinRate,
                        maxWinRate,
                        minAvgAttempts,
                        maxAvgAttempts,
                        leaderboardPosition,
                        minLeaderboardPosition,
                        maxLeaderboardPosition))
                .sorted(Comparator.comparing(GameResult::getPlayedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(result -> toAdminDto(result, playerContexts.get(result.getPlayerId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminGameResultDTO getResult(Long id) {
        GameResult result = gameResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Result not found: " + id));
        Map<Long, PlayerContext> playerContexts = buildPlayerContexts();
        return toAdminDto(result, playerContexts.get(result.getPlayerId()));
    }

    @Transactional
    public AdminGameResultDTO updateResult(Long id, AdminGameResultUpdateRequest request) {
        GameResult result = gameResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Result not found: " + id));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            Long playerId = resolvePlayerIdByUsername(request.getUsername().trim());
            result.setPlayerId(playerId);
        }
        if (request.getTargetWord() != null) {
            result.setTargetWord(request.getTargetWord().trim().toUpperCase());
        }
        if (request.getAttemptsCount() != null) {
            result.setAttemptsCount(request.getAttemptsCount());
        }
        if (request.getIsWinner() != null) {
            result.setIsWinner(request.getIsWinner());
        }
        if (request.getPlayedAt() != null) {
            result.setPlayedAt(request.getPlayedAt());
        }

        GameResult saved = gameResultRepository.save(result);
        Map<Long, PlayerContext> playerContexts = buildPlayerContexts();
        return toAdminDto(saved, playerContexts.get(saved.getPlayerId()));
    }

    @Transactional
    public void deleteResult(Long id) {
        if (!gameResultRepository.existsById(id)) {
            throw new IllegalArgumentException("Result not found: " + id);
        }
        gameResultRepository.deleteById(id);
    }

    private Set<Long> resolvePlayerIdsByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        try {
            List<PlayerDTO> players = playerClient.searchPlayers(username.trim());
            return players.stream()
                    .map(PlayerDTO::getId)
                    .collect(Collectors.toSet());
        } catch (Exception ex) {
            return Set.of();
        }
    }

    private boolean matchesUsername(GameResult result, Set<Long> usernameMatchedIds, String username) {
        if (username == null || username.isBlank()) {
            return true;
        }
        return usernameMatchedIds != null && usernameMatchedIds.contains(result.getPlayerId());
    }

    private boolean matchesDate(GameResult result, LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime playedAt = result.getPlayedAt();
        if (playedAt == null) {
            return startDate == null && endDate == null;
        }
        if (startDate != null && playedAt.toLocalDate().isBefore(startDate.toLocalDate())) {
            return false;
        }
        if (endDate != null && playedAt.toLocalDate().isAfter(endDate.toLocalDate())) {
            return false;
        }
        return true;
    }

    private boolean matchesPlayerStats(
            Long playerId,
            Map<Long, PlayerContext> playerContexts,
            Double minWinRate,
            Double maxWinRate,
            Double minAvgAttempts,
            Double maxAvgAttempts,
            Integer leaderboardPosition,
            Integer minLeaderboardPosition,
            Integer maxLeaderboardPosition) {

        PlayerContext context = playerContexts.get(playerId);
        if (context == null) {
            return false;
        }

        if (minWinRate != null && context.winRate < minWinRate) return false;
        if (maxWinRate != null && context.winRate > maxWinRate) return false;
        if (minAvgAttempts != null && context.avgAttempts < minAvgAttempts) return false;
        if (maxAvgAttempts != null && context.avgAttempts > maxAvgAttempts) return false;
        if (leaderboardPosition != null && !leaderboardPosition.equals(context.leaderboardPosition)) return false;
        if (minLeaderboardPosition != null && context.leaderboardPosition < minLeaderboardPosition) return false;
        if (maxLeaderboardPosition != null && context.leaderboardPosition > maxLeaderboardPosition) return false;

        return true;
    }

    private Map<Long, PlayerContext> buildPlayerContexts() {
        List<GameResult> allResults = gameResultRepository.findAll();
        Map<Long, List<GameResult>> grouped = allResults.stream()
                .collect(Collectors.groupingBy(GameResult::getPlayerId));

        Map<Long, PlayerStatsSummary> statsByPlayer = new HashMap<>();
        for (Map.Entry<Long, List<GameResult>> entry : grouped.entrySet()) {
            List<GameResult> results = entry.getValue();
            long totalGames = results.size();
            long totalWins = results.stream()
                    .filter(gr -> Boolean.TRUE.equals(gr.getIsWinner()))
                    .count();
            double winRate = totalGames > 0 ? (double) totalWins / totalGames * 100 : 0.0;
            double avgAttempts = results.stream()
                    .mapToInt(GameResult::getAttemptsCount)
                    .average()
                    .orElse(0.0);
            long totalScore = results.stream()
                    .mapToLong(gr -> gr.getPointsEarned() != null ? gr.getPointsEarned() : 0)
                    .sum();
            statsByPlayer.put(entry.getKey(), new PlayerStatsSummary(totalWins, winRate, avgAttempts, totalScore));
        }

        List<Map.Entry<Long, PlayerStatsSummary>> ranked = statsByPlayer.entrySet().stream()
                .sorted(Comparator.comparingLong((Map.Entry<Long, PlayerStatsSummary> e) -> e.getValue().totalScore).reversed()
                        .thenComparingLong(e -> e.getValue().totalWins))
                .collect(Collectors.toList());

        Map<Long, Integer> positions = new HashMap<>();
        for (int i = 0; i < ranked.size(); i++) {
            positions.put(ranked.get(i).getKey(), i + 1);
        }

        Map<Long, PlayerContext> contexts = new HashMap<>();
        Set<Long> playerIds = new HashSet<>(grouped.keySet());
        for (Long playerId : playerIds) {
            PlayerStatsSummary stats = statsByPlayer.getOrDefault(playerId, new PlayerStatsSummary(0, 0.0, 0.0, 0L));
            contexts.put(playerId, new PlayerContext(
                    resolveUsername(playerId),
                    stats.winRate,
                    stats.avgAttempts,
                    positions.getOrDefault(playerId, ranked.size() + 1),
                    stats.totalScore
            ));
        }
        return contexts;
    }

    private AdminGameResultDTO toAdminDto(GameResult result, PlayerContext context) {
        String username = context != null ? context.username : resolveUsername(result.getPlayerId());
        Double winRate = context != null ? context.winRate : 0.0;
        Double avgAttempts = context != null ? context.avgAttempts : 0.0;
        Integer position = context != null ? context.leaderboardPosition : null;

        Long totalScore = context != null ? context.totalScore : 0L;

        return AdminGameResultDTO.builder()
                .id(result.getId())
                .gameId(result.getGameId())
                .playerId(result.getPlayerId())
                .username(username)
                .targetWord(result.getTargetWord())
                .attemptsCount(result.getAttemptsCount())
                .isWinner(result.getIsWinner())
                .playedAt(result.getPlayedAt())
                .difficulty(result.getDifficulty())
                .pointsEarned(result.getPointsEarned())
                .playerWinRate(winRate)
                .playerAvgAttempts(avgAttempts)
                .playerLeaderboardPosition(position)
                .playerTotalScore(totalScore)
                .build();
    }

    private Long resolvePlayerIdByUsername(String username) {
        try {
            List<PlayerDTO> players = playerClient.searchPlayers(username);
            return players.stream()
                    .filter(player -> player.getUsername() != null
                            && player.getUsername().equalsIgnoreCase(username))
                    .map(PlayerDTO::getId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable: " + username));
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Impossible de résoudre le joueur: " + username);
        }
    }

    private String resolveUsername(Long playerId) {
        try {
            PlayerDTO player = playerClient.getPlayer(playerId);
            if (player != null && player.getUsername() != null && !player.getUsername().isBlank()) {
                return player.getUsername();
            }
        } catch (Exception ex) {
            // Resilience if player-service is unavailable
        }
        return "Joueur inconnu";
    }

    private record PlayerStatsSummary(long totalWins, double winRate, double avgAttempts, long totalScore) {}

    private record PlayerContext(String username, double winRate, double avgAttempts, int leaderboardPosition, long totalScore) {}
}
