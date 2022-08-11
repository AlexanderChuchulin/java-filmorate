package ru.yandex.practicum.filmorate.postmanCrutches;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class PlugObject {
    @JsonProperty()
    private Integer id;
    @JsonProperty()
    private String name;

    public PlugObject(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

}
