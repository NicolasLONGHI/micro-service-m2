package dauphine.projetmicroservices.playerservice.controller;

import dauphine.projetmicroservices.playerservice.dto.ChangeEmailRequest;
import dauphine.projetmicroservices.playerservice.dto.ChangePasswordRequest;
import dauphine.projetmicroservices.playerservice.dto.PlayerLoginRequest;
import dauphine.projetmicroservices.playerservice.dto.PlayerRegistrationRequest;
import dauphine.projetmicroservices.playerservice.dto.PlayerResponse;
import dauphine.projetmicroservices.playerservice.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping("/register")
    public ResponseEntity<EntityModel<PlayerResponse>> registerPlayer(@RequestBody PlayerRegistrationRequest request) {
        PlayerResponse response = playerService.registerPlayer(request);
        EntityModel<PlayerResponse> model = toModel(response);
        return ResponseEntity.created(linkTo(methodOn(PlayerController.class).getPlayer(response.getId())).toUri())
                .body(model);
    }

    @PostMapping("/login")
    public ResponseEntity<EntityModel<PlayerResponse>> login(@RequestBody PlayerLoginRequest request) {
        PlayerResponse response = playerService.login(request);
        return ResponseEntity.ok(toModel(response));
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<EntityModel<PlayerResponse>> changeEmail(
            @PathVariable Long id,
            @RequestBody ChangeEmailRequest request) {
        PlayerResponse response = playerService.changeEmail(id, request);
        return ResponseEntity.ok(toModel(response));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<EntityModel<PlayerResponse>> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        PlayerResponse response = playerService.changePassword(id, request);
        return ResponseEntity.ok(toModel(response));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlayerResponse>> searchPlayers(
            @RequestParam(required = false) String username) {
        return ResponseEntity.ok(playerService.searchPlayers(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PlayerResponse>> getPlayer(@PathVariable Long id) {
        PlayerResponse response = playerService.getPlayer(id);
        return ResponseEntity.ok(toModel(response));
    }

    private EntityModel<PlayerResponse> toModel(PlayerResponse response) {
        return EntityModel.of(response,
                linkTo(methodOn(PlayerController.class).getPlayer(response.getId())).withSelfRel());
    }
}
