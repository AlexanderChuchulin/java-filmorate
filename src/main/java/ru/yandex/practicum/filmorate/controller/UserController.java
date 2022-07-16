package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;

@Component
@RestController
@RequestMapping("/users")
public class UserController extends StorageController<User> {
    UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        super(inMemoryUserStorage);
        this.userService = userService;
    }

    @PutMapping("/{userId}/friends/{friendId}")
    void addFriend(@PathVariable int userId, @PathVariable int friendId) {
        inMemoryEntityStorage.addConnection(userId, friendId, true);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    void removeFriend(@PathVariable int userId, @PathVariable int friendId) {
        inMemoryEntityStorage.removeConnection(userId, friendId, true);
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
