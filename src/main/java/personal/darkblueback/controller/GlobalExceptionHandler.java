package personal.darkblueback.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import personal.darkblueback.exception.CustomException;
import personal.darkblueback.exception.LoginException;
import personal.darkblueback.model.CustomError;
import personal.darkblueback.model.ValidacionResponse;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidacionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        List<CustomError> errorsL = ex.getBindingResult().getFieldErrors().stream().map(
                        field -> new CustomError(field.getField(), field.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.status(400).body(new ValidacionResponse(errorsL, new Date()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomError> handleUsuarioYaExisteException(CustomException ex) {
        return ResponseEntity.status(409).body(new CustomError("Error en el registro: ", ex.getMessage()));
    }
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<CustomError> handleLoginException(LoginException ex) {
        return ResponseEntity.status(401).body(new CustomError("Error en el login: ", ex.getMessage()));
    }
}
