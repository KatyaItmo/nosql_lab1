package com.fitness.nosql_lab1.services;

import com.fitness.nosql_lab1.dtos.MessageDto;
import com.fitness.nosql_lab1.objects.Message;
import com.fitness.nosql_lab1.objects.User;
import com.fitness.nosql_lab1.repository.MessageRepository;
import com.fitness.nosql_lab1.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final SessionService sessionService;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, SessionService sessionService, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.sessionService = sessionService;
        this.userRepository = userRepository;
    }

    public void addMessage(Message message) {
        messageRepository.save(message);
    }

    public void makeNewsletter(User user, String header, String message) {
        String messageId = UUID.randomUUID().toString();
        Message messageObject = new Message(
                messageId,
                user,
                header,
                message,
                false
        );
        addMessage(messageObject);
    }

    public void makeNewsletter(String role, String header, String message) {
        List<User> users = getUsers(role);
        users.forEach(user -> makeNewsletter(user, header, message));
    }

    public ResponseEntity<List<MessageDto>> getUserMessage(String sessionToken) throws Exception {
        if (!sessionService.checkUser(sessionToken, "user")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        User user = sessionService.getUser(sessionToken);

        List<MessageDto> messages = fromMessageListToDto(messageRepository.findByUser(user));
        return ResponseEntity.ok(messages);
    }

    public void changeStatus(String messageId) {
        messageRepository.findById(messageId).ifPresent(message -> message.setChecked(true));
    }

    private List<MessageDto> fromMessageListToDto(List<Message> messages) {
        return messages.stream()
                .map(message -> new MessageDto(
                        message.getId(),
                        message.getHeader(),
                        message.getMessage(),
                        message.isChecked()
                ))
                .toList();
    }

    private List<User> getUsers(String role) {
        return userRepository.findByRole(role);
    }
}
