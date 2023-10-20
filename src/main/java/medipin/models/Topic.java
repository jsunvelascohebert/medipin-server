package medipin.models;

import java.util.Objects;

public class Topic {
    private int topicId;
    private String name;

    public Topic() {}

    public Topic(int topicId, String name) {
        this.topicId = topicId;
        this.name = name;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "topicId=" + topicId +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return topicId == topic.topicId && Objects.equals(name, topic.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topicId, name);
    }
}
