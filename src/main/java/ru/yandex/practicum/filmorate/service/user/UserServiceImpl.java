package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.Collection;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(@Qualifier("jdbcUserRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            final String login = user.getLogin();
            user.setName(login);
            log.info("У пользователя не указано имя. Установили логин: {} в качестве имени.", login);
        }
        return userRepository.saveUser(user);
    }

    @Override
    public User updateUser(User user) {
        final User savedUser = userRepository.getUserById(user.getId());
        log.debug("Проверили, что пользователь с таким ID: {} существует в базе данных", user.getId());

        if (user.getName() == null || user.getName().isEmpty()) {
            final String login = user.getLogin();
            user.setName(user.getLogin());
            log.info("У пользователя не указано имя. Установили логин: {} в качестве имени.", login);
        }
        return userRepository.updateUser(user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User getUserById(long id) {
        return userRepository.getUserById(id);
    }
}
