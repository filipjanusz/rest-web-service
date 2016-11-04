package filip.janusz.controller;

import filip.janusz.exception.UserNotFoundException;
import filip.janusz.model.Book;
import filip.janusz.repository.BookRepository;
import filip.janusz.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/{userId}/books")
public class UsersBookRestController {

    private BookRepository bookRepository;
    private UserRepository userRepository;

    @Autowired
    public UsersBookRestController(BookRepository bookRepository,
                                   UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addBookToUser(@PathVariable String userId, @RequestBody Book book) {
        validateUser(userId);
        return this.userRepository.findByUsername(userId)
                .map(user -> {
                    Book result = bookRepository.save(new Book.Builder(book.getTitle(),
                            book.getAuthor(),
                            book.getIsbnNumber())
                            .setUser(book.getUsers())
                            .build());

                    List<Book> usersBookList = new ArrayList<>(bookRepository.findByUsersUsername(user.getUsername()));
                    usersBookList.add(result);
                    user.setBooks(usersBookList);
                    userRepository.save(user);

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest()
                            .path("/{id}").buildAndExpand(result.getId()).toUri());
                    return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
                }).get();
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<Book> readBooksByUser(@PathVariable String userId) {
        validateUser(userId);
        return this.bookRepository.findByUsersUsername(userId);
    }

    private void validateUser(String userId) {
        this.userRepository.findByUsername(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }
}