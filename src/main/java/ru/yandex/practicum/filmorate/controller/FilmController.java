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
    InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    protected FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        super(inMemoryFilmStorage);
        this.filmService = filmService;
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    @PutMapping("/{filmId}/like/{userId}")
    void addFilmLike(@PathVariable int filmId, @PathVariable int userId) {
        inMemoryFilmStorage.addConnection(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    void removeFilmLike(@PathVariable int filmId, @PathVariable int userId) {
        inMemoryFilmStorage.removeConnection(filmId, userId);
    }

    @GetMapping("/popular")
    Collection<Film> getTopFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getTopFilms(count);
    }

}
