package com.example.springproject.service;

import com.example.springproject.dto.ReactionRequest;
import com.example.springproject.dto.ReactionResponse;
import com.example.springproject.model.Comment;
import com.example.springproject.model.Post;
import com.example.springproject.model.Reaction;
import com.example.springproject.model.User;
import com.example.springproject.model.enums.ReactionType;
import com.example.springproject.repository.CommentRepository;
import com.example.springproject.repository.PostRepository;
import com.example.springproject.repository.ReactionRepository;
import com.example.springproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReactionService {

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void togglePostReaction(Long userId, Long postId, ReactionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<Reaction> existingReaction = reactionRepository.findByPostAndUser(post, user);

        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();
            if (reaction.getReactionType() == request.getReactionType()) {
                // Toggle off
                reactionRepository.delete(reaction);
            } else {
                // Change reaction type
                reaction.setReactionType(request.getReactionType());
                reactionRepository.save(reaction);
            }
        } else {
            // New reaction
            Reaction reaction = Reaction.builder()
                    .reactionType(request.getReactionType())
                    .post(post)
                    .user(user)
                    .build();
            reactionRepository.save(reaction);
        }
    }

    @Transactional
    public void toggleCommentReaction(Long userId, Long commentId, ReactionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Optional<Reaction> existingReaction = reactionRepository.findByCommentAndUser(comment, user);

        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();
            if (reaction.getReactionType() == request.getReactionType()) {
                // Toggle off
                reactionRepository.delete(reaction);
            } else {
                // Change reaction type
                reaction.setReactionType(request.getReactionType());
                reactionRepository.save(reaction);
            }
        } else {
            // New reaction
            Reaction reaction = Reaction.builder()
                    .reactionType(request.getReactionType())
                    .comment(comment)
                    .user(user)
                    .build();
            reactionRepository.save(reaction);
        }
    }

    public List<ReactionResponse> getPostReactions(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<Reaction> reactions = reactionRepository.findByPost(post);
        return aggregateReactions(reactions, currentUserId);
    }
    
    public List<ReactionResponse> getCommentReactions(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        List<Reaction> reactions = reactionRepository.findByComment(comment);
        return aggregateReactions(reactions, currentUserId);
    }

    private List<ReactionResponse> aggregateReactions(List<Reaction> reactions, Long currentUserId) {
        Map<ReactionType, Long> counts = reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getReactionType, Collectors.counting()));

        Map<ReactionType, Boolean> userReactedMap = reactions.stream()
                .filter(r -> r.getUser().getId().equals(currentUserId))
                .collect(Collectors.toMap(Reaction::getReactionType, r -> true, (a, b) -> a));

        return counts.entrySet().stream()
                .map(entry -> ReactionResponse.builder()
                        .reactionType(entry.getKey())
                        .count(entry.getValue())
                        .userReacted(userReactedMap.getOrDefault(entry.getKey(), false))
                        .build())
                .collect(Collectors.toList());
    }
}
