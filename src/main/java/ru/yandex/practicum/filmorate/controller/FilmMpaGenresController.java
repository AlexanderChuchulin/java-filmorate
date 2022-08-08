package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.util.Map;

@RestController
public class FilmMpaGenresController {
    private final FilmService filmService;

    @Autowired
    public FilmMpaGenresController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/mpa")
    Map<Integer, String> getAllMpaByController() {
        return filmService.getFilmMpaGenres(true);
    }

    @GetMapping("/mpa/{mpaId}")
    Map<Integer, String> getMpaByIdByController(@PathVariable int mpaId) {
        return filmService.getFilmMpaGenres(true, mpaId);
    }

    @GetMapping("/genres")
    Map<Integer, String> getAllGenresByController() {
        return filmService.getFilmMpaGenres(false);
    }

    @GetMapping("/genres/{genreId}")
    Map<Integer, String> getGenreByIdByController(@PathVariable int genreId) {
        return filmService.getFilmMpaGenres(false, genreId);
    }

}