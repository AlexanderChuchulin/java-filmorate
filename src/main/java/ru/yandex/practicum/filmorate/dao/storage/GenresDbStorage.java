package ru.yandex.practicum.filmorate.dao.storage;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.postmanCrutches.FilmMpaGenresSerializer;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GenresDbStorage {

    //  Метод возвращает из БД жанр по его id, либо вcю таблицу если id не задан
    @JsonSerialize(using = FilmMpaGenresSerializer.class)
    public Map<Integer, String> getFilmGenres(int... genreId) {
        String name = "Жанр фильма";
        String excMsg;
        Map<Integer, String> filmGenresMap = new HashMap<>();

        EntityDbStorage.crudDbSimpleDataMap("genres", filmGenresMap,
                null, true, false);

        if (genreId.length == 0) {
            log.info("Возвращён список тип - " + name + ". Количество объектов " + filmGenresMap.size() + ".");
            return filmGenresMap;
        } else {
            if (InMemoryFilmStorage.genreNotFoundCheck(genreId[0])) {
                excMsg = "Жанр по id не найден, таблица доступных жанров: " + filmGenresMap + ". " +
                        name + " не возвращён. ";

                if (!excMsg.isBlank()) {
                    log.info("Ошибка поиска объектов. " + excMsg);
                    throw new EntityNotFoundException("Ошибка поиска объектов. " + excMsg);
                }
            }
            log.info(name + " с id " + genreId[0] + " возвращён. Имя " + filmGenresMap.get(genreId[0]) + ".");
            return Map.of(genreId[0], filmGenresMap.get(genreId[0]));
        }
    }
}