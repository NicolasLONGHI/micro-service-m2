package dauphine.projetmicroservices.gameserver.controller;

import dauphine.projetmicroservices.gameserver.dto.GameResponse;
import dauphine.projetmicroservices.gameserver.dto.GameStartRequest;
import dauphine.projetmicroservices.gameserver.dto.GuessRequest;
import dauphine.projetmicroservices.gameserver.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<EntityModel<GameResponse>> startGame(@RequestBody GameStartRequest request) {
        GameResponse response = gameService.startNewGame(request.getPlayerId(), request.getDifficulty());
        EntityModel<GameResponse> model = buildModel(response);

        return ResponseEntity.created(linkTo(methodOn(GameController.class).getGame(response.getGameId())).toUri())
                .body(model);
    }

    @PostMapping("/{gameId}/guesses")
    public ResponseEntity<EntityModel<GameResponse>> submitGuess(
            @PathVariable Long gameId,
            @RequestBody GuessRequest request) {
        GameResponse response = gameService.submitGuess(gameId, request.getGuessWord());
        return ResponseEntity.ok(buildModel(response));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<EntityModel<GameResponse>> getGame(@PathVariable Long gameId) {
        GameResponse response = gameService.getGame(gameId);
        return ResponseEntity.ok(buildModel(response));
    }

    private EntityModel<GameResponse> buildModel(GameResponse response) {
        EntityModel<GameResponse> model = EntityModel.of(response,
                linkTo(methodOn(GameController.class).getGame(response.getGameId())).withSelfRel(),
                linkTo(methodOn(GameController.class).startGame(null)).withRel("startGame"));

        if (!"WON".equals(response.getStatus()) && !"LOST".equals(response.getStatus())) {
            model.add(linkTo(methodOn(GameController.class).submitGuess(response.getGameId(), null)).withRel("submitGuess"));
        }
        return model;
    }
}
