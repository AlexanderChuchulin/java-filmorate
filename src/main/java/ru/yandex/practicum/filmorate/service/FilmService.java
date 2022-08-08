package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FilmService extends EntityService<Film, User> {
    private final InMemoryFilmStorage inMemoryFilmStorage;

    public FilmService(InMemoryUserStorage inMemoryUserStorage, InMemoryFilmStorage inMemoryFilmStorage) {
        super(inMemoryUserStorage, inMemoryFilmStorage);
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        inMemoryStorage = inMemoryFilmStorage;
        entityName = inMemoryFilmStorage.getEntityName();
        actionName = inMemoryFilmStorage.getActionName();
    }


    public Map<Integer, Film> getSameKindEntityMap() {
        return inMemoryFilmStorage.getSameKindEntityMap();
    }


    @Override
    public void entityNotFoundCheck(String conclusion, int parentId, boolean isNotSameKindChild, int... childId) {
        inMemoryFilmStorage.entityNotFoundCheck(conclusion, parentId, isNotSameKindChild, childId);
    }


    public Map<Integer, String> getFilmMpaGenres(boolean isMpa, int... propId) {
        return inMemoryFilmStorage.getFilmMpaGenres(isMpa, propId);
    }

    // Метод возвращает список фильмов заданного размера с наибольшим количеством лайков
    public List<Film> getTopFilms(int limit) {
        List<Film> topFilmsList;
        int filmsWithoutLikesCount = 0;

        if (limit == 0) {
            limit = 10;
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
                .sorted((e1, e2) -> (e1.getValue().size() - e2.getValue().size()) * -1).limit(limit)
                .map(Map.Entry::getKey)
                .map(inMemoryFilmStorage.getSameKindEntityMap()::get)
                .collect(Collectors.toList());

        log.info("Возвращён топ " + limit + " фильмов по количеству лайков. Размер списка: " + topFilmsList.size()
                + ". Из них фильмов с оценками: " + (topFilmsList.size() - filmsWithoutLikesCount) + ".");

        return topFilmsList;
    }

}
