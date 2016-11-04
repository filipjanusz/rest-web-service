package filip.janusz;

import filip.janusz.model.Book;
import filip.janusz.model.User;
import filip.janusz.repository.BookRepository;
import filip.janusz.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LibraryRestWebServiceApplication.class)
@WebAppConfiguration
public class UsersBookRestControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private String username = "jkowalski";
    private User user;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private List<Book> bookList = new ArrayList<>();
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.bookRepository.deleteAllInBatch();
        this.userRepository.deleteAllInBatch();

        this.bookList.add(bookRepository.save(new Book.Builder("Pan Tadeusz", "A.Mickiewicz", 12345).build()));
        this.bookList.add(bookRepository.save(new Book.Builder("Dziady", "A.Mickiewicz", 12346).build()));
        this.user = new User(username, "password");
        user.setBooks(this.bookList);
        this.userRepository.save(user);
    }

    @Test
    public void userNotFound() throws Exception {
        mockMvc.perform(post("/filip/books/").content(this.json(new Book())).contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void readBooks() throws Exception {
        mockMvc.perform(get("/" + username + "/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is((int) this.bookList.get(0).getId())))
                .andExpect(jsonPath("$[0].title", is("Pan Tadeusz")))
                .andExpect(jsonPath("$[0].author", is("A.Mickiewicz")))
                .andExpect(jsonPath("$[0].isbnNumber", is(12345)))
                .andExpect(jsonPath("$[1].id", is((int) this.bookList.get(1).getId())))
                .andExpect(jsonPath("$[1].title", is("Dziady")))
                .andExpect(jsonPath("$[1].author", is("A.Mickiewicz")))
                .andExpect(jsonPath("$[1].isbnNumber", is(12346)));
    }

    @Test
    public void createBook() throws Exception {
        String jsonBook = json(new Book.Builder("Krzyzacy", "H.Sienkiewicz", 12347).setUser(Arrays.asList(user)).build());
        mockMvc.perform(post("/" + username + "/books").contentType(contentType).content(jsonBook))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/" + username + "/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].title", is("Krzyzacy")))
                .andExpect(jsonPath("$[2].author", is("H.Sienkiewicz")))
                .andExpect(jsonPath("$[2].isbnNumber", is(12347)));
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
