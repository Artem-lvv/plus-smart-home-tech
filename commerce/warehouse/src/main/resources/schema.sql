CREATE TABLE IF NOT EXISTS products
(
    product_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name     VARCHAR(255)     NOT NULL,
    description      TEXT             NOT NULL,
    image_src        VARCHAR(255),
    quantity_state   VARCHAR(50)      NOT NULL,
    product_state    VARCHAR(50)      NOT NULL,
    rating           DOUBLE PRECISION NOT NULL,
    product_category VARCHAR(50),
    price            NUMERIC(19, 2)   NOT NULL
);

CREATE TABLE IF NOT EXISTS dimensions
(
    dimension_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    width        DOUBLE PRECISION NOT NULL,
    height       DOUBLE PRECISION NOT NULL,
    depth        DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS product_in_warehouse
(
    product_in_warehouse_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id              UUID             NOT NULL REFERENCES products (product_id) ON DELETE CASCADE,
    fragile                 BOOLEAN          NOT NULL,
    dimension_id            UUID             NOT NULL REFERENCES dimensions (dimension_id) ON DELETE CASCADE,
    weight                  DOUBLE PRECISION NOT NULL,
    available_stock         INTEGER          NOT NULL
);

CREATE TABLE IF NOT EXISTS reserved_products
(
    reserved_products_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shopping_cart_id     UUID    NOT NULL,
    product_id           UUID    NOT NULL,
    reserved_quantity    INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS order_deliveries
(
    order_delivery_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id          UUID    NOT NULL,
    delivery_id       INTEGER NOT NULL
);