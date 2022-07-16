package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    // Метод возвращает список фильмов заданного размера с наибольшим количестворм лайков
    public List<Film> getTopFilms(int count) {
        List<Film> topFilmsList;

        if (count == 0) {
            count = 10;
        }

        topFilmsList = inMemoryFilmStorage.getConnectionsMap().entrySet().stream()
                .sorted((e1, e2) -> (e1.getValue().size() - e2.getValue().size()) * -1).limit(count)
                .map(Map.Entry::getKey)
                .map(inMemoryFilmStorage.getEntityMap()::get)
                .collect(Collectors.toList());

        log.info("Возвращён топ " + count + " фильмов по количеству лайков. Всего фильмов с оценками " + inMemoryFilmStorage.getConnectionsMap().size() + ".");
        return topFilmsList;
        }
    }
