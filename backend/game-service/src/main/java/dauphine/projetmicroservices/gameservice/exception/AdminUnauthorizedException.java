package dauphine.projetmicroservices.gameserver.exception;

public class AdminUnauthorizedException extends RuntimeException {
    public AdminUnauthorizedException() {
        super("Invalid admin credentials");
    }
}
