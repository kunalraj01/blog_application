package com.kunal.blog.application.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public Tag getTag() {
        return tag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

 
    public void setId(Long id) {
        this.id = id;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    @Override
    public String toString() {
        return "PostTag{" +
                "id=" + id +
                ", post=" + post +
                ", tag=" + tag +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
