package tn.esprit.spring_app.entities;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Position {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private User user;
    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;
    private LocalDateTime timestamp;
}
