package medipin.models;

import java.util.Objects;

public class UserTopic {

    private int userId;
    private int topicId;

    public UserTopic() {}

    public UserTopic(int userId, int topicId) {
        this.userId = userId;
        this.topicId = topicId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    @Override
    public String toString() {
        return "UserTopic{" +
                ", userId=" + userId +
                ", topicId=" + topicId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTopic userTopic = (UserTopic) o;
        return userId == userTopic.userId && topicId == userTopic.topicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, topicId);
    }
}
