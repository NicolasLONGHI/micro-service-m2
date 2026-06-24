package dauphine.projetmicroservices.playerservice.repository;

import dauphine.projetmicroservices.playerservice.model.Player;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, JpaSpecificationExecutor<Player> {
	Optional<Player> findByUsername(String username);
	Optional<Player> findByEmail(String email);
	List<Player> findByUsernameContainingIgnoreCase(String username);
}
