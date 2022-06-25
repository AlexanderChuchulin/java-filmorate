package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.other.IntIdGenerator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class EntityController<T extends Entity> {
    private final Map<Integer, T> entityMap = new HashMap<>();
    private final IntIdGenerator<T> intIdGenerator = new IntIdGenerator<>();
    String entityName;

    public EntityController() {
        this.entityName = "";
    }

    public Map<Integer, T> getEntityMap() {
        return entityMap;
    }

    @GetMapping()
    public ArrayList<T> getAllEntity() {
        log.info("Возвращён список тип - " + entityName + ". Количество объектов " + entityMap.size() + ".");
        return new ArrayList<>(entityMap.values());
    }

    @PostMapping()
    public ResponseEntity<T> createEntity(@Valid @RequestBody T entity) {
        String conclusion = entityName + " не создан.";

        validateEntity(entity, false, conclusion);
        entity.setId(intIdGenerator.generateId(entityMap));
        entityMap.put(entity.getId(), entity);
        log.info("Создан " + entityName + " с id " + entity.getId() + ". " + entity);
        return new ResponseEntity<>(entity, HttpStatus.CREATED);
    }

    @PutMapping()
    public T updateEntity(@Valid @RequestBody T entity) {
        String conclusion = entityName + " не обновлён.";

        if (!entityMap.containsKey(entity.getId())) {
            throw new ValidationException(entityName + " с id " + entity.getId() + " не найден. " + conclusion);
        }
        validateEntity(entity, true, conclusion);
        entityMap.put(entity.getId(), entity);
        log.info(entityName + " с id " + entity.getId() + " обновлён. " + entity);
        return entity;
    }

    public abstract void validateEntity(T entity, Boolean isUpdate, String conclusion);

}
