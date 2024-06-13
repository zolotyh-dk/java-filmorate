package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private int id;

    @Email(message = "Email должен быть корректным")
    private String email;

    @NotBlank(message = "Логин не должен быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd") // без этого LocalDate сериализуется некорректно
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
