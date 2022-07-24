package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.storage.InMemoryEntityStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@Service
@Slf4j
public abstract class EntityService<T extends Entity, V extends Entity> {
    String entityName;
    String actionName;
    private final Map<Integer, LinkedHashSet<Integer>> sameKindEntityConnectionsMap = new HashMap<>();
    private final Map<Integer, LinkedHashSet<Integer>> otherKindEntityConnectionsMap = new HashMap<>();
    Map<Integer, LinkedHashSet<Integer>> workingConnectionsMap = new HashMap<>();
    InMemoryEntityStorage<T, V> inMemoryStorage;

    @Autowired
    public EntityService(InMemoryUserStorage inMemoryUserStorage, InMemoryFilmStorage inMemoryFilmStorage) {
        inMemoryUserStorage.setOtherKindEntityMap(inMemoryFilmStorage.getSameKindEntityMap());
        inMemoryFilmStorage.setOtherKindEntityMap(inMemoryUserStorage.getSameKindEntityMap());
    }

    public Map<Integer, LinkedHashSet<Integer>> getWorkingConnectionsMap() {
        return workingConnectionsMap;
    }

    public T createEntity(T entity) {
        return inMemoryStorage.createEntity(entity);
    }

    public T updateEntity(T entity) {
        return inMemoryStorage.updateEntity(entity);
    }

    public void deleteEntityById(int entityId) {
        inMemoryStorage.deleteEntityById(entityId);
    }

    public void deleteAllEntity() {
        inMemoryStorage.deleteAllEntity();
    }

    public Entity getEntityById(int entityId) {
        return inMemoryStorage.getEntityById(entityId);
    }

    public ArrayList<T> getAllEntity() {
        return inMemoryStorage.getAllEntity();
    }

/*    Метод добавляет связь между объектами по id. Если связь двусторонняя (isTwoWayConnection) -
    делает отметку о связи у обоих объектов если предполагается связь между разными видами сущностей (isNotSameKindEntity)
    метод дополнительно работает и со второй таблицей связей*/
    public void addConnection(int parentId, int childId, boolean isTwoWayConnection, boolean isNotSameKindEntity) {
        String conclusion = actionName + " не добавлен.";

        if (!isNotSameKindEntity) {
            workingConnectionsMap = sameKindEntityConnectionsMap;

            if (parentId == childId) {
                log.info("Попытка создать в памяти связь между объектами одного вида с одинаковым id " +
                        parentId + ". " + conclusion);
                return;
            }
        } else {
            workingConnectionsMap = otherKindEntityConnectionsMap;
        }

        entityNotFoundCheck(conclusion, parentId, isNotSameKindEntity, childId);

        if (!workingConnectionsMap.containsKey(parentId)) {
            workingConnectionsMap.put(parentId, new LinkedHashSet<>());
        }
        workingConnectionsMap.get(parentId).add(childId);

        if (!isNotSameKindEntity & isTwoWayConnection) {
            if (!workingConnectionsMap.containsKey(childId)) {
                workingConnectionsMap.put(childId, new LinkedHashSet<>());
            }
            workingConnectionsMap.get(childId).add(parentId);
        }

        log.info("Для объекта " + entityName + " с id " + parentId + " в память добавлен " +
                actionName + " с id " + childId
                + ". Количество связанных объектов для id " + parentId + " в памяти " +
                workingConnectionsMap.get(parentId).size() + ".");
    }

/*   Метод удаляет связь между объектами по id. Если связь двусторонняя (isTwoWayConnection) -
    удаляет отметку о связи у обоих объектов если предполагается связь между разными видами сущностей (isSameKindEntity)
    метод дополнительно работает и со второй таблицей связей*/
    public void removeConnection(int parentId, int childId, boolean isTwoWayConnection, boolean isNotSameKindEntity) {
        String excMsg = "Связь между " + entityName + " с id " + parentId + " и объектом с id " + childId +
                ", который инициировал удаление " + actionName + " в памяти не найдена. ";
        String conclusion = actionName + " для " + entityName + " из памяти не удалён.";

        if (!isNotSameKindEntity) {
            workingConnectionsMap = sameKindEntityConnectionsMap;

            if (parentId == childId) {
                log.info("Попытка удалить из памяти связь между объектами одного вида с одинаковым id " +
                        parentId + ". " + conclusion);
                return;
            }
        } else {
            workingConnectionsMap = otherKindEntityConnectionsMap;
        }

        entityNotFoundCheck(conclusion, parentId, isNotSameKindEntity, childId);

        if (!isNotSameKindEntity & isTwoWayConnection) {
            if (workingConnectionsMap.containsKey(childId) & workingConnectionsMap.get(childId).contains(parentId)) {
                workingConnectionsMap.get(childId).remove(parentId);
            }
        }

        if (!isNotSameKindEntity && workingConnectionsMap.containsKey(parentId) &&
                workingConnectionsMap.get(parentId).contains(childId)) {
            workingConnectionsMap.get(parentId).remove(childId);
            log.info("Для объекта " + entityName + " id " + parentId + " из памяти удалён " +
                    actionName + " с id " + childId
                    + ". Количество связанных объектов для id " + parentId + " в памяти " +
                    workingConnectionsMap.get(parentId).size() + ".");
        } else {
            log.info(excMsg);
        }
    }

    public abstract void entityNotFoundCheck(String conclusion, int parentId, boolean isNotSameKindChild, int... childId);

}
