package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_likes")
public class ReactionOnEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User participant;
    @ManyToOne(targetEntity = Event.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_event", nullable = false)
    private Event event;
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ReactionStatus reaction;
    @Column(name = "created", nullable = false)
    private LocalDateTime timestamp;

}