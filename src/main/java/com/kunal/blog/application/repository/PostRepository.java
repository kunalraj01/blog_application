package com.kunal.blog.application.repository;

import com.kunal.blog.application.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


    @Repository
    public interface PostRepository extends JpaRepository<Post, Long> {

      @Query("SELECT DISTINCT p.author FROM Post p WHERE p.isPublished = true")

       // @Query("SELECT DISTINCT p.author FROM Post p")
        List<String> findAllAuthors();


        @Query("SELECT DISTINCT p FROM Post p " +
                "JOIN p.tags t " +
                "WHERE (t.name IN :tagNames) " + //only select post whose tags in tagnames
                "AND (p.author IN :authorNames) " +
                "AND ((:titleFilter IS NULL OR :titleFilter = '' OR p.title LIKE %:titleFilter%)" +
                "OR (:titleFilter IS NULL OR :titleFilter = '' OR p.content LIKE %:titleFilter%)" +
                "OR (:titleFilter IS NULL OR :titleFilter = '' OR p.excerpt LIKE %:titleFilter%)) " +
                "AND p.isPublished = true")
        Page<Post> getPosts(@Param("tagNames") List<String> tagNames,
                            @Param("authorNames") List<String> authorNames,
                            @Param("titleFilter") String titleFilter,
                            Pageable pageable);

        @Query("SELECT DISTINCT p FROM Post p " +
                "WHERE p.author = :author " +
                "AND p.isPublished = false")
        List<Post> findByIsPublishedFalse(@Param("author") String author);


        List<Post> findByIsPublishedFalse();
    }





