package dauphine.projetmicroservices.scoreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminGameResultUpdateRequest {
    private String username;
    private String targetWord;
    private Integer attemptsCount;
    private Boolean isWinner;
    private LocalDateTime playedAt;
}
