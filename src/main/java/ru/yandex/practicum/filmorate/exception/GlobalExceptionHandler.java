package ru.yandex.practicum.filmorate.exception;

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

    @ExceptionHandler(FilmNotFoundException.class)
    public ResponseEntity<Object> handleFilmNotFoundException(FilmNotFoundException exception) {
        log.error("Фильм не найден: {}", exception.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception) {
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    static class ErrorDetails {
        private String error;

        public ErrorDetails(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}