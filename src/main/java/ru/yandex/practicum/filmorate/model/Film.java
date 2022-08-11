package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.postmanCrutches.FilmDeserializer;
import ru.yandex.practicum.filmorate.postmanCrutches.FilmSerializer;

import java.time.LocalDate;
import java.util.TreeSet;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@JsonSerialize(using = FilmSerializer.class)
@JsonDeserialize(using = FilmDeserializer.class)
public class Film extends Entity {
    private String filmName;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Integer mpaRatingId;
    private TreeSet<Integer> genreIdSet;

}
