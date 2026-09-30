package com.teammate.service;

import com.teammate.dto.MessageResponse;
import com.teammate.dto.SendMessageRequest;
import com.teammate.entity.Message;
import com.teammate.entity.MessageType;
import com.teammate.entity.Project;
import com.teammate.entity.User;
import com.teammate.repository.MessageRepository;
import com.teammate.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          ProjectService projectService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.projectService = projectService;
    }

    public List<MessageResponse> getMessages(Long projectId, Long userId) {
        projectService.getProjectForMember(projectId, userId);
        return messageRepository.findByProjectIdOrderByCreatedAtAsc(projectId)
                .stream()
                .map(MessageResponse::from)
                .toList();
    }

    public MessageResponse sendMessage(Long projectId, SendMessageRequest request) {
        if (request.userId() == null || request.content() == null || request.content().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId and content are required");
        }

        Project project = projectService.getProjectForMember(projectId, request.userId());
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Message message = new Message(project, user, request.content().trim(), MessageType.USER);
        return MessageResponse.from(messageRepository.save(message));
    }
}
