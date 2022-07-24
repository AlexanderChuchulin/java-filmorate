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
public class MpaDbStorage {

    //  Метод возвращает из БД рейтинг MPA по его id, либо вcю таблицу если id не задан
    @JsonSerialize(using = FilmMpaGenresSerializer.class)
    public Map<Integer, String> getFilmMpaDb(int... mpaId) {
        String name = "MPA рейтинг";
        String excMsg;
        Map<Integer, String> mpaRatingMap = new HashMap<>();

        EntityDbStorage.crudDbSimpleDataMap("mpa_rating", mpaRatingMap,
                null, true, false);

        if (mpaId.length == 0) {
            log.info("Возвращён список тип - " + name + ". Количество объектов " + mpaRatingMap.size() + ".");
            return mpaRatingMap;
        } else {
            if (InMemoryFilmStorage.mpaNotFoundCheck(mpaId[0])) {
                excMsg = "Рейтинг MPA по id не найден, таблица доступных видов рейтинга MPA: " +
                        mpaRatingMap + ". " + name + " не возвращён. ";

                if (!excMsg.isBlank()) {
                    log.info("Ошибка поиска объектов. " + excMsg);
                    throw new EntityNotFoundException("Ошибка поиска объектов. " + excMsg);
                }
            }
            log.info(name + " с id " + mpaId[0] + " возвращён. Имя " + mpaRatingMap.get(mpaId[0]) + ".");
            return Map.of(mpaId[0], mpaRatingMap.get(mpaId[0]));
        }
    }

}
