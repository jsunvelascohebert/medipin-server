package medipin.domain;

import medipin.data.ArticleRepository;
import medipin.models.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ArticleServiceTest {

    @Autowired
    ArticleService service;

    @MockBean
    ArticleRepository repository;

    @Test
    void shouldFindAllArticles() {
        List<Article> articles = List.of(
            new Article(30574,
                    "Gestational Diabetes Screening: Questions for the Doctor",
                    "https://health.gov/sites/default/files/2022-06/gdsqd.jpg",
                    "Pregnant woman smiling."));

        when(repository.getAll()).thenReturn(articles);
        Result<List<Article>> result = service.getAll();
        assertTrue(result.isSuccess());
        assertEquals(result.getPayload(), articles);
    }

    @Test
    void shouldReturnMessageWhenNoArticlesFound() {
        when(repository.getAll()).thenReturn(null);
        Result<List<Article>> result = service.getAll();
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("No articles found"));

        when(repository.getAll()).thenReturn(new ArrayList<>());
        result = service.getAll();
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("No articles found"));
    }

    @Test
    void shouldFindByValidId() {
        Article article = new Article(30574,
                "Gestational Diabetes Screening: Questions for the Doctor",
                "https://health.gov/sites/default/files/2022-06/gdsqd.jpg",
                "Pregnant woman smiling.");
        when(repository.getById(1)).thenReturn(article);

        Result<Article> result = service.getById(1);
        assertTrue(result.isSuccess());
        assertEquals(result.getPayload(), article);
    }

    @Test
    void shouldNotFindByMissingId() {
        when(repository.getById(100)).thenReturn(null);
        Result<Article> result = service.getById(100);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Could not find article " +
                "with id 100"));
    }

    @Test
    void shouldValidateCorrectly() {
        Result<Article> result = service.add(null);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Article cannot be null"));

        Article invalid = new Article(0, null, null, null);
        result = service.add(invalid);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertEquals(result.getMessages().size(), 1);
        assertTrue(result.getMessages().contains("Article title cannot be " +
                "blank"));
    }

    @Test
    void shouldAddValidArticle() {
        Article article = new Article(30574,
                "Gestational Diabetes Screening: Questions for the Doctor",
                "https://health.gov/sites/default/files/2022-06/gdsqd.jpg",
                "Pregnant woman smiling.");
        Article expected = new Article(30574,
                "Gestational Diabetes Screening: Questions for the Doctor",
                "https://health.gov/sites/default/files/2022-06/gdsqd.jpg",
                "Pregnant woman smiling.");
        when(repository.add(article)).thenReturn(expected);

        Result<Article> result = service.add(article);
        result.getMessages().forEach(System.out::println);

        assertTrue(result.isSuccess());
        assertEquals(result.getPayload(), expected);
    }

    @Test
    void shouldUpdateValidArticle() {
        Article article = new Article(30574,
                "Gestational Diabetes Screening: Questions for the Doctor",
                "https://health.gov/sites/default/files/2022-06/gdsqd.jpg",
                "Pregnant woman smiling.");
        when(repository.update(article)).thenReturn(true);
        Result<Article> result = service.update(article);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotUpdateInvalidArticle() {
        // invalid id
        Article article = new Article(-1,
                "Gestational Diabetes Screening: Questions for the Doctor",
                "https://health.gov/sites/default/files/2022-06/gdsqd.jpg",
                "Pregnant woman smiling.");
        Result<Article> result = service.update(article);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Article id must be set for " +
                "updating"));

        // missing id
        article = new Article(1,
                "Gestational Diabetes Screening: Questions for the Doctor",
                "https://health.gov/sites/default/files/2022-06/gdsqd.jpg",
                "Pregnant woman smiling.");
        when(repository.update(article)).thenReturn(false);
        result = service.update(article);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Could not update missing " +
                "article with id 1"));
    }

    @Test
    void shouldDeleteValidId() {
        when(repository.deleteById(1)).thenReturn(true);
        Result<Article> result = service.deleteById(1);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(repository.deleteById(100)).thenReturn(false);
        Result<Article> result = service.deleteById(100);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Article id 100 not found, " +
                "cannot delete"));
    }

    @Test
    void shouldNotDeleteExistingAttachedToTopicArticleOrUTAN() {
        when(repository.isAttachedToUTAN(1)).thenReturn(true);
        when(repository.isAttachedToTopicArticle(1)).thenReturn(true);
        Result<Article> result = service.deleteById(1);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Cannot delete Article with " +
                "id 1 attached to either TopicArticle or " +
                "UserTopicArticleNote"));
    }
}