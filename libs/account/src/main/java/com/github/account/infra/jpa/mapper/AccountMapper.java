package com.github.account.infra.jpa.mapper;

import com.github.account.domain.entity.Account;
import com.github.account.infra.jpa.entity.AccountEntity;

import java.time.ZoneId;
import java.util.Date;

public class AccountMapper {

    private AccountMapper() {}

    public static AccountEntity toEntity(Account domain) {
        return AccountEntity.builder()
                .externalId(domain.getId())
                .customerId(domain.getCustomerId())
                .branchCode(domain.getBranchCode())
                .accountAgency(domain.getAgency())
                .accountNumber(domain.getNumber())
                .accountDigit(domain.getDigit() != null ? domain.getDigit() : null)
                .accountType(domain.getType())
                .createdAt(domain.getCreatedAt() != null ?
                        domain.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null)
                .updatedAt(domain.getUpdatedAt() != null ?
                        domain.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null)
                .build();
    }

    public static Account toDomain(AccountEntity entity) {
        return Account.builder()
                .id(entity.getExternalId())
                .customerId(entity.getCustomerId())
                .branchCode(entity.getBranchCode())
                .agency(entity.getAccountAgency())
                .number(entity.getAccountNumber())
                .digit(entity.getAccountDigit() != null ? entity.getAccountDigit() : null)
                .type(entity.getAccountType())
                .createdAt(entity.getCreatedAt() != null ?
                        Date.from(entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()) : null)
                .updatedAt(entity.getUpdatedAt() != null ?
                        Date.from(entity.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()) : null)
                .build();
    }

}
