package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;

@RestController
@RequestMapping("/users")
public class UserController extends StorageController<User, Film> {
    UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        super(inMemoryUserStorage);
        this.userService = userService;
    }

    @PutMapping("/{userId}/friends/{friendId}")
    void addFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.addConnection(userId, friendId, true, false);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    void removeFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.removeConnection(userId, friendId, true, false);
    }

    @GetMapping("/{userId}/friends")
    ArrayList<User> getAllFriends(@PathVariable int userId) {
        return userService.getAllFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    ArrayList<User> getCommonFriends(@PathVariable int userId, @PathVariable int otherUserId) {
        return userService.getCommonFriends(userId,otherUserId);
    }


}
