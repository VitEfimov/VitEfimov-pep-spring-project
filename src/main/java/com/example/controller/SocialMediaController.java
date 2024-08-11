package com.example.controller;

import java.util.*;
import javax.security.sasl.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
@RequestMapping
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    // ## 1: Our API should be able to process new User registrations.
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody Account account) throws AuthenticationException {
        try {
            accountService.login(account.getUsername(), account.getPassword());
            Account loggedInAccount = accountService.login(account.getUsername(), account.getPassword());
            return ResponseEntity.status(HttpStatus.OK).header("username", account.getUsername()).body(loggedInAccount);
        } catch (AuthenticationException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // ## 2: Our API should be able to process User logins.
    @PostMapping("register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        Account createdAccount = accountService.registerAccount(account);
        return ResponseEntity.status(HttpStatus.OK).body(createdAccount);
    }

    // ## 3: Our API should be able to process the creation of new messages.
    @PostMapping("messages")
    public @ResponseBody ResponseEntity<Message> createNewMessage(@RequestBody Message newMessage) {
        messageService.addMessage(newMessage);
        return ResponseEntity.status(HttpStatus.OK).body(newMessage);
    }

    // ## 4: Our API should be able to retrieve all messages.
    @GetMapping("messages")
    public @ResponseBody ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getMessages();
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    // ## 5: Our API should be able to retrieve a message by its ID.
    @GetMapping("messages/{messageId}")
    public ResponseEntity<Message> findMessageById(@PathVariable int messageId) {
        Message message = messageService.findMessageById(messageId);
        if (message == null) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    //## 6: Our API should be able to delete a message identified by a message ID.
    @DeleteMapping("messages/{messageId}")
    public @ResponseBody ResponseEntity<Integer> deleteMessageById(@PathVariable int messageId) {
        if (messageService.findMessageById(messageId) == null) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        messageService.deleteMessageById(messageId);
        return ResponseEntity.status(HttpStatus.OK).body(1);
    }

    //## 7: Our API should be able to update a message text identified by a message ID.
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> patchMessageById(
            @PathVariable int messageId,
            @RequestBody Map<String, String> requestBody) {
        String messageText = requestBody.get("messageText");
        messageService.patchMessage(messageId, messageText);
        return ResponseEntity.status(HttpStatus.OK).body(1);
    }

    //## 8: Our API should be able to retrieve all messages written by a particular user.
    @GetMapping("accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable int accountId) {
        List<Message> messages = messageService.getMessagesByAccountId(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
