package ru.yandex.practicum.filmorate.dao.storage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryEntityStorage;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.util.*;

@Component
@Slf4j
public abstract class EntityDbStorage<T extends Entity, V extends Entity> {
    String entityName;
    String sameKindDbTableName;
    String otherKindDbTableName;
    InMemoryEntityStorage<T, V> inMemoryStorage;
    private static JdbcTemplate jdbcTemplate;

    @Autowired
    public EntityDbStorage(JdbcTemplate jdbcTemplate) {
        EntityDbStorage.jdbcTemplate = jdbcTemplate;
    }

    public static JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public String getSameKindDbTableName() {
        return sameKindDbTableName;
    }

    public String getOtherKindDbTableName() {
        return otherKindDbTableName;
    }

    public InMemoryEntityStorage<T, V> getInMemoryStorage() {
        return inMemoryStorage;
    }

    // Метод проверят максимальное значение id в БД и устанавливает его в памяти как стартовое
    void setMaxIdFromDb() {
        String columnName = "";

        if (sameKindDbTableName.contains("user")) {
            columnName = "user_id";
        } else if (sameKindDbTableName.contains("film")) {
            columnName = "film_id";
        }

        String sql = String.format("SELECT MAX(\"%s\") AS max_id FROM \"%s\"", columnName, sameKindDbTableName);
        Integer maxId = jdbcTemplate.queryForObject(sql, Integer.class);

        sql = String.format("SELECT COUNT (*) FROM \"%s\"", sameKindDbTableName);
        Integer rowCount = jdbcTemplate.queryForObject(sql, Integer.class);

        if (maxId != null && maxId > inMemoryStorage.getStartId()) {
            inMemoryStorage.setStartId(maxId);

            log.info("В БД таблице " + sameKindDbTableName + " найдено строк: " + rowCount + ". " +
                    "По максимальному значению id из БД установлено стартовое значение id в памяти "
                    + inMemoryStorage.getStartId() + ".");
        }
    }

    public T createEntity(T entity) {
        findAndGetEntityFromDb(entity);
        T newEntity = inMemoryStorage.createEntity(entity);
        saveEntityToDb(newEntity, false);
        return newEntity;
    }

    public T updateEntity(T entity) {
        findAndGetEntityFromDb(entity);
        T updatedEntity = inMemoryStorage.updateEntity(entity);
        saveEntityToDb(updatedEntity, true);
        return updatedEntity;
    }

    public void deleteEntityById(int entityId) {
        String columnName = "";

        if (sameKindDbTableName.contains("user")) {
            columnName = "user_id";
        } else if (sameKindDbTableName.contains("film")) {
            columnName = "film_id";
        }

        String sql = String.format("DELETE FROM \"%s\" WHERE \"%s\" = '%s'", sameKindDbTableName, columnName, entityId);
        jdbcTemplate.update(sql);

        log.info("Удалены данные объекта с id " + entityId + " из базы данных таблица - " + sameKindDbTableName + ". ");
        inMemoryStorage.deleteEntityById(entityId);
    }

    public void deleteAllEntity() {
        String sql = String.format("DELETE FROM \"%s\"", sameKindDbTableName);
        jdbcTemplate.update(sql);
        log.info("Удалены все данные из базы данных таблица - " + sameKindDbTableName + ". ");
        inMemoryStorage.deleteAllEntity();
    }

    public T getEntityById(int entityId) {
        loadEntityFromDb(sameKindDbTableName, inMemoryStorage.getSameKindEntityMap(), entityId);
        return inMemoryStorage.getEntityById(entityId);
    }

    public ArrayList<T> getAllEntity() {
        loadEntityFromDb(sameKindDbTableName, inMemoryStorage.getSameKindEntityMap());
        return inMemoryStorage.getAllEntity();
    }

    // Метод сохраняет в БД, загружает и удаляет из БД примитивы и строки из определённой таблицы с двумя столбцами
    public static void crudDbSimpleDataMap(String tableNameFromDb, Map inMemoryMap, List inMemoryList,
                                           boolean isLoadFromDb, boolean isRemove, int... entityId) {
        String sql = "";
        String columnName = "";
        String otherColumnName = "";
        int rowCount = 0;

        if (tableNameFromDb.contains("friends")) {
            columnName = "user_id";
            otherColumnName = "friend_id";
        } else if (tableNameFromDb.contains("likes")) {
            columnName = "film_id";
            otherColumnName = "user_id";
        }

        if (isLoadFromDb) {
            if (entityId.length == 0) {
                sql = String.format("SELECT COUNT(*) FROM \"%s\"", tableNameFromDb);
                rowCount = jdbcTemplate.queryForObject(sql, Integer.class);
                log.info("База данных тип - " + tableNameFromDb + ". Количество строк в базе " + rowCount + ".");

                sql = String.format("SELECT * FROM \"%s\"", tableNameFromDb);
            } else if (entityId.length == 1) {
                sql = String.format("SELECT COUNT (*) FROM \"%s\" WHERE \"%s\" = %s", tableNameFromDb, columnName, entityId[0]);
                rowCount = jdbcTemplate.queryForObject(sql, Integer.class);
                log.info("База данных тип - " + tableNameFromDb + ". Количество строк с id " + entityId[0] +
                        " найденных в базе " + rowCount + ".");

                sql = String.format("SELECT * FROM \"%s\" WHERE \"%s\" = %s", tableNameFromDb, columnName, entityId[0]);
            } else if (entityId.length == 2) {
                sql = String.format("SELECT COUNT (*) FROM \"%s\" WHERE \"%s\" = %s AND \"%s\" = %s", tableNameFromDb,
                        columnName, entityId[0], otherColumnName, entityId[1]);
                rowCount = jdbcTemplate.queryForObject(sql, Integer.class);
                log.info("База данных тип - " + tableNameFromDb + ". Количество строк с сочетанием id " + entityId[0] +
                        " и " + entityId[1] + " найденных в базе " + rowCount + ".");

                sql = String.format("SELECT * FROM \"%s\" WHERE \"%s\" = %s AND \"%s\" = %s", tableNameFromDb, columnName,
                        entityId[0], otherColumnName, entityId[1]);
            }

            if (rowCount > 0 && (inMemoryMap != null || inMemoryList != null)) {
                if (inMemoryMap != null) {
                    jdbcTemplate.query(sql, (resSet, rowNum) -> inMemoryMap.put(resSet.getInt(1),
                            resSet.getString(2)));
                }
                if (inMemoryList != null) {
                    jdbcTemplate.query(sql, (resSet, rowNum) -> inMemoryList.add(resSet.getInt(2)));
                }

                log.info("Загружены данные из базы данных таблица - " + tableNameFromDb + ". " +
                        "Количество загруженных строк " + rowCount + ".");
            }
        }

        if (!isLoadFromDb & !isRemove) {
            int saveRowCount = 0;

            if (inMemoryMap.size() > 0) {
                for (Object parentId : inMemoryMap.keySet()) {
                    sql = String.format("SELECT COUNT (*) FROM \"%s\" WHERE \"%s\" = %s AND \"%s\" = %s",
                            tableNameFromDb, columnName, parentId, otherColumnName, inMemoryMap.get(parentId));
                    rowCount = jdbcTemplate.queryForObject(sql, Integer.class);

                    if (rowCount == 0) {
                        sql = String.format("INSERT INTO \"%s\" VALUES ('%s', '%s')", tableNameFromDb, parentId,
                                inMemoryMap.get(parentId));
                        jdbcTemplate.update(sql);

                        saveRowCount++;
                        log.info("Сохранён ряд в базу данных таблица - " + tableNameFromDb +
                                " с значениями " + parentId + " и " + inMemoryMap.get(parentId) + ". " +
                                "Общее количество сохранённых строк " + saveRowCount + ".");
                    } else {
                        log.info("В БД найден дубликат записи. Ряд с значениями " + parentId + " и " +
                                inMemoryMap.get(parentId) + " в БД таблица " + tableNameFromDb +
                                " не сохранён. Общее количество сохранённых строк " + saveRowCount + ".");
                    }
                }
            }
        }

        if (isRemove) {
            if (entityId.length == 0) {
                sql = String.format("SELECT COUNT(*) FROM \"%s\"", tableNameFromDb);
                rowCount = jdbcTemplate.queryForObject(sql, Integer.class);
                log.info("База данных тип - " + tableNameFromDb + ". Количество строк в базе " + rowCount + ".");

                sql = String.format("DELETE FROM \"%s\"", tableNameFromDb);
            } else if (entityId.length == 1) {
                sql = String.format("SELECT COUNT (*) FROM \"%s\" WHERE \"%s\" = %s", tableNameFromDb, columnName, entityId[0]);
                rowCount = jdbcTemplate.queryForObject(sql, Integer.class);
                log.info("База данных тип - " + tableNameFromDb + ". Количество строк с id " + entityId[0] +
                        " найденных в базе " + rowCount + ".");

                sql = String.format("DELETE FROM \"%s\" WHERE \"%s\" = %s", tableNameFromDb, columnName, entityId[0]);
            } else if (entityId.length == 2) {
                sql = String.format("SELECT COUNT (*) FROM \"%s\" WHERE \"%s\" = %s AND \"%s\" = %s", tableNameFromDb,
                        columnName, entityId[0], otherColumnName, entityId[1]);
                rowCount = jdbcTemplate.queryForObject(sql, Integer.class);
                log.info("База данных тип - " + tableNameFromDb + ". Количество строк с сочетанием id " + entityId[0] +
                        " и " + entityId[1] + " найденных в базе " + rowCount + ".");

                sql = String.format("DELETE FROM \"%s\" WHERE \"%s\" = %s AND \"%s\" = %s", tableNameFromDb, columnName,
                        entityId[0], otherColumnName, entityId[1]);
            }
            if (rowCount > 0) {
                jdbcTemplate.update(sql);
                log.info("Удалены данные из базы данных таблица - " + tableNameFromDb +
                        ". Количество удалённых строк " + rowCount + ".");
            }
        }
    }

    // Метод загружает в память все сущности из базы данных или одну сущность если задан id
    public void loadEntityFromDb(String tableNameFromDB, Map imMemoryMap, int... entityId) {
        String columnName = "";
        String logMsg;
        String sql = String.format("SELECT COUNT(*) FROM \"%s\"", tableNameFromDB);
        int rowCount = jdbcTemplate.queryForObject(sql, Integer.class);

        log.info("База данных тип - " + tableNameFromDB + ". Количество объектов в базе " + rowCount + ".");

        if (tableNameFromDB.contains("user")) {
            columnName = "user_id";
        } else if (tableNameFromDB.contains("film")) {
            columnName = "film_id";
        }

        if (entityId.length == 0) {
            sql = String.format("SELECT * FROM \"%s\"", tableNameFromDB);
            logMsg = "Загружены данные из базы данных таблица - " + tableNameFromDB +
                    ". Количество загруженных объектов " + rowCount + ".";
        } else {
            sql = String.format("SELECT COUNT (*) FROM \"%s\" WHERE \"%s\" = %s",
                    tableNameFromDB, columnName, entityId[0]);
            rowCount = jdbcTemplate.queryForObject(sql, Integer.class);

            sql = String.format("SELECT * FROM \"%s\" WHERE \"%s\" = %s", tableNameFromDB, columnName, entityId[0]);

            logMsg = "Загружена строка с id " + entityId[0] + " из базы данных таблица - " + tableNameFromDB + ". ";
        }

        if (rowCount > 0) {
            String finalColumnName = columnName;

            jdbcTemplate.query(sql, (resSet, rowNum) -> imMemoryMap.put(resSet.getInt(finalColumnName),
                    convertResSetToEntity(resSet, tableNameFromDB.contains("user"))));
            log.info(logMsg);
        }
    }

    /*    При создании и обновлении объектов, для контроля за дубликатами сущностей по ключевым полям,
        метод ищет и при нахождении загружает в память сами сущности
        и их ключевые поля в соответствующие таблицы главных свойств: для пользователя - логин и e-mail,
        для фильма сочетание названия и даты выпуска*/
    private void findAndGetEntityFromDb(Entity entity) {
        String sql = "";
        int rowCount = 0;

        if (entity.getClass() == User.class) {
            User user = (User) entity;

            sql = String.format("SELECT COUNT (*) FROM \"users\" WHERE \"user_email\" = '%s' OR \"user_login\" = '%s'",
                    user.getEmail(), user.getLogin());
            rowCount = jdbcTemplate.queryForObject(sql, Integer.class);

            sql = String.format("SELECT * FROM \"users\" WHERE \"user_email\" = '%s' OR \"user_login\" = '%s'",
                    user.getEmail(), user.getLogin());
        }

        if (entity.getClass() == Film.class) {
            Film film = (Film) entity;
            sql = String.format("SELECT COUNT (*) FROM \"films\" WHERE \"film_name\" = '%s'", film.getFilmName());
            rowCount = jdbcTemplate.queryForObject(sql, Integer.class);

            sql = String.format("SELECT * FROM \"films\" WHERE \"film_name\" = '%s'", film.getFilmName());
        }

        if (rowCount == 0) {
            return;
        }

        if (entity.getClass() == User.class) {
            jdbcTemplate.query(sql, (resSet, rowNum)
                    -> inMemoryStorage.getSameKindEntityMap().put(resSet.getInt("user_id"),
                    convertResSetToEntity(resSet, true)));
            jdbcTemplate.query(sql, (resSet, rowNum)
                    -> inMemoryStorage.getEntityMainPropMap().put(resSet.getString("user_login"),
                    resSet.getString("user_email")));

            log.info("При проверке найдены и загружены данные из базы данных таблица - Пользователи. " +
                    "Количество загруженных объектов " + rowCount + ".");
        }

        if (entity.getClass() == Film.class) {
            jdbcTemplate.query(sql, (resSet, rowNum)
                    -> inMemoryStorage.getSameKindEntityMap().put(resSet.getInt("film_id"),
                    convertResSetToEntity(resSet, false)));
            jdbcTemplate.query(sql, (resSet, rowNum)
                    -> inMemoryStorage.getEntityMainPropMap().put(resSet.getString("film_name") + ";" +
                            resSet.getString("film_release_date"),
                    resSet.getString("film_name") + ";" + resSet.getString("film_release_date")));

            log.info("При проверке найдены и загружены данные из базы данных таблица - Фильмы. " +
                    "Количество загруженных объектов " + rowCount + ".");
        }

    }

    // Метод сохраняет сущность в соответствующую таблицу БД при создании или обновлении
    private void saveEntityToDb(T entity, boolean isUpdate) {
        String sqlKeyWord = "INSERT INTO";
        String sql;
        String actionName = " сохранён в базу данных. ";
        int entityId;

        if (isUpdate) {
            sqlKeyWord = "MERGE INTO";
            actionName = " обновлён в базе данных. ";
        }

        if (entity.getClass() == User.class) {
            User user = (User) entity;

            sql = sqlKeyWord + " \"users\" (\"user_id\", \"user_email\", \"user_login\", \"user_birthdate\", \"user_name\")"
                    + " VALUES " + "('" + user.getId() + "', '" + user.getEmail() + "', '"
                    + user.getLogin() + "', '" + user.getBirthday() + "', '" + user.getUserName() + "');";
            jdbcTemplate.update(sql);
        }

        if (entity.getClass() == Film.class) {
            Film film = (Film) entity;

            if (film.getMpaRatingId() != null) {
                sql = sqlKeyWord + " \"films\" (\"film_id\", \"film_name\", \"film_duration_min\", \"film_release_date\"," +
                        " \"mpa_id\", \"film_desc\")" + " VALUES " + "('" + film.getId() + "', '" + film.getFilmName() +
                        "', '" + film.getDuration() + "', '" + film.getReleaseDate() + "', '" + film.getMpaRatingId() +
                        "', '" + film.getDescription() + "');";
            } else {
                sql = sqlKeyWord + " \"films\" (\"film_id\", \"film_name\", \"film_duration_min\", \"film_release_date\"," +
                        " \"film_desc\")" + " VALUES " + "('" + film.getId() + "', '" + film.getFilmName() + "', '"
                        + film.getDuration() + "', '" + film.getReleaseDate() + "', '" + film.getDescription() + "');";
            }
            jdbcTemplate.update(sql);

            if (sqlKeyWord.equals("MERGE INTO")) {
                sql = "DELETE FROM \"film_genre\" WHERE \"film_id\" = " + film.getId();
                jdbcTemplate.update(sql);
            }

            if (film.getGenreIdSet() != null && !film.getGenreIdSet().isEmpty()) {
                for (int genreId : film.getGenreIdSet()) {
                    sql = "INSERT INTO \"film_genre\" (\"film_id\", \"genre_id\")" + " VALUES " +
                            "('" + film.getId() + "', '" + genreId + "');";
                    jdbcTemplate.update(sql);
                }
            }
        }
        entityId = entity.getId();
        log.info(entityName + " с id " + entityId + actionName);
    }

    // Метод преобразует данные из базы данных в объект Entity
    @SneakyThrows
    public T convertResSetToEntity(ResultSet resSet, boolean isUser) {
        if (isUser) {
            User user = User.builder()
                    .login(resSet.getString("user_login"))
                    .email(resSet.getString("user_email"))
                    .birthday(resSet.getDate("user_birthdate").toLocalDate())
                    .userName(resSet.getString("user_name"))
                    .build();

            user.setId(resSet.getInt("user_id"));
            return (T) user;
        } else {
            String sql;

            Film film = Film.builder()
                    .filmName(resSet.getString("film_name"))
                    .releaseDate(resSet.getDate("film_release_date").toLocalDate())
                    .duration(resSet.getInt("film_duration_min"))
                    .description(resSet.getString("film_desc"))
                    .mpaRatingId(resSet.getInt("mpa_id"))
                    .build();

            film.setId(resSet.getInt("film_id"));

            sql = String.format("SELECT COUNT (*) FROM \"film_genre\" WHERE \"film_id\" = '%s'", film.getId());
            Integer rowCount = jdbcTemplate.queryForObject(sql, Integer.class);

            if (rowCount > 0) {
                if (film.getGenreIdSet() == null) {
                    film.setGenreIdSet(new TreeSet<>());
                }

                sql = String.format("SELECT \"genre_id\" FROM \"film_genre\" WHERE \"film_id\" = '%s'", film.getId());
                jdbcTemplate.query(sql, (resSetGenres, rowNum)
                        -> film.getGenreIdSet().add(resSetGenres.getInt("genre_id")));

                log.info("Для фильма c id " + film.getId() + " найдены и загружены жанры из базы данных таблица - " +
                        "Фильм-Жанр. Количество загруженных жанров " + rowCount + ".");
            }
            return (T) film;
        }
    }

}
