package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;

@Component
@RestController
@RequestMapping("/films")
public class FilmController extends StorageController<Film> {
    FilmService filmService;

    @Autowired
    protected FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        super(inMemoryFilmStorage);
        this.filmService = filmService;
    }

    @PutMapping("/{filmId}/like/{userId}")
    void addFilmLike(@PathVariable int filmId, @PathVariable int userId) {
        inMemoryEntityStorage.addConnection(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    void removeFilmLike(@PathVariable int filmId, @PathVariable int userId) {
        inMemoryEntityStorage.removeConnection(filmId, userId);
    }

    @GetMapping("/popular")
    Collection<Film> getTopFilms(@RequestParam(required = false) int count) {
        return filmService.getTopFilms(count);
    }

}
