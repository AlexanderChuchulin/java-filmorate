package ru.yandex.practicum.filmorate.postmanCrutches;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.SneakyThrows;
import org.springframework.boot.jackson.JsonComponent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.ArrayList;

@JsonComponent
public class FilmSerializer extends JsonSerializer<Film> {

    @SneakyThrows
    @Override
    public void serialize(Film film, JsonGenerator jGen, SerializerProvider serProvider) {
        ArrayList<PlugObject> genresObjList = new ArrayList<>();

        if (film.getGenreIdSet() != null) {
            for (Integer genreId : film.getGenreIdSet()) {
                genresObjList.add(new PlugObject(genreId, InMemoryFilmStorage.getFilmGenresMap().get(genreId)));
            }
        }

        jGen.writeStartObject();
        jGen.writeNumberField("id", film.getId());
        jGen.writeStringField("name", film.getFilmName());
        jGen.writeStringField("description", film.getDescription());
        jGen.writeStringField("releaseDate", String.valueOf(film.getReleaseDate()));
        jGen.writeNumberField("duration", film.getDuration());
        jGen.writePOJOField("mpa", new PlugObject(film.getMpaRatingId(),
                InMemoryFilmStorage.getMpaRatingMap().get(film.getMpaRatingId())));
        jGen.writePOJOField("genres", genresObjList);
        jGen.writeEndObject();
    }
}
