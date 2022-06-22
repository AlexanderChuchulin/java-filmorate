package ru.yandex.practicum.filmorate.other;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class Validator {

    static String excMsg = "";

    public static void validateFilm(Film film, String conclusion) {
        excMsg = "";

        if (film.getName().isEmpty() | film.getName().isBlank()) {
            excMsg = "Название фильма не может быть пустым.";
        }
        if (film.getDescription().length() > 200) {
            excMsg = "Максимальная длина описания — 200 символов.";
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            excMsg = "Дата релиза — " + film.getReleaseDate() + " должна быть позже 28 декабря 1895 года.";
        }
        if (film.getDuration() <= 0) {
            excMsg = "Продолжительность фильма должна быть больше 0.";
        }

        if (excMsg.length() > 0) {
            log.warn("Ошибка валидации фильма. " + excMsg + conclusion);
            throw new ValidationException(excMsg + conclusion);
        }
    }

    public static void validateUser(User user, String conclusion) {
        excMsg = "";

        if (!user.getEmail().contains("@") | !user.getEmail().contains(".")) {
            excMsg = "Неверный формат электронной почты";
        }

        if (user.getLogin().isEmpty() | user.getLogin().isBlank() | user.getLogin().contains(" ")) {
            excMsg = "Логин не может быть пустым и содержать пробелы.";
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            excMsg = "Дата рождения — " + user.getBirthday() + " должна быть раньше текущей даты " + LocalDate.now() + ".";
        }

        if (excMsg.length() > 0) {
            log.warn("Ошибка валидации пользователя. " + excMsg + conclusion);
            throw new ValidationException(excMsg + conclusion);
        }
    }
}
