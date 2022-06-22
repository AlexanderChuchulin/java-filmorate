package ru.yandex.practicum.filmorate.other;

import java.util.Map;

public class IntIdGenerator<T> {

   public int generateId(Map<Integer, T> dataMap) {
        int Id = 0;

        if (!dataMap.isEmpty()) {
            for (Integer currentId : dataMap.keySet()) {
                if (currentId > Id) {
                    Id = currentId;
                }
            }
        }

        Id++;

        return Id;
    }

}
