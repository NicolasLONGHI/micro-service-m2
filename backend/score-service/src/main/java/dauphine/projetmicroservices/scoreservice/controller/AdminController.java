package dauphine.projetmicroservices.scoreservice.controller;

import dauphine.projetmicroservices.scoreservice.dto.AdminGameResultDTO;
import dauphine.projetmicroservices.scoreservice.dto.AdminGameResultUpdateRequest;
import dauphine.projetmicroservices.scoreservice.dto.AdminLoginRequest;
import dauphine.projetmicroservices.scoreservice.dto.AdminLoginResponse;
import dauphine.projetmicroservices.scoreservice.service.AdminAuthService;
import dauphine.projetmicroservices.scoreservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/scores/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    public static final String ADMIN_PASSWORD_HEADER = "X-Admin-Password";

    private final AdminAuthService adminAuthService;
    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminAuthService.login(request.getPassword());
        if (!response.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/results")
    public ResponseEntity<List<AdminGameResultDTO>> searchResults(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Boolean isWinner,
            @RequestParam(required = false) Double minWinRate,
            @RequestParam(required = false) Double maxWinRate,
            @RequestParam(required = false) Double minAvgAttempts,
            @RequestParam(required = false) Double maxAvgAttempts,
            @RequestParam(required = false) Integer leaderboardPosition,
            @RequestParam(required = false) Integer minLeaderboardPosition,
            @RequestParam(required = false) Integer maxLeaderboardPosition) {

        adminAuthService.validateAdminPassword(adminPassword);

        List<AdminGameResultDTO> results = adminService.searchResults(
                username,
                parseStartDate(startDate),
                parseEndDate(endDate),
                isWinner,
                minWinRate,
                maxWinRate,
                minAvgAttempts,
                maxAvgAttempts,
                leaderboardPosition,
                minLeaderboardPosition,
                maxLeaderboardPosition);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/{id}")
    public ResponseEntity<AdminGameResultDTO> getResult(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @PathVariable Long id) {
        adminAuthService.validateAdminPassword(adminPassword);
        return ResponseEntity.ok(adminService.getResult(id));
    }

    @PutMapping("/results/{id}")
    public ResponseEntity<AdminGameResultDTO> updateResult(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @PathVariable Long id,
            @RequestBody AdminGameResultUpdateRequest request) {
        adminAuthService.validateAdminPassword(adminPassword);
        return ResponseEntity.ok(adminService.updateResult(id, request));
    }

    @DeleteMapping("/results/{id}")
    public ResponseEntity<Void> deleteResult(
            @RequestHeader(ADMIN_PASSWORD_HEADER) String adminPassword,
            @PathVariable Long id) {
        adminAuthService.validateAdminPassword(adminPassword);
        adminService.deleteResult(id);
        return ResponseEntity.noContent().build();
    }

    private LocalDateTime parseStartDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() == 10) {
            return LocalDate.parse(trimmed).atStartOfDay();
        }
        return parseDateTime(trimmed);
    }

    private LocalDateTime parseEndDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() == 10) {
            return LocalDate.parse(trimmed).atTime(23, 59, 59);
        }
        return parseDateTime(trimmed);
    }

    private LocalDateTime parseDateTime(String value) {
        try {
            String normalized = value;
            if (normalized.length() == 16) {
                normalized += ":00";
            }
            return LocalDateTime.parse(normalized, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Format de date invalide: " + value);
        }
    }
}
