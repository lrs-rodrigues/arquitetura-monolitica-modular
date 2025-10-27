package com.github.balance.infra.jpa;

import com.github.balance.domain.entity.BalanceType;
import com.github.balance.domain.repository.BalanceTransactionRepository;
import com.github.balance.infra.jpa.dao.BalanceTransactionDAO;
import com.github.balance.infra.jpa.entity.BalanceTransactionEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class JpaBalanceTransactionRepository implements BalanceTransactionRepository {

    private final BalanceTransactionDAO balanceTransactionDAO;

    public JpaBalanceTransactionRepository(BalanceTransactionDAO balanceTransactionDAO) {
        this.balanceTransactionDAO = balanceTransactionDAO;
    }

    @Override
    public void insertTransaction(BalanceType balanceType, UUID accountId, Long amount) {
        var entity = BalanceTransactionEntity.builder()
                .externalId(UUID.randomUUID())
                .accountId(accountId)
                .type(balanceType)
                .amount(BigDecimal.valueOf(amount))
                .description(balanceType.getSuccessMessage())
                .build();

        balanceTransactionDAO.save(entity);
    }

}
