package com.github.accountservice.application.query;

import com.github.account.domain.entity.Account;
import com.github.account.domain.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FindAccountsByCustomerIdQuery {

    private final AccountRepository accountRepository;

    public FindAccountsByCustomerIdQuery(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> handler(UUID customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

}
