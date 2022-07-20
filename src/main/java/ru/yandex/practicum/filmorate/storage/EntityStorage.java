package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Entity;

import java.util.ArrayList;


interface EntityStorage<T extends Entity> {

    // Метод создаёт объект
    T createEntity(T entity);

    // Метод обновляет объект
    T updateEntity(T entity);

    // Метод удаляет объект по id
    void deleteEntityById(int entityId);

    // Метод удаляет все объекты
    void deleteAllEntity();

    // Метод возвращает объект по id
    T getEntityById(int entityId);

    // Метод возвращает список всех объектов
    ArrayList<T> getAllEntity();

    // Метод генерирует id для создаваемого объекта
    int generateId();

    // Метод проверяет по id существование сущностей
    void entityNotFoundCheck(String conclusion, int parentId, boolean isNotSameKindChild, int... childId);

    // Метод производит валидацию объекта при его создании или обновлении
    default void validateEntity(T entity, Boolean isUpdate, String conclusion) {
    }

}
