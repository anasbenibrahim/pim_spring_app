package com.example.springproject.service;

import com.example.springproject.dto.CommentRequest;
import com.example.springproject.dto.CommentResponse;
import com.example.springproject.model.Comment;
import com.example.springproject.model.Post;
import com.example.springproject.model.User;
import com.example.springproject.repository.CommentRepository;
import com.example.springproject.repository.PostRepository;
import com.example.springproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PseudonymService pseudonymService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public CommentResponse addComment(Long userId, Long postId, CommentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.isDeleted()) {
            throw new RuntimeException("Cannot comment on a deleted post");
        }

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));

            // Enforce max 2 levels of nesting
            if (parentComment.getParentComment() != null) {
                // The parent is already a reply. Set the parent to the top-level comment.
                parentComment = parentComment.getParentComment();
            }
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .pseudonym(pseudonymService.generatePseudonym())
                .post(post)
                .author(user)
                .parentComment(parentComment)
                .build();

        comment = commentRepository.save(comment);
        CommentResponse response = mapToResponse(comment);
        
        // Broadcast the new comment via WebSocket
        messagingTemplate.convertAndSend("/topic/posts/" + postId + "/comments", response);
        
        return response;
    }

    public Page<CommentResponse> getCommentsForPost(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Page<Comment> mainComments = commentRepository.findByPostAndParentCommentIsNullOrderByCreatedAtAsc(post, pageable);
        return mainComments.map(this::mapToResponseWithReplies);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .pseudonym(comment.getPseudonym())
                .postId(comment.getPost().getId())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private CommentResponse mapToResponseWithReplies(Comment comment) {
        CommentResponse response = mapToResponse(comment);
        List<Comment> repliesList = commentRepository.findByParentCommentOrderByCreatedAtAsc(comment);
        
        List<CommentResponse> replies = repliesList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        response.setReplies(replies);
        return response;
    }
}
