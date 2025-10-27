package com.github.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.github")
@EnableJpaRepositories(basePackages = {
   "com.github.account.infra.jpa.dao",
   "com.github.balance.infra.jpa.dao"
})
@EntityScan(basePackages = {
   "com.github.account.infra.jpa.entity",
   "com.github.balance.infra.jpa.entity",
})
public class AccountServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccountServiceApplication.class, args);
  }
}
