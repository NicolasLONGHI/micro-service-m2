package dauphine.projetmicroservices.gameserver.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guesses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Game game;

    private String guessWord;
    private Integer attemptNumber;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "guess", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<LetterFeedback> feedback = new ArrayList<>();

    public void addFeedback(LetterFeedback letterFeedback) {
        letterFeedback.setGuess(this);
        this.feedback.add(letterFeedback);
    }
}
