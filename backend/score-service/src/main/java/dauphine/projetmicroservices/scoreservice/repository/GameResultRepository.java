package dauphine.projetmicroservices.scoreservice.repository;

import dauphine.projetmicroservices.scoreservice.model.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    List<GameResult> findByPlayerId(Long playerId);

    List<GameResult> findByPlayerIdAndIsWinner(Long playerId, Boolean isWinner);

    List<GameResult> findByPlayedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT gr FROM GameResult gr WHERE " +
           "(:playerId IS NULL OR gr.playerId = :playerId) AND " +
           "(:isWinner IS NULL OR gr.isWinner = :isWinner) AND " +
           "(:startDate IS NULL OR gr.playedAt >= :startDate) AND " +
           "(:endDate IS NULL OR gr.playedAt <= :endDate)")
    List<GameResult> findByFilters(
            @Param("playerId") Long playerId,
            @Param("isWinner") Boolean isWinner,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
