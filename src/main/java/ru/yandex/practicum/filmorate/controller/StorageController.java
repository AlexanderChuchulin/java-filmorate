package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.storage.InMemoryEntityStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.ArrayList;


@RestController
public abstract class StorageController<T extends Entity> {
    final InMemoryEntityStorage inMemoryEntityStorage;


    @Autowired
    protected StorageController(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryEntityStorage = inMemoryFilmStorage;
        String test = "film";
    }

    @Autowired
    public StorageController(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryEntityStorage = inMemoryUserStorage;
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
