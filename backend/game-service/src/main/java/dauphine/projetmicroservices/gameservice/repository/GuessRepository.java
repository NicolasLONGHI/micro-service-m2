package dauphine.projetmicroservices.gameserver.repository;

import dauphine.projetmicroservices.gameserver.model.Guess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuessRepository extends JpaRepository<Guess, Long> {
}
