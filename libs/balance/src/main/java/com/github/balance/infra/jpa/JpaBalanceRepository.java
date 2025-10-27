package com.github.balance.infra.jpa;

import com.github.balance.domain.entity.Balance;
import com.github.balance.domain.entity.BalanceType;
import com.github.balance.domain.repository.BalanceRepository;
import com.github.balance.infra.jpa.dao.BalanceDAO;
import com.github.balance.infra.jpa.entity.BalanceEntity;
import com.github.balance.infra.jpa.mapper.BalanceMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class JpaBalanceRepository implements BalanceRepository {

    private final BalanceDAO balanceDAO;

    public JpaBalanceRepository(BalanceDAO balanceDAO) {
        this.balanceDAO = balanceDAO;
    }

    @Override
    public Balance checkBalance(UUID accountId) {
        var entity = balanceDAO.checkBalance(accountId);
        return BalanceMapper.toDomain(entity);
    }

    @Override
    public void initBalance(UUID accountId) {
        var entity = BalanceEntity.builder()
                .externalId(UUID.randomUUID())
                .accountId(accountId)
                .amount(BigDecimal.valueOf(0))
                .build();

        balanceDAO.save(entity);
    }

    @Override
    public void updateBalance(BalanceType balanceType, UUID accountId, Long amount) {
        var entity = balanceDAO.checkBalance(accountId);

        if (BalanceType.DEPOSIT.equals(balanceType) || BalanceType.TRANSFER_IN.equals(balanceType)) {
            entity.setAmount(entity.getAmount().add(BigDecimal.valueOf(amount)));
        }

        if (BalanceType.WITHDRAW.equals(balanceType) || BalanceType.TRANSFER_OUT.equals(balanceType)) {
            entity.setAmount(entity.getAmount().subtract(BigDecimal.valueOf(amount)));
        }

        balanceDAO.save(entity);
    }

}
