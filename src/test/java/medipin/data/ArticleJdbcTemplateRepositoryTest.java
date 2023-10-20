package medipin.data;

import medipin.models.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ArticleJdbcTemplateRepositoryTest {

    @Autowired
    ArticleJdbcTemplateRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup(){
        knownGoodState.set();
    }

    /* ***** ***** getAll and getById tests ***** ***** */

    @Test
    void shouldGetAllArticles() {
        List<Article> articles = repository.getAll();
        assertNotNull(articles);
        assertTrue(articles.size() > 0);
    }

    @Test
    void shouldGetByValidId() {
        Article article = repository.getById(30574);
        assertNotNull(article);
        assertEquals(article.getTitle(),
                "Gestational Diabetes Screening: Questions for the Doctor");
        assertEquals(article.getImageUrl(),
                "https://health.gov/sites/default/files/2022-06/gdsqd.jpg");
        assertEquals(article.getImageAlt(),
                "Pregnant woman smiling.");
    }

    @Test
    void shouldNotGetByInvalidId() {
        Article article = repository.getById(100);
        assertNull(article);
    }

    /* ***** ***** add tests ***** ***** */

    @Test
    void shouldAddValidArticle() {
        Article article = new Article(30594,
                "Medicines to Prevent Heart Attack and Stroke: Questions for the Doctor",
                "https://health.gov/sites/default/files/2022-07/mtphaasqftd.jpg",
                "A health care provider shares information with an older woman.");

        Article result = repository.add(article);
        assertNotNull(result);
        assertTrue(result.getArticleId() > 0);
        assertEquals(30594, result.getArticleId());
    }

    /* ***** ***** update tests ***** ***** */

    @Test
    void shouldUpdateValidArticle() {
        Article startingArticle = new Article(30536,
                "Take Steps to Prevent Type 2 Diabetes",
                "https://health.gov/sites/default/files/2022-06/tstpt2d.jpg",
                "Two adults talking a walk.");
        Article verifyArticle = repository.getById(30536);
        assertEquals(verifyArticle, startingArticle);

        Article endingArticle = new Article(
                30536,
                "test title update",
                "test image url update",
                "test image alt update"
        );

        boolean result = repository.update(endingArticle);
        assertTrue(result);
        verifyArticle = repository.getById(30536);
        assertEquals(endingArticle, verifyArticle);
    }

    @Test
    void shouldNotUpdateMissingArticle() {
        Article article = new Article(100,
                "test missing article update (title)",
                "test missing article update (image url)",
                "test missing article update (image alt)");
        assertFalse(repository.update(article));
    }

    /* ***** ***** delete tests ***** ***** */

    @Test
    void shouldDeleteExistingAndNotAttached() {
        assertTrue(repository.deleteById(30591));
    }

    @Test
    void shouldNotDeleteMissingArticle() {
        assertFalse(repository.deleteById(1));
    }

    @Test
    void shouldBeAttachedToTopicArticle() {
        assertTrue(repository.isAttachedToTopicArticle(30574));
    }

    @Test
    void shouldBeAttachedToUTAN() {
        assertTrue(repository.isAttachedToUTAN(30574));
    }

    @Test
    void shouldNotBeAttachedToTopicArticle() {
        assertFalse(repository.isAttachedToTopicArticle(30617));
    }

    @Test
    void shouldNotBeAttachedToUTAN() {
        assertFalse(repository.isAttachedToUTAN(30591));
    }


}