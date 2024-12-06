CREATE TABLE IF NOT EXISTS payments
(
    payment_id       UUID PRIMARY KEY,
    shopping_cart_id UUID,
    order_id         UUID,
    payment_status   VARCHAR(20) CHECK (payment_status IN (
               'PENDING', 'SUCCESS', 'FAILED')),
    total_payment    NUMERIC(10, 2),
    delivery_total   NUMERIC(10, 2),
    fee_total        NUMERIC(10, 2)
);

