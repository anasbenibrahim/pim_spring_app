package com.example.springproject.repository;

import com.example.springproject.model.Post;
import com.example.springproject.model.enums.PostCategory;
import com.example.springproject.model.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findByCategoryAndDeletedFalseOrderByCreatedAtDesc(PostCategory category, Pageable pageable);
}
