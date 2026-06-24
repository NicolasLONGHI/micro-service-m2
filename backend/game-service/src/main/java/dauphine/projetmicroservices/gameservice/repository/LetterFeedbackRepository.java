package dauphine.projetmicroservices.gameserver.repository;

import dauphine.projetmicroservices.gameserver.model.LetterFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterFeedbackRepository extends JpaRepository<LetterFeedback, Long> {
}
