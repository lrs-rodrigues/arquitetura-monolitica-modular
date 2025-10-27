package com.github.balance.domain.entity;

import lombok.Getter;

@Getter
public enum BalanceType {

    DEPOSIT("Depósito realizado"),
    WITHDRAW("Saque realizado"),
    TRANSFER_IN("Transferência recebida"),
    TRANSFER_OUT("Transferência enviada");

    private final String successMessage;

    BalanceType(String successMessage) {
        this.successMessage = successMessage;
    }

}
