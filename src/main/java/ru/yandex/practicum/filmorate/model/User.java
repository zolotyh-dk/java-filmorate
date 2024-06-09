package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.Instant;

@Data
public class User {
    private int id;
    @Email
    private String email;
    private String login;
    private String name;
    private Instant birthday;
}
