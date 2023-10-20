package medipin.models;

import java.util.Objects;

public class TopicArticle {
    private int topicId;
    private int articleId;

    public TopicArticle() {}

    public TopicArticle(int topicId, int articleId) {
        this.topicId = topicId;
        this.articleId = articleId;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicArticle that = (TopicArticle) o;
        return topicId == that.topicId && articleId == that.articleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(topicId, articleId);
    }
}
