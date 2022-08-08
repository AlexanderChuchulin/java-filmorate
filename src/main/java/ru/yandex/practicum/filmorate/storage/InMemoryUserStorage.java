package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Component
@Slf4j
public class InMemoryUserStorage extends InMemoryEntityStorage<User, Film> {

    @Autowired
    public InMemoryUserStorage() {
        this.entityName = "Пользователь";
        this.actionName = "Друг";
    }

    @Override
    public void validateEntity(User user, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (user.getUserName() == null || user.getUserName().isEmpty()) {
            user.setUserName(user.getLogin());
        }
        if (user.getEmail() == null || !Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(user.getEmail()).find()) {
            excMsg += "Адрес электронной почты должен быть задан и иметь верный формат. ";
        }
        if (user.getLogin() == null || !Pattern.compile("^[a-zA-Z\\d._-]{3,}$", Pattern.CASE_INSENSITIVE).matcher(user.getLogin()).find()) {
            excMsg += "Логин должен быть задан и состоять только из a-z, A-Z, 0-9, точек, тире и подчёркиваний. ";
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            excMsg += "Дата рождения должна быть задана и быть раньше текущей даты " + LocalDate.now() + ". ";
        }
        //если пользователь с таким e-mail существует и не происходит обновление, выбросить исключение
        if (entityMainPropMap.containsValue(user.getEmail()) & !isUpdate) {
            excMsg += "Пользователь с e-mail " + user.getEmail() + " уже зарегистрирован. ";
        }
        // если пользователь с таким логином существует и не происходит обновление, выбросить исключение
        // если же происходит обновление логина, то нужно удалить старую запись из таблицы логинов-e-mail
        if (entityMainPropMap.containsKey(user.getLogin()) & !isUpdate) {
            excMsg += "Пользователь с логином " + user.getLogin() + " уже зарегистрирован. ";
        } else if (entityMainPropMap.containsKey(user.getLogin()) & isUpdate) {
            entityMainPropMap.remove(user.getLogin());
        }
        if (excMsg.length() > 0) {
            log.warn("Ошибка валидации пользователя. " + excMsg + conclusion);
            throw new ValidationException(excMsg + conclusion);
        }
        entityMainPropMap.put(user.getLogin(), user.getEmail());
    }
}
