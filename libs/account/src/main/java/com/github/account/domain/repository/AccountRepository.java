package com.github.account.domain.repository;

import com.github.account.domain.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {

    Boolean existsAccount(String agency, String account, String digit);
    UUID createAccount(Account account);
    Account findByAccountId(UUID accountId);
    List<Account> findByCustomerId(UUID customerId);

}
