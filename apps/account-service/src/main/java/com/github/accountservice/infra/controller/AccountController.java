package com.github.accountservice.infra.controller;

import com.github.account.domain.entity.Account;
import com.github.accountservice.application.command.CreateAccountCommand;
import com.github.accountservice.application.command.handler.CreateAccountCommandHandler;
import com.github.accountservice.application.query.FindAccountByIdQuery;
import com.github.accountservice.application.query.FindAccountsByCustomerIdQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class AccountController {

    private final CreateAccountCommandHandler createAccountCommandHandler;
    private final FindAccountByIdQuery findAccountByIdQuery;
    private final FindAccountsByCustomerIdQuery findAccountsByCustomerIdQuery;

    public AccountController(CreateAccountCommandHandler createAccountCommandHandler, FindAccountByIdQuery findAccountByIdQuery, FindAccountsByCustomerIdQuery findAccountsByCustomerIdQuery) {
        this.createAccountCommandHandler = createAccountCommandHandler;
        this.findAccountByIdQuery = findAccountByIdQuery;
        this.findAccountsByCustomerIdQuery = findAccountsByCustomerIdQuery;
    }

    @PostMapping("/account")
    public ResponseEntity<UUID> createAccount(@RequestBody CreateAccountCommand createAccountCommand) {
        var response = createAccountCommandHandler.handler(createAccountCommand);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<Account> getByAccountId(@PathVariable("accountId") UUID accountId) {
        var response = findAccountByIdQuery.handler(accountId);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/account/customer/{customerId}")
    public ResponseEntity<List<Account>> getByCustomerId(@PathVariable("customerId") UUID customerId) {
        var response = findAccountsByCustomerIdQuery.handler(customerId);
        return ResponseEntity.status(200).body(response);
    }

}
