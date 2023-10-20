package medipin.domain;

import medipin.data.UserRepository;
import medipin.models.User;
import medipin.security.Credentials;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.getByUsername(username);
        if (user == null || !user.isEnabled()) {
            throw new UsernameNotFoundException(username + " not found");
        }
        return user;
    }

    public Result<User> getById(int userId) {
        Result<User> result = new Result<>();

        User found = repository.getById(userId);
        if (found != null) {
            result.setPayload(found);
        } else {
            result.addMessage("User id not found", ResultType.NOT_FOUND);
        }

        return result;
    }

    public Result<User> getByUsername(String username) {
        Result<User> result = new Result<>();

        User found = repository.getByUsername(username);
        if (found != null) {
            result.setPayload(found);
        } else {
            result.addMessage("Username not found", ResultType.NOT_FOUND);
        }

        return result;
    }

    public Result<User> add(Credentials credentials) {
        Result<User> result = validate(credentials);
        if (!result.isSuccess()) {
            return result;
        }

        String hashedPassword = encoder.encode(credentials.getPassword());
        User user = new User(0,
                credentials.getUsername(),
                hashedPassword, true, null);
        List<String> roles = List.of("USER");
        user.setAuthorities(roles.stream().map(r -> new SimpleGrantedAuthority(r)).collect(Collectors.toList()));

        try {
            user = repository.add(user);
            result.setPayload(user);
        } catch (DuplicateKeyException e) {
            result.addMessage("Provided username already exists",
                    ResultType.INVALID);
        }

        return result;
    }

    public Result<User> update(User user) {
        Credentials credentials = new Credentials(user.getUsername(), user.getPassword());

        Result<User> result = validate(credentials);

        if (user.getUserId() <= 0) {
            result.addMessage("Invalid user id, cannot update",
                    ResultType.INVALID);
        }

        if (!result.isSuccess()) {
            return result;
        }

        if (!repository.update(user)) {
            result.addMessage("User id not found, cannot update", ResultType.NOT_FOUND);
        } else {
            result.setPayload(user);
        }

        return result;
    }

    public Result<Void> deleteById(int userId) {
        Result<Void> result = new Result<>();
        boolean repoResult = repository.deleteById(userId);
        if (!repoResult) {
            result.addMessage("User id not found, cannot delete", ResultType.NOT_FOUND);
        }
        return result;
    }

    /* ***** ***** validations ***** ***** */

    private Result<User> validate(Credentials credentials) {
        Result<User> result = new Result<>();

        if (credentials == null) {
            result.addMessage("Credentials cannot be null", ResultType.INVALID);
            return result;
        }

        if (credentials.getUsername() == null || credentials.getUsername().isBlank()) {
            result.addMessage("Username is required", ResultType.INVALID);
        }

        if (credentials.getPassword() == null || credentials.getPassword().isBlank()) {
            result.addMessage("Password is required", ResultType.INVALID);
        }

        if (!result.isSuccess()) {
            return result;
        }

        if (credentials.getUsername().length() > 50) {
            result.addMessage("Username cannot be longer than 50 characters",
                    ResultType.INVALID);
        }

        if (!isValidPassword(credentials.getPassword())) {
            result.addMessage("Password must be at least 8 characters long " +
                    "and contain a digit, letter, and non-digit/non-letter",
                    ResultType.INVALID);
        }

        return result;
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        int digits = 0;
        int letters = 0;
        int others = 0;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                digits++;
            } else if (Character.isLetter(c)) {
                letters++;
            } else {
                others++;
            }
        }
        return digits > 0 && letters > 0 && others > 0;
    }
}
