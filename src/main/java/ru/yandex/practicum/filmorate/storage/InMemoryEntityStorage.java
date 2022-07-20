package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@Component
@Slf4j
public abstract class InMemoryEntityStorage<T extends Entity, V extends Entity> implements EntityStorage<T> {

    protected String entityName;
    protected String actionName;
    private final Map<Integer, T> sameKindEntityMap = new HashMap<>();
    protected Map<Integer, V> otherKindEntityMap;
    private Map<Integer, LinkedHashSet<Integer>> sameKindEntityConnectionsMap = new HashMap<>();
    private Map<Integer, LinkedHashSet<Integer>> otherKindEntityConnectionsMap = new HashMap<>();

    public Map<Integer, T> getSameKindEntityMap() {
        return sameKindEntityMap;
    }

    public Map<Integer, V> getOtherKindEntityMap() {
        return otherKindEntityMap;
    }

    public Map<Integer, LinkedHashSet<Integer>> getSameKindEntityConnectionsMap() {
        return sameKindEntityConnectionsMap;
    }

    public void setSameKindEntityConnectionsMap(Map<Integer, LinkedHashSet<Integer>> sameKindEntityConnectionsMap) {
        this.sameKindEntityConnectionsMap = sameKindEntityConnectionsMap;
    }

    public Map<Integer, LinkedHashSet<Integer>> getOtherKindEntityConnectionsMap() {
        return otherKindEntityConnectionsMap;
    }

    public void setOtherKindEntityConnectionsMap(Map<Integer, LinkedHashSet<Integer>> otherKindEntityConnectionsMap) {
        this.otherKindEntityConnectionsMap = otherKindEntityConnectionsMap;
    }

    @Override
    public T createEntity(T entity) {
        String conclusion = entityName + " не создан.";

        validateEntity(entity, false, conclusion);
        entity.setId(generateId());
        sameKindEntityMap.put(entity.getId(), entity);
        log.info("Создан " + entityName + " с id " + entity.getId() + ". " + entity);
        return entity;
    }

    @Override
    public T updateEntity(T entity) {
        String conclusion = entityName + " не обновлён.";

        entityNotFoundCheck(conclusion, entity.getId(), false);
        validateEntity(entity, true, conclusion);
        sameKindEntityMap.put(entity.getId(), entity);
        log.info(entityName + " с id " + entity.getId() + " обновлён. " + entity);
        return entity;
    }

    @Override
    public void deleteEntityById(int entityId) {
        String conclusion = entityName + " не удалён.";

        entityNotFoundCheck(conclusion, entityId, false);
        sameKindEntityMap.remove(entityId);
        log.info(entityName + " с id " + entityId + " удалён. " + sameKindEntityMap.get(entityId));
    }

    @Override
    public void deleteAllEntity() {
        sameKindEntityMap.clear();
    }

    @Override
    public T getEntityById(int entityId) {
        String conclusion = entityName + " не возвращён.";

        entityNotFoundCheck(conclusion, entityId, false);
        log.info(entityName + " с id " + entityId + " возвращён. " + sameKindEntityMap.get(entityId));
        return sameKindEntityMap.get(entityId);
    }

    public ArrayList<T> getAllEntity() {
        log.info("Возвращён список тип - " + entityName + ". Количество объектов " + sameKindEntityMap.size() + ".");
        return new ArrayList<>(sameKindEntityMap.values());
    }

    @Override
    public int generateId() {
        int id = 0;

        if (!sameKindEntityMap.isEmpty()) {
            for (Integer currentId : sameKindEntityMap.keySet()) {
                if (currentId > id) {
                    id = currentId;
                }
            }
        }
        id++;
        return id;
    }

    // Метод проверяет по id существование сущностей и при отсуствии выбрасывает исключение EntityNotFoundException
    @Override
    public void entityNotFoundCheck(String conclusion, int parentId, boolean isNotSameKindChild, int... childId) {
        String excMsg = "";
        Map<Integer, ? extends Entity> childCheckMap;

        if (isNotSameKindChild) {
            //childCheckMap = getOtherKindEntityMap();//Ни как не могу связать поле childCheckMap с sameKindEntityMap в другом классе наследнике
            childCheckMap = getSameKindEntityMap();
        } else {
            childCheckMap = getSameKindEntityMap();
        }
        if (!sameKindEntityMap.containsKey(parentId)) {
            excMsg = "Целевой объект " + entityName + " с id " + parentId + " не найден. ";
        }
        if (childId.length == 1) {
            if (!childCheckMap.containsKey(childId[0])) {
                excMsg += "Объект с id " + childId[0] + ", который инициировал действие " + actionName + " не найден. ";
            }
        }
        if (excMsg.length() > 0) {
            log.warn("Ошибка поиска объектов. " + excMsg + conclusion);
            throw new EntityNotFoundException(excMsg + conclusion);
        }
    }
}
