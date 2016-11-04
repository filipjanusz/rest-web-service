package filip.janusz.controller;

import filip.janusz.exception.BookExistsException;
import filip.janusz.exception.BookNotFoundException;
import filip.janusz.exception.UserNotFoundException;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestControllerAdvice {

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public VndErrors userNotFoundExceptionHandler(UserNotFoundException e) {
        return new VndErrors("userNotFoundError", e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public VndErrors bookNotFoundExceptionHandler(BookNotFoundException e) {
        return new VndErrors("userNotFoundError", e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(BookExistsException.class)
    @ResponseStatus(HttpStatus.FOUND)
    public VndErrors bookExistsExceptionHandler(BookExistsException e) {
        return new VndErrors("bookExistsError", e.getMessage());
    }

}
