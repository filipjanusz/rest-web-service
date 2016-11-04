package filip.janusz.exception;

public class BookExistsException extends RuntimeException {
    public BookExistsException(String title) {
        super("Book with this title: '" + title + "' already exists");
    }
}
