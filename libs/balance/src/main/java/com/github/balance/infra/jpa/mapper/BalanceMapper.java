package com.github.balance.infra.jpa.mapper;


import com.github.balance.domain.entity.Balance;
import com.github.balance.infra.jpa.entity.BalanceEntity;

import java.time.ZoneId;
import java.util.Date;

public class BalanceMapper {

    private BalanceMapper() {}

    public static BalanceEntity toEntity(Balance domain) {
        if (domain == null) return null;

        return BalanceEntity.builder()
                .accountId(domain.getAccountId())
                .amount(domain.getAmount() != null ?
                        new java.math.BigDecimal(domain.getAmount()) : java.math.BigDecimal.ZERO)
                .updatedAt(domain.getUpdatedAt() != null ?
                        domain.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null)
                .build();
    }

    public static Balance toDomain(BalanceEntity entity) {
        if (entity == null) return null;

        return Balance.builder()
                .id(entity.getExternalId())
                .accountId(entity.getAccountId())
                .amount(entity.getAmount() != null ? entity.getAmount().longValue() : 0L)
                .updatedAt(entity.getUpdatedAt() != null ?
                        Date.from(entity.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()) : null)
                .build();
    }
}
