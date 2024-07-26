package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть null")
    @JsonFormat(pattern = "yyyy-MM-dd") // без этого LocalDate сериализуется некорректно
    @ValidReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    private Mpa mpa; //films.mpa_id <- mpa.get()

    private Set<Genre> genres; //genre_films.film_id и genre_id

    @JsonIgnore
    private Set<Long> usersLikeIds;

    public void addGenre(Genre genre) {
        if (genres == null) {
            genres = new HashSet<>();
        }
        genres.add(genre);
    }
}
