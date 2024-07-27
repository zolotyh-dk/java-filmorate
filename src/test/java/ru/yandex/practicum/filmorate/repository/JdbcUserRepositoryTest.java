package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.JdbcUserRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(JdbcUserRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcUserRepositoryTest {
    private final JdbcUserRepository userRepository;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setLogin("newlogin");
        user.setName("New User");
        user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));

        User savedUser = userRepository.saveUser(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getLogin()).isEqualTo(user.getLogin());
        assertThat(savedUser.getName()).isEqualTo(user.getName());
        assertThat(savedUser.getBirthday()).isEqualTo(user.getBirthday());
    }

    @Test
    public void testUpdateUser() {
        User user = userRepository.getUserById(1L);
        assertThat(user).isNotNull();

        user.setEmail("updated_email@example.com");
        user.setName("Updated Name");

        User updatedUser = userRepository.updateUser(user);
        User userFromDb = userRepository.getUserById(user.getId());

        assertThat(updatedUser).isNotNull();
        assertThat(userFromDb).isNotNull();
        assertThat(updatedUser).isEqualTo(userFromDb);
        assertThat(updatedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(updatedUser.getName()).isEqualTo(user.getName());
    }

    @Test
    public void testGetUserById() {
        User user = userRepository.getUserById(1L);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("user1@example.com");
        assertThat(user.getLogin()).isEqualTo("user1");
        assertThat(user.getName()).isEqualTo("User One");
        assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1990, Month.JANUARY, 1));
    }

    @Test
    public void testGetUsersByIds() {
        List<Long> userIds = List.of(1L, 2L);
        List<User> usersFromDb = userRepository.getUsersByIds(userIds);

        User user1 = usersFromDb.get(0);
        User user2 = usersFromDb.get(1);

        assertThat(usersFromDb).isNotNull().isNotEmpty();
        assertThat(usersFromDb.size()).isEqualTo(2);

        assertThat(user1).isNotNull();
        assertThat(user1.getEmail()).isEqualTo("user1@example.com");
        assertThat(user1.getLogin()).isEqualTo("user1");
        assertThat(user1.getName()).isEqualTo("User One");
        assertThat(user1.getBirthday()).isEqualTo(LocalDate.of(1990, Month.JANUARY, 1));

        assertThat(user2).isNotNull();
        assertThat(user2.getEmail()).isEqualTo("user2@example.com");
        assertThat(user2.getLogin()).isEqualTo("user2");
        assertThat(user2.getName()).isEqualTo("User Two");
        assertThat(user2.getBirthday()).isEqualTo(LocalDate.of(1991, Month.FEBRUARY, 1));
    }
}
