CREATE TABLE products (
                          id  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                          name        TEXT NOT NULL,
                          description TEXT,
                          category    TEXT NOT NULL,
                          price       NUMERIC(10,2) NOT NULL CHECK (price >= 0),
                          stock_quantity  INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
                          created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);