package com.example.springproject.service;

import com.example.springproject.dto.PostRequest;
import com.example.springproject.dto.PostResponse;
import com.example.springproject.model.Post;
import com.example.springproject.model.User;
import com.example.springproject.model.enums.PostCategory;
import com.example.springproject.model.enums.PostStatus;
import com.example.springproject.repository.PostRepository;
import com.example.springproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PseudonymService pseudonymService;

    @Transactional
    public PostResponse createPost(Long userId, PostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = Post.builder()
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .category(request.getCategory())
                .moodEmoji(request.getMoodEmoji())
                .pseudonym(pseudonymService.generatePseudonym())
                .status(PostStatus.APPROVED) // Auto-approve for now so they show in feed
                .author(user)
                .deleted(false)
                .build();

        post = postRepository.save(post);
        return mapToResponse(post);
    }

    @Transactional
    public PostResponse editPost(Long userId, Long postId, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to edit this post");
        }

        if (post.isDeleted()) {
            throw new RuntimeException("Cannot edit a deleted post");
        }

        // 5-minute edit rule
        if (LocalDateTime.now().isAfter(post.getCreatedAt().plusMinutes(5))) {
            throw new RuntimeException("Edit time window (5 minutes) has expired");
        }

        post.setContent(request.getContent());
        post.setMediaUrl(request.getMediaUrl());
        post.setCategory(request.getCategory());
        post.setMoodEmoji(request.getMoodEmoji());
        post.setStatus(PostStatus.PENDING); // Re-flag for moderation

        post = postRepository.save(post);
        return mapToResponse(post);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this post");
        }

        post.setDeleted(true);
        postRepository.save(post); // Soft delete
    }

    public Page<PostResponse> getFeed(PostCategory category, Pageable pageable) {
        Page<Post> posts;
        if (category != null) {
            posts = postRepository.findByCategoryAndDeletedFalseOrderByCreatedAtDesc(category, pageable);
        } else {
            posts = postRepository.findByDeletedFalseOrderByCreatedAtDesc(pageable);
        }
        return posts.map(this::mapToResponse);
    }

    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (post.isDeleted()) {
            throw new RuntimeException("Post has been deleted");
        }
        return mapToResponse(post);
    }

    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .mediaUrl(post.getMediaUrl())
                .category(post.getCategory())
                .moodEmoji(post.getMoodEmoji())
                .pseudonym(post.getPseudonym())
                .status(post.getStatus())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .authorRole(post.getAuthor().getRole())
                .build();
    }
}
