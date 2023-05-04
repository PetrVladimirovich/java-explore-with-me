package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto dto) {
        log.info("Create User: {}", dto);
        return new ResponseEntity<>(service.createUser(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting User with id={}", ids);
        return ResponseEntity.ok(service.getUsers(ids, from, size));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "userId") Long userId) {
        log.info("Deleting user with id={}", userId);
        service.deleteUser(userId);
    }
}