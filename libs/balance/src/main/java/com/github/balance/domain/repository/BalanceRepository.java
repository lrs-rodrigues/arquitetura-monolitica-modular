package com.github.balance.domain.repository;

import com.github.balance.domain.entity.Balance;
import com.github.balance.domain.entity.BalanceType;

import java.util.UUID;

public interface BalanceRepository {

    Balance checkBalance(UUID accountId);
    void initBalance(UUID accountId);
    void updateBalance(BalanceType balanceType,
                       UUID accountId,
                       Long amount);

}
