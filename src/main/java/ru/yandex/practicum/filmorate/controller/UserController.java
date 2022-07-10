package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;

@RestController
@RequestMapping("/users")
public class UserController extends StorageController<User, Film> {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        super(userService);
        this.userService = userService;
    }

    @PutMapping("/{userId}/friends/{friendId}")
    void addFriendByController(@PathVariable int userId, @PathVariable int friendId) {
        userService.addConnection(userId, friendId, true, false);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    void removeFriendByController(@PathVariable int userId, @PathVariable int friendId) {
        userService.removeConnection(userId, friendId, true, false);
    }

    @GetMapping("/{userId}/friends")
    ArrayList<User> getAllFriendsByController(@PathVariable int userId) {
        return userService.getAllFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    ArrayList<User> getCommonFriendsByController(@PathVariable int userId, @PathVariable int otherUserId) {
        return userService.getCommonFriends(userId,otherUserId);
    }


}
