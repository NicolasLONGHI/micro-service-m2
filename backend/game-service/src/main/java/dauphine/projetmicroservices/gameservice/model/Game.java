package dauphine.projetmicroservices.gameserver.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerId;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private Integer attemptsRemaining;
    private Integer maxAttempts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Word targetWord;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Guess> guesses = new ArrayList<>();

    public void addGuess(Guess guess) {
        guess.setGame(this);
        this.guesses.add(guess);
    }
}
