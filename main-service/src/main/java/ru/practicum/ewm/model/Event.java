package ru.practicum.ewm.model;

import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 2000)
    private String annotation;
    @ManyToOne(targetEntity = Category.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(nullable = false, length = 7000)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User initiator;
    @Column(nullable = false)
    private Float lat;
    @Column(nullable = false)
    private Float lon;
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventStatus state;
    @Column(nullable = false, length = 120)
    private String title;
    @Formula("(select count(r.id) from requests r where r.event_id = id and r.status = 'CONFIRMED')")
    private Long confirmedRequests;
    @Formula("(select count(b.id) - count(c.id) from event_likes a " +
            "left join event_likes b on a.id=b.id and b.reaction = 'LIKE' " +
            "left join event_likes c on a.id=c.id and c.reaction = 'DISLIKE' " +
            "where a.id_event = id " +
            ")")
    private Long rating;
}