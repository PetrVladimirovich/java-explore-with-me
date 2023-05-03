package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserRatingDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRating;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "isActive", constant = "false")
    User toUser(UserDto dto);

    UserDto toUserDto(User user);

    List<UserDto> toUserDtos(List<User> users);

    UserShortDto toUserShortDto(User user);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    UserRatingDto toUserRatingDto(UserRating userRating);

    List<UserRatingDto> toUserRatingDtos(List<UserRating> userRating);
}