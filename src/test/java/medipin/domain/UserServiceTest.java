package medipin.domain;

import medipin.data.UserRepository;
import medipin.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceTest {

    @Autowired
    UserService service;

    @MockBean
    UserRepository repository;

    @Test
    void shouldGetByValidId() {
        User user = new User(1, "user1", "user1Password!",
                true, List.of("USER"));
        when(repository.getById(1)).thenReturn(user);

        Result<User> actual = service.getById(1);
        assertTrue(actual.isSuccess());
        assertEquals(user, actual.getPayload());
    }

    @Test
    void shouldNotGetByInvalidId() {
        when(repository.getById(100)).thenReturn(null);

        Result<User> actual = service.getById(100);
        assertFalse(actual.isSuccess());
        assertEquals(actual.getType(), ResultType.NOT_FOUND);
        assertTrue(actual.getMessages().contains("User id not found"));
    }

    @Test
    void shouldGetByValidUsername() {
        User user = new User(1, "user1", "user1Password!",
                true, List.of("USER"));
        when(repository.getByUsername("user1")).thenReturn(user);

        Result<User> actual = service.getByUsername("user1");
        assertTrue(actual.isSuccess());
        assertEquals(user, actual.getPayload());
    }

    @Test
    void shouldNotGetByInvalidUsername() {
        when(repository.getByUsername("missing")).thenReturn(null);

        Result<User> actual = service.getByUsername("missing");
        assertFalse(actual.isSuccess());
        assertEquals(actual.getType(), ResultType.NOT_FOUND);
        assertTrue(actual.getMessages().contains("Username not found"));
    }

}