package com.github.accountservice.application.usecase;

import com.github.account.domain.entity.AccountNumber;
import com.github.account.domain.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GeneratorAccountUseCase {

    private final AccountRepository accountRepository;

    public GeneratorAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountNumber execute() {
        AccountNumber accountNumber;
        do {
            accountNumber = buildAccountNumber();
        } while (accountRepository.existsAccount(accountNumber.agency(), accountNumber.number(), accountNumber.digit()));

        return accountNumber;
    }

    private AccountNumber buildAccountNumber() {
        Random random = new Random();

        String agency = getAgency(random);
        String account = getAccount(random);
        String digit = calculateDigit(account);

        return new AccountNumber(agency, account, digit);
    }

    private String getAgency(Random random) {
        StringBuilder agency = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            agency.append(random.nextInt(10));
        }
        agency.append("0");

        return agency.toString();
    }

    private String getAccount(Random random) {
        StringBuilder account = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            account.append(random.nextInt(10));
        }

        return account.toString();
    }

    private String calculateDigit(String account) {
        int[] pesos = {2, 1};
        int soma = 0;
        int pesoIndex = 0;

        for (int i = account.length() - 1; i >= 0; i--) {
            int num = Character.getNumericValue(account.charAt(i));
            int mult = num * pesos[pesoIndex];

            if (mult > 9) {
                mult = (mult / 10) + (mult % 10);
            }

            soma += mult;
            pesoIndex = (pesoIndex + 1) % 2;
        }

        int resto = soma % 10;

        return String.valueOf((10 - resto) % 10);
    }

}
