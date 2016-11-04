package filip.janusz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Book {

    @Id
    @GeneratedValue
    private long id;
    private String title;
    private String author;
    private long isbnNumber;

    @JsonIgnore
    @ManyToMany(mappedBy = "books")
    private List<User> users;

    public Book() {}

    public Book(Builder builder) {
        this.title = builder.title;
        this.author = builder.author;
        this.isbnNumber = builder.isbnNumber;
        this.users = builder.user;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public long getIsbnNumber() {
        return isbnNumber;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbnNumber(long isbnNumber) {
        this.isbnNumber = isbnNumber;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public static class Builder {

        private String title;
        private String author;
        private long isbnNumber;
        private List<User> user;

        public Builder(String title, String author, long isbnNumber) {
            this.title = title;
            this.author = author;
            this.isbnNumber = isbnNumber;
        }

        public Builder setUser(List<User> user) {
            this.user = user;
            return this;
        }

        public Book build() {
            return new Book(this);
        }
    }
}
