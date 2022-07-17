package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.storage.InMemoryEntityStorage;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
public abstract class StorageController<T extends Entity, V extends Entity> {
    InMemoryEntityStorage<T, V> inMemoryEntityStorage;

    @Autowired
    public StorageController(InMemoryEntityStorage<T, V> inMemoryEntityStorage) {
        this.inMemoryEntityStorage = inMemoryEntityStorage;
    }

    @PostMapping()
    public Entity createEntity(@Valid @RequestBody T entity) {
        return inMemoryEntityStorage.createEntity(entity);
    }

    @PutMapping()
    public Entity updateEntity(@Valid @RequestBody T entity) {
        return inMemoryEntityStorage.updateEntity(entity);
    }

    @DeleteMapping("/{entityId}")
    public void deleteEntityById(@PathVariable int entityId) {
        inMemoryEntityStorage.deleteEntityById(entityId);
    }

    @DeleteMapping
    public void deleteAllEntity() {
        inMemoryEntityStorage.deleteAllEntity();
    }

    @GetMapping("/{entityId}")
    public Entity getEntityById(@PathVariable int entityId) {
        return inMemoryEntityStorage.getEntityById(entityId);
    }

    @GetMapping()
    public ArrayList<T> getAllEntity() {
        return inMemoryEntityStorage.getAllEntity();
    }

}
