package com.teammate.controller;

import com.teammate.dto.MessageResponse;
import com.teammate.dto.SendMessageRequest;
import com.teammate.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<MessageResponse> getMessages(
            @PathVariable Long projectId,
            @RequestParam Long userId) {
        return messageService.getMessages(projectId, userId);
    }

    @PostMapping
    public MessageResponse sendMessage(
            @PathVariable Long projectId,
            @RequestBody SendMessageRequest request) {
        return messageService.sendMessage(projectId, request);
    }
}
