package dauphine.projetmicroservices.scoreservice.client;

import dauphine.projetmicroservices.scoreservice.dto.PlayerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "player-service", url = "http://player-service:8082")
public interface PlayerClient {

    @GetMapping("/players/{id}")
    PlayerDTO getPlayer(@PathVariable Long id);

    @GetMapping("/players/search")
    List<PlayerDTO> searchPlayers(@RequestParam(required = false) String username);
}
