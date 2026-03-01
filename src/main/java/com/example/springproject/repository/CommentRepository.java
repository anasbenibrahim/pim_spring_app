package com.example.springproject.repository;

import com.example.springproject.model.Comment;
import com.example.springproject.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostAndParentCommentIsNullOrderByCreatedAtAsc(Post post, Pageable pageable);
    List<Comment> findByParentCommentOrderByCreatedAtAsc(Comment parentComment);
    long countByPostId(Long postId);
}
