CREATE TABLE IF NOT EXISTS payments
(
    id               UUID PRIMARY KEY,
    shopping_cart_id UUID,
    total_payment    NUMERIC(10, 2),
    delivery_total   NUMERIC(10, 2),
    fee_total        NUMERIC(10, 2)
);

