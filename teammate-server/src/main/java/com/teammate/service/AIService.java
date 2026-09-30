package com.teammate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teammate.entity.Message;
import com.teammate.entity.MessageType;
import com.teammate.entity.Project;
import com.teammate.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AIService {

    private final MessageRepository messageRepository;
    private final ProjectService projectService;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public AIService(MessageRepository messageRepository,
                     ProjectService projectService,
                     ObjectMapper objectMapper,
                     @Value("${teammate.ai.api-key:}") String apiKey,
                     @Value("${teammate.ai.model:gpt-5.5}") String model) {
        this.messageRepository = messageRepository;
        this.projectService = projectService;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .build();
    }

    public String summarize(Long projectId, Long userId) {
        String context = buildConversationContext(projectId, userId);
        return callModel(
                "You are TeamMate, an AI project assistant. Summarize the team's recent discussion in Chinese. " +
                        "Highlight key decisions, important discussion points, and explicit next steps. " +
                        "Do not invent information that is not present in the conversation.",
                context
        );
    }

    public String extractTasks(Long projectId, Long userId) {
        String context = buildConversationContext(projectId, userId);
        return callModel(
                "You are TeamMate, an AI project assistant. Extract actionable tasks from the team's recent discussion. " +
                        "For each task, include the task and the responsible person when the conversation makes that clear. " +
                        "If no responsible person is known, say '未明确'. Return a concise numbered list in Chinese. " +
                        "Do not invent tasks or owners.",
                context
        );
    }

    public String ask(Long projectId, Long userId, String question) {
        if (question == null || question.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "question is required");
        }

        String context = buildConversationContext(projectId, userId);
        return callModel(
                "You are TeamMate, an AI project assistant. Answer the user's question using only the project conversation provided. " +
                        "If the conversation does not contain enough information, clearly say that it is not known. " +
                        "Answer in Chinese and do not invent facts.",
                "项目聊天记录：\n" + context + "\n\n用户问题：\n" + question.trim()
        );
    }

    private String buildConversationContext(Long projectId, Long userId) {
        Project project = projectService.getProjectForMember(projectId, userId);
        List<Message> messages = messageRepository.findByProjectIdOrderByCreatedAtAsc(projectId);

        // V1 只把最近 50 条消息交给模型，避免上下文无限增长。
        if (messages.size() > 50) {
            messages = messages.subList(messages.size() - 50, messages.size());
        }

        return "项目：" + project.getName() + "\n" + messages.stream()
                .map(message -> {
                    String sender = message.getType() == MessageType.AI
                            ? "AI"
                            : (message.getUser() == null ? "未知用户" : message.getUser().getUsername());
                    return sender + ": " + message.getContent();
                })
                .collect(Collectors.joining("\n"));
    }

    private String callModel(String instructions, String input) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "OpenAI API key is not configured. Set OPENAI_API_KEY in the Codespace environment."
            );
        }

        try {
            Map<String, Object> body = Map.of(
                    "model", model,
                    "instructions", instructions,
                    "input", input
            );

            String responseBody = restClient.post()
                    .uri("/responses")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode outputText = root.get("output_text");
            if (outputText != null && outputText.isTextual()) {
                return outputText.asText();
            }

            StringBuilder fallback = new StringBuilder();
            JsonNode output = root.get("output");
            if (output != null && output.isArray()) {
                for (JsonNode item : output) {
                    JsonNode content = item.get("content");
                    if (content != null && content.isArray()) {
                        for (JsonNode contentItem : content) {
                            JsonNode text = contentItem.get("text");
                            if (text != null && text.isTextual()) {
                                if (fallback.length() > 0) fallback.append("\n");
                                fallback.append(text.asText());
                            }
                        }
                    }
                }
            }

            if (fallback.length() == 0) {
                throw new IllegalStateException("OpenAI returned no text output");
            }
            return fallback.toString();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "AI service request failed: " + e.getMessage(),
                    e
            );
        }
    }

    public Message saveAIMessage(Long projectId, Long userId, String content) {
        Project project = projectService.getProjectForMember(projectId, userId);
        return messageRepository.save(new Message(project, null, content, MessageType.AI));
    }
}
