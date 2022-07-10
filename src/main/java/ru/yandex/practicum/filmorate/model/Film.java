package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class Film extends Entity{
    private int id;
    @NotBlank(message = "Необходимо указать название")
    private String name;
    @NotBlank(message = "Необходимо задать описание")
    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    private String description;
    @NotNull(message = "Необходимо задать дату релиза")
    private LocalDate releaseDate;
    @NotNull
    private int duration;
}
