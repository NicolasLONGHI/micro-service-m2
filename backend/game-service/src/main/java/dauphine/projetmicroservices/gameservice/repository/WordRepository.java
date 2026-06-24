package dauphine.projetmicroservices.gameserver.repository;

import dauphine.projetmicroservices.gameserver.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long>, JpaSpecificationExecutor<Word> {
    Optional<Word> findByValue(String value);

    @Query(value = "SELECT * FROM words ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Word> findRandomWord();

    @Query(value = "SELECT * FROM words WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Word> findRandomWordByDifficulty(@Param("difficulty") String difficulty);
}
