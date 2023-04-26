package ru.practicum.ewm.service;

import org.springframework.data.domain.Page;
import ru.practicum.ewm.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto dto);

    void deleteUser(Long userId);

    Page<UserDto> getUsers(List<Long> ids, Integer from, Integer size);
}