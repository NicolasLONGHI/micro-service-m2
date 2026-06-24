package dauphine.projetmicroservices.playerservice.repository;

import dauphine.projetmicroservices.playerservice.model.Player;
import org.springframework.data.jpa.domain.Specification;

public final class PlayerSpecifications {

    private PlayerSpecifications() {
    }

    public static Specification<Player> withUsernameSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + search.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("username")), pattern);
        };
    }
}
