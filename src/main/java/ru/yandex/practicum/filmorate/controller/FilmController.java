package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.other.Validator;
import ru.yandex.practicum.filmorate.other.IntIdGenerator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> filmsMap = new HashMap<>();
    IntIdGenerator<Film> intIdGenerator = new IntIdGenerator<>();

    @GetMapping()
    public ArrayList<Film> getAllFilms() {
        return new ArrayList<>(filmsMap.values());
    }

    @PostMapping()
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        Validator.validateFilm(film, " Фильм не создан.");
        film.setId(intIdGenerator.generateId(filmsMap));
        filmsMap.put(film.getId(), film);
        log.info("Создан фильм с id. " + film.getId() + ". " + film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!filmsMap.containsKey(film.getId())) {
            throw new ValidationException("Фильм с id " + film.getId() + " не найден. Фильм не обновлён.");
        }
        Validator.validateFilm(film,  " Фильм не обновлён.");
        filmsMap.put(film.getId(), film);
        log.info("Фильм с id " + film.getId() + " обновлён. " + film);
        return film;
    }

}
