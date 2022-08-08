package ru.yandex.practicum.filmorate.storage;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.postmanCrutches.FilmMpaGenresSerializer;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage extends InMemoryEntityStorage<Film, User> {

    private static final Map<Integer, String> mpaRatingMap = new HashMap<>();
    private static final Map<Integer, String> filmGenresMap = new HashMap<>();

    public InMemoryFilmStorage() {
        entityName = "Фильм";
        actionName = "Лайк";
    }


    public static Map<Integer, String> getMpaRatingMap() {
        return mpaRatingMap;
    }

    public static Map<Integer, String> getFilmGenresMap() {
        return filmGenresMap;
    }

    @Override
    public void validateEntity(Film film, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (film.getFilmName() == null || film.getFilmName().isBlank() || film.getFilmName().length() > 200) {
            excMsg += "Название фильма должно быть задано и быть не более 200 символов. ";
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            excMsg += "Описание должно быть задано и быть не более 200 символов. ";
        }
        if (film.getReleaseDate() == null || !film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 27))) {
            excMsg += "Дата релиза должна быть задана после 27 декабря 1895 года. ";
        }
        if (film.getDuration() <= 0 || film.getDuration() > 1000000000) {
            excMsg += "Продолжительность фильма должна быть задана больше 0 и меньше 1000000000. ";
        }
        if (genreNotFoundCheck(0, film.getGenreIdSet())) {
            excMsg += "Жанр по id не найден, таблица доступных жанров: " + filmGenresMap + ". ";
        }
        if (mpaNotFoundCheck(film.getMpaRatingId())) {
            excMsg += "Рейтинг MPA по id не найден, таблица доступных видов рейтинга MPA: " + mpaRatingMap + ". ";
        }
        // если фильм с такой же датой существует и не происходит обновление, выбросить исключение
        // если же происходит обновление названия или года выпуска, то нужно удалить старую запись из списка
        if (entityMainPropMap.containsKey(film.getFilmName() + ";" + film.getReleaseDate()) & !isUpdate) {
            excMsg += "Фильм с названием — " + film.getFilmName() + " и с датой выпуска " + film.getReleaseDate() + " уже существует. ";
        } else if (entityMainPropMap.containsKey(film.getFilmName() + ";" + film.getReleaseDate()) & isUpdate) {
            entityMainPropMap.remove(film.getFilmName() + ";" + film.getReleaseDate());
        }
        if (excMsg.length() > 0) {
            log.warn("Ошибка валидации фильма. " + excMsg + conclusion);
            throw new ValidationException(excMsg + conclusion);
        }
        entityMainPropMap.put(film.getFilmName() + ";" + film.getReleaseDate(), film.getFilmName() + ";" + film.getReleaseDate());
    }

    //  Метод возвращает рейтинг MPA или жанр по его id, либо вcю таблицу если id не задан
    @JsonSerialize(using = FilmMpaGenresSerializer.class)
    public Map<Integer, String> getFilmMpaGenres(boolean isMpa, int... propId) {
        String name;
        String excMsg = "";
        Map<Integer, String> workingMap;

        if (isMpa) {
            name = "MPA рейтинг";
            workingMap = mpaRatingMap;
        } else {
            name = "Жанр фильма";
            workingMap = filmGenresMap;
        }
        if (propId.length == 0) {
            log.info("Возвращён список тип - " + name + ". Количество объектов " + workingMap.size() + ".");
            return workingMap;
        } else {
            if (isMpa && mpaNotFoundCheck(propId[0])) {
                excMsg = "Рейтинг MPA по id не найден, таблица доступных видов рейтинга MPA: " + mpaRatingMap +  ". " + name + " не возвращён. ";
            } else if (!isMpa && genreNotFoundCheck(propId[0])) {
                excMsg = "Жанр по id не найден, таблица доступных жанров: " + filmGenresMap +  ". " + name + " не возвращён. ";
            }
            if (!excMsg.isBlank()) {
                log.info("Ошибка поиска объектов. " + excMsg);
                throw new EntityNotFoundException("Ошибка поиска объектов. " + excMsg);
            }
        }
        log.info(name + " с id " + propId[0] + " возвращён. Имя " + workingMap.get(propId[0]) + ".");
        return Map.of(propId[0], workingMap.get(propId[0]));
    }

    //  Метод проверяет существование жанров по одному id или по коллекции id и при отсутствии выбрасывает исключение EntityNotFoundException
    @SafeVarargs
    public final boolean genreNotFoundCheck(int genreId, TreeSet<Integer>... genreIdSet) {
        String excMsg = "Ошибка поиска объектов. Жанр по id не найден, таблица доступных жанров: " + filmGenresMap + ". ";
        boolean isGenreError = false;

        if (genreIdSet.length == 1) {
            if (genreIdSet[0] == null || genreIdSet[0].isEmpty()) {
                return false;
            } else if (genreIdSet[0].size() <= filmGenresMap.size()) {
                for (Integer gId : genreIdSet[0]) {
                    if (gId <= 0 || gId > filmGenresMap.size()) {
                        isGenreError = true;
                        break;
                    }
                }
            } else {
                isGenreError = true;
            }
        }
        if (genreIdSet.length != 1) {
            if (genreId <= 0 || genreId > filmGenresMap.size()) {
                isGenreError = true;
            }
        }

        if (isGenreError) {
            log.warn(excMsg);
        }

        return isGenreError;
    }

    //  Метод проверяет существование MPA рейтингов по id и при отсутствии выбрасывает исключение EntityNotFoundException
    public boolean mpaNotFoundCheck(Integer mpaId) {
        String excMsg = "Ошибка поиска объектов. MPA рейтинг по id не найден, таблица доступных вариантов MPA рейтинга: " + mpaRatingMap + ". ";
        boolean isMpaError = false;

        if (mpaId != null && (mpaId <= 0 || mpaId > mpaRatingMap.size())) {
            isMpaError = true;
            log.warn(excMsg);
        }

        return isMpaError;
    }

}
