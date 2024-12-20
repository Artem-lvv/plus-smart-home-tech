CREATE TABLE IF NOT EXISTS orders
(
    order_id         UUID PRIMARY KEY,
    shopping_cart_id UUID,
    payment_id       UUID NOT NULL,
    username         VARCHAR(255),
    delivery_id      UUID NOT NULL,
    state            VARCHAR(20) CHECK (state IN (
          'NEW', 'ON_PAYMENT', 'ON_DELIVERY', 'DONE', 'DELIVERED', 'ASSEMBLED',
          'PAID', 'COMPLETED', 'DELIVERY_FAILED', 'ASSEMBLY_FAILED',
          'PAYMENT_FAILED', 'PRODUCT_RETURNED', 'CANCELED'
        )),
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile          BOOLEAN,
    total_price      NUMERIC(10, 2),
    delivery_price   NUMERIC(10, 2),
    product_price    NUMERIC(10, 2)
);

CREATE TABLE IF NOT EXISTS order_products
(
    order_products_id UUID PRIMARY KEY,
    order_id          UUID,
    quantity          INT NOT NULL
);

CREATE TABLE IF NOT EXISTS address
(
    address_id      SERIAL PRIMARY KEY,
    country VARCHAR(255) NOT NULL,
    city    VARCHAR(255) NOT NULL,
    street  VARCHAR(255) NOT NULL,
    house   VARCHAR(50)  NOT NULL,
    flat    VARCHAR(50)
);
