package dauphine.projetmicroservices.scoreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerStatsResponse {
    private Long playerId;
    private Long totalGames;
    private Long totalWins;
    private Double winRate;
    private Double averageAttempts;
    private Long totalScore;
}
