package ru.yandex.practicum.filmorate.base.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.base.exception.ValidationException;
import ru.yandex.practicum.filmorate.base.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserController userController;

    @BeforeEach
    public void setup() {
        userController = new UserController();
    }

    @Test
    public void testSaveUserValidUser() {
        User user = new User();
        user.setName("Пользователь");
        user.setLogin("user");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(1990, 6, 1));

        User savedUser = userController.saveUser(user);

        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getLogin(), savedUser.getLogin());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getBirthday(), savedUser.getBirthday());
    }

    @Test
    public void testSaveUserInvalidEmail() {
        User user = new User();
        user.setName("Пользователь");
        user.setLogin("user");
        user.setEmail("invalidemail");
        user.setBirthday(LocalDate.of(1990, 6, 1));

        assertThrows(ValidationException.class, () -> userController.saveUser(user));
    }

    @Test
    public void testSaveUserInvalidLogin() {
        User user = new User();
        user.setName("Пользователь");
        user.setLogin("невалидный логин с пробелами");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(1990, 6, 1));

        assertThrows(ValidationException.class, () -> userController.saveUser(user));
    }

    @Test
    public void testSaveUserFutureBirthday() {
        User user = new User();
        user.setName("Пользователь");
        user.setLogin("user");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.saveUser(user));
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setName("Пользователь");
        user.setLogin("user");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(1990, 6, 1));
        User savedUser = userController.saveUser(user);

        savedUser.setName("Обновленный Пользователь");
        User updatedUser = userController.updateUser(savedUser);

        assertNotNull(updatedUser);
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals(savedUser.getName(), updatedUser.getName());
        assertEquals(savedUser.getLogin(), updatedUser.getLogin());
        assertEquals(savedUser.getEmail(), updatedUser.getEmail());
        assertEquals(savedUser.getBirthday(), updatedUser.getBirthday());
    }

    @Test
    public void testUpdateNonExistingUser() {
        User updatedUser = new User();
        updatedUser.setId(999);
        updatedUser.setName("Обновленный Пользователь");
        updatedUser.setLogin("updatedUser");
        updatedUser.setEmail("updatedtest@example.com");
        updatedUser.setBirthday(LocalDate.of(1995, 7, 1));

        assertThrows(NoSuchElementException.class, () -> userController.updateUser(updatedUser));
    }

    @Test
    public void testGetAllUsers() {
        Collection<User> users = userController.getAllUsers();
        assertTrue(users.isEmpty());

        User user1 = new User();
        user1.setName("Пользователь 1");
        user1.setLogin("user1");
        user1.setEmail("test1@example.com");
        user1.setBirthday(LocalDate.of(1990, 6, 1));
        userController.saveUser(user1);

        User user2 = new User();
        user2.setName("Пользователь 2");
        user2.setLogin("user2");
        user2.setEmail("test2@example.com");
        user2.setBirthday(LocalDate.of(1990, 6, 2));
        userController.saveUser(user2);

        users = userController.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }
}
