## Arquitetura Monorepo - Monolito Modular


### Modulos

#### Account

##### **Contexto**
- Responsabilidade: Gerenciar dados das contas bancárias
- Tabelas: account

##### Tabela

```sql
-- Account  
  
CREATE TABLE account (  
    id BIGSERIAL PRIMARY KEY,  
    external_id UUID NOT NULL UNIQUE,  
    customer_id UUID NOT NULL,  
    branch_code VARCHAR(10) NOT NULL,  
  
    account_agency VARCHAR(20) NOT NULL,  
    account_number VARCHAR(20) NOT NULL,  
    account_digit CHAR(1) NOT NULL,  
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('CREDIT_ACCOUNT', 'DEBIT_ACCOUNT', 'SALARY_ACCOUNT')),  
  
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),  
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),  
  
    UNIQUE (account_agency, account_number, account_digit)  
);  
  
CREATE INDEX idx_account_external_id ON account (external_id);  
CREATE INDEX idx_account_customer_id ON account (customer_id);  
CREATE INDEX idx_account_agency ON account (account_agency, account_number, account_digit);
```


#### Balance

##### **Contexto**
- Responsabilidade: Controlar o saldo e o histórico de transações
- Tabelas: balance, balance_transaction

##### Tabela

```sql
-- Balance  
  
CREATE TABLE balance (  
    id BIGSERIAL PRIMARY KEY,  
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,  
    amount NUMERIC(18,2) NOT NULL DEFAULT 0,  
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),  
    CONSTRAINT uq_balance_account UNIQUE (account_id)  
);  
  
CREATE INDEX idx_balance_account_id ON balance (account_id);  
  
-- Balance Transaction  
  
CREATE TABLE balance_transaction (  
    id BIGSERIAL PRIMARY KEY,  
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,  
    type VARCHAR(20) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER_IN', 'TRANSFER_OUT')),  
    reference_id UUID, -- Ex: id da operação ou correlação  
    amount NUMERIC(18,2) NOT NULL CHECK (amount > 0),  
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),  
    description TEXT  
);  
  
CREATE INDEX idx_balance_transaction_account_id ON balance_transaction (account_id);  
CREATE INDEX idx_balance_transaction_type ON balance_transaction (type);  
CREATE INDEX idx_balance_transaction_created_at ON balance_transaction (created_at DESC);
```

##### Funcionalidades

###### Iniciar Saldo

Função que inicia o saldo para o cliente `balanceRepository.initBalance(accountId)`

###### Consultar Saldo

O saldo deve ser consultado na tabela `balance` usando o `account_id` do cliente

```sql 
SELECT * FROM balance WHERE balance.account_id = account_id
```

###### Consultar Transações de Saldo

As transações de saldo devem ser consultados na tabela `balance_transaction` usando o `account_id` do cliente

```sql 
SELECT *
FROM balance_transaction
WHERE account_id = account_id OR type = :type
ORDER BY created_at DESC;
```

###### Efetivar Transação de Saldo

Devemos verifica se a conta existe, caso não exista devemos retorna uma exception

```sql 
SELECT EXISTS(  
    SELECT 1
    FROM account 
	WHERE account_id = :account_id 
)
```

Devemos gerar uma transação de saldo `balance_transaction`com os tipos:

1. `DEPOSIT` e salvar o novo valor valor no `balance` do cliente

```sql
	INSERT INTO balance_transaction (account_id, type, amount, description)
	VALUES (:account_id, 'DEPOSIT', :amount, 'Depósito realizado');
	
	UPDATE balance
	SET amount = amount + :amount, updated_at = NOW()
	WHERE account_id = :account_id;
```

2. `WITHDRAW` e salvar o novo valor valor no `balance` do cliente

```sql
	INSERT INTO balance_transaction (account_id, type, amount, description)
	VALUES (:account_id, 'WITHDRAW', :amount, 'Saque realizado');

	UPDATE balance
	SET amount = amount - :amount, updated_at = NOW()
	WHERE account_id = :account_id;
```

3. para transferir :
    1. `TRANSFER_OUT` para quando estamos retirando dinheiro de nossa conta para outra conta
    2. `TRANSFER_IN` para quando estamos recebendo dinheiro de uma outra conta

```sql
-- Debita dinheiro que foi transferido para outra conta
	INSERT INTO balance_transaction (account_id, type, amount, description)
	VALUES (:source_id, 'TRANSFER_OUT', :amount, 'Transferência enviada');
	
	UPDATE balance
	SET amount = amount - :amount
	WHERE account_id = :source_id;
	
-- Credita dinheiro que foi transferido de outra conta
	INSERT INTO balance_transaction (account_id, type, amount, description)
	VALUES (:dest_id, 'TRANSFER_IN', :amount, 'Transferência recebida');
	
	UPDATE balance
	SET amount = amount + :amount
	WHERE account_id = :dest_id;
```

#### Transfer

##### **Contexto**
- Responsabilidade: Histórico de transações
- Tabelas: transfer

##### Tabela

```sql
CREATE TABLE transfer (
    id BIGSERIAL PRIMARY KEY,
    source_account_id BIGINT NOT NULL REFERENCES account(id),
    data JSON,
    amount NUMERIC(18,2) NOT NULL CHECK (amount > 0),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP
);

-- Índices
CREATE INDEX idx_transfer_source_account ON transfer (source_account_id);
CREATE INDEX idx_transfer_dest_account ON transfer (destination_account_id);
CREATE INDEX idx_transfer_status ON transfer (status);
```

##### Funcionalidades

###### Inicia a transferencia

```sql
INSERT INTO transfer (source_account_id, destination_account_id, external_destination, amount, status)
VALUES (:source_id, :dest_id, :external_dest, :amount, 'PENDING')
RETURNING id;
```

###### Efetiva a transferencia (Debitado ou Creditado)

```sql
INSERT INTO transfer (source_account_id, destination_account_id, external_destination, amount, status)
VALUES (:source_id, :dest_id, :external_dest, :amount, 'PENDING')
RETURNING id;
```

1. Para transferencias que debitam de nossa conta, devemos chamar o `BalanceTransfer.execute(BalanceType.TRANSFER_OUT, BalanceTransfer)
2. Para transferencias que creditam de nossa conta, devemos chamar o `BalanceTransfer.execute(BalanceType.TRANSFER_IN, BalanceTransfer)`
3. Para finalizar devemos grava um novo historico da transferencia como `SUCCESS`

---

### App

####  Account Service

Essa aplicação depende dos modulos `account` e `balance`
##### **Funcionalidades**

###### Cadastrar Conta

Endpoint: /account
Metodo: POST
Depende de: account, balance
Command: CreateAccountCommand
Command Handler: CreateAccountCommandHandler

Request
```json
{
	"customerId": "c0d69ba8-f418-46cc-8bff-67b0bc1039e4",
	"branchCode": "136",
	"type": "CREDIT_ACCOUNT"
}
```

Response
Status: 201
Payload:
```json
"2b43d838-c190-4628-9433-37f625226b48"
```

Regra de negocio
1. Gera um numero de conta bancaria em `generatorAccountUseCase.execute()`
2. Monta o `account` em `buildAccount(command, accountNumber)`
3. Salva a nova conta no banco de dados `accountRepository.createAccount(account)`
4. Criar um `balance` para a conta bancaria `balanceRepository.initBalance(accountId)`

###### Consultar Conta pelo ID da Conta

Endpoint: /account/{accountId}
Metodo: GET

Response
Status: 200
Payload:
```json
{
	"id": "2b43d838-c190-4628-9433-37f625226b48",
	"customerId": "c0d69ba8-f418-46cc-8bff-67b0bc1039e4",
	"branchCode": "136",
	"agency": "18780",
	"number": "85091",
	"digit": "7",
	"type": "CREDIT_ACCOUNT",
	"createdAt": "2025-10-26T16:33:50.078+00:00",
	"updatedAt": "2025-10-26T16:33:50.078+00:00"
}
```


###### Consultar Conta pelo ID do Cliente

Endpoint: /account/customer/{customerId}
Metodo: GET

Response
Status: 200
Payload:
```json
[
	{
		"id": "2b43d838-c190-4628-9433-37f625226b48",
		"customerId": "c0d69ba8-f418-46cc-8bff-67b0bc1039e4",
		"branchCode": "136",
		"agency": "18780",
		"number": "85091",
		"digit": "7",
		"type": "CREDIT_ACCOUNT",
		"createdAt": "2025-10-26T16:33:50.078+00:00",
		"updatedAt": "2025-10-26T16:33:50.078+00:00"
	}
]
```

#### Balance Service

Essa aplicação depende dos modulos `account` e `balance`
##### **Funcionalidades**

###### Consultar Saldo

Endpoint: /balance/{accountId}
Metodo: GET

**Response**
Status: 200
Payload:
```json
{
	"amount": "0",
	"updatedAt": "2025-10-26T16:33:50.078+00:00"
}
```


###### Consultar Transações de Saldo

Endpoint: /balance/{accountId}/transactions
Metodo: GET

**Response**
Status: 200
Payload:
```json
[
	{
	    "id": "3b43d838-c190-4628-9433-37f625226b48",
		"amount": "200",
		"type": "WITHDRAW",
		"createdAt": "2025-10-26T16:33:50.078+00:00",
		"description": "Saque realizado"
	},
	{
		"id": "4b43d838-c190-4628-9433-37f625226b48",
		"amount": "1000",
		"type": "DEPOSIT",
		"createdAt": "2025-10-26T16:33:50.078+00:00",
		"description": "Depósito realizado"
	}
]
```


###### Realizar Deposito

Endpoint: /balance/deposit
Metodo: POST

**Request**
```json
{
	"accountId": "2b43d838-c190-4628-9433-37f625226b48",
	"customerId": "c0d69ba8-f418-46cc-8bff-67b0bc1039e4",
	"amount": "200"
}
```

**Response**
Status: 200
Payload:
```json
{
	"message": "Depósito realizado",
	"transactionId": "4b43d838-c190-4628-9433-37f625226b48",
}
```


###### Realizar Saque

Endpoint: /balance/withdraw
Metodo: POST

**Request**
```json
{
	"accountId": "2b43d838-c190-4628-9433-37f625226b48",
	"customerId": "c0d69ba8-f418-46cc-8bff-67b0bc1039e4",
	"amount": "50"
}
```

**Response**
Status: 200
Payload:
```json
{
	"message": "Depósito realizado",
	"transactionId": "4b43d838-c190-4628-9433-37f625226b48",
}
```



#### Transfer Service

Essa aplicação depende dos modulos `account`, `balance` e `transfer`

##### **Funcionalidades**

###### Transferir dinheiro

Endpoint: /transfer
Metodo: POST

**Request**
```json
{	
	"origin": {
		"accountId": "2b43d838-c190-4628-9433-37f625226b48",
		"customerId": "c0d69ba8-f418-46cc-8bff-67b0bc1039e4",	
	},
	"destination": {
		"key": "119875374444"
	},
	"type": "PIX",
	"amount": "50"
}
```

**Response**
Status: 200
Payload:
```json
{
	"message": "Transferência enviada",
	"transactionId": "4b43d838-c190-4628-9433-37f625226b48",
}
```


**Regra de negocio**
1. Verificamos se a conta existe
2. Verificamos se o tipo de transferencia existe
3. Verificamos se a conta possui saldo
4. Inicia a transferencia com o status `PENDING`
5. Debita o saldo da conta
6. Finaliza a transferencia com o status `SUCCESS`


## NX Commands

**Iniciar o workspace do NX vazio**

npx create-nx-workspace@latest bank-monorepo --preset=empty .

**Instalamos o Jnxplus**

npm install --save-dev @jnxplus/nx-maven

**Iniciamos o workspace do jnxplus**

npx nx generate @jnxplus/nx-maven:init

**Criar modulos**

npx nx generate @jnxplus/nx-maven:library account --language=java --groupId=com.github

**Criar apps**

npx nx generate @jnxplus/nx-maven:application account-service --language=java --framework=spring-boot --groupId=com.github