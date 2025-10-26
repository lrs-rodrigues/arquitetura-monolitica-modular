package com.github.account.domain.entity;

public record AccountNumber(
        String agency,
        String number,
        String digit
) { }
