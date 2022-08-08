package ru.yandex.practicum.filmorate.dao.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.EntityDbStorage;
import ru.yandex.practicum.filmorate.dao.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Service
@Slf4j
public class FilmDbService extends EntityDbService <Film, User> {

    @Autowired
    public FilmDbService(FilmDbStorage filmDbStorage, FilmService filmService) {
        connectionTableNameDb = "film_user_likes";
        dbStorage = filmDbStorage;
        inMemoryService = filmService;
    }

    // Метод возвращает из БД список фильмов заданного размера с наибольшим количеством лайков
    public List<Film> getTopFilmsDb(int limit) {
        String sql;
        int rowCount;
        int likeCount;
        List<Film> topFilmsList = new ArrayList<>();

        sql = String.format("SELECT COUNT(*) FROM \"%s\"", "films");
        rowCount = EntityDbStorage.getJdbcTemplate().queryForObject(sql, Integer.class);
        sql = "SELECT COUNT (DISTINCT likes.\"film_id\") FROM \"film_user_likes\" AS likes";
        likeCount = EntityDbStorage.getJdbcTemplate().queryForObject(sql, Integer.class);
        log.info("База данных таблица - films. Количество строк в базе " + rowCount + ".");

        if (rowCount > 0) {
            sql = String.format("SELECT \"films\".* FROM \"films\" LEFT OUTER JOIN \"film_user_likes\" AS likes " +
                    "ON likes.\"film_id\" = \"films\".\"film_id\" GROUP BY \"films\".\"film_id\" " +
                    "ORDER BY COUNT(likes.\"film_id\") DESC LIMIT %s", limit);
            EntityDbStorage.getJdbcTemplate().query(sql, (resSet, rowNum) -> topFilmsList.add(dbStorage.convertResSetToEntity(resSet, false)));
            log.info("Из БД Возвращён топ " + limit + " фильмов по количеству лайков. Размер списка: " + topFilmsList.size()
                    + ". Всего фильмов с оценками: " + likeCount + ".");
        }
        return topFilmsList;
    }
}
