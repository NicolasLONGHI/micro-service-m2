package dauphine.projetmicroservices.gameserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordCreateRequest {
    private String value;
    private String language;
    private String difficulty;
}
