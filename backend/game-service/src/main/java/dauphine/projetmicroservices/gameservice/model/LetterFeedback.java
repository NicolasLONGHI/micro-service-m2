package dauphine.projetmicroservices.gameserver.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "letter_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guess_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Guess guess;

    private Integer position;
    private String letter;

    @Enumerated(EnumType.STRING)
    private LetterFeedbackStatus status;
}
