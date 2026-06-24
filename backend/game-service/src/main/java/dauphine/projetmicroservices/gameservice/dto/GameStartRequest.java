package dauphine.projetmicroservices.gameserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStartRequest {
    private String playerId;
    private String difficulty;
}
