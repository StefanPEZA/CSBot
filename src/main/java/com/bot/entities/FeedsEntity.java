package com.bot.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "feeds", schema = "main")
public class FeedsEntity {
    private String name;
    private String feedUrl;

    @Id
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "feed_url")
    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedsEntity that = (FeedsEntity) o;
        return Objects.equals(name, that.name) && Objects.equals(feedUrl, that.feedUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, feedUrl);
    }
}
