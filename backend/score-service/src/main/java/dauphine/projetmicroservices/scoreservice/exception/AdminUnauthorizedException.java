package dauphine.projetmicroservices.scoreservice.exception;

public class AdminUnauthorizedException extends RuntimeException {
    public AdminUnauthorizedException() {
        super("Invalid admin credentials");
    }
}
