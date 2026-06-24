package dauphine.projetmicroservices.playerservice.service;

import dauphine.projetmicroservices.playerservice.dto.AdminPlayerCreateRequest;
import dauphine.projetmicroservices.playerservice.dto.AdminPlayerDTO;
import dauphine.projetmicroservices.playerservice.dto.AdminPlayerUpdateRequest;
import dauphine.projetmicroservices.playerservice.dto.PagedPlayersResponse;
import dauphine.projetmicroservices.playerservice.model.Player;
import dauphine.projetmicroservices.playerservice.repository.PlayerRepository;
import dauphine.projetmicroservices.playerservice.repository.PlayerSpecifications;
import dauphine.projetmicroservices.playerservice.util.EmailValidator;
import dauphine.projetmicroservices.playerservice.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PlayerAdminService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PagedPlayersResponse searchPlayers(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<Player> resultPage = playerRepository.findAll(
                PlayerSpecifications.withUsernameSearch(emptyToNull(search)),
                pageable);

        return PagedPlayersResponse.builder()
                .content(resultPage.getContent().stream().map(this::toDto).toList())
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .totalPlayers(playerRepository.count())
                .build();
    }

    @Transactional(readOnly = true)
    public AdminPlayerDTO getPlayer(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable: " + id));
        return toDto(player);
    }

    @Transactional
    public AdminPlayerDTO createPlayer(AdminPlayerCreateRequest request) {
        String username = normalizeUsername(request.getUsername());
        String password = request.getPassword();

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        PasswordValidator.validate(password);

        if (playerRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("Ce pseudo est déjà utilisé");
        }

        String email = EmailValidator.normalize(request.getEmail());
        EmailValidator.validate(email);
        if (playerRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Cette adresse e-mail est déjà utilisée");
        }

        Player player = Player.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .registrationDate(LocalDateTime.now())
                .build();

        return toDto(playerRepository.save(player));
    }

    @Transactional
    public AdminPlayerDTO updatePlayer(Long id, AdminPlayerUpdateRequest request) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable: " + id));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            String newUsername = normalizeUsername(request.getUsername());
            playerRepository.findByUsername(newUsername).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalStateException("Ce pseudo est déjà utilisé");
                }
            });
            player.setUsername(newUsername);
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = EmailValidator.normalize(request.getEmail());
            EmailValidator.validate(newEmail);
            playerRepository.findByEmail(newEmail).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalStateException("Cette adresse e-mail est déjà utilisée");
                }
            });
            player.setEmail(newEmail);
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            PasswordValidator.validate(request.getPassword());
            player.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return toDto(playerRepository.save(player));
    }

    @Transactional
    public void deletePlayer(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable: " + id));
        playerRepository.delete(player);
    }

    private AdminPlayerDTO toDto(Player player) {
        return AdminPlayerDTO.builder()
                .id(player.getId())
                .username(player.getUsername())
                .email(player.getEmail())
                .registrationDate(player.getRegistrationDate())
                .build();
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Le pseudo est obligatoire");
        }
        return username.trim();
    }

    private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
