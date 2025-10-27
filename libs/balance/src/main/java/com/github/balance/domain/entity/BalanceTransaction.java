package com.github.balance.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceTransaction {

    private UUID id;
    private UUID accountId;
    private BalanceType type;
    private String description;
    private Long amount;
    private Date createdAt;

}
