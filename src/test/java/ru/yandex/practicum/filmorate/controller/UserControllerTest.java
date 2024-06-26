package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class UserControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper mapper;
    @Autowired
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // без этого LocalDate сериализуется некорректно
    }

    @Test
    void saveValidUser() throws Exception {
        User user = new User();
        user.setLogin("user");
        user.setEmail("test@example.com");
        user.setName("Пользователь");
        user.setBirthday(LocalDate.of(2024, 6, 13));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Пользователь"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("2024-06-13"));
    }

    @Test
    void saveUserWithEmptyName() throws Exception {
        User user = new User();
        user.setLogin("user");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(2024, 6, 13));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("2024-06-13"));
    }

    @Test
    void saveUserWithInvalidEmail() throws Exception {
        User user = new User();
        user.setLogin("user");
        user.setEmail("invalidEmail");
        user.setBirthday(LocalDate.now());

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveUserWithEmptyLogin() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.now());

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveUserWithInvalidLogin() throws Exception {
        User user = new User();
        user.setLogin("invalid login");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.now());

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void saveUserWithFutureBirthday() throws Exception {
        User user = new User();
        user.setLogin("testUser");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateUserSuccess() throws Exception {
        User userToCreate = new User();
        userToCreate.setLogin("user");
        userToCreate.setEmail("test@example.com");
        userToCreate.setName("Пользователь");
        userToCreate.setBirthday(LocalDate.of(1991, 6, 13));

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToCreate)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        long createdUserId = mapper.readValue(response, User.class).getId();

        User userToUpdate = new User();
        userToUpdate.setId(createdUserId);
        userToUpdate.setLogin("updatedUser");
        userToUpdate.setEmail("updated@example.com");
        userToUpdate.setName("Обновленный Пользователь");
        userToUpdate.setBirthday(LocalDate.of(1992, 6, 13));

        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToUpdate)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdUserId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("updatedUser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("updated@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Обновленный Пользователь"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("1992-06-13"));
    }

    @Test
    void updateUserNotFound() throws Exception {
        User userToUpdate = new User();
        userToUpdate.setId(999);
        userToUpdate.setLogin("updatedUser");
        userToUpdate.setEmail("updated@example.com");
        userToUpdate.setBirthday(LocalDate.now());

        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToUpdate)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getAllUsers() throws Exception {
        User user1 = new User();
        user1.setLogin("user1");
        user1.setEmail("user1@example.com");
        user1.setBirthday(LocalDate.now());

        User user2 = new User();
        user2.setLogin("user2");
        user2.setEmail("user2@example.com");
        user2.setBirthday(LocalDate.now());

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user1)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user2)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].login").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].email").value("user1@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].login").value("user2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].email").value("user2@example.com"));
    }
}
