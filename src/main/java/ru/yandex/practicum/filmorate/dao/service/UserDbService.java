package ru.yandex.practicum.filmorate.dao.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.EntityDbStorage;
import ru.yandex.practicum.filmorate.dao.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;


@Service
@Slf4j
public class UserDbService extends EntityDbService<User, Film> {

    @Autowired
    public UserDbService(UserDbStorage userDbStorage, UserService userService) {
        connectionTableNameDb = "user_user_friends";
        dbStorage = userDbStorage;
        inMemoryService = userService;
    }

    // Метод возвращает из БД список всех друзей пользователя по id
    public ArrayList<User> getFriendsByUserIdDb(int userId) {
        inMemoryService.entityNotFoundCheck("Список друзей не возвращён", userId, false);

        String sql;
        int rowCount;
        ArrayList<User> userFriendsList = new ArrayList<>();

        sql = "SELECT COUNT (*) FROM \"user_user_friends\"";
        rowCount = EntityDbStorage.getJdbcTemplate().queryForObject(sql, Integer.class);
        log.info("База данных таблица - friends. Общее количество строк в базе " + rowCount + ".");

        sql = String.format("SELECT COUNT (*) FROM \"users\" WHERE \"users\".\"user_id\" " +
                "IN (SELECT friends.\"friend_id\" FROM \"user_user_friends\" " +
                "AS friends WHERE friends.\"user_id\" = %s)", userId);
        rowCount = EntityDbStorage.getJdbcTemplate().queryForObject(sql, Integer.class);

        if (rowCount > 0) {
            sql = String.format("SELECT * FROM \"users\" WHERE \"users\".\"user_id\" " +
                    "IN (SELECT friends.\"friend_id\" FROM \"user_user_friends\" " +
                    "AS friends WHERE friends.\"user_id\" = %s)", userId);

            EntityDbStorage.getJdbcTemplate().query(sql, (resSet, rowNum)
                    -> userFriendsList.add(dbStorage.convertResSetToEntity(resSet, true)));

            log.info("Для Пользователя с id " + userId +
                    " из БД возвращён список друзей. Количество строк " + rowCount + ".");
        } else {
            log.info("Для Пользователя с id " + userId + " в БД друзья не найдены.");
        }
        return userFriendsList;
    }

    // Метод возвращает из БД список общих друзей по id обоих пользователей
    public ArrayList<User> getCommonFriendsDb(int userId, int otherUserId) {
        inMemoryService.entityNotFoundCheck("Список общих друзей не возвращён из БД.",
                userId, false, otherUserId);

        String sql;
        int rowCount;
        ArrayList<User> commonFriendsList = new ArrayList<>();

        sql = "SELECT COUNT (*) FROM \"user_user_friends\"";
        rowCount = EntityDbStorage.getJdbcTemplate().queryForObject(sql, Integer.class);
        log.info("База данных таблица - friends. Общее количество строк в базе " + rowCount + ".");

        sql = String.format("SELECT COUNT (*) FROM \"users\" WHERE \"users\".\"user_id\" " +
                "IN (SELECT friends.\"friend_id\" FROM \"user_user_friends\" AS friends WHERE friends.\"user_id\" = %s" +
                "INTERSECT SELECT friends.\"friend_id\" FROM \"user_user_friends\" AS friends " +
                "WHERE friends.\"user_id\" = %s)", userId, otherUserId);
        rowCount = EntityDbStorage.getJdbcTemplate().queryForObject(sql, Integer.class);

        if (rowCount > 0) {
            sql = String.format("SELECT * FROM \"users\" WHERE \"users\".\"user_id\" " +
                    "IN (SELECT friends.\"friend_id\" FROM \"user_user_friends\" AS friends " +
                    "WHERE friends.\"user_id\" = %s" +
                    "INTERSECT SELECT friends.\"friend_id\" FROM \"user_user_friends\" AS friends " +
                    "WHERE friends.\"user_id\" = %s)", userId, otherUserId);

            EntityDbStorage.getJdbcTemplate().query(sql, (resSet, rowNum)
                    -> commonFriendsList.add(dbStorage.convertResSetToEntity(resSet, true)));

            log.info("Для Пользователей с id " + userId + " и " + otherUserId +
                    " из БД возвращён список общих друзей. Количество строк " + rowCount + ".");
        } else {
            log.info("Для Пользователей с id " + userId + " и " + otherUserId + " в БД общие друзья не найдены.");
        }
        return commonFriendsList;
    }

}
