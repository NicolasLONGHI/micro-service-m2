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
public class GuessResponse {
    private Long guessId;
    private Integer attemptNumber;
    private String guessWord;
    private LocalDateTime createdAt;
    private List<LetterFeedbackResponse> feedback;
}
