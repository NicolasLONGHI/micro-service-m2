package dauphine.projetmicroservices.scoreservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long gameId;
    private Long playerId;
    private String targetWord;
    private Integer attemptsCount;
    private Boolean isWinner;
    private LocalDateTime playedAt;
    private String difficulty;
    private Integer pointsEarned;
}
