package ru.yandex.practicum.filmorate.dao.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

@Component
public class FilmDbStorage extends EntityDbStorage<Film, User> {

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, InMemoryFilmStorage inMemoryFilmStorage) {
        super(jdbcTemplate);
        mpaAndGenresInitializeFromDB();
        inMemoryStorage = inMemoryFilmStorage;
        entityName = inMemoryStorage.getEntityName();
        sameKindDbTableName = "films";
        otherKindDbTableName = "users";
        setMaxIdFromDb();
    }

    // Метод для загрузки таблиц MPA рейтинга и жанров из БД
    private void mpaAndGenresInitializeFromDB() {
        crudDbSimpleDataMap("mpa_rating", InMemoryFilmStorage.getMpaRatingMap(), null, true, false);
        crudDbSimpleDataMap("genres", InMemoryFilmStorage.getFilmGenresMap(), null, true, false);
    }
}
