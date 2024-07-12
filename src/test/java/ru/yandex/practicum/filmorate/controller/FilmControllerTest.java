package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.exception.GlobalExceptionHandler;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper mapper;
    @Autowired
    private FilmController filmController;
    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(filmController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // без этого LocalDate сериализуется некорректно
    }

    @Test
    void saveValidFilm() throws Exception {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2024, 6, 13));
        film.setDuration(120);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Фильм"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Описание"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("2024-06-13"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(120));
    }

    @Test
    void testSaveFilmBlankName() throws Exception {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2024, 6, 13));
        film.setDuration(120);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testSaveFilmReleaseDateBeforeMinDate() throws Exception {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveFilmLongDescription() throws Exception {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание".repeat(30));
        film.setReleaseDate(LocalDate.of(2024, 6, 1));
        film.setDuration(120);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testSaveFilmNegativeDuration() throws Exception {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2024, 6, 1));
        film.setDuration(-120);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateFilmSuccess() throws Exception {
        Film filmToCreate = new Film();
        filmToCreate.setName("Фильм для обновления");
        filmToCreate.setDescription("Описание для обновления");
        filmToCreate.setReleaseDate(LocalDate.of(2024, 6, 1));
        filmToCreate.setDuration(130);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(filmToCreate)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        long createdFilmId = mapper.readValue(response, Film.class).getId();

        Film filmToUpdate = new Film();
        filmToUpdate.setId(createdFilmId);
        filmToUpdate.setName("Новое название");
        filmToUpdate.setDescription("Новое описание");
        filmToUpdate.setReleaseDate(LocalDate.of(2023, 6, 1));
        filmToUpdate.setDuration(150);

        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(filmToUpdate)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdFilmId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Новое название"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Новое описание"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("2023-06-01"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(150));

    }

    @Test
    void updateFilmNotFound() throws Exception {
        Film filmToUpdate = new Film();
        filmToUpdate.setId(999L);
        filmToUpdate.setName("Новое название");
        filmToUpdate.setDescription("Новое описание");
        filmToUpdate.setReleaseDate(LocalDate.of(2023, 6, 1));
        filmToUpdate.setDuration(150);

        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(filmToUpdate)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getAllFilms() throws Exception {
        Film film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание фильма 1");
        film1.setReleaseDate(LocalDate.of(2024, 6, 1));
        film1.setDuration(120);

        Film film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание фильма 2");
        film2.setReleaseDate(LocalDate.of(2024, 6, 2));
        film2.setDuration(130);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film1)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film2)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/films"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("Фильм 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value("Описание фильма 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].releaseDate").value("2024-06-01"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].duration").value(120))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("Фильм 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].description").value("Описание фильма 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].releaseDate").value("2024-06-02"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].duration").value(130));
    }

    @Test
    void getFilmById() throws Exception {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2024, 6, 1));
        film.setDuration(120);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        long createdFilmId = mapper.readValue(response, Film.class).getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/films/" + createdFilmId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdFilmId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Фильм"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Описание"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("2024-06-01"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(120));
    }

    @Test
    void getFilmByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/films/999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void addLike() throws Exception {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2024, 6, 1));
        film.setDuration(120);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        long createdFilmId = mapper.readValue(response, Film.class).getId();

        long userId = createUserAndGetId();

        mockMvc.perform(MockMvcRequestBuilders.put("/films/" + createdFilmId + "/like/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void removeLike() throws Exception {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2024, 6, 1));
        film.setDuration(120);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        long createdFilmId = mapper.readValue(response, Film.class).getId();

        long userId = createUserAndGetId();

        mockMvc.perform(MockMvcRequestBuilders.put("/films/" + createdFilmId + "/like/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/films/" + createdFilmId + "/like/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getPopularFilms() throws Exception {
        Film film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание фильма 1");
        film1.setReleaseDate(LocalDate.of(2024, 6, 1));
        film1.setDuration(120);

        Film film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание фильма 2");
        film2.setReleaseDate(LocalDate.of(2024, 6, 2));
        film2.setDuration(130);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film1)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        long film1Id = mapper.readValue(response, Film.class).getId();

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film2)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        long userId = createUserAndGetId();

        mockMvc.perform(MockMvcRequestBuilders.put("/films/" + film1Id + "/like/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/films/popular?count=1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("Фильм 1"));
    }

    private long createUserAndGetId() throws Exception {
        User user = new User();
        user.setName("Пользователь");
        user.setEmail("email@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User savedUser = userStorage.saveUser(user);
        return savedUser.getId();
    }
}
