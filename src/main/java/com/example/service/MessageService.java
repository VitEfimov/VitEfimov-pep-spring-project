package com.example.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Message;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    
    private MessageRepository messageRepository;
    private AccountRepository accountRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    // Validate message by requirements
    private void messageValidation(String messageText) {
        if (messageText.isBlank() || messageText == null || messageText.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty or blank.");
        }
        if (messageText.length() >= 255) {
            throw new IllegalArgumentException("Message should be less than 255 characters.");
        }
    }

    // ## 3: Our API should be able to process the creation of new messages.
    public void addMessage(Message message) {
        if (!accountRepository.findById(message.getPostedBy()).isPresent()) {
            throw new IllegalArgumentException("User with id " + message.getPostedBy() + " does not exist.");
        }
        messageValidation(message.getMessageText());
        messageRepository.save(message);
    }

    // ## 4: Our API should be able to retrieve all messages.
    public List<Message> getMessages() {
        return messageRepository.findAll();
    }

    // ## 5: Our API should be able to retrieve a message by its ID.
    public Message findMessageById(int messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    // ## 6: Our API should be able to delete a message identified by a message ID.
    public void deleteMessageById(int messageId) {
        messageRepository.deleteById(messageId);
    }

    // ## 7: Our API should be able to update a message text identified by a message
    // ID.
    public void patchMessage(int messageId, String messageText) throws ResourceNotFoundException {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message with ID " + messageId + " not found"));
        messageValidation(messageText);
        message.setMessageText(messageText);
        messageRepository.save(message);
    }

    // ## 8: Our API should be able to retrieve all messages written by a particular
    // user.
    public List<Message> getMessagesByAccountId(int accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("User with ID " + accountId + " not found");
        }
        return messageRepository.findByPostedBy(accountId);
    }
}
