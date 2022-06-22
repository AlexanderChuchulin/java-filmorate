package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class Film extends Entity{
    int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    private int duration;
}
