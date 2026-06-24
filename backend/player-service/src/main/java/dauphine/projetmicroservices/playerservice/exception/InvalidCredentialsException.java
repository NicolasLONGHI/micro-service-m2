package dauphine.projetmicroservices.playerservice.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Pseudo ou mot de passe incorrect");
    }
}
