package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage extends InMemoryEntityStorage<Film> {
    Set<String> allFilmsNameAndDate = new HashSet<>();
    private final Map<Integer, Film> filmsMap;
    private final Map<Integer, LinkedHashSet<Integer>> connectionsFilmMap;
    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public InMemoryFilmStorage(InMemoryUserStorage inMemoryUserStorage) {
        this.entityName = "Фильм";
        this.actionName = "Лайк";
        this.filmsMap = getEntityMap();
        this.connectionsFilmMap = getConnectionsMap();
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

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


    // перегруженный метод из-за невозможности проверить второй набор сущностей из супер-класса
    public void addConnection(int parentId, int childId) {
        String conclusion = actionName + " не добавлен.";

        entityNotFoundCheck(conclusion, parentId, childId);

        if (!connectionsFilmMap.containsKey(parentId)) {
            connectionsFilmMap.put(parentId, new LinkedHashSet<>());
        }
        connectionsFilmMap.get(parentId).add(childId);

        log.info("Для объекта " + entityName + " id " + parentId + " добавлен " + actionName + " с id " + childId
                + ". Количество связанных объектов " + connectionsFilmMap.get(parentId).size() + ".");
    }


    // перегруженный метод из-за невозможности проверить второй набор сущностей из супер-класса, пока не создан наследник
    public void removeConnection(int parentId, int childId) {
        String excMsg = "Связь между " + entityName + " с id " + parentId + " и объектом с id" + childId + " инициировавший действие " + actionName + " не найдена. ";
        String conclusion = actionName + " для " + entityName + " не удалён.";

        entityNotFoundCheck(conclusion, parentId, childId);

        if (connectionsFilmMap.containsKey(parentId)) {
            connectionsFilmMap.get(parentId).remove(childId);
            log.info("Для объекта " + entityName + " id " + parentId + " удалён " + actionName + " с id " + childId
                    + ". Количество связанных объектов " + connectionsFilmMap.get(parentId).size() + ".");

            // если у сущности пустой список связей, удалить сущность из таблицы связей
/*            if (connectionsFilmMap.get(parentId).isEmpty()) {
                connectionsFilmMap.remove(parentId);
            }*/
        } else {
            log.info(excMsg);
        }
    }


    // перегруженный метод из-за невозможности проверить второй набор сущностей из супер-класса, пока не создан наследник
    public void entityNotFoundCheck(String conclusion, int parentId, int childId) {
        String excMsg = "";

        if (!filmsMap.containsKey(parentId)) {
            excMsg = "Целевой объект " + entityName + " с id " + parentId + " не найден. ";
        }
        if (!inMemoryUserStorage.getEntityMap().containsKey(childId)) {
            excMsg += "Объект с id " + childId + " инициировавший действие " + actionName + " не найден. ";
        }
        if (excMsg.length() > 0) {
            log.warn("Ошибка поиска объектов. " + excMsg + conclusion);
            throw new EntityNotFoundException(excMsg + conclusion);
        }
    }

}
