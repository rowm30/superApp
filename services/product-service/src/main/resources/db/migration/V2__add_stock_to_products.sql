-- Har product ka stock. Purane rows ko 100 de rahe hain.
-- ⚠️ NOT NULL column add karte waqt DEFAULT zaroori hai,
-- warna purane rows ke liye value hi nahi hogi aur migration fail hogi.
ALTER TABLE products ADD COLUMN stock INTEGER NOT NULL DEFAULT 100;

-- Stock kabhi negative na ho — database level pe pakka
ALTER TABLE products ADD CONSTRAINT chk_products_stock_non_negative CHECK (stock >= 0);