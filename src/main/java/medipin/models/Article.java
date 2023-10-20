package medipin.models;

import java.time.LocalDate;
import java.util.Objects;

public class Article {

    private int articleId;
    private String title;
    private String imageUrl;
    private String imageAlt;

    public Article() {}

    public Article(int articleId, String title, String imageUrl, String imageAlt) {
        this.articleId = articleId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.imageAlt = imageAlt;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageAlt() {
        return imageAlt;
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    @Override
    public String toString() {
        return "Article{" +
                "articleId=" + articleId +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageAlt='" + imageAlt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return articleId == article.articleId && Objects.equals(title, article.title) && Objects.equals(imageUrl, article.imageUrl) && Objects.equals(imageAlt, article.imageAlt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId, title, imageUrl, imageAlt);
    }
}
