package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Map;

@Service
@Slf4j
public class UserService extends EntityService<User, Film> {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage, InMemoryFilmStorage inMemoryFilmStorage) {
        super(inMemoryUserStorage, inMemoryFilmStorage);
        this.inMemoryUserStorage = inMemoryUserStorage;
        inMemoryStorage = inMemoryUserStorage;
        entityName = inMemoryUserStorage.getEntityName();
        actionName = inMemoryUserStorage.getActionName();
    }

    public Map<Integer, User> getSameKindEntityMap() {
        return inMemoryUserStorage.getSameKindEntityMap();
    }


    @Override
    public void entityNotFoundCheck(String conclusion, int parentId, boolean isNotSameKindChild, int... childId) {
        inMemoryUserStorage.entityNotFoundCheck(conclusion, parentId, isNotSameKindChild, childId);
    }

    // Метод возвращает список всех друзей пользователя по id
    public ArrayList<User> getFriendsByUserId(int userId) {
        entityNotFoundCheck("Список друзей не возвращён", userId, false);

        ArrayList<User> friendsList = new ArrayList<>();

        if (workingConnectionsMap.containsKey(userId) && !workingConnectionsMap.get(userId).isEmpty()) {
            for (Integer Id : workingConnectionsMap.get(userId)) {
                friendsList.add(inMemoryUserStorage.getSameKindEntityMap().get(Id));
            }
        }
        log.info("Для Пользователя с id " + userId + " возвращён список друзей. " +
                "Количество объектов " + friendsList.size() + ".");
        return friendsList;
    }

    // Метод возвращает список общих друзей по id обоих пользователей
    public ArrayList<User> getCommonFriends(int userId, int otherUserId) {
        entityNotFoundCheck("Список общих друзей не возвращён.", userId, false, otherUserId);

        ArrayList<User> commonFriendsList = new ArrayList<>();

        if (workingConnectionsMap.containsKey(userId) && workingConnectionsMap.containsKey(otherUserId)) {
            for (Integer Id : workingConnectionsMap.get(userId)) {
                if (workingConnectionsMap.get(otherUserId).contains(Id)) {
                    commonFriendsList.add(inMemoryUserStorage.getSameKindEntityMap().get(Id));
                }
            }
        }
        log.info("Для Пользователей с id " + userId + " и " + otherUserId + " " +
                "возвращён список общих друзей. Количество объектов " + commonFriendsList.size() + ".");
        return commonFriendsList;
    }


}
