package ru.yandex.practicum.filmorate;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.service.FilmDbService;
import ru.yandex.practicum.filmorate.dao.service.UserDbService;
import ru.yandex.practicum.filmorate.dao.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmorateApplicationTests {

    private final UserService userService;
    private final FilmService filmService;
    private final UserDbService userDbService;
    private final FilmDbService filmDbService;


    @Autowired
    public FilmorateApplicationTests(UserService userService, FilmService filmService,
                                     UserDbService userDbService, FilmDbService filmDbService) {
        this.userService = userService;
        this.filmService = filmService;
        this.userDbService = userDbService;
        this.filmDbService = filmDbService;
    }


    // Тесты UserService и UserDbService
    @SneakyThrows
    @Test
    void userControllerShouldBeCreateUpdateAndGetAllUsers() {
        String userEmail = "email@User1.ru";
        String userLogin = "loginUser1";
        String userName = "nameUser1";
        LocalDate userBirthDate = LocalDate.of(2001, 1, 1);
        User userFromMap;

        // Тесты на создание пользователей
        assertEquals(0, userService.getSameKindEntityMap().size(),
                "На начало тестов таблица пользователей не пустая");
        assertEquals(List.of(), userDbService.getAllEntityDb(), "" +
                "На начало тестов БД пользователей не пустая");

        User user0 = User.builder()
                .login(userLogin)
                .userName(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        userDbService.createEntityDb(user0);
        assertTrue(userService.getSameKindEntityMap().containsKey(1),
                "Созданный пользователь не найден в памяти по id 1)");
        assertTrue(userDbService.getAllEntityDb().contains(user0),
                "Созданный пользователь не найден в БД");

        userFromMap = userService.getSameKindEntityMap().get(1);

        assertEquals(userName, userFromMap.getUserName(), "Имя пользователя не совпадает");
        assertEquals(userLogin, userFromMap.getLogin(), "Логин пользователя не совпадает");
        assertEquals(userEmail, userFromMap.getEmail(), "e-mail пользователя не совпадает");
        assertEquals(userBirthDate, userFromMap.getBirthday(), "Дата рождения пользователя не совпадает");


        userEmail = "email@User2.ru";
        userLogin = "loginUser2";
        userName = "";
        userBirthDate = LocalDate.of(2002, 2, 2);

        assertEquals(1, userService.getSameKindEntityMap().size(),
                "Перед созданием второго пользователя в таблице не один пользователь");
        assertEquals(1, userDbService.getAllEntityDb().size(),
                "Перед созданием второго пользователя в БД не один пользователь");

        User user01 = User.builder()
                .login(userLogin)
                .userName(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        userDbService.createEntityDb(user01);

        assertTrue(userService.getSameKindEntityMap().containsKey(2),
                "Второй созданный пользователь не найден в памяти по id 2)");
        assertTrue(userDbService.getAllEntityDb().contains(user01),
                "Второй созданный пользователь с id " + user01.getId() + " не найден в БД");

        userFromMap = userService.getSameKindEntityMap().get(2);

        assertEquals(userLogin, userFromMap.getLogin(), "Пустое имя пользователя не совпадает с логином");
        assertEquals(userLogin, userFromMap.getLogin(), "Логин пользователя2 не совпадает");
        assertEquals(userEmail, userFromMap.getEmail(), "e-mail пользователя2 не совпадает");
        assertEquals(userBirthDate, userFromMap.getBirthday(), "Дата рождения пользователя2 не совпадает");

        assertEquals(2, userService.getSameKindEntityMap().size(),
                "После создания второго пользователя в таблице не два пользователя");
        assertEquals(2, userDbService.getAllEntityDb().size(),
                "После создания второго пользователя в БД не два пользователя");


        // Тесты на валидацию при создании пользователей с выбросом исключений
        userEmail = "";
        userLogin = "loginUser3";
        userName = "nameUser3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        User failUser = User.builder()
                .login(userLogin)
                .userName(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser1 = failUser;
        assertThrows(ValidationException.class, () -> userDbService.createEntityDb(finalFailUser1),
                "При попытке создать пользователя с пустым e-mail не выброшено исключение");

        userEmail = "e-Ma.il@WrongFormat";
        userLogin = "loginUser3";
        userName = "nameUser3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        failUser = User.builder()
                .login(userLogin)
                .userName(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser2 = failUser;
        assertThrows(ValidationException.class, () -> userDbService.createEntityDb(finalFailUser2),
                "При попытке создать пользователя с неправильным форматом e-mail не выброшено исключение");

        userEmail = "email@User2.ru";
        userLogin = "loginUser3";
        userName = "nameUser3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        failUser = User.builder()
                .login(userLogin)
                .userName(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser3 = failUser;
        assertThrows(ValidationException.class, () -> userDbService.createEntityDb(finalFailUser3),
                "При попытке создать пользователя с уже зарегистрированным e-mail не выброшено исключение");

        userEmail = "email@User3.ru";
        userLogin = "";
        userName = "nameUser3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        failUser = User.builder()
                .login(userLogin)
                .userName(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser4 = failUser;
        assertThrows(ValidationException.class, () -> userDbService.createEntityDb(finalFailUser4),
                "При попытке создать пользователя с пустым логином не выброшено исключение");

        userEmail = "email@User3.ru";
        userLogin = "login User3";
        userName = "nameUser3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        failUser = User.builder()
                .login(userLogin)
                .userName(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser5 = failUser;
        assertThrows(ValidationException.class, () -> userDbService.createEntityDb(finalFailUser5),
                "При попытке создать пользователя с логином с пробелами не выброшено исключение");

        userEmail = "email@User3.ru";
        userLogin = "loginUser2";
        userName = "nameUser3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        failUser = User.builder()
                .login(userLogin)
                .userName(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser9 = failUser;
        assertThrows(ValidationException.class, () -> userDbService.createEntityDb(finalFailUser9),
                "При попытке создать пользователя с уже зарегистрированным логином не выброшено исключение");

        User finalFailUser6 = failUser;
        assertThrows(ValidationException.class, () -> userDbService.createEntityDb(finalFailUser6),
                "При попытке создать пользователя с пустой датой рождения не выброшено исключение");

        userEmail = "email@User3.ru";
        userLogin = "loginUser3";
        userName = "nameUser3";
        userBirthDate = LocalDate.now().plusYears(1000);

        failUser = User.builder()
                .login(userLogin)
                .userName(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        User finalFailUser7 = failUser;
        assertThrows(ValidationException.class, () -> userDbService.createEntityDb(finalFailUser7),
                "При попытке создать пользователя с датой рождения из будущего не выброшено исключение");


        // Тесты на обновление пользователей
        String updatingUserEmail = "emailUpdated@User1.ru";
        userLogin = "loginUser1";
        String updatingUserName = "nameUser1Updated";
        userBirthDate = LocalDate.of(2003, 3, 3);
        int userId = 1;

        User user1 = User.builder()
                .login(userLogin)
                .userName(updatingUserName)
                .email(updatingUserEmail)
                .birthday(userBirthDate)
                .build();
        user1.setId(userId);

        userDbService.updateEntityDb(user1);

        userFromMap = userService.getSameKindEntityMap().get(userId);

        assertTrue(userService.getSameKindEntityMap().containsKey(userId),
                "Обновлённый пользователь не найден в памяти по id " + userId);
        assertEquals(updatingUserName, userFromMap.getUserName(),
                "Обновлённое имя пользователя не совпадает");
        assertEquals(updatingUserEmail, userFromMap.getEmail(),
                "Обновлённый и-мэйл пользователя не совпадает");
        assertTrue(userDbService.getAllEntityDb().contains(user1),
                "Обновлённый пользователь с id " + user1.getId() + " не найден в БД");

        // тест на проверку ошибочного id обновляемого пользователя
        userEmail = "email@User2.ru";
        userLogin = "loginUser2";
        userName = "nameUser2Update";
        userBirthDate = LocalDate.of(2002, 2, 2);
        userId = -1;

        failUser = User.builder()
                .login(userLogin)
                .userName(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        failUser.setId(userId);

        User finalFailUser8 = failUser;
        assertThrows(EntityNotFoundException.class, () -> userDbService.updateEntityDb(finalFailUser8),
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
                .userName(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        failUser.setId(userId);

        User finalFailUser10 = failUser;
        assertThrows(ValidationException.class, () -> userDbService.updateEntityDb(finalFailUser10),
                "При попытке обновить пользователя с множественными нарушениями валидации не выброшено исключение");

        userEmail = "email@User3.ru";
        userLogin = "loginUser3";
        userName = "Name User3";
        userBirthDate = LocalDate.of(2003, 3, 3);

        User user3 = User.builder()
                .login(userLogin)
                .userName(userName)
                .email(userEmail)
                .birthday(userBirthDate)
                .build();

        userDbService.createEntityDb(user3);

        // тесты на возврат пользователей из памяти и БД
        assertEquals(userService.getSameKindEntityMap().get(3), userDbService.getEntityByIdDb(3),
                "Возвращаемый объект пользователя по id из БД не совпадает");
        assertThrows(EntityNotFoundException.class, () -> userDbService.getEntityByIdDb(-1),
                "При попытке вернуть из БД пользователя с несуществующим id не выброшено исключение");

        assertEquals(List.of(userService.getSameKindEntityMap().get(1), userService.getSameKindEntityMap().get(2),
                        userService.getSameKindEntityMap().get(3)), userService.getAllEntity(),
                "Возвращаемые объекты всех пользователей не совпадает");
        assertEquals(List.of(userService.getSameKindEntityMap().get(1), userService.getSameKindEntityMap().get(2),
                userService.getSameKindEntityMap().get(3)), userDbService.getAllEntityDb(),
                "Возвращаемые объекты всех пользователей из БД не совпадает");

        // тесты работы в БД со связями пользователей тип друзья
        List<Integer> tempList = new ArrayList<>();
        String tableNameFromDb = "user_user_friends";

        UserDbStorage.crudDbSimpleDataMap(tableNameFromDb, null, tempList, true, false);
        assertEquals(0, tempList.size(), "На начало тестов БД друзей не пустая");

        userDbService.addConnectionDb(1, 2, false, false);
        userDbService.addConnectionDb(1, 3, false, false);
        userDbService.addConnectionDb(2, 3, false, false);
        UserDbStorage.crudDbSimpleDataMap(tableNameFromDb, null, tempList, true, false);
        assertEquals(3, tempList.size(), "После добавления трёх связей в БД друзей не три связи");
        tempList.clear();

        assertThrows(EntityNotFoundException.class, ()
                        -> userDbService.addConnectionDb(-1, 2, false, false),
                "При попытке создать связь Друзья с несуществующим id пользователя не выброшено исключение");
        assertThrows(EntityNotFoundException.class, ()
                        -> userDbService.addConnectionDb(2, -1, false, false),
                "При попытке создать связь Друзья с несуществующим id инициатора не выброшено исключение");

        userDbService.addConnectionDb(1, 1, false, false);
        UserDbStorage.crudDbSimpleDataMap(tableNameFromDb, null, tempList, true, false);
        assertEquals(3, tempList.size(), "При попытке создать связь Друзья с одинаковыми id в БД друзей не три связи");
        tempList.clear();

        assertEquals(userService.getFriendsByUserId(1), userDbService.getFriendsByUserIdDb(1),
                "Возвращаемые объекты друзей для пользователя с id 1 из БД не совпадают");
        assertEquals(List.of(userService.getSameKindEntityMap().get(2),
                        userService.getSameKindEntityMap().get(3)), userDbService.getFriendsByUserIdDb(1),
                "Возвращаемые объекты друзей для пользователя с id 1 не пользователи с id 2 и 3");
        assertThrows(EntityNotFoundException.class, () -> userDbService.getEntityByIdDb(-1),
                "При попытке вернуть из БД объекты друзей по id несуществующего пользователя не выброшено исключение");

        assertEquals(userService.getCommonFriends(1, 2), userDbService.getCommonFriendsDb(1, 2),
                "Возвращаемые объекты общих друзей для пользователя с id 1 и 2 из БД не совпадают");
        assertEquals(List.of(userService.getSameKindEntityMap().get(3)), userDbService.getCommonFriendsDb(1, 2),
                "Возвращаемый объект общих друзей друзей для пользователя с id 1 и 2 не пользователь с id 3");
        assertEquals(List.of(userService.getSameKindEntityMap().get(2), userService.getSameKindEntityMap().get(3)),
                userDbService.getCommonFriendsDb(1, 1),
                "При попытке вернуть из БД объекты общих друзей с одинаковыми id " +
                        "не возвращены объекты друзей для пользователя с этим id");

        assertThrows(EntityNotFoundException.class, () -> userDbService.getCommonFriendsDb(-1, 2),
                "При попытке вернуть из БД объекты общих друзей по id несуществующего пользователя не выброшено исключение");
        assertThrows(EntityNotFoundException.class, () -> userDbService.getCommonFriendsDb(2, -1),
                "При попытке вернуть из БД объекты общих друзей по id несуществующего инициатора не выброшено исключение");

        userDbService.removeConnectionDb(1, 3, false, false);
        assertEquals(List.of(userService.getSameKindEntityMap().get(2)), userDbService.getFriendsByUserIdDb(1),
                "После удаления связи из БД для пользователя с id 1 друга с id 3 не возвращает одного оставшегося друга с id 2");
        userDbService.removeConnectionDb(1, 2, false, false);
        assertEquals(List.of(), userDbService.getCommonFriendsDb(1, 2),
                "После удаления связи из БД для пользователя с id 1 друга с id 3, в качестве общих друзей возвращает не пустой список");

        userDbService.deleteEntityByIdDb(1);
        assertThrows(EntityNotFoundException.class, () -> userDbService.getEntityByIdDb(1),
                "После удаления из БД пользователя с id 1, при попытке получить его по id не выброшено исключение");
        assertThrows(EntityNotFoundException.class, () -> userDbService.getFriendsByUserIdDb(1),
                "После удаления из БД пользователя с id 1 при попытке получить по id его друзей не выброшено исключение");
        assertThrows(EntityNotFoundException.class, () -> userDbService.getCommonFriendsDb(1, 2),
                "После удаления из БД пользователя с id 1 при попытке получить общих друзей по его id и id 2 не выброшено исключение");

    }

    // Тесты FilmService и FilmDbService
    @SneakyThrows
    @Test
    void filmControllerShouldBeCreateUpdateAndGetAllFilms() {

        String filmName = "nameFilm1";
        String filmDescription = "descFilm1";
        LocalDate filmReleaseDate = LocalDate.of(1991, 1, 1);
        int filmDuration = 91;

        Film filmFromMap;

        // Тесты на создание фильмов
        assertEquals(0, filmService.getSameKindEntityMap().size(), "На начало тестов таблица фильмов не пустая");
        assertEquals(List.of(), filmDbService.getAllEntityDb(), "На начало тестов БД фильмов не пустая");

        Film film0 = Film.builder()
                .filmName(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .mpaRatingId(1)
                .build();

        filmDbService.createEntityDb(film0);

        assertTrue(filmService.getSameKindEntityMap().containsKey(1), "Созданный фильм не найден в памяти по id 1)");
        assertTrue(filmDbService.getAllEntityDb().contains(film0), "Созданный фильм с id " + film0.getId() + " не найден в БД");

        filmFromMap = filmService.getSameKindEntityMap().get(1);

        assertEquals(filmName, filmFromMap.getFilmName(), "Имя фильма не совпадает");
        assertEquals(filmDescription, filmFromMap.getDescription(), "Описание фильма не совпадает");
        assertEquals(filmReleaseDate, filmFromMap.getReleaseDate(), "Дата релиза фильма не совпадает");
        assertEquals(filmDuration, filmFromMap.getDuration(), "Длительность фильма не совпадает");


        filmName = "nameFilm1"; // фильм с тем же именем, но другой датой релиза должен быть создан
        filmDescription = "";
        filmReleaseDate = LocalDate.of(1895, 12, 28); // граничная дата, когда фильм должен быть создан
        filmDuration = 92;

        assertEquals(1, filmService.getSameKindEntityMap().size(), "Перед созданием второго фильма в таблице не один фильм");
        assertEquals(1, filmDbService.getAllEntityDb().size(), "Перед созданием второго фильма в БД не один фильм");

        Film film01 = Film.builder()
                .filmName(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .mpaRatingId(2)
                .build();

        filmDbService.createEntityDb(film01);

        assertTrue(filmService.getSameKindEntityMap().containsKey(2), "Второй созданный фильм не найден в памяти по id 2)");
        assertTrue(filmDbService.getAllEntityDb().contains(film01), "Второй созданный фильм с id " + film01.getId() + " не найден в БД");

        filmFromMap = filmService.getSameKindEntityMap().get(2);

        assertEquals(filmName, filmFromMap.getFilmName(), "Имя фильма2 не совпадает");
        assertEquals(filmDescription, filmFromMap.getDescription(), "Описание фильма2 не совпадает");
        assertEquals(filmReleaseDate, filmFromMap.getReleaseDate(), "Дата релиза фильма2 не совпадает");
        assertEquals(filmDuration, filmFromMap.getDuration(), "Длительность фильма2 не совпадает");

        assertEquals(2, filmService.getSameKindEntityMap().size(), "После создания второго фильма в таблице не два фильма");
        assertEquals(2, filmDbService.getAllEntityDb().size(), "После создания второго фильма в БД не два фильма");

        filmName = "nameFilmWithGenresWithoutMpa"; // фильм c жанрами и без рейтинга MPA должен быть создан
        filmDescription = "";
        filmReleaseDate = LocalDate.of(2003, 3, 3);
        filmDuration = 333;

        Film filmWithGenres = Film.builder()
                .filmName(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .genreIdSet(new TreeSet<>(Set.of(1, 2 ,3)))
                .build();

        filmDbService.createEntityDb(filmWithGenres);

        assertEquals(3, filmDbService.getAllEntityDb().size(),
                "После создания третьего фильма с жанрами и без MPA, в БД не три фильма");
        assertEquals(0, filmService.getSameKindEntityMap().get(3).getMpaRatingId(),
                "После создания третьего фильма с жанрами и без MPA, MPA не 0");
        assertEquals(Set.of(1, 2 ,3), filmService.getSameKindEntityMap().get(3).getGenreIdSet(),
                "После создания третьего фильма с жанрами и без MPA, id жанров не соответствуют 1, 2, 3");

        // Тесты на валидацию при создании фильмов с выбросом исключений
        filmName = "";
        filmDescription = "descFilm3";
        filmReleaseDate = LocalDate.of(1993, 3, 3);
        filmDuration = 93;

        Film failFilm = Film.builder()
                .filmName(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .build();

        Film finalFailFilm1 = failFilm;
        assertThrows(ValidationException.class, () -> filmDbService.createEntityDb(finalFailFilm1),
                "При попытке создать фильм с пустым названием не выброшено исключение");


        filmName = "nameFilm3";
        filmDescription = "DescriptionMore200Symbols".repeat(10);
        filmReleaseDate = LocalDate.of(1993, 3, 3);
        filmDuration = 99;

        assertTrue(filmDescription.length() > 200, "Описание фильма для теста на ограничение менее 200 символов");

        failFilm = Film.builder()
                .filmName(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .build();

        Film finalFailFilm2 = failFilm;
        assertThrows(ValidationException.class, () -> filmDbService.createEntityDb(finalFailFilm2),
                "При попытке создать фильм с описанием более 200 символов не выброшено исключение");

        filmName = "nameFilm3";
        filmDescription = "descFilm3";
        filmReleaseDate = LocalDate.of(1895, 12, 27);
        filmDuration = 93;

        failFilm = Film.builder()
                .filmName(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .build();

        Film finalFailFilm3 = failFilm;
        assertThrows(ValidationException.class, () -> filmDbService.createEntityDb(finalFailFilm3),
                "При попытке создать фильм с датой релиза ранее 28 декабря 1895 не выброшено исключение");

        filmName = "nameFilm3";
        filmDescription = "descFilm3";
        filmReleaseDate = LocalDate.of(1993, 3, 3);
        filmDuration = 500;

        failFilm = Film.builder()
                .filmName(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .mpaRatingId(-1)
                .build();


        Film finalFailFilm4 = failFilm;
        assertThrows(ValidationException.class, () -> filmDbService.createEntityDb(finalFailFilm4),
                "При попытке создать фильм с несуществующим id MPA рейтинга не выброшено исключение");

        filmName = "nameFilm3";
        filmDescription = "descFilm3";
        filmReleaseDate = LocalDate.of(1993, 3, 3);
        filmDuration = 400;

        failFilm = Film.builder()
                .filmName(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .genreIdSet(new TreeSet<>(Set.of(-1, 2 ,3)))
                .build();


        Film finalFailFilm5 = failFilm;
        assertThrows(ValidationException.class, () -> filmDbService.createEntityDb(finalFailFilm5),
                "При попытке создать фильм с несуществующим id жанра не выброшено исключение");


        filmFromMap = filmService.getSameKindEntityMap().get(1);
        filmName = filmFromMap.getFilmName();
        filmDescription = "descFilm3";
        filmReleaseDate = filmFromMap.getReleaseDate();
        filmDuration = 93;

        failFilm = Film.builder()
                .filmName(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .build();

        Film finalFailFilm6 = failFilm;
        assertThrows(ValidationException.class, () -> filmDbService.createEntityDb(finalFailFilm6),
                "При попытке создать фильм у которого в базе совпадает и имя и дата релиза не выброшено исключение");


        // Тесты на обновление фильмов
        String updatingFilmName = "nameFilm1Updated";
        String updatingFilmDesc = "descFilm1Updated";
        LocalDate updatingFilmReleaseDate = LocalDate.of(2001, 11, 11);
        int updatingFilmDuration = 111;
        int filmId = 1;

        Film film1 = Film.builder()
                .filmName(updatingFilmName)
                .description(updatingFilmDesc)
                .releaseDate(updatingFilmReleaseDate)
                .duration(updatingFilmDuration)
                .mpaRatingId(1)
                .build();
        film1.setId(filmId);

        filmDbService.updateEntityDb(film1);

        filmFromMap = filmService.getSameKindEntityMap().get(filmId);

        assertTrue(filmService.getSameKindEntityMap().containsKey(filmId),
                "Обновлённый фильм не найден в памяти по id " + filmId);
        assertEquals(updatingFilmName, filmFromMap.getFilmName(),
                "Обновлённое название фильма не совпадает");
        assertEquals(updatingFilmDesc, filmFromMap.getDescription(),
                "Обновлённое описание фильма не совпадает");
        assertEquals(updatingFilmReleaseDate, filmFromMap.getReleaseDate(),
                "Обновлённая дата релиза фильма не совпадает");
        assertEquals(updatingFilmDuration, filmFromMap.getDuration(),
                "Обновлённая длительность фильма не совпадает");
        assertTrue(filmDbService.getAllEntityDb().contains(film1),
                "Обновлённый фильм с id " + film1.getId() +  " не найден в БД");

        // тест на проверку ошибочного id обновляемого фильма
        filmId = -1;

        failFilm = Film.builder()
                .filmName(updatingFilmName)
                .description(updatingFilmDesc)
                .releaseDate(updatingFilmReleaseDate)
                .duration(updatingFilmDuration)
                .build();
        failFilm.setId(filmId);

        Film finalFailFilm7 = failFilm;
        assertThrows(EntityNotFoundException.class, () -> filmDbService.updateEntityDb(finalFailFilm7),
                "При попытке обновить фильм с несуществующим id не выброшено исключение");

        // тест на валидацию фильма при обновлении сразу по нескольким полям.
        // валидация по каждому полю отдельно аналогична тестам валидацию при создании пользователя.

        updatingFilmName = "";
        updatingFilmDesc = "DescriptionMore200Symbols".repeat(10);
        updatingFilmReleaseDate = LocalDate.of(1895, 12, 27);
        updatingFilmDuration = 0;
        filmId = 1;

        failFilm = Film.builder()
                .filmName(updatingFilmName)
                .description(updatingFilmDesc)
                .releaseDate(updatingFilmReleaseDate)
                .duration(updatingFilmDuration)
                .mpaRatingId(-1)
                .genreIdSet(new TreeSet<>(Set.of(-1, 2 ,3)))
                .build();
        failFilm.setId(filmId);

        Film finalFailFilm10 = failFilm;
        assertThrows(ValidationException.class, () -> filmDbService.updateEntityDb(finalFailFilm10),
                "При попытке обновить фильм с множественными нарушениями валидации не выброшено исключение");


        // тест на возврат фильмов из памяти и БД
        assertEquals(filmService.getSameKindEntityMap().get(1), filmDbService.getEntityByIdDb(1),
                "Возвращаемый объект фильма по id 1 из БД не совпадает");
        assertThrows(EntityNotFoundException.class, () -> filmDbService.getEntityByIdDb(-1),
                "При попытке вернуть из БД фильм с несуществующим id не выброшено исключение");

        assertEquals(List.of(filmService.getSameKindEntityMap().get(1), filmService.getSameKindEntityMap().get(2),
                        filmService.getSameKindEntityMap().get(3)), filmService.getAllEntity(),
                "Возвращаемые объекты всех фильмов не совпадает");
        assertEquals(List.of(filmService.getSameKindEntityMap().get(1), filmService.getSameKindEntityMap().get(2),
                        filmService.getSameKindEntityMap().get(3)), filmDbService.getAllEntityDb(),
                "Возвращаемые объекты всех фильмов из БД не совпадает");

        // тесты работы в БД со связями фильмов тип лайк
        List<Integer> tempList = new ArrayList<>();
        String tableNameFromDb = "film_user_likes";

        FilmDbStorage.crudDbSimpleDataMap(tableNameFromDb, null, tempList, true, false);
        assertEquals(0, tempList.size(), "На начало тестов БД фильмов не пустая");

        filmDbService.addConnectionDb(1, 2, false, true);
        filmDbService.addConnectionDb(2, 3, false, true);
        filmDbService.addConnectionDb(2, 2, false, true);
        FilmDbStorage.crudDbSimpleDataMap(tableNameFromDb, null, tempList, true, false);
        assertEquals(3, tempList.size(), "После добавления трёх связей в БД лайков не три связи");
        tempList.clear();

        assertThrows(EntityNotFoundException.class, ()
                        -> filmDbService.addConnectionDb(-1, 2, false, true),
                "При попытке создать связь Лайк с несуществующим id фильма не выброшено исключение");
        assertThrows(EntityNotFoundException.class, ()
                        -> filmDbService.addConnectionDb(2, -1, false, true),
                "При попытке создать связь связь Лайк с несуществующим id инициатора пользователя не выброшено исключение");

        assertEquals(filmService.getTopFilms(5), filmDbService.getTopFilmsDb(5),
                "Возвращаемые объекты топа фильмов из БД не совпадают");
        assertEquals(List.of(filmService.getSameKindEntityMap().get(2), filmService.getSameKindEntityMap().get(1),
                        filmService.getSameKindEntityMap().get(3)), filmDbService.getTopFilmsDb(5),
                "Возвращаемый топ фильмов не содержит объекты фильмов с последовательностью id 2, 1, 3");

        assertEquals(2, filmDbService.getTopFilmsDb(2).size(),
                "Возвращаемый топ фильмов с ограничением по количеству возвращает список размером не 2");
        assertEquals(List.of(filmService.getSameKindEntityMap().get(2), filmService.getSameKindEntityMap().get(1)),
                filmDbService.getTopFilmsDb(2),
                "Возвращаемый топ фильмов не содержит объекты фильмов с последовательностью id 2, 1");

        filmDbService.removeConnectionDb(2, 2, false, true);
        assertEquals(List.of(filmService.getSameKindEntityMap().get(1), filmService.getSameKindEntityMap().get(2),
                        filmService.getSameKindEntityMap().get(3)), filmDbService.getTopFilmsDb(5),
                "После удаления связи лайк из БД для фильма с id 2 от пользователя с id 2, " +
                        "возвращаемый топ 5 не соответствует новой последовательности id 1, 2, 3");

        filmDbService.deleteEntityByIdDb(3);
        assertThrows(EntityNotFoundException.class, () -> filmDbService.getEntityByIdDb(3),
                "После удаления из БД фильма с id 3, при попытке получить его по id не выброшено исключение");
        assertEquals(List.of(filmService.getSameKindEntityMap().get(1), filmService.getSameKindEntityMap().get(2)),
                filmDbService.getTopFilmsDb(5), "После удаления из БД фильма с id 3, " +
                        "возвращаемый топ 5 не соответствует новой последовательности id 1, 2");


    }
}
