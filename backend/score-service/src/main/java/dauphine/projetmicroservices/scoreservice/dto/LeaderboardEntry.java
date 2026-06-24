package dauphine.projetmicroservices.scoreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardEntry {
    private Long playerId;
    private String username;
    private Long totalWins;
    private Long totalScore;
    private Double winRate;
    private Double averageAttempts;
}
