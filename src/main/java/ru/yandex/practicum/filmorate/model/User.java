package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.Instant;

@Data
public class User {
    private int id;
    @Email(message = "Email должен быть корректным")
    private String email;

    @NotBlank(message = "Логин не должен быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    private String login;
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private Instant birthday;
}
