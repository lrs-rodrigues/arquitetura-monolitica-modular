package com.github.accountservice.application.query;

import com.github.account.domain.entity.Account;
import com.github.account.domain.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FindAccountByIdQuery {

    private final AccountRepository accountRepository;

    public FindAccountByIdQuery(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account handler(UUID accountId) {
        return accountRepository.findByAccountId(accountId);
    }

}
