package filip.janusz.controller;

import filip.janusz.exception.BookExistsException;
import filip.janusz.exception.BookNotFoundException;
import filip.janusz.model.Book;
import filip.janusz.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;

@RestController
@RequestMapping(value = "/books")
public class BookRestController {

    private BookRepository bookRepository;

    @Autowired
    public BookRestController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @RequestMapping(value = "/{bookId}", method = RequestMethod.GET)
    public Book readBook(@PathVariable Long bookId) {
        validateBook(bookId);
        return this.bookRepository.findOne(bookId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<Book> readBooks() {
        return this.bookRepository.findAll();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        validateBookIfExist(book.getTitle());
        this.bookRepository.save(book);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/books/{bookId}")
                .buildAndExpand(book.getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update/{bookId}")
    public ResponseEntity<?> updateBook(@RequestBody Book book, @PathVariable Long bookId) {
        validateBook(bookId);
        Book currentBook = bookRepository.findOne(bookId);
        currentBook.setIsbnNumber(book.getIsbnNumber());
        currentBook.setTitle(book.getTitle());
        currentBook.setAuthor(book.getAuthor());
        this.bookRepository.save(currentBook);
        return new ResponseEntity<>(currentBook, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable Long bookId) {
        validateBook(bookId);
        this.bookRepository.delete(bookId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void validateBook(Long bookId) {
        this.bookRepository.findById(bookId).orElseThrow(
                () -> new BookNotFoundException(bookId));
    }

    private void validateBookIfExist(String title) {
        if(this.bookRepository.findByTitle(title) != null)
            throw new BookExistsException(title);
    }
}