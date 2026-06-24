package dauphine.projetmicroservices.scoreservice.service;

import dauphine.projetmicroservices.scoreservice.dto.AdminLoginResponse;
import dauphine.projetmicroservices.scoreservice.exception.AdminUnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService {

    @Value("${motus.admin.password}")
    private String adminPassword;

    public AdminLoginResponse login(String password) {
        if (adminPassword.equals(password)) {
            return AdminLoginResponse.builder()
                    .authenticated(true)
                    .message("Connexion administrateur réussie")
                    .build();
        }
        return AdminLoginResponse.builder()
                .authenticated(false)
                .message("Mot de passe incorrect")
                .build();
    }

    public void validateAdminPassword(String password) {
        if (password == null || !adminPassword.equals(password)) {
            throw new AdminUnauthorizedException();
        }
    }
}
