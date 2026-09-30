package com.teammate.controller;

import com.teammate.dto.AIAskRequest;
import com.teammate.dto.MessageResponse;
import com.teammate.entity.Message;
import com.teammate.service.AIService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/summarize")
    public MessageResponse summarize(
            @PathVariable Long projectId,
            @RequestParam Long userId) {
        String answer = aiService.summarize(projectId, userId);
        Message message = aiService.saveAIMessage(projectId, userId, answer);
        return MessageResponse.from(message);
    }

    @PostMapping("/tasks")
    public MessageResponse tasks(
            @PathVariable Long projectId,
            @RequestParam Long userId) {
        String answer = aiService.extractTasks(projectId, userId);
        Message message = aiService.saveAIMessage(projectId, userId, answer);
        return MessageResponse.from(message);
    }

    @PostMapping("/ask")
    public MessageResponse ask(
            @PathVariable Long projectId,
            @RequestParam Long userId,
            @RequestBody AIAskRequest request) {
        String answer = aiService.ask(projectId, userId, request.question());
        Message message = aiService.saveAIMessage(projectId, userId, answer);
        return MessageResponse.from(message);
    }
}
