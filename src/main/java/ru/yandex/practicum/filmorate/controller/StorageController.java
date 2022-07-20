package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.service.EntityService;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
public abstract class StorageController<T extends Entity, V extends Entity> {
    private final EntityService<T, V> entityService;

    @Autowired
    public StorageController(EntityService<T, V> entityService) {
        this.entityService = entityService;
    }

    @PostMapping()
    public Entity createEntityByController(@Valid @RequestBody T entity) {
        return entityService.createEntity(entity);
    }

    @PutMapping()
    public Entity updateEntityByController(@Valid @RequestBody T entity) {
        return entityService.updateEntity(entity);
    }

    @DeleteMapping("/{entityId}")
    public void deleteEntityByIdByController(@PathVariable int entityId) {
        entityService.deleteEntityById(entityId);
    }

    @DeleteMapping
    public void deleteAllEntityByController() {
        entityService.deleteAllEntity();
    }

    @GetMapping("/{entityId}")
    public Entity getEntityByIdByController(@PathVariable int entityId) {
        return entityService.getEntityById(entityId);
    }

    @GetMapping()
    public ArrayList<T> getAllEntityByController() {
        return entityService.getAllEntity();
    }

}
