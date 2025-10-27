package com.github.balance.infra.jpa.mapper;

import com.github.balance.domain.entity.BalanceTransaction;
import com.github.balance.infra.jpa.entity.BalanceTransactionEntity;

import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

public class BalanceTransactionMapper {

    private BalanceTransactionMapper() {}

    public static BalanceTransactionEntity toEntity(BalanceTransaction domain) {
        if (domain == null) return null;

        return BalanceTransactionEntity.builder()
                .accountId(domain.getAccountId())
                .type(domain.getType())
                .amount(domain.getAmount() != null ?
                        new java.math.BigDecimal(domain.getAmount()) : java.math.BigDecimal.ZERO)
                .createdAt(domain.getCreatedAt() != null ?
                        domain.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null)
                .description(domain.getDescription())
                .build();
    }

    public static BalanceTransaction toDomain(BalanceTransactionEntity entity, UUID accountExternalId) {
        if (entity == null) return null;

        return BalanceTransaction.builder()
                .id(entity.getExternalId())
                .accountId(accountExternalId)
                .type(entity.getType())
                .amount(entity.getAmount() != null ? entity.getAmount().longValue() : 0L)
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt() != null ?
                        Date.from(entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()) : null)
                .build();
    }
}
