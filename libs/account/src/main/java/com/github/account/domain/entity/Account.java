package com.github.account.domain.entity;

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
public class Account {

    private UUID id;
    private UUID customerId;
    private String branchCode;

    private String agency;
    private String number;
    private Integer digit;
    private AccountType type;

    private Date createdAt;
    private Date updatedAt;

}
