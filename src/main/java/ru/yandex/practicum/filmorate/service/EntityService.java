package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.storage.InMemoryEntityStorage;

import java.util.LinkedHashSet;
import java.util.Map;

@Service
@Slf4j
public abstract class EntityService<T extends Entity, V extends Entity> extends InMemoryEntityStorage<T, V> {
    Map<Integer, LinkedHashSet<Integer>> workingConnectionsMap;

    // Метод добавляет связь между объектами по id. Если связь двусторонняя (isTwoWayConnection) - делает отметку о связи у обоих объектов
    // если предполагается связь между разными видами сущностей (isNotSameKindEntity) - метод дополнительно работает и со второй таблицей связей
    public void addConnection(int parentId, int childId, boolean isTwoWayConnection, boolean isNotSameKindEntity) {
        String conclusion = actionName + " не добавлен.";

        entityNotFoundCheck(conclusion, parentId, isNotSameKindEntity, childId);

        if (isNotSameKindEntity) {
            workingConnectionsMap = getOtherKindEntityConnectionsMap();
        } else {
            workingConnectionsMap = getSameKindEntityConnectionsMap();
        }

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
    }

    // Метод добавляет связь между объектами по id. Если связь двусторонняя (isTwoWayConnection) - удаляет отметку о связи у обоих объектов
    // если предполагается связь между разными видами сущностей (isSameKindEntity) - метод дополнительно работает и со второй таблицей связей
    public void removeConnection(int parentId, int childId, boolean isTwoWayConnection, boolean isNotSameKindEntity) {
        String excMsg = "Связь между " + entityName + " с id " + parentId + " и объектом с id " + childId + ", который инициировал удаление " + actionName + " не найдена. ";
        String conclusion = actionName + " для " + entityName + " не удалён.";

        entityNotFoundCheck(conclusion, parentId, isNotSameKindEntity, childId);

        if (isNotSameKindEntity) {
            workingConnectionsMap = getOtherKindEntityConnectionsMap();
        } else {
            workingConnectionsMap = getSameKindEntityConnectionsMap();
        }

        if (!isNotSameKindEntity & isTwoWayConnection) {
            if (workingConnectionsMap.containsKey(childId) & workingConnectionsMap.get(childId).contains(parentId)) {
                workingConnectionsMap.get(childId).remove(parentId);
            }
        }
        if (!isNotSameKindEntity && workingConnectionsMap.containsKey(parentId) && workingConnectionsMap.get(parentId).contains(childId)) {
            workingConnectionsMap.get(parentId).remove(childId);
            log.info("Для объекта " + entityName + " id " + parentId + " удалён " + actionName + " с id " + childId
                    + ". Количество связанных объектов " + workingConnectionsMap.get(parentId).size() + ".");
        } else {
            log.info(excMsg);
        }
    }
}
