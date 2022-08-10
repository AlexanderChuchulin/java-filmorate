package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.storage.GenresDbStorage;

import java.util.Map;

@RestController
@RequestMapping("genres")
public class FilmGenresController {
    private final GenresDbStorage genresDbStorage;

    @Autowired
    public FilmGenresController(GenresDbStorage genresDbStorage) {
        this.genresDbStorage = genresDbStorage;
    }

    @GetMapping
    Map<Integer, String> getAllGenresController() {
        return genresDbStorage.getFilmGenres();
    }

    @GetMapping("/{genreId}")
    Map<Integer, String> getGenreByIdController(@PathVariable int genreId) {
        return genresDbStorage.getFilmGenres(genreId);
    }

}
