package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.TreeSet;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class Film extends Entity {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private int mpaRatingId;
    private TreeSet<Integer> genreIdSet;
}
