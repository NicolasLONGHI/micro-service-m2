package dauphine.projetmicroservices.playerservice.service;

import dauphine.projetmicroservices.playerservice.dto.ChangeEmailRequest;
import dauphine.projetmicroservices.playerservice.dto.ChangePasswordRequest;
import dauphine.projetmicroservices.playerservice.dto.PlayerLoginRequest;
import dauphine.projetmicroservices.playerservice.dto.PlayerRegistrationRequest;
import dauphine.projetmicroservices.playerservice.dto.PlayerResponse;
import dauphine.projetmicroservices.playerservice.exception.InvalidCredentialsException;
import dauphine.projetmicroservices.playerservice.model.Player;
import dauphine.projetmicroservices.playerservice.repository.PlayerRepository;
import dauphine.projetmicroservices.playerservice.util.EmailValidator;
import dauphine.projetmicroservices.playerservice.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PlayerResponse registerPlayer(PlayerRegistrationRequest request) {
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
        ensureEmailAvailable(email, null);

        Player player = Player.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .registrationDate(LocalDateTime.now())
                .build();

        return mapToResponse(playerRepository.save(player));
    }

    @Transactional(readOnly = true)
    public PlayerResponse login(PlayerLoginRequest request) {
        String username = normalizeUsername(request.getUsername());
        String password = request.getPassword();

        if (password == null || password.isBlank()) {
            throw new InvalidCredentialsException();
        }

        Player player = playerRepository.findByUsername(username)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(password, player.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return mapToResponse(player);
    }

    @Transactional
    public PlayerResponse changeEmail(Long playerId, ChangeEmailRequest request) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable: " + playerId));

        String email = EmailValidator.normalize(request.getEmail());
        EmailValidator.validate(email);
        ensureEmailAvailable(email, playerId);

        player.setEmail(email);
        return mapToResponse(playerRepository.save(player));
    }

    @Transactional
    public PlayerResponse changePassword(Long playerId, ChangePasswordRequest request) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable: " + playerId));

        if (request.getOldPassword() == null || request.getNewPassword() == null) {
            throw new IllegalArgumentException("Les mots de passe sont obligatoires");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), player.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        PasswordValidator.validate(request.getNewPassword());
        player.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        return mapToResponse(playerRepository.save(player));
    }

    @Transactional(readOnly = true)
    public Optional<PlayerResponse> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return playerRepository.findByUsername(normalizeUsername(username)).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public PlayerResponse getPlayer(Long id) {
        return playerRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> searchPlayers(String username) {
        List<Player> players = (username == null || username.isBlank())
                ? playerRepository.findAll()
                : playerRepository.findByUsernameContainingIgnoreCase(username.trim());
        return players.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private void ensureEmailAvailable(String email, Long excludePlayerId) {
        playerRepository.findByEmail(email).ifPresent(existing -> {
            if (excludePlayerId == null || !existing.getId().equals(excludePlayerId)) {
                throw new IllegalStateException("Cette adresse e-mail est déjà utilisée");
            }
        });
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Le pseudo est obligatoire");
        }
        return username.trim();
    }

    private PlayerResponse mapToResponse(Player player) {
        return PlayerResponse.builder()
                .id(player.getId())
                .username(player.getUsername())
                .email(player.getEmail())
                .registrationDate(player.getRegistrationDate())
                .build();
    }
}
