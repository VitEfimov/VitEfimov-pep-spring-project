package com.example.service;
import java.util.*;
import javax.security.sasl.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {
    
    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    //## 1: Our API should be able to process new User registrations.
    public Account registerAccount(Account newAccount) {
        if (newAccount.getUsername() == null || newAccount.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank.");
        }
        if (newAccount.getPassword() == null || newAccount.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        if (accountRepository.findByUsername(newAccount.getUsername()).isPresent()) {
            throw new IllegalStateException("An account with the username already exists.");
        }
        return accountRepository.save(newAccount);
    }

    //## 2: Our API should be able to process User logins.
    public Account login(String username, String password) throws AuthenticationException {
        return accountRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new IllegalStateException("Invalid username or password."));
    }
    
    public List<Account> getAccountList() {
        return accountRepository.findAll();
    }
}
