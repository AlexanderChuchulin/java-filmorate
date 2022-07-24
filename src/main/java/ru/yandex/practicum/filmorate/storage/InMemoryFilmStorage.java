package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FilmGenres;
import ru.yandex.practicum.filmorate.enums.MpaRating;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage extends InMemoryEntityStorage<Film, User> {
    private final Set<String> allFilmsNameAndDate = new HashSet<>();

    @Override
    public void validateEntity(Film film, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (film.getName() == null || film.getName().isBlank() || film.getName().length() > 200) {
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
        if (film.getMpaRatingId() <= 0 || film.getMpaRatingId() > MpaRating.values().length - 1) {
            excMsg += "Рейтинг MPA должен быть задан по id: " + MpaRating.getMpaRatingDescription() + ". ";
        }
        // если жанр задан проверить id на соответствие перечислению жанров
        if (film.getGenreIdSet() != null) {
            boolean isThereGenreError = false;

            if(film.getGenreIdSet().size() <= FilmGenres.values().length - 1) {
                for (Integer genreId : film.getGenreIdSet()) {
                    if (genreId <= 0 || genreId > FilmGenres.values().length - 1) {
                        isThereGenreError = true;
                        break;
                    }
                }
            } else {
                isThereGenreError = true;
            }
            if (isThereGenreError) {
                excMsg += "Жанр должен быть задан по id: " + FilmGenres.getFilmGenresDescription() + ". ";
            }

        }


        // если фильм с такой же датой существует и не происходит обновление, выбросить исключение
        // если же происходит обновление названия или года выпуска, то нужно удалить старую запись из списка
        if (allFilmsNameAndDate.contains(film.getName() + ";" + film.getReleaseDate()) & !isUpdate) {
            excMsg += "Фильм с названием — " + film.getName() + " и с датой выпуска " + film.getReleaseDate() + " уже есть в базе. ";
        } else if (allFilmsNameAndDate.contains(film.getName() + ";" + film.getReleaseDate()) & isUpdate) {
            allFilmsNameAndDate.remove(film.getName() + ";" + film.getReleaseDate());
        }
        if (excMsg.length() > 0) {
            log.warn("Ошибка валидации фильма. " + excMsg + conclusion);
            throw new ValidationException(excMsg + conclusion);
        }
        allFilmsNameAndDate.add(film.getName() + ";" + film.getReleaseDate());
    }
}
