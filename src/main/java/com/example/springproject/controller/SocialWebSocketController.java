package com.example.springproject.controller;

import com.example.springproject.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SocialWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Whenever a new comment is added (by calling the REST API), the service or the
     * controller could broadcast it here, or clients can directly send comments via
     * WebSocket. For simplicity, if we use the REST API, the REST controller can
     * auto-broadcast the new comment. But if the client sends a STOMP message:
     */
    @MessageMapping("/social/posts/{postId}/comment")
    public void broadcastComment(@DestinationVariable Long postId, @Payload CommentResponse comment) {
        // Broadcasts to subscribers watching "/topic/posts/{postId}/comments"
        messagingTemplate.convertAndSend("/topic/posts/" + postId + "/comments", comment);
    }
}
