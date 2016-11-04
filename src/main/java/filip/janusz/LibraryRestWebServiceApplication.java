package filip.janusz;

import filip.janusz.model.Book;
import filip.janusz.model.User;
import filip.janusz.repository.BookRepository;
import filip.janusz.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class LibraryRestWebServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(LibraryRestWebServiceApplication.class, args);
	}

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BookRepository bookRepository;

	@Override
	public void run(String... strings) throws Exception {
		Book book1 = bookRepository.save(new Book.Builder("Pan Tadeusz", "A.Mickiewicz", 12345).build());
		Book book2 = bookRepository.save(new Book.Builder("Dziady", "A.Mickiewicz", 12346).build());
		List<String> list = Arrays.asList("jkowalski,pnowak,ktarnowski,ptomaszewski,szewczak,jacenty".split(","));
		for(String e: list) {
			User user = new User(e, "password");
			user.setBooks(new ArrayList<>(Arrays.asList(book1, book2)));
			userRepository.save(user);
		}
	}
}

