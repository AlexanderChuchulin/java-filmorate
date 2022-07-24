package ru.yandex.practicum.filmorate.enums;

import java.util.HashMap;
import java.util.Map;

public enum FilmGenres {
    UNDEFINED ("Undefined"),
    A ("Комедия"),
    B ("Драма"),
    C ("Мультфильм"),
    D ("Триллер"),
    E ("Документальный"),
    F ("Боевик");

    private final String genreTitle;

    FilmGenres(String genreTitle) {
        this.genreTitle = genreTitle;
    }

        public static Map<Integer, String> getFilmGenresDescription() {
        Map<Integer, String> filmGenresMap = new HashMap<>();

        for (FilmGenres filmGenres: FilmGenres.values()) {
            filmGenresMap.put(filmGenres.ordinal(), filmGenres.genreTitle);
        }
        filmGenresMap.remove(0);
        return filmGenresMap;
    }
}
