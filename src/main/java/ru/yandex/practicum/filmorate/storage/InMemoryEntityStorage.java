package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class InMemoryEntityStorage<T extends Entity, V extends Entity> implements EntityStorage<T> {
    private int startId;
    String entityName;
    String actionName;
    Map<String, String> entityMainPropMap = new HashMap<>();
    private final Map<Integer, T> sameKindEntityMap = new HashMap<>();
    private Map<Integer, V> otherKindEntityMap;

    public int getStartId() {
        return startId;
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Map<String, String> getEntityMainPropMap() {
        return entityMainPropMap;
    }

    public Map<Integer, T> getSameKindEntityMap() {
        return sameKindEntityMap;
    }

    public Map<Integer, V> getOtherKindEntityMap() {
        return otherKindEntityMap;
    }

    public void setOtherKindEntityMap(Map<Integer, V> otherKindEntityMap) {
        this.otherKindEntityMap = otherKindEntityMap;
    }

    @Override
    public T createEntity(T entity) {
        String conclusion = entityName + " не создан в памяти.";

        validateEntity(entity, false, conclusion);
        entity.setId(generateId());
        sameKindEntityMap.put(entity.getId(), entity);

        log.info("В памяти создан " + entityName + " с id " + entity.getId() + ". " + entity);
        return entity;
    }

    @Override
    public T updateEntity(T entity) {
        String conclusion = entityName + " не обновлён в памяти.";

        entityNotFoundCheck(conclusion, entity.getId(), false);
        validateEntity(entity, true, conclusion);
        sameKindEntityMap.put(entity.getId(), entity);
        log.info(entityName + " с id " + entity.getId() + " обновлён в памяти. " + entity);
        return entity;
    }

    @Override
    public void deleteEntityById(int entityId) {
        String conclusion = entityName + " не удалён из памяти.";
        Entity entity = sameKindEntityMap.get(entityId);

        entityNotFoundCheck(conclusion, entityId, false);

        if (entity.getClass() == User.class) {
            User user = (User) entity;
            entityMainPropMap.remove(user.getLogin());
        }

        if (entity.getClass() == Film.class) {
            Film film = (Film) entity;
            entityMainPropMap.remove(film.getFilmName() + ";" + film.getReleaseDate());
        }

        sameKindEntityMap.remove(entityId);
        log.info(entityName + " с id " + entityId + " удалён из памяти. ");
    }

    @Override
    public void deleteAllEntity() {
        sameKindEntityMap.clear();
        entityMainPropMap.clear();
        log.info("Все объекты тип - " + entityName + " удалены из памяти. ");
    }

    @Override
    public T getEntityById(int entityId) {
        String conclusion = entityName + " не возвращён из памяти.";

        entityNotFoundCheck(conclusion, entityId, false);
        log.info(entityName + " с id " + entityId + " возвращён из памяти. " + sameKindEntityMap.get(entityId));
        return sameKindEntityMap.get(entityId);
    }

    public ArrayList<T> getAllEntity() {
        log.info("Из памяти возвращён список тип - " + entityName + ". " +
                "Количество объектов " + sameKindEntityMap.size() + ".");
        return new ArrayList<>(sameKindEntityMap.values());
    }

    @Override
    public int generateId() {
        int id = startId;

        if (!sameKindEntityMap.isEmpty()) {
            for (Integer currentId : sameKindEntityMap.keySet()) {
                if (currentId > id) {
                    id = currentId;
                }
            }
        }

        id++;
        setStartId(id);
        return id;
    }

    // Метод проверяет по id существование фильмов или пользователей и при отсутствии выбрасывает исключение EntityNotFoundException
    @Override
    public void entityNotFoundCheck(String conclusion, int parentId, boolean isNotSameKindChild, int... childId) {
        String excMsg = "";
        Map<Integer, ? extends Entity> childCheckMap;

        if (isNotSameKindChild) {
            childCheckMap = getOtherKindEntityMap();
        } else {
            childCheckMap = getSameKindEntityMap();
        }

        if (!sameKindEntityMap.containsKey(parentId)) {
            excMsg = "Целевой объект " + entityName + " с id " + parentId + " в памяти не найден. ";
        }

        if (childId.length == 1) {
            if (!childCheckMap.containsKey(childId[0])) {
                excMsg += "Объект с id " + childId[0] + ", который инициировал действие связанное с " +
                        actionName + " в памяти не найден. ";
            }
        }

        if (excMsg.length() > 0) {
            log.warn("Ошибка поиска объектов. " + excMsg + conclusion);
            throw new EntityNotFoundException(excMsg + conclusion);
        }
    }

}
