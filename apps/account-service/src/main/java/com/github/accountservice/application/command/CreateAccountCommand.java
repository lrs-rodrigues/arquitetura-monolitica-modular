package com.github.accountservice.application.command;

import com.github.account.domain.entity.AccountType;

import java.util.UUID;

public record CreateAccountCommand(UUID customerId,
                                   String branchCode,
                                   AccountType type) {
}
