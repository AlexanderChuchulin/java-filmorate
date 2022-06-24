package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class User extends Entity {
    int id;
    @NotNull
    private String login;
    private String name;
    @Email
    private String email;
    @NotNull
    private LocalDate birthday;
}
