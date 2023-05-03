package ru.practicum.ewm.repository;

import lombok.*;
import ru.practicum.ewm.model.User;

@Data
public class UserRating {
    private User user;
    private Long rate;
}