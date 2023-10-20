package medipin.data;

import medipin.models.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository {

    @Transactional
    User getById(int userId);

    @Transactional
    User getByUsername(String username);

    @Transactional
    User add(User user);

    @Transactional
    boolean update(User user);

    @Transactional
    boolean deleteById(int userId);
}
