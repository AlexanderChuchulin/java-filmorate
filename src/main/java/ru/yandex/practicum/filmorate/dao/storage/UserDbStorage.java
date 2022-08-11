package ru.yandex.practicum.filmorate.dao.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

@Component
public class UserDbStorage extends EntityDbStorage<User, Film> {

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, InMemoryUserStorage inMemoryUserStorage) {
        super(jdbcTemplate);
        inMemoryStorage = inMemoryUserStorage;
        entityName = inMemoryStorage.getEntityName();
        sameKindDbTableName = "users";
        otherKindDbTableName = "films";
        setMaxIdFromDb();
    }

}
