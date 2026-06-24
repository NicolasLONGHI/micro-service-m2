package dauphine.projetmicroservices.gameserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResponse {
    private Long gameId;
    private String playerId;
    private String status;
    private Integer attemptsRemaining;
    private Integer maxAttempts;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private List<GuessResponse> guesses;
    private Integer wordLength;
    private String firstLetter;
    private String targetWord;
    private String difficulty;
    private Integer pointsEarned;
}
