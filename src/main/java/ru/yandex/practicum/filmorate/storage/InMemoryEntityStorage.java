package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@Slf4j
public abstract class InMemoryEntityStorage<T extends Entity> implements EntityStorage<T> {

    String entityName;
    String actionName;
    private final Map<Integer, T> entityMap = new HashMap<>();
    private final Map<Integer, LinkedHashSet<Integer>> connectionsMap = new HashMap<>();

    public Map<Integer, T> getEntityMap() {
        return entityMap;
    }

    public Map<Integer, LinkedHashSet<Integer>> getConnectionsMap() {
        return connectionsMap;
    }

    @Override
    public T createEntity(T entity) {
        String conclusion = entityName + " не создан.";

        validateEntity(entity, false, conclusion);
        entity.setId(generateId());
        entityMap.put(entity.getId(), entity);
        log.info("Создан " + entityName + " с id " + entity.getId() + ". " + entity);
        return entity;
    }

    @Override
    public T updateEntity(T entity) {
        String conclusion = entityName + " не обновлён.";

        entityNotFoundCheck(conclusion, entity.getId());
        validateEntity(entity, true, conclusion);
        entityMap.put(entity.getId(), entity);
        log.info(entityName + " с id " + entity.getId() + " обновлён. " + entity);
        return entity;
    }

    @Override
    public void deleteEntityById(int entityId) {
        String conclusion = entityName + " не удалён.";

        entityNotFoundCheck(conclusion, entityId);
        entityMap.remove(entityId);
        log.info(entityName + " с id " + entityId + " удалён. " + entityMap.get(entityId));
    }

    @Override
    public void deleteAllEntity() {
        entityMap.clear();
    }

    @Override
    public T getEntityById(int entityId) {
        String conclusion = entityName + " не возвращён.";

        entityNotFoundCheck(conclusion, entityId);
        log.info(entityName + " с id " + entityId + " возвращён. " + entityMap.get(entityId));
        return entityMap.get(entityId);
    }

    public ArrayList<T> getAllEntity() {
        log.info("Возвращён список тип - " + entityName + ". Количество объектов " + entityMap.size() + ".");
        return new ArrayList<>(entityMap.values());
    }

    @Override
    public void addConnection(int parentId, int childId) {
        String conclusion = actionName + " не добавлен.";

        entityNotFoundCheck(conclusion, parentId, childId);
        if (!connectionsMap.containsKey(parentId)) {
            connectionsMap.put(parentId, new LinkedHashSet<>());
        }
        if (!connectionsMap.containsKey(childId)) {
            connectionsMap.put(childId, new LinkedHashSet<>());
        }

        connectionsMap.get(parentId).add(childId);
        connectionsMap.get(childId).add(parentId);
        log.info("Для - " + entityName + " id " + parentId + " добавлен " + actionName + " с id " + childId
                + ". Общее количество связанных объектов " + connectionsMap.get(parentId).size() + ".");
    }

    @Override
    public void removeConnection(int parentId, int childId) {
        String excMsg = "Связь между " + entityName + " с id " + parentId + " и " + actionName + " с id " + childId + " не найдена. ";
        String conclusion = actionName + " для " + entityName + " не удалён.";


        entityNotFoundCheck(conclusion, parentId, childId);

        if (connectionsMap.containsKey(parentId) && connectionsMap.get(parentId).contains(childId)) {
            connectionsMap.get(parentId).remove(childId);
            connectionsMap.get(childId).remove(parentId);
            log.info("Для - " + entityName + " id " + parentId + " удалён " + actionName + " с id " + childId
                    + ". Общее количество связанных объектов " + connectionsMap.get(parentId).size() + ".");
        } else {
            log.info(excMsg);
        }
    }

    @Override
    public int generateId() {
        int Id = 0;

        if (!entityMap.isEmpty()) {
            for (Integer currentId : entityMap.keySet()) {
                if (currentId > Id) {
                    Id = currentId;
                }
            }
        }
        Id++;
        return Id;
    }

    // Метод проверяет по id существование сущностей и при отсуствии выбрасывает исключение EntityNotFoundException
    @Override
    public void entityNotFoundCheck(String conclusion, int parentId, int... childId) {
        String excMsg = "";

        if (!entityMap.containsKey(parentId)) {
            excMsg = "Исходный " + entityName + " с id " + parentId + " не найден. ";
        }
        if (childId.length == 1 && !entityMap.containsKey(childId[0])) {
            excMsg += "Добавляемый " + entityName + " с id " + childId[0] + " не найден. ";
        }
        if (excMsg.length() > 0) {
            log.warn("Ошибка поиска объекта " + entityName + ". " + excMsg + conclusion);
            throw new EntityNotFoundException(excMsg + conclusion);
        }

    }

}
