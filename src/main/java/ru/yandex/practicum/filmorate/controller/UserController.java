package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends EntityController<User> {

    public UserController() {
        this.entityName = "Пользователь";
    }

    Map<String, String> allUsersLoginAndEmail = new HashMap<>();

    @Override
    public void validateEntity(User user, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            excMsg += "Неверный формат электронной почты. ";
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            excMsg += "Логин не может быть пустым и содержать пробелы. ";
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
