package dauphine.projetmicroservices.gameserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterFeedbackResponse {
    private Integer position;
    private String letter;
    private String status;
}
