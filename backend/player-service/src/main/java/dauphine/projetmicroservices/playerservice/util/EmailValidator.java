package dauphine.projetmicroservices.playerservice.util;

import java.util.regex.Pattern;

public final class EmailValidator {

    public static final String EMAIL_RULE_MESSAGE = "Adresse e-mail invalide";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private EmailValidator() {
    }

    public static String normalize(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'adresse e-mail est obligatoire");
        }
        return email.trim().toLowerCase();
    }

    public static void validate(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(EMAIL_RULE_MESSAGE);
        }
    }
}
