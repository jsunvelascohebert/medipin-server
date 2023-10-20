package medipin.data.mappers;

import medipin.models.Article;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Date;
import java.time.LocalDate;

public class ArticleMapper implements RowMapper<Article> {
    @Override
    public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
        Article article = new Article();
        article.setArticleId(rs.getInt("article_id"));
        article.setTitle(rs.getString("title"));
        article.setImageUrl(rs.getString("image_url"));
        article.setImageAlt(rs.getString("image_alt"));
        return article;
    }
}
