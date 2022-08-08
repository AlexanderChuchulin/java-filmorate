package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.service.EntityDbService;
import ru.yandex.practicum.filmorate.model.Entity;

import java.util.ArrayList;

@RestController
public abstract class StorageController<T extends Entity, V extends Entity> {
    EntityDbService<T, V> dbService;

    @PostMapping()
    public Entity createEntityController(@RequestBody T entity) {
        return dbService.createEntityDb(entity);
    }

    @PutMapping()
    public Entity updateEntityController(@RequestBody T entity) {
        return dbService.updateEntityDb(entity);
    }

    @DeleteMapping("/{entityId}")
    public void deleteEntityByIdController(@PathVariable int entityId) {
        dbService.deleteEntityByIdDb(entityId);
    }

    @DeleteMapping
    public void deleteAllEntityController() {
        dbService.deleteAllEntityDb();
    }

    @GetMapping("/{entityId}")
    public Entity getEntityByIdController(@PathVariable int entityId) {
        return dbService.getEntityByIdDb(entityId);
    }

    @GetMapping()
    public ArrayList<T> getAllEntityController() {
        return dbService.getAllEntityDb();
    }

}
