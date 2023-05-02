package ru.practicum.ewm.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRating {
    private User user;
    private Long rate;
}