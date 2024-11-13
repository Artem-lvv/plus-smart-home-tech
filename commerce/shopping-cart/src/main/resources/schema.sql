CREATE TABLE IF NOT EXISTS products (
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

-- Таблица корзин покупок
CREATE TABLE IF NOT EXISTS shopping_carts
(
    shopping_cart_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username         VARCHAR(255) NOT NULL,
    deactivated      boolean NOT NULL
);

-- Промежуточная таблица для связи "многие ко многим"
-- Таблица cart_products
CREATE TABLE IF NOT EXISTS cart_products
(
    shopping_cart_id UUID      NOT NULL REFERENCES shopping_carts(shopping_cart_id),
    product_id       UUID      NOT NULL REFERENCES products(product_id),
    quantity         INT       NOT NULL,
    added_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (shopping_cart_id, product_id)
,

    CONSTRAINT fk_shopping_cart
        FOREIGN KEY (shopping_cart_id)
            REFERENCES shopping_carts (shopping_cart_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_product
        FOREIGN KEY (product_id)
            REFERENCES products (product_id)
            ON DELETE CASCADE
);
