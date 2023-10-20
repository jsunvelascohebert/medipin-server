package medipin.data;

import medipin.data.mappers.UserMapper;
import medipin.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Repository
public class UserJdbcTemplateRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public User getById(int userId) {
        List<String> roles = getRolesById(userId);

        final String sql = """
                select user_id, username, password_hash, enabled
                from `user`
                where user_id = ?;""";

        return jdbcTemplate.query(sql, new UserMapper(roles), userId)
                .stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public User getByUsername(String username) {
        List<String> roles = getRolesByUsername(username);

        final String sql = """
            select user_id, username, password_hash, enabled
            from `user`
            where username = ?;""";

        return jdbcTemplate.query(sql, new UserMapper(roles), username)
                .stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public User add(User user) {
        final String sql = """
            insert into user (username, password_hash)
            values (?, ?);""";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        user.setUserId(keyHolder.getKey().intValue());
        updateRoles(user);
        return user;
    }

    @Override
    @Transactional
    public boolean update(User user) {
        final String sql = """
            update user set
                username = ?,
                password_hash = ?,
                enabled = ?
            where user_id = ?;""";

        int rowsReturned = jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.getUserId());
        if (rowsReturned > 0) {
            updateRoles(user);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteById(int userId) {
        // delete from user_role
        jdbcTemplate.update("delete from user_role where user_id = ?;", userId);
        // delete from user_topic
        jdbcTemplate.update("delete from user_topic where user_id = ?;",
                userId);
        // delete from user_topic_article_note
        jdbcTemplate.update("delete from user_topic_article_note where " +
                "user_id = ?;", userId);
        // delete from user and check size
        return jdbcTemplate.update("delete from user where user_id = ?;",
                userId) > 0;
    }

    /* ***** ***** helpers ***** ***** */

    private List<String> getRolesById(int id) {
        final String sql = """
            select r.`role`
            from user_role ur
            inner join access_role r on ur.role_id = r.role_id
            inner join `user` u on ur.user_id = u.user_id
            where u.user_id = ?;""";
        return jdbcTemplate.query(sql, (rs, rowId) -> rs.getString("role"), id);
    }

    private List<String> getRolesByUsername(String username) {
        final String sql = """
            select r.`role`
            from user_role ur
            inner join access_role r on ur.role_id = r.role_id
            inner join `user` u on ur.user_id = u.user_id
            where u.username = ?;""";
        return jdbcTemplate.query(sql, (rs, rowId) -> rs.getString("role"),
                username);
    }

    private void updateRoles(User user) {
        jdbcTemplate.update("delete from user_role where user_id = ?;", user.getUserId());

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        if (authorities == null) {
            return;
        }

        for (GrantedAuthority role : authorities) {
            String sql = """
                insert into user_role (user_id, role_id)
                select ?, role_id from access_role where `role` = ?;""";
            jdbcTemplate.update(sql, user.getUserId(), role.getAuthority());
        }
    }
}
