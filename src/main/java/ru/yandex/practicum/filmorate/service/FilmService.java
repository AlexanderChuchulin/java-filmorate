package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FilmService extends EntityService {
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        super(inMemoryFilmStorage);
        this.entityName = "Фильм";
        this.actionName = "Лайк";
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.workingConnectionsMap = inMemoryFilmStorage.getOtherKindEntityConnectionsMap();
    }

    // Метод возвращает список фильмов заданного размера с наибольшим количестворм лайков
    public List<Film> getTopFilms(int count) {
        List<Film> topFilmsList;
        int filmsWithoutLikesCount = 0;

        if (count == 0) {
            count = 10;
        }

        // дополнительно добавить в топ фильмов по лайкам все фильмы без лайков, потому что наставник, который писал тесты считает что это логично
        for (Integer filmId : inMemoryFilmStorage.getSameKindEntityMap().keySet()) {
            if (!workingConnectionsMap.containsKey(filmId)) {
                workingConnectionsMap.put(filmId, new LinkedHashSet<>());
            }
            if (workingConnectionsMap.get(filmId).isEmpty()) {
                filmsWithoutLikesCount++;
            }
        }

        topFilmsList = workingConnectionsMap.entrySet().stream()
                .sorted((e1, e2) -> (e1.getValue().size() - e2.getValue().size()) * -1).limit(count)
                .map(Map.Entry::getKey)
                .map(inMemoryFilmStorage.getSameKindEntityMap()::get)
                .collect(Collectors.toList());

        log.info("Возвращён топ " + count + " фильмов по количеству лайков. Размер списка: " + topFilmsList.size()
                + ". Из них фильмов с оценками: " + (topFilmsList.size() - filmsWithoutLikesCount) + ".");

        return topFilmsList;
    }
}
