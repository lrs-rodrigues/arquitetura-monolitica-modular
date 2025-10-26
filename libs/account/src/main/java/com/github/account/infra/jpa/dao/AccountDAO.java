package com.github.account.infra.jpa.dao;

import com.github.account.domain.entity.Account;
import com.github.account.infra.jpa.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountDAO extends JpaRepository<AccountEntity, Long> {

    @Query(
        value = """ 
                    SELECT COUNT(*)
                    FROM account
                    WHERE account_agency = :agency AND account_number = :account AND account_digit = :digit
                """,
        nativeQuery = true
    )
    boolean existsAccount(@Param("agency") String agency, @Param("account") String account, @Param("digit") Integer digit);

    @Query(
            value = """ 
                    SELECT *
                    FROM account
                    WHERE external_id = :accountId
                """,
            nativeQuery = true
    )
    Account findByAccountId(@Param("accountId") UUID accountId);

    @Query(
            value = """ 
                    SELECT *
                    FROM account
                    WHERE customer_id = :customerId
                """,
            nativeQuery = true
    )
    List<Account> findByCustomerId(@Param("customerId") UUID customerId);

}
