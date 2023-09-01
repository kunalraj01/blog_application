package com.kunal.blog.application.repository;

import com.kunal.blog.application.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

}
