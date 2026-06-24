package dauphine.projetmicroservices.playerservice.util;

public final class PasswordValidator {

    public static final String PASSWORD_RULE_MESSAGE =
            "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre";

    private PasswordValidator() {
    }

    public static void validate(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(PASSWORD_RULE_MESSAGE);
        }
        if (!password.chars().anyMatch(Character::isUpperCase)) {
            throw new IllegalArgumentException(PASSWORD_RULE_MESSAGE);
        }
        if (!password.chars().anyMatch(Character::isLowerCase)) {
            throw new IllegalArgumentException(PASSWORD_RULE_MESSAGE);
        }
        if (!password.chars().anyMatch(Character::isDigit)) {
            throw new IllegalArgumentException(PASSWORD_RULE_MESSAGE);
        }
    }
}
