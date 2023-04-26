package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stats")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String app;
    @Column(nullable = false, length = 150)
    private String uri;
    @Column(length = 150)
    private String ip;
    @Column(nullable = false)
    private LocalDateTime timestamp;
}