package com.github.balance.domain.repository;

import com.github.balance.domain.entity.BalanceType;

import java.util.UUID;

public interface BalanceTransactionRepository {

    void insertTransaction(BalanceType balanceType,
                           UUID accountId,
                           Long amount);

}
