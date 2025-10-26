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

-- Transfer

CREATE TABLE transfer (
    id BIGSERIAL PRIMARY KEY,
    source_account_id BIGINT NOT NULL REFERENCES account(id),
    destination_account_id BIGINT REFERENCES account(id), -- pode ser NULL se for externa
    external_destination TEXT, -- dados do PIX externo (CPF/CNPJ/chave)
    amount NUMERIC(18,2) NOT NULL CHECK (amount > 0),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP
);

CREATE INDEX idx_transfer_source_account ON transfer (source_account_id);
CREATE INDEX idx_transfer_dest_account ON transfer (destination_account_id);
CREATE INDEX idx_transfer_status ON transfer (status);

