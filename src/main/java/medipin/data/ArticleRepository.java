package medipin.data;

import medipin.models.Article;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticleRepository {

    List<Article> getAll();

    @Transactional
    Article getById(int articleId);

    Article add(Article article);

    boolean update(Article article);

    @Transactional
    boolean deleteById(int articleId);

    @Transactional
    boolean isAttachedToTopicArticle(int articleId);

    @Transactional
    boolean isAttachedToUTAN(int articleId);

}
