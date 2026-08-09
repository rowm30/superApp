CREATE TABLE products(
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         price NUMERIC(12, 2) NOT NULL,
                         seller_id VARCHAR(64) NOT NULL,
                         created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_product_seller_id ON products (seller_id);