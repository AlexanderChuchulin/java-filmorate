package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;

@Service
@Slf4j
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    // Метод возвращает список всех друзей по id пользователя
    public ArrayList<User> getAllFriends(int userId) {
        if (!inMemoryUserStorage.getConnectionsMap().containsKey(userId)) {
            // исключение пользователь не найден?
        }

        if(inMemoryUserStorage.getConnectionsMap().get(userId).isEmpty()) {
            // исключение пользователь не найден?
            return null;
        }

        ArrayList<User> friendsList = new ArrayList<>();

        for (Integer Id : inMemoryUserStorage.getConnectionsMap().get(userId)) {
            friendsList.add(inMemoryUserStorage.getEntityMap().get(Id));
        }
        log.info("Для Пользователя с id " + userId + " возвращён список друзей. Количество объектов " + friendsList.size() + ".");
        return friendsList;
    }

    // Метод возвращает список общих друзей по id обоих пользователей
    public ArrayList<User> getCommonFriends(int userId, int otherUserId) {
        if (!inMemoryUserStorage.getConnectionsMap().containsKey(userId)) {
            // исключение пользователь не найден?
        } else if (!inMemoryUserStorage.getConnectionsMap().containsKey(otherUserId)) {
            // исключение другой пользователь не найден?
        }

        ArrayList<User> commonFriendsList = new ArrayList<>();

        for (Integer Id : inMemoryUserStorage.getConnectionsMap().get(userId)) {
            if (inMemoryUserStorage.getConnectionsMap().get(otherUserId).contains(Id)) {
                commonFriendsList.add(inMemoryUserStorage.getEntityMap().get(Id));
            }
        }
        log.info("Для Пользователей с id " + userId + " и " + otherUserId + " возвращён список общих друзей. Количество объектов " + commonFriendsList.size() + ".");
        return commonFriendsList;
    }
}
