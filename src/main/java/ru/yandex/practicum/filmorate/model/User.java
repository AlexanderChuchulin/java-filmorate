package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class User extends Entity {
    private int id;
    @NotNull(message = "Логин должен быть задан")
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелов")
    private String login;
    private String name;
    @Email(message = "Email должен быть корректным адресом электронной почты")
    private String email;
    @NotNull(message = "Дата рождения должна быть указана")
    @Past(message = "Дата рождения не должна быть позднее текущей")
    private LocalDate birthday;
}
