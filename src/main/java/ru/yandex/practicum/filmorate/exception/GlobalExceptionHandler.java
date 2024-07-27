package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : result.getFieldErrors()) {
            errorMessage.append(fieldError.getDefaultMessage()).append("; ");
        }
        log.error("Ошибка валидации: {}", errorMessage);
        ErrorDetails errorDetails = new ErrorDetails(errorMessage.toString());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class, RequestException.class})
    public ResponseEntity<Object> handleNotFoundExceptions(RuntimeException exception) {
        log.error("Ресурс не найден: {}", exception.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST); // для Mpa и жанров Postman ожидает код 400
    }

    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class, GenreNotFoundException.class, MpaNotFoundException.class})
    public ResponseEntity<Object> handleFilmNotFoundExceptions(RuntimeException exception) {
        log.error("Ресурс не найден: {}", exception.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND); // для фильмов и пользователей и жанров Postman ожидает код 404
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception exception) {
        log.error("Внутренняя ошибка сервера: {}", exception.getMessage(), exception);
        ErrorDetails errorDetails = new ErrorDetails("Произошла внутренняя ошибка сервера.");
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Getter
    static class ErrorDetails {
        private final String error;

        public ErrorDetails(String error) {
            this.error = error;
        }
    }
}