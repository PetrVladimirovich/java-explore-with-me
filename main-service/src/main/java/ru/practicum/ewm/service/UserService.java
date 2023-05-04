package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserRatingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    UserDto createUser(UserDto dto);

    void deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    List<UserRatingDto> getMostRatingUser(Integer from, Integer size, LocalDateTime eventPublishedDate);
}