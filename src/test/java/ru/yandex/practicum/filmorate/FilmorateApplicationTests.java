package ru.yandex.practicum.filmorate;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RestController
class FilmorateApplicationTests {

    @GetMapping
    public @ResponseBody
    <T extends Entity> T getObjToJSONString(T entity) {
        return entity;
    }


    // Тесты UserController
    @SneakyThrows
    @Test
    void userControllerShouldBeCreateUpdateAndGetAllUsers() {
        UserController userController = new UserController();
        String userEmail = "email@User1.ru";
        String userLogin = "loginUser1";
        String userName = "nameUser1";
        LocalDate userBirthDate = LocalDate.of(2001, 1, 1);
        User userFromMap;

        // Тесты на создание пользователей
        assertEquals(0, userController.getEntityMap().size(), "На начало тестов таблица пользователей не пустая");

        userController.createEntity(User.builder()
                .login(userLogin)
                .name(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build());

        assertTrue(userController.getEntityMap().containsKey(1), "Созданный пользователь не найден в памяти по id 1)");

        userFromMap = userController.getEntityMap().get(1);

        assertEquals(userName, userFromMap.getName(), "Имя пользователя не совпадает");
        assertEquals(userLogin, userFromMap.getLogin(), "Логин пользователя не совпадает");
        assertEquals(userEmail, userFromMap.getEmail(), "e-mail пользователя не совпадает");
        assertEquals(userBirthDate, userFromMap.getBirthday(), "Дата рождения пользователя не совпадает");


        userEmail = "email@User2.ru";
        userLogin = "loginUser2";
        userName = "";
        userBirthDate = LocalDate.of(2002, 2, 2);

        assertEquals(1, userController.getEntityMap().size(), "Перед созданием второго пользователя в таблице не 1 пользователь");

        userController.createEntity(User.builder()
                .login(userLogin)
                .name(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build());

        assertTrue(userController.getEntityMap().containsKey(2), "Второй созданный пользователь не найден в памяти по id 2)");

        userFromMap = userController.getEntityMap().get(2);

        assertEquals(userLogin, userFromMap.getLogin(), "Пустое имя пользователя не совпадает с логином");
        assertEquals(userLogin, userFromMap.getLogin(), "Логин пользователя2 не совпадает");
        assertEquals(userEmail, userFromMap.getEmail(), "e-mail пользователя2 не совпадает");
        assertEquals(userBirthDate, userFromMap.getBirthday(), "Дата рождения пользователя2 не совпадает");

        assertEquals(2, userController.getEntityMap().size(), "После создания второго пользователя в таблице не 2 пользователя");


        // Тесты на валидацию при создании пользователей с выбросом исключений
        userEmail = "";
        userLogin = "loginUser3";
        userName = "nameUser3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        User failUser = User.builder()
                .login(userLogin)
                .name(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser1 = failUser;
        assertThrows(ValidationException.class, () -> userController.createEntity(finalFailUser1),
                "При попытке создать пользователя с пустым e-mail не выброшено исключение");

        userEmail = "e-Mail@WrongFormat";
        userLogin = "loginUser3";
        userName = "nameUser3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        failUser = User.builder()
                .login(userLogin)
                .name(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser2 = failUser;
        assertThrows(ValidationException.class, () -> userController.createEntity(finalFailUser2),
                "При попытке создать пользователя с неправильным форматом e-mail не выброшено исключение");

        userEmail = "email@User2.ru";
        userLogin = "loginUser3";
        userName = "nameUser3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        failUser = User.builder()
                .login(userLogin)
                .name(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser3 = failUser;
        assertThrows(ValidationException.class, () -> userController.createEntity(finalFailUser3),
                "При попытке создать пользователя с уже зарегистрированным e-mail не выброшено исключение");

        userEmail = "email@User3.ru";
        userLogin = "";
        userName = "nameUser3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        failUser = User.builder()
                .login(userLogin)
                .name(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser4 = failUser;
        assertThrows(ValidationException.class, () -> userController.createEntity(finalFailUser4),
                "При попытке создать пользователя с пустым логином не выброшено исключение");

        userEmail = "email@User3.ru";
        userLogin = "login User3";
        userName = "nameUser3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        failUser = User.builder()
                .login(userLogin)
                .name(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser5 = failUser;
        assertThrows(ValidationException.class, () -> userController.createEntity(finalFailUser5),
                "При попытке создать пользователя с логином с пробелами не выброшено исключение");

        userEmail = "email@User3.ru";
        userLogin = "loginUser2";
        userName = "nameUser3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        failUser = User.builder()
                .login(userLogin)
                .name(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser9 = failUser;
        assertThrows(ValidationException.class, () -> userController.createEntity(finalFailUser9),
                "При попытке создать пользователя с уже зарегистрированным логином не выброшено исключение");

        User finalFailUser6 = failUser;
        assertThrows(ValidationException.class, () -> userController.createEntity(finalFailUser6),
                "При попытке создать пользователя с пустой датой рождения не выброшено исключение");

        userEmail = "email@User3.ru";
        userLogin = "loginUser3";
        userName = "nameUser3";
        userBirthDate = LocalDate.now().plusYears(1000);

        failUser = User.builder()
                .login(userLogin)
                .name(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser7 = failUser;
        assertThrows(ValidationException.class, () -> userController.createEntity(finalFailUser7),
                "При попытке создать пользователя с датой рождения из будущего не выброшено исключение");


        // Тесты на обновление пользователей
        String updatingUserEmail = "emailUpdated@User1.ru";
        userLogin = "loginUser1";
        String updatingUserName = "nameUser1Updated";
        userBirthDate = LocalDate.of(2003, 3, 3);
        int userId = 1;

        userController.updateEntity(User.builder()
                .login(userLogin)
                .name(updatingUserName)
                .email(updatingUserEmail)
                .birthday(userBirthDate)
                .id(userId)
                .build());

        userFromMap = userController.getEntityMap().get(userId);

        assertTrue(userController.getEntityMap().containsKey(userId), "Обновлённый пользователь не найден в памяти по id " + userId);
        assertEquals(updatingUserName, userFromMap.getName(), "Обновлённое имя пользователя не совпадает");
        assertEquals(updatingUserEmail, userFromMap.getEmail(), "Обновлённый и-мэйл пользователя не совпадает");

        // тест на проверку ошибочного id обновляемого пользователя
        userEmail = "email@User2.ru";
        userLogin = "loginUser2";
        userName = "nameUser2Update";
        userBirthDate = LocalDate.of(2002, 2, 2);
        userId = -1;

        failUser = User.builder()
                .login(userLogin)
                .name(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .id(userId)
                .build();

        User finalFailUser8 = failUser;
        assertThrows(ValidationException.class, () -> userController.updateEntity(finalFailUser8),
                "При попытке обновить пользователя с несуществующим id не выброшено исключение");

        // тест на валидацию при обновлении пользователя сразу по нескольким полям.
        // валидация по каждому полю отдельно аналогична тестам валидацию при создании пользователя.
        userEmail = "emailUser1.ru";
        userLogin = "login User1";
        userName = "nameUser1Update";
        userBirthDate = LocalDate.now().plusYears(1000);
        userId = 1;

        failUser = User.builder()
                .login(userLogin)
                .name(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .id(userId)
                .build();

        User finalFailUser10 = failUser;
        assertThrows(ValidationException.class, () -> userController.updateEntity(finalFailUser10),
                "При попытке обновить пользователя с множественными нарушениями валидации не выброшено исключение");

        // тест на возврат всех пользователей
        assertEquals(List.of(userController.getEntityMap().get(1), userController.getEntityMap().get(2)), userController.getAllEntity(),
                "Возвращаемое значение всех пользователей не совпадает");

    }

    // Тесты FilmController
    @SneakyThrows
    @Test
    void filmControllerShouldBeCreateUpdateAndGetAllFilms() {
        FilmController filmController = new FilmController();
        String filmName = "nameFilm1";
        String filmDescription = "descFilm1";
        LocalDate filmReleaseDate = LocalDate.of(1991, 1, 1);
        int filmDuration = 91;

        Film filmFromMap;

        // Тесты на создание фильмов
        assertEquals(0, filmController.getEntityMap().size(), "На начало тестов таблица фильмов не пустая");

        filmController.createEntity(Film.builder()
                .name(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .build());

        assertTrue(filmController.getEntityMap().containsKey(1), "Созданный фильм не найден в памяти по id 1)");

        filmFromMap = filmController.getEntityMap().get(1);

        assertEquals(filmName, filmFromMap.getName(), "Имя фильма не совпадает");
        assertEquals(filmDescription, filmFromMap.getDescription(), "Описание фильма не совпадает");
        assertEquals(filmReleaseDate, filmFromMap.getReleaseDate(), "Дата релиза фильма не совпадает");
        assertEquals(filmDuration, filmFromMap.getDuration(), "Длительность фильма не совпадает");


        filmName = "nameFilm1"; // фильм с тем же именем, но другой датой релиза должен быть создан
        filmDescription = "";
        filmReleaseDate = LocalDate.of(1895, 12, 28); // граничная дата, когда фильм должен быть создан
        filmDuration = 92;

        assertEquals(1, filmController.getEntityMap().size(), "Перед созданием второго фильма в таблице не 1 фильм");

        filmController.createEntity(Film.builder()
                .name(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .build());

        assertTrue(filmController.getEntityMap().containsKey(2), "Второй созданный фильм не найден в памяти по id 2)");

        filmFromMap = filmController.getEntityMap().get(2);

        assertEquals(filmName, filmFromMap.getName(), "Имя фильма2 не совпадает");
        assertEquals(filmDescription, filmFromMap.getDescription(), "Описание фильма2 не совпадает");
        assertEquals(filmReleaseDate, filmFromMap.getReleaseDate(), "Дата релиза фильма2 не совпадает");
        assertEquals(filmDuration, filmFromMap.getDuration(), "Длительность фильма2 не совпадает");

        assertEquals(2, filmController.getEntityMap().size(), "После создания фильма пользователя в таблице не 2 пользователя");


        // Тесты на валидацию при создании фильмов с выбросом исключений
        filmName = "";
        filmDescription = "descFilm3";
        filmReleaseDate = LocalDate.of(1993, 3, 3);
        filmDuration = 93;

        Film failFilm = Film.builder()
                .name(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .build();

        Film finalFailFilm1 = failFilm;
        assertThrows(ValidationException.class, () -> filmController.createEntity(finalFailFilm1),
                "При попытке создать фильм с пустым названием не выброшено исключение");



        filmName = "nameFilm3";
        filmDescription = "DescriptionMore200Symbols".repeat(10);
        filmReleaseDate = LocalDate.of(1993, 3, 3);
        filmDuration = 93;

        assertTrue(filmDescription.length() > 200, "Описание фильма для теста на ограничение менее 200 символов");

        failFilm = Film.builder()
                .name(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .build();

        Film finalFailFilm2 = failFilm;
        assertThrows(ValidationException.class, () -> filmController.createEntity(finalFailFilm2),
                "При попытке создать фильм с описанием более 200 символов не выброшено исключение");

        filmName = "nameFilm3";
        filmDescription = "descFilm3";
        filmReleaseDate = LocalDate.of(1895, 12, 27);
        filmDuration = 93;

        failFilm = Film.builder()
                .name(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .build();

        Film finalFailFilm3 = failFilm;
        assertThrows(ValidationException.class, () -> filmController.createEntity(finalFailFilm3),
                "При попытке создать фильм с датой релиза ранее 28 декабря 1895 не выброшено исключение");

        filmName = "nameFilm3";
        filmDescription = "descFilm3";
        filmReleaseDate = LocalDate.of(1993, 3, 3);
        filmDuration = 0;

        failFilm = Film.builder()
                .name(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .build();

        Film finalFailFilm4 = failFilm;
        assertThrows(ValidationException.class, () -> filmController.createEntity(finalFailFilm4),
                "При попытке создать фильм с нулевой длительностью не выброшено исключение");

        filmFromMap = filmController.getEntityMap().get(1);
        filmName = filmFromMap.getName();
        filmDescription = "descFilm3";
        filmReleaseDate = filmFromMap.getReleaseDate();
        filmDuration = 93;

        failFilm = Film.builder()
                .name(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .build();

        Film finalFailFilm5 = failFilm;
        assertThrows(ValidationException.class, () -> filmController.createEntity(finalFailFilm5),
                "При попытке создать фильм у которого в базе совпадает и имя и дата релиза не выброшено исключение");


        // Тесты на обновление фильмов
        String updatingFilmName = "nameFilm1Updated";
        String updatingFilmDesc = "descFilm1Updated";
        LocalDate updatingFilmReleaseDate = LocalDate.of(2001, 11, 11);
        int updatingFilmDuration = 111;
        int filmId = 1;

        filmController.updateEntity(Film.builder()
                .name(updatingFilmName)
                .description(updatingFilmDesc)
                .releaseDate(updatingFilmReleaseDate)
                .duration(updatingFilmDuration)
                .id(filmId)
                .build());

        filmFromMap = filmController.getEntityMap().get(filmId);

        assertTrue(filmController.getEntityMap().containsKey(filmId), "Обновлённый фильм не найден в памяти по id " + filmId);
        assertEquals(updatingFilmName, filmFromMap.getName(), "Обновлённое название фильма не совпадает");
        assertEquals(updatingFilmDesc, filmFromMap.getDescription(), "Обновлённое описание фильма не совпадает");
        assertEquals(updatingFilmReleaseDate, filmFromMap.getReleaseDate(), "Обновлённая дата релиза фильма не совпадает");
        assertEquals(updatingFilmDuration, filmFromMap.getDuration(), "Обновлённая длительность фильма не совпадает");

        // тест на проверку ошибочного id обновляемого фильма
        filmId = -1;

        failFilm = Film.builder()
                .name(updatingFilmName)
                .description(updatingFilmDesc)
                .releaseDate(updatingFilmReleaseDate)
                .duration(updatingFilmDuration)
                .id(filmId)
                .build();

        Film finalFailFilm6 = failFilm;
        assertThrows(ValidationException.class, () -> filmController.updateEntity(finalFailFilm6),
                "При попытке обновить фильм с несуществующим id не выброшено исключение");

        // тест на валидацию фильма при обновлении сразу по нескольким полям.
        // валидация по каждому полю отдельно аналогична тестам валидацию при создании пользователя.

        updatingFilmName = "";
        updatingFilmDesc = "DescriptionMore200Symbols".repeat(10);
        updatingFilmReleaseDate = LocalDate.of(1895, 12, 27);
        updatingFilmDuration = 0;
        filmId = 1;

        failFilm = Film.builder()
                .name(updatingFilmName)
                .description(updatingFilmDesc)
                .releaseDate(updatingFilmReleaseDate)
                .duration(updatingFilmDuration)
                .id(filmId)
                .build();

        Film finalFailFilm10 = failFilm;
        assertThrows(ValidationException.class, () -> filmController.updateEntity(finalFailFilm10),
                "При попытке обновить фильм с множественными нарушениями валидации не выброшено исключение");


        // тест на возврат всех фильмов
        assertEquals(List.of(filmController.getEntityMap().get(1), filmController.getEntityMap().get(2)), filmController.getAllEntity(),
                "Возвращаемое значение всех фильмов не совпадает");

    }
}
