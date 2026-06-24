package dauphine.projetmicroservices.playerservice.service;

import dauphine.projetmicroservices.playerservice.exception.AdminUnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService {

    @Value("${motus.admin.password}")
    private String adminPassword;

    public void validateAdminPassword(String password) {
        if (password == null || !adminPassword.equals(password)) {
            throw new AdminUnauthorizedException();
        }
    }
}
