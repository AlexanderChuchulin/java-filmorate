package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.service.FilmDbService;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController extends StorageController<Film, User> {
    private final FilmDbService filmDbService;

    @Autowired
    public FilmController(FilmDbService filmDbService) {
        this.filmDbService = filmDbService;
        dbService = filmDbService;
    }

    @PutMapping("/{filmId}/like/{userId}")
    void addFilmLikeController(@PathVariable int filmId, @PathVariable int userId) {
        filmDbService.addConnectionDb(filmId, userId, false, true);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    void removeFilmLikeController(@PathVariable int filmId, @PathVariable int userId) {
        filmDbService.removeConnectionDb(filmId, userId,false, true);
    }

    @GetMapping("/popular")
    Collection<Film> getTopFilmsController(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmDbService.getTopFilmsDb(count);
    }
}
