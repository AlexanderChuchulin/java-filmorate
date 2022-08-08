package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.service.UserDbService;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;

@RestController
@RequestMapping("/users")
public class UserController extends StorageController<User, Film> {
    private final UserDbService userDbService;

    @Autowired
    public UserController(UserDbService userDbService) {
        this.userDbService = userDbService;
        dbService = userDbService;
    }


    @PutMapping("/{userId}/friends/{friendId}")
    void addFriendController(@PathVariable int userId, @PathVariable int friendId) {
        userDbService.addConnectionDb(userId, friendId, false, false);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    void removeFriendController(@PathVariable int userId, @PathVariable int friendId) {
        userDbService.removeConnectionDb(userId, friendId, false, false);
    }

    @GetMapping("/{userId}/friends")
    ArrayList<User> getAllFriendsController(@PathVariable int userId) {
        return userDbService.getFriendsByUserIdDb(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    ArrayList<User> getCommonFriendsController(@PathVariable int userId, @PathVariable int otherUserId) {
        return userDbService.getCommonFriendsDb(userId, otherUserId);
    }
}
