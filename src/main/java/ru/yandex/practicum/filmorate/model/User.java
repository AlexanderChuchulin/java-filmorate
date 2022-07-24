package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class User extends Entity {
    private int id;
    private String login;
    private String name;
    private String email;
    private LocalDate birthday;
}
