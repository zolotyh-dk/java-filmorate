package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.friend.FriendService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FriendService friendService;

    @PostMapping
    public User saveUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на сохранение пользователя. POST /users c телом: {}", user);
        final User savedUser = userService.saveUser(user);
        log.info("Возвращаем сохраненного пользователя. POST /users c телом: {}", savedUser);
        return savedUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя. PUT /users c телом {}", user);
        final User updatedUser = userService.updateUser(user);
        log.info("Возвращаем обновленного пользователя. PUT /users c телом: {}", updatedUser);
        return updatedUser;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей. GET /users");
        final Collection<User> allUsers = userService.getAllUsers();
        log.info("Возвращаем всех пользователей. GET /users c телом {}", allUsers);
        return allUsers;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        log.info("Получен запрос на получение пользователя по ID. GET /users/{}", id);
        final User user = userService.getUserById(id);
        log.info("Возвращаем пользователя. GET /users/{} c телом: {}", id, user);
        return user;
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Получен запрос на добавление в друзья. PUT /users/{}/friends/{}", id, friendId);
        friendService.addFriend(id, friendId);
        log.info("Возвращаем ответ OK. PUT /users/{}/friends/{}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Получен запрос на удаление из друзей. DELETE /users/{}/friends/{}", id, friendId);
        friendService.removeFriend(id, friendId);
        log.info("Возвращаем ответ OK. DELETE /users/{}/friends/{}", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        log.info("Получен запрос на получение списка друзей. GET /users/{}/friends", id);
        final List<User> friends = friendService.getFriends(id);
        log.info("Возвращаем список друзей. GET /users/{}/friends c телом: {}", id, friends);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Получен запрос на получение списка общих друзей. GET /users/{}/friends/common{}", id, otherId);
        final List<User> commonFriends = friendService.getCommonFriends(id, otherId);
        log.info("Возвращаем список друзей. GET /users/{}/friends/common/{} c телом: {}", id, otherId, commonFriends);
        return commonFriends;
    }
}
