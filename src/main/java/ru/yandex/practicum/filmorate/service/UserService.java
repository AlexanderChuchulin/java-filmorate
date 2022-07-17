package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;

@Service
@Slf4j
public class UserService extends EntityService {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        super(inMemoryUserStorage);
        this.entityName = "Пользователь";
        this.actionName = "Друг";
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.workingConnectionsMap = inMemoryUserStorage.getSameKindEntityConnectionsMap();
    }

    // Метод возвращает список всех друзей пользователя по id
    public ArrayList<User> getAllFriends(int userId) {
        inMemoryUserStorage.entityNotFoundCheck("Список друзей не возвращён", userId, false);

        ArrayList<User> friendsList = new ArrayList<>();

        if(!workingConnectionsMap.get(userId).isEmpty()) {
            for (Integer Id : workingConnectionsMap.get(userId)) {
                friendsList.add(inMemoryUserStorage.getSameKindEntityMap().get(Id));
            }
        }
        log.info("Для Пользователя с id " + userId + " возвращён список друзей. Количество объектов " + friendsList.size() + ".");
        return friendsList;
    }

    // Метод возвращает список общих друзей по id обоих пользователей
    public ArrayList<User> getCommonFriends(int userId, int otherUserId) {
        inMemoryUserStorage.entityNotFoundCheck("Список общих друзей не возвращён.", userId, false);

        ArrayList<User> commonFriendsList = new ArrayList<>();

        if (workingConnectionsMap.containsKey(userId) && workingConnectionsMap.containsKey(otherUserId)) {
            for (Integer Id : workingConnectionsMap.get(userId)) {
                if (workingConnectionsMap.get(otherUserId).contains(Id)) {
                    commonFriendsList.add(inMemoryUserStorage.getSameKindEntityMap().get(Id));
                }
            }
        }
        log.info("Для Пользователей с id " + userId + " и " + otherUserId + " возвращён список общих друзей. Количество объектов " + commonFriendsList.size() + ".");
        return commonFriendsList;
    }
}
