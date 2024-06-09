package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, Instant> {
    private static final Instant MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant();

    @Override
    public boolean isValid(Instant value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null проверяется отдельно через @NotNull
        }
        return !value.isBefore(MIN_RELEASE_DATE);
    }
}
