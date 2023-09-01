package com.kunal.blog.application.repository;

import com.kunal.blog.application.entity.Comment;
import com.kunal.blog.application.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
}
