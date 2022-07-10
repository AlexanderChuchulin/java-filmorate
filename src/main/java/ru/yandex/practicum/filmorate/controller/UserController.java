package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

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
        inMemoryEntityStorage.addConnection(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    void removeFriend(@PathVariable int userId, @PathVariable int friendId) {
        inMemoryEntityStorage.removeConnection(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    void getAllFriends(@PathVariable int userId) {
        userService.getAllFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    void getCommonFriends(@PathVariable int userId, @PathVariable int otherUserId) {
        userService.getCommonFriends(userId,otherUserId);
    }


}
