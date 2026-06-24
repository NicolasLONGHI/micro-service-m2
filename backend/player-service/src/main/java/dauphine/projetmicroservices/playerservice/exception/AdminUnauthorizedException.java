package dauphine.projetmicroservices.playerservice.exception;

public class AdminUnauthorizedException extends RuntimeException {
    public AdminUnauthorizedException() {
        super("Mot de passe administrateur invalide");
    }
}
