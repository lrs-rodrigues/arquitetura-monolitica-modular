package com.github.accountservice.application.command.handler;

import com.github.account.domain.entity.Account;
import com.github.account.domain.entity.AccountNumber;
import com.github.account.domain.repository.AccountRepository;
import com.github.accountservice.application.command.CreateAccountCommand;
import com.github.accountservice.application.usecase.GeneratorAccountUseCase;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateAccountCommandHandler {

    private final GeneratorAccountUseCase generatorAccountUseCase;
    private final AccountRepository accountRepository;

    public CreateAccountCommandHandler(GeneratorAccountUseCase generatorAccountUseCase, AccountRepository accountRepository) {
        this.generatorAccountUseCase = generatorAccountUseCase;
        this.accountRepository = accountRepository;
    }

    public UUID handler(CreateAccountCommand command) {
        AccountNumber accountNumber = generatorAccountUseCase.execute();
        Account account = buildAccount(command, accountNumber);

        return accountRepository.createAccount(account);
    }

    private Account buildAccount(CreateAccountCommand command, AccountNumber accountNumber) {
        return Account.builder()
                .id(UUID.randomUUID())
                .customerId(command.customerId())
                .branchCode(command.branchCode())
                .type(command.type())
                .agency(accountNumber.agency())
                .number(accountNumber.number())
                .digit(accountNumber.digit())
                .build();
    }

}
