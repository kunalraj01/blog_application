package com.kunal.blog.application.repository;

import com.kunal.blog.application.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT DISTINCT t.name FROM Tag t")
    List<String> findAllTagNames();
}

