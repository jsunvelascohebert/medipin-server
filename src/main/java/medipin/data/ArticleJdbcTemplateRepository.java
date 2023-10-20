package medipin.data;

import medipin.data.mappers.ArticleMapper;
import medipin.models.Article;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ArticleJdbcTemplateRepository implements ArticleRepository {
    private final JdbcTemplate jdbcTemplate;

    public ArticleJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Article> getAll() {
        final String sql = """
                select article_id, title, image_url, image_alt
                from article;""";
        return jdbcTemplate.query(sql, new ArticleMapper());
    }

    @Override
    @Transactional
    public Article getById(int articleId) {
        final String sql = """
                select article_id, title, image_url, image_alt
                from article
                where article_id = ?;""";
        return jdbcTemplate.query(sql, new ArticleMapper(), articleId)
                .stream().findAny().orElse(null);
    }

    @Override
    public Article add(Article article) {
        final String sql = """
                insert into article
                	(article_id, title, image_url, image_alt)
                values
                    (?, ?, ?, ?);""";

        jdbcTemplate.update(sql,
                article.getArticleId(),
                article.getTitle(),
                article.getImageUrl(),
                article.getImageAlt());

        return article;
    }

    @Override
    public boolean update(Article article) {
        final String sql = """
                update article set
                	title = ?,
                    image_url = ?,
                    image_alt = ?
                where article_id = ?;
                """;

        return jdbcTemplate.update(sql,
                article.getTitle(),
                article.getImageUrl(),
                article.getImageAlt(),
                article.getArticleId()) > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(int articleId) {
        final String sql =
                "delete from article " +
                "where article_id = ?;";
        return jdbcTemplate.update(sql, articleId) > 0;
    }

    /* ***** ***** helpers ***** ***** */

    @Override
    @Transactional
    public boolean isAttachedToTopicArticle(int articleId) {
        final String sql = """
                select count(*)
                from topic_article
                where article_id = ?;""";

        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, articleId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean isAttachedToUTAN(int articleId) {
        final String sql = """
            select count(*)
            from user_topic_article_note
            where article_id = ?;""";

        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, articleId) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
