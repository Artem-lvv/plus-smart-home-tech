CREATE TABLE addresses
(
    id      SERIAL PRIMARY KEY,
    country VARCHAR(255) NOT NULL,
    city    VARCHAR(255) NOT NULL,
    street  VARCHAR(255) NOT NULL,
    house   VARCHAR(255) NOT NULL,
    flat    VARCHAR(255)
);

CREATE TABLE deliveries
(
    delivery_id              SERIAL PRIMARY KEY,
    delivery_state  VARCHAR(50) NOT NULL,
    from_address_id INTEGER     NOT NULL REFERENCES addresses (id),
    to_address_id   INTEGER     NOT NULL REFERENCES addresses (id),
    order_id        UUID        NOT NULL
);