package ru.yandex.practicum.filmorate.postmanCrutches;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.SneakyThrows;
import org.springframework.boot.jackson.JsonComponent;

import java.util.ArrayList;
import java.util.Map;

@JsonComponent
public class FilmMpaGenresSerializer extends JsonSerializer<Map<Integer, String>> {

    @SneakyThrows
    @Override
    public void serialize(Map<Integer, String> addPropMap, JsonGenerator jGen, SerializerProvider serProvider) {
        ArrayList<PlugObject> addPropObjList = new ArrayList<>();

        if (addPropMap != null) {
            for (Integer addPropId : addPropMap.keySet()) {
                addPropObjList.add(new PlugObject(addPropId, addPropMap.get(addPropId)));
            }
        }

        if (addPropObjList.size() > 1) {
            jGen.writePOJO(addPropObjList);
        } else {
            jGen.writePOJO(addPropObjList.get(0));
        }
    }

}
