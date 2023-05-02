package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserRatingDto;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

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

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<UserDto> userDtos;
        if (CollectionUtils.isEmpty(ids)) {
            Sort sortById = Sort.by(Sort.Direction.ASC, "id");
            Pageable page = PageRequest.of(from / size, size, sortById);
            Page<User> usersPage = repository.findAll(page);
            userDtos = mapper.toUserDtos(usersPage.getContent());
            return userDtos;
        } else {
            userDtos = mapper.toUserDtos(repository.findAllById(ids));
            return userDtos;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserRatingDto> getMostRatingUser(Integer from, Integer size, LocalDateTime eventPublishedDate) {
        Sort sortByRating = Sort.by(Sort.Direction.DESC, "rate");
        Pageable page = PageRequest.of(from / size, size, sortByRating);
        if (eventPublishedDate == null) {
            eventPublishedDate = LocalDateTime.now().minusMonths(3);
        }
        Page<UserRepository.UserRating> mostRatingUserPage = repository.getMostRateUser(eventPublishedDate, page);
        List<UserRepository.UserRating> mostRatingUser = mostRatingUserPage.getContent();
        return mapper.toUserRatingDtos(mostRatingUser);
    }
}