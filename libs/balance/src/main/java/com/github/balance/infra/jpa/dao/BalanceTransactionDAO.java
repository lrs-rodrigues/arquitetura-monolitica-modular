package com.github.balance.infra.jpa.dao;

import com.github.balance.infra.jpa.entity.BalanceTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceTransactionDAO extends JpaRepository<BalanceTransactionEntity, Long> {
}
