package com.github.account.infra.jpa;

import com.github.account.domain.entity.Account;
import com.github.account.domain.repository.AccountRepository;
import com.github.account.infra.jpa.dao.AccountDAO;
import com.github.account.infra.jpa.entity.AccountEntity;
import com.github.account.infra.jpa.mapper.AccountMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JpaAccountRepository implements AccountRepository {

    private final AccountDAO accountDAO;

    public JpaAccountRepository(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public Boolean existsAccount(String agency, String account, String digit) {
        return accountDAO.existsAccount(agency, account, digit);
    }

    @Override
    public UUID createAccount(Account account) {
        AccountEntity entity = AccountMapper.toEntity(account);

        try {
            accountDAO.save(entity);
            return entity.getExternalId();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Erro ao cadastrar: " + e.getMessage());
        }
    }

    @Override
    public Account findByAccountId(UUID accountId) {
        var entity = accountDAO.findByAccountId(accountId);
        return AccountMapper.toDomain(entity);
    }

    @Override
    public List<Account> findByCustomerId(UUID customerId) {
        var entities = accountDAO.findByCustomerId(customerId);
        return entities.stream().map(AccountMapper::toDomain).toList();
    }

}
