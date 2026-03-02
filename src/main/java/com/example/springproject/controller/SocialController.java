package com.example.springproject.controller;

import com.example.springproject.dto.CommentRequest;
import com.example.springproject.dto.CommentResponse;
import com.example.springproject.dto.PostRequest;
import com.example.springproject.dto.PostResponse;
import com.example.springproject.dto.ReactionRequest;
import com.example.springproject.dto.ReactionResponse;
import com.example.springproject.model.User;
import com.example.springproject.model.enums.PostCategory;
import com.example.springproject.repository.UserRepository;
import com.example.springproject.service.CommentService;
import com.example.springproject.service.PostService;
import com.example.springproject.service.ReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/social")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SocialController {

    private final PostService postService;
    private final CommentService commentService;
    private final ReactionService reactionService;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    // --- POSTS ---

    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.createPost(getCurrentUserId(), request));
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> editPost(@PathVariable Long postId, @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.editPost(getCurrentUserId(), postId, request));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(getCurrentUserId(), postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponse>> getFeed(
            @RequestParam(required = false) PostCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.getFeed(category, pageable));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    // --- COMMENTS ---

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId, 
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(getCurrentUserId(), postId, request));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.getCommentsForPost(postId, pageable));
    }

    // --- REACTIONS ---

    @PostMapping("/posts/{postId}/reactions")
    public ResponseEntity<Void> togglePostReaction(
            @PathVariable Long postId, 
            @Valid @RequestBody ReactionRequest request) {
        reactionService.togglePostReaction(getCurrentUserId(), postId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comments/{commentId}/reactions")
    public ResponseEntity<Void> toggleCommentReaction(
            @PathVariable Long commentId, 
            @Valid @RequestBody ReactionRequest request) {
        reactionService.toggleCommentReaction(getCurrentUserId(), commentId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/{postId}/reactions")
    public ResponseEntity<List<ReactionResponse>> getPostReactions(@PathVariable Long postId) {
        return ResponseEntity.ok(reactionService.getPostReactions(postId, getCurrentUserId()));
    }

    @GetMapping("/comments/{commentId}/reactions")
    public ResponseEntity<List<ReactionResponse>> getCommentReactions(@PathVariable Long commentId) {
        return ResponseEntity.ok(reactionService.getCommentReactions(commentId, getCurrentUserId()));
    }
}
