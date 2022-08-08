package ru.yandex.practicum.filmorate.dao.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.EntityDbStorage;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.service.EntityService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public abstract class EntityDbService<T extends Entity, V extends Entity> {
    String connectionTableNameDb;
    EntityService<T, V> inMemoryService;
    EntityDbStorage<T, V> dbStorage;


    public Entity createEntityDb(T entity) {
        return dbStorage.createEntity(entity);
    }


    public Entity updateEntityDb(T entity) {
        return dbStorage.updateEntity(entity);
    }


    public void deleteEntityByIdDb(int entityId) {
        dbStorage.deleteEntityById(entityId);
    }


    public void deleteAllEntityDb() {
        dbStorage.deleteAllEntity();
    }


    public Entity getEntityByIdDb(int entityId) {
        return dbStorage.getEntityById(entityId);
    }


    public ArrayList<T> getAllEntityDb() {
        return dbStorage.getAllEntity();
    }


    // Метод добавляет связь в соответствующую таблицу в БД
    public void addConnectionDb(int parentId, int childId, boolean isTwoWayConnection, boolean isNotSameKindEntity) {
        if (!prepareConnectionData(parentId, childId, isNotSameKindEntity)) {
            return;
        }
        Map<Integer, Integer> connectionDataMap = new HashMap<>();

        inMemoryService.addConnection(parentId, childId, isTwoWayConnection, isNotSameKindEntity);

        for (Integer dataId : inMemoryService.getWorkingConnectionsMap().get(parentId)) {
            connectionDataMap.put(parentId, dataId);
        }
        EntityDbStorage.crudDbSimpleDataMap(connectionTableNameDb, connectionDataMap, null, false, false, parentId);
    }


    // Метод удаляет связь из соответствующей таблицы в БД
    public void removeConnectionDb(int parentId, int childId, boolean isTwoWayConnection, boolean isNotSameKindEntity) {
        if (!prepareConnectionData(parentId, childId, isNotSameKindEntity)) {
            return;
        }
        inMemoryService.removeConnection(parentId, childId, isTwoWayConnection, isNotSameKindEntity);
        EntityDbStorage.crudDbSimpleDataMap(connectionTableNameDb, null, null, false, true, parentId, childId);
    }


    // Метод загружает из БД в память необходимые объекты для их анализа перед действиями со связями
    private boolean prepareConnectionData(int parentId, int childId, boolean isNotSameKindEntity) {
        if (!isNotSameKindEntity) {
            if (parentId == childId) {
                log.info("Попытка создания или удаления связи в БД между объектами одного вида с одинаковым id " + parentId + ". Обработка прервана.");
                return false;
            }
            dbStorage.loadEntityFromDb(dbStorage.getSameKindDbTableName(), dbStorage.getInMemoryStorage().getSameKindEntityMap(), childId);
        } else {
            dbStorage.loadEntityFromDb(dbStorage.getOtherKindDbTableName(), dbStorage.getInMemoryStorage().getOtherKindEntityMap(), childId);
        }
        dbStorage.loadEntityFromDb(dbStorage.getSameKindDbTableName(), dbStorage.getInMemoryStorage().getSameKindEntityMap(), parentId);
        return true;
    }

}

