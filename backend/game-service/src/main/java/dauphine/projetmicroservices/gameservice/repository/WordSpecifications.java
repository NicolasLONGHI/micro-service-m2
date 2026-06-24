package dauphine.projetmicroservices.gameserver.repository;

import dauphine.projetmicroservices.gameserver.model.Word;
import org.springframework.data.jpa.domain.Specification;

public final class WordSpecifications {

    private WordSpecifications() {
    }

    public static Specification<Word> withFilters(String search, String difficulty, Integer minLength, Integer maxLength) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("value")), pattern));
            }
            if (difficulty != null && !difficulty.isBlank()) {
                predicates = cb.and(predicates, cb.equal(root.get("difficulty"), difficulty.trim().toUpperCase()));
            }
            if (minLength != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(cb.length(root.get("value")), minLength));
            }
            if (maxLength != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(cb.length(root.get("value")), maxLength));
            }

            return predicates;
        };
    }
}
