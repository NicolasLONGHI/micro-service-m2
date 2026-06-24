package dauphine.projetmicroservices.gameserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordDTO {
    private Long id;
    private String value;
    private String language;
    private String difficulty;
    private Integer length;
}
