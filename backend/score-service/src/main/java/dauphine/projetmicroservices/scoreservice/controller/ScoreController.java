package dauphine.projetmicroservices.scoreservice.controller;

import dauphine.projetmicroservices.scoreservice.dto.GameResultRequest;
import dauphine.projetmicroservices.scoreservice.dto.LeaderboardEntry;
import dauphine.projetmicroservices.scoreservice.dto.PlayerStatsResponse;
import dauphine.projetmicroservices.scoreservice.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScoreController {

    private final ScoreService scoreService;

    @PostMapping
    public ResponseEntity<Void> recordGameResult(@RequestBody GameResultRequest request) {
        scoreService.recordGameResult(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/players/{playerId}/stats")
    public ResponseEntity<PlayerStatsResponse> getPlayerStats(@PathVariable Long playerId) {
        PlayerStatsResponse stats = scoreService.getPlayerStats(playerId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard() {
        List<LeaderboardEntry> leaderboard = scoreService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

}
