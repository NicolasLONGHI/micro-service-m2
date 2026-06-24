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
public class GameResultRequest {
    private Long gameId;
    private Long playerId;
    private String targetWord;
    private Integer attemptsCount;
    private Boolean isWinner;
    private LocalDateTime playedAt;
    private String difficulty;
    private Integer pointsEarned;
}
