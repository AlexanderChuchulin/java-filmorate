package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    // Метод возвращает список фильмов заданного размера с наибольшим количестворм лайков
    public Collection<Film> getTopFilms(int count) {
        if (count == 0) {
            count = 10;
        }

        int i = count;
        List<Film> filmsLikeList = new ArrayList<>();

        Comparator comparator = new Comparator<Map.Entry<Film, Integer>>() {
            @Override
            public int compare(Map.Entry<Film, Integer> e1, Map.Entry<Film, Integer> e2) {
                return e1.getValue().compareTo(e2.getValue());
            }
        };

        Map<Film, Integer> filmsLikeMap = new TreeMap<Film, Integer>(comparator);

        for (Integer filmId : inMemoryFilmStorage.getEntityMap().keySet()) {
            filmsLikeMap.put(inMemoryFilmStorage.getEntityMap().get(filmId), inMemoryFilmStorage.getConnectionsMap().get(filmId).size());
        }

        for (Film film : filmsLikeMap.keySet()) {
            if (i == 0) {
                break;
            }
            filmsLikeList.add(film);
            i--;
        }
        log.info("Возвращён топ " + count + " фильмов по количеству лайков. Всего фильмов с оценками " + filmsLikeMap.size() + ".");
        return filmsLikeList;
        }
    }
