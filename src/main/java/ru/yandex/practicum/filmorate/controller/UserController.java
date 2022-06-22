package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.other.IntIdGenerator;
import ru.yandex.practicum.filmorate.other.Validator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> usersMap = new HashMap<>();
    IntIdGenerator<User> intIdGenerator = new IntIdGenerator<>();

    @GetMapping()
    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(usersMap.values());
    }

    @PostMapping()
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        Validator.validateUser(user, " Пользователь не создан.");
        user.setId(intIdGenerator.generateId(usersMap));
        if(user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        usersMap.put(user.getId(), user);
        log.info("Создан пользователь с id. " + user.getId() + ". " + user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        if (!usersMap.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с id " + user.getId() + " не найден. Пользователь не обновлён.");
        }
        Validator.validateUser(user,  " Пользователь не обновлён.");
        usersMap.put(user.getId(), user);
        log.info("Пользователь с id " + user.getId() + " обновлён. " + user);
        return user;
    }

}
