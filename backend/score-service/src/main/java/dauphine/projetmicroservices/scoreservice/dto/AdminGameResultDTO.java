package dauphine.projetmicroservices.scoreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminGameResultDTO {
    private Long id;
    private Long gameId;
    private Long playerId;
    private String username;
    private String targetWord;
    private Integer attemptsCount;
    private Boolean isWinner;
    private LocalDateTime playedAt;
    private String difficulty;
    private Integer pointsEarned;
    private Double playerWinRate;
    private Double playerAvgAttempts;
    private Integer playerLeaderboardPosition;
    private Long playerTotalScore;
}
