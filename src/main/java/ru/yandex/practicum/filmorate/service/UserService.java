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

    // Метод возвращает список всех друзей пользователя по id
    public ArrayList<User> getAllFriends(int userId) {
        inMemoryUserStorage.entityNotFoundCheck("Список друзей не возвращён", userId);

        ArrayList<User> friendsList = new ArrayList<>();

        if(!inMemoryUserStorage.getConnectionsMap().get(userId).isEmpty()) {
            for (Integer Id : inMemoryUserStorage.getConnectionsMap().get(userId)) {
                friendsList.add(inMemoryUserStorage.getEntityMap().get(Id));
            }
        }
        log.info("Для Пользователя с id " + userId + " возвращён список друзей. Количество объектов " + friendsList.size() + ".");
        return friendsList;
    }

    // Метод возвращает список общих друзей по id обоих пользователей
    public ArrayList<User> getCommonFriends(int userId, int otherUserId) {
        inMemoryUserStorage.entityNotFoundCheck("Список общих друзей не возвращён.", userId, otherUserId);

        ArrayList<User> commonFriendsList = new ArrayList<>();

        if (inMemoryUserStorage.getConnectionsMap().containsKey(userId) && inMemoryUserStorage.getConnectionsMap().containsKey(otherUserId)) {
            for (Integer Id : inMemoryUserStorage.getConnectionsMap().get(userId)) {
                if (inMemoryUserStorage.getConnectionsMap().get(otherUserId).contains(Id)) {
                    commonFriendsList.add(inMemoryUserStorage.getEntityMap().get(Id));
                }
            }
        }
        log.info("Для Пользователей с id " + userId + " и " + otherUserId + " возвращён список общих друзей. Количество объектов " + commonFriendsList.size() + ".");
        return commonFriendsList;
    }
}
