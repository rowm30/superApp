-- Ek order = ek khareed
CREATE TABLE orders (
                        id           BIGSERIAL PRIMARY KEY,

    -- Kisne khareeda. Passport ke X-Passport-Sub se aayega.
                        buyer_id     VARCHAR(64) NOT NULL,

    -- PLACED, PAID, SHIPPED, CANCELLED
    -- VARCHAR rakha hai, database ENUM nahi — kyunki ENUM badalna
    -- production mein table lock kar deta hai. Naya status add karna aasan rahe.
                        status       VARCHAR(32) NOT NULL,

    -- Poore order ka kul daam. Items se jod ke nikalte hain,
    -- par yahan bhi rakh rahe hain — kyunki order ek contract hai.
                        total_amount NUMERIC(12, 2) NOT NULL,

                        created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- "mere orders dikhao" tez ho jayega
CREATE INDEX idx_orders_buyer_id ON orders (buyer_id);


-- Order ke andar ke items
CREATE TABLE order_items (
                             id           BIGSERIAL PRIMARY KEY,

    -- Kis order ka item.
    -- ON DELETE CASCADE = order mita toh uske items bhi mit jayein.
    -- Ye foreign key CHAL SAKTI hai kyunki dono table EK hi database mein hain.
                             order_id     BIGINT NOT NULL REFERENCES orders (id) ON DELETE CASCADE,

    -- ⚠️ Ye sirf ek number hai, foreign key NAHI —
    -- products table doosre database mein hai. Ye seemaa jaan-boojh kar hai.
                             product_id   BIGINT NOT NULL,

    -- ── SNAPSHOT: khareed ke waqt ki tasveer ──
    -- Seller kal naam ya daam badal de, ye nahi badlega.
                             product_name VARCHAR(255) NOT NULL,
                             unit_price   NUMERIC(12, 2) NOT NULL,

                             quantity     INTEGER NOT NULL CHECK (quantity > 0)
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);