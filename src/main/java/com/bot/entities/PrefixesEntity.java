package com.bot.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "prefixes", schema = "main")
public class PrefixesEntity {
    private String id;
    private String prefix;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "prefix")
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrefixesEntity that = (PrefixesEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(prefix, that.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prefix);
    }
}
