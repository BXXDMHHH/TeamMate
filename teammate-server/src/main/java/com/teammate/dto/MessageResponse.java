package com.teammate.dto;

import com.teammate.entity.Message;

import java.time.Instant;

public record MessageResponse(
        Long id,
        Long userId,
        String username,
        String content,
        String type,
        Instant createdAt
) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getUser() == null ? null : message.getUser().getId(),
                message.getUser() == null ? "AI" : message.getUser().getUsername(),
                message.getContent(),
                message.getType().name(),
                message.getCreatedAt()
        );
    }
}
