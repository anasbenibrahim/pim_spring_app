package com.example.springproject.repository;

import com.example.springproject.model.Comment;
import com.example.springproject.model.Post;
import com.example.springproject.model.Reaction;
import com.example.springproject.model.User;
import com.example.springproject.model.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByPostAndUserAndReactionType(Post post, User user, ReactionType reactionType);
    Optional<Reaction> findByCommentAndUserAndReactionType(Comment comment, User user, ReactionType reactionType);
    
    // Check if user already reacted to this post with ANY reaction (User can have only 1 reaction per content)
    Optional<Reaction> findByPostAndUser(Post post, User user);
    Optional<Reaction> findByCommentAndUser(Comment comment, User user);

    List<Reaction> findByPost(Post post);
    List<Reaction> findByComment(Comment comment);
}
