package dauphine.projetmicroservices.gameserver.client;

import dauphine.projetmicroservices.gameserver.dto.PlayerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "player-service", url = "http://player-service:8082")
public interface PlayerClient {

    @GetMapping("/players/{id}")
    PlayerDTO getPlayer(@PathVariable Long id);
}
