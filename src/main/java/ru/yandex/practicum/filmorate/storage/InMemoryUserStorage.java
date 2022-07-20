package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@Slf4j
public class InMemoryUserStorage extends InMemoryEntityStorage<User, Film> {
    private final Map<String, String> allUsersLoginAndEmail = new HashMap<>();

    @Override
    public void validateEntity(User user, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getEmail().isEmpty()) {
            excMsg += "Адрес электронной почты не может быть пустым. ";
        } else if (!Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(user.getEmail()).find()) {
            excMsg += "Неверный формат электронной почты " + user.getEmail() + ". ";
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ") ||
                !Pattern.compile("^[a-zA-Z\\d._-]{3,}$", Pattern.CASE_INSENSITIVE).matcher(user.getLogin()).find()) {
            excMsg += "Логин не может быть пустым или содержать пробелы, должен состоять только из a-z, A-Z, 0-9, точек, тире и подчёркиваний. ";
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            excMsg += "Дата рождения — " + user.getBirthday() + " должна быть раньше текущей даты " + LocalDate.now() + ". ";
        }
        //если пользователь с таким e-mail существует и не происходит обновление, выбросить исключение
        if (allUsersLoginAndEmail.containsValue(user.getEmail()) & !isUpdate) {
            excMsg += "Пользователь с e-mail " + user.getEmail() + " уже зарегистрирован. ";
        }
        // если пользователь с таким логином существует и не происходит обновление, выбросить исключение
        // если же происходит обновление логина, то нужно удалить старую запись из таблицы логинов-e-mail
        if (allUsersLoginAndEmail.containsKey(user.getLogin()) & !isUpdate) {
            excMsg += "Пользователь с логином " + user.getLogin() + " уже зарегистрирован. ";
        } else if (allUsersLoginAndEmail.containsKey(user.getLogin()) & isUpdate) {
            allUsersLoginAndEmail.remove(user.getLogin());
        }
        if (excMsg.length() > 0) {
            log.warn("Ошибка валидации пользователя. " + excMsg + conclusion);
            throw new ValidationException(excMsg + conclusion);
        }
        allUsersLoginAndEmail.put(user.getLogin(), user.getEmail());
    }
}
