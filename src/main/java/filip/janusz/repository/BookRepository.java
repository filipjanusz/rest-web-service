package filip.janusz.repository;

import filip.janusz.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Collection<Book> findByUsersUsername(String username);
    Optional<Book> findById(Long bookId);
    Book findByTitle(String title);
}
