package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EntityService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController extends StorageController<Film, User> {
    private final FilmService filmService;

    @Autowired
    public FilmController(EntityService<Film, User> entityService, FilmService filmService) {
        super(entityService);
        this.filmService = filmService;
    }

    @PutMapping("/{filmId}/like/{userId}")
    void addFilmLikeByController(@PathVariable int filmId, @PathVariable int userId) {
        filmService.addConnection(filmId, userId, false, true);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    void removeFilmLikeByController(@PathVariable int filmId, @PathVariable int userId) {
        filmService.removeConnection(filmId, userId,false, true);
    }

    @GetMapping("/popular")
    Collection<Film> getTopFilmsByController(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getTopFilms(count);
    }

}
