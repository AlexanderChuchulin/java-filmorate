package ru.yandex.practicum.filmorate.postmanCrutches;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import org.springframework.boot.jackson.JsonComponent;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.TreeSet;

@JsonComponent
public class FilmDeserializer extends JsonDeserializer<Film> {

    @SneakyThrows
    @Override
    public Film deserialize(JsonParser jsonParser, DeserializationContext desCon) {
        Film film;
        String name = "";
        String description = "";
        LocalDate releaseDate = null;
        int duration = -1;

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        if (node.hasNonNull("name")) {
            name = node.get("name").asText();
        }

        if (node.hasNonNull("description")) {
            description = node.get("description").asText();
        }

        if (node.hasNonNull("releaseDate")) {
            releaseDate = LocalDate.parse(node.get("releaseDate").asText());
        }

        if (node.hasNonNull("duration")) {
            duration = node.get("duration").asInt();
        }

        film = Film.builder()
                .filmName(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .build();

        if (node.hasNonNull("id")) {
            film.setId(node.get("id").asInt());
        }

        if (node.toString().contains("mpa")) {
            if (node.hasNonNull("mpa")) {
                film.setMpaRatingId(node.get("mpa").get("id").asInt());
            } else {
                film.setMpaRatingId(-12345);
            }
        }

        if (node.hasNonNull("genres") && node.get("genres").size() > 0) {
            TreeSet<Integer> genreIdSet = new TreeSet<>();

            for (String s : node.get("genres").toString().split(",")) {
                genreIdSet.add(Integer.valueOf(s.substring(s.indexOf(":") + 1, s.indexOf("}"))));
            }
            film.setGenreIdSet(genreIdSet);
        }
        return film;
    }

}
