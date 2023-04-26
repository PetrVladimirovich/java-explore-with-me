package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserDto createUser(UserDto dto) {
        return mapper.toUserDto(repository.save(mapper.toUser(dto)));
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            repository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    public Page<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<UserDto> userDtos;
        if (CollectionUtils.isEmpty(ids)) {
            Sort sortById = Sort.by(Sort.Direction.ASC, "id");
            Pageable page = PageRequest.of(from / size, size, sortById);
            Page<User> usersPage = repository.findAll(page);
            userDtos = mapper.toUserDtos(usersPage.getContent());
            return new PageImpl<>(userDtos.stream()
                    .sorted(Comparator.comparing(UserDto::getId))
                    .collect(Collectors.toList()), usersPage.getPageable(), usersPage.getTotalElements());
        } else {
            userDtos = mapper.toUserDtos(repository.findAllById(ids));
            return new PageImpl<>(userDtos.stream()
                    .sorted(Comparator.comparing(UserDto::getId))
                    .collect(Collectors.toList()));
        }
    }
}