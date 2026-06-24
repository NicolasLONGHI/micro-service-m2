package dauphine.projetmicroservices.gameserver.controller;

import dauphine.projetmicroservices.gameserver.dto.PagedWordsResponse;
import dauphine.projetmicroservices.gameserver.dto.WordCreateRequest;
import dauphine.projetmicroservices.gameserver.dto.WordDTO;
import dauphine.projetmicroservices.gameserver.dto.WordUpdateRequest;
import dauphine.projetmicroservices.gameserver.service.AdminAuthService;
import dauphine.projetmicroservices.gameserver.service.WordAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games/admin/words")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WordAdminController {

    public static final String ADMIN_PASSWORD_HEADER = "X-Admin-Password";

    private final AdminAuthService adminAuthService;
    private final WordAdminService wordAdminService;

    @GetMapping
    public ResponseEntity<PagedWordsResponse> searchWords(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer minLength,
            @RequestParam(required = false) Integer maxLength,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        adminAuthService.validateAdminPassword(adminPassword);
        return ResponseEntity.ok(wordAdminService.searchWords(search, difficulty, minLength, maxLength, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WordDTO> getWord(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @PathVariable Long id) {
        adminAuthService.validateAdminPassword(adminPassword);
        return ResponseEntity.ok(wordAdminService.getWord(id));
    }

    @PostMapping
    public ResponseEntity<WordDTO> createWord(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @RequestBody WordCreateRequest request) {
        adminAuthService.validateAdminPassword(adminPassword);
        WordDTO created = wordAdminService.createWord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WordDTO> updateWord(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @PathVariable Long id,
            @RequestBody WordUpdateRequest request) {
        adminAuthService.validateAdminPassword(adminPassword);
        return ResponseEntity.ok(wordAdminService.updateWord(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @PathVariable Long id) {
        adminAuthService.validateAdminPassword(adminPassword);
        wordAdminService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }
}
