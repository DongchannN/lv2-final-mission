package finalmission.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Trainer {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    private int creditPrice;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    public Trainer(String name, int creditPrice, String description, String imageUrl, Gym gym) {
        this.name = name;
        this.creditPrice = creditPrice;
        this.description = description;
        this.imageUrl = imageUrl;
        this.gym = gym;
    }
}
