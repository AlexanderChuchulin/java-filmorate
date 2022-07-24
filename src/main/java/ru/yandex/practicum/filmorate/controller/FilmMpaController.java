package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.storage.MpaDbStorage;

import java.util.Map;

@RestController
@RequestMapping("mpa")
public class FilmMpaController {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmMpaController(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    @GetMapping
    private Map<Integer, String> getAllMpaController() {
        return mpaDbStorage.getFilmMpaDb();
    }

    @GetMapping("/{mpaId}")
    private Map<Integer, String> getMpaByIdController(@PathVariable int mpaId) {
        return mpaDbStorage.getFilmMpaDb(mpaId);
    }

}
