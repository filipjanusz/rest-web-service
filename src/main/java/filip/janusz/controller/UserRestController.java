package filip.janusz.controller;

import filip.janusz.exception.UserNotFoundException;
import filip.janusz.model.User;
import filip.janusz.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping(value = "/users")
public class UserRestController {

    private UserRepository userRepository;

    @Autowired
    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{username}")
    public Optional<User> readUser(@PathVariable String username) {
        validateUserByUsername(username);
        return this.userRepository.findByUsername(username);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<User> readUsers() {
        return this.userRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        this.userRepository.save(user);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/users/{userId}")
                .buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update/{userId}")
    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable Long userId) {
        validateUserById(userId);
        User currentUser = this.userRepository.findOne(userId);
        currentUser.setUsername(user.getUsername());
        currentUser.setPassword(user.getPassword());
        currentUser.setBooks(user.getBooks());
        this.userRepository.save(currentUser);
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        validateUserById(userId);
        this.userRepository.delete(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void validateUserByUsername(String username) {
        this.userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    private void validateUserById(Long userId) {
        User user = this.userRepository.findOne(userId);
        if(user == null)
            throw new UserNotFoundException(user.getUsername());
    }
}
