package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class InMemoryFilmStorage extends InMemoryEntityStorage<Film> {

    public InMemoryFilmStorage() {
        this.entityName = "Фильм";
        this.actionName = "Лайк";
    }

    Set<String> allFilmsNameAndDate = new HashSet<>();

    @Override
    public void validateEntity(Film film, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (film.getName().isEmpty() || film.getName().isBlank()) {
            excMsg += "Название фильма не может быть пустым. ";
        }
        if (film.getDescription().length() > 200) {
            excMsg += "Максимальная длина описания — 200 символов. ";
        }
        if (!film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 27))) {
            excMsg += "Дата релиза — " + film.getReleaseDate() + " не должна быть раньше 28 декабря 1895 года. ";
        }
        if (film.getDuration() <= 0) {
            excMsg += "Продолжительность фильма должна быть больше 0. ";
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
