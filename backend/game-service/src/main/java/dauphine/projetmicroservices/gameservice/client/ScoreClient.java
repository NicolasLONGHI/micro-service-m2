package dauphine.projetmicroservices.gameserver.client;

import dauphine.projetmicroservices.gameserver.dto.GameResultRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "score-service", url = "http://score-service:8083")
public interface ScoreClient {

    @PostMapping("/scores")
    void recordGameResult(@RequestBody GameResultRequest request);
}
