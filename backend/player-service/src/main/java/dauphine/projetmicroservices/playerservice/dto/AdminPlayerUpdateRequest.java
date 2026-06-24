package dauphine.projetmicroservices.playerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminPlayerUpdateRequest {
    private String username;
    private String email;
    private String password;
}
