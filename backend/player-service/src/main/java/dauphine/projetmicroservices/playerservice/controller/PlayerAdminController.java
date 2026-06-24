package dauphine.projetmicroservices.playerservice.controller;

import dauphine.projetmicroservices.playerservice.dto.AdminPlayerCreateRequest;
import dauphine.projetmicroservices.playerservice.dto.AdminPlayerDTO;
import dauphine.projetmicroservices.playerservice.dto.AdminPlayerUpdateRequest;
import dauphine.projetmicroservices.playerservice.dto.PagedPlayersResponse;
import dauphine.projetmicroservices.playerservice.service.AdminAuthService;
import dauphine.projetmicroservices.playerservice.service.PlayerAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlayerAdminController {

    public static final String ADMIN_PASSWORD_HEADER = "X-Admin-Password";

    private final AdminAuthService adminAuthService;
    private final PlayerAdminService playerAdminService;

    @GetMapping
    public ResponseEntity<PagedPlayersResponse> searchPlayers(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        adminAuthService.validateAdminPassword(adminPassword);
        return ResponseEntity.ok(playerAdminService.searchPlayers(search, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminPlayerDTO> getPlayer(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @PathVariable Long id) {
        adminAuthService.validateAdminPassword(adminPassword);
        return ResponseEntity.ok(playerAdminService.getPlayer(id));
    }

    @PostMapping
    public ResponseEntity<AdminPlayerDTO> createPlayer(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @RequestBody AdminPlayerCreateRequest request) {
        adminAuthService.validateAdminPassword(adminPassword);
        AdminPlayerDTO created = playerAdminService.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminPlayerDTO> updatePlayer(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @PathVariable Long id,
            @RequestBody AdminPlayerUpdateRequest request) {
        adminAuthService.validateAdminPassword(adminPassword);
        return ResponseEntity.ok(playerAdminService.updatePlayer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @PathVariable Long id) {
        adminAuthService.validateAdminPassword(adminPassword);
        playerAdminService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}
