CREATE TABLE product
(
    id         BIGINT NOT NULL,
    url        VARCHAR(500) NULL,
    name       VARCHAR(255) NULL,
    image      VARCHAR(255) NULL,
    price DOUBLE NOT NULL,
    price_on_sale DOUBLE NOT NULL,
    is_on_sale BIT(1) NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

CREATE TABLE user
(
    id       BIGINT       NOT NULL,
    username VARCHAR(100) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE wishlist
(
    product_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL
);

ALTER TABLE wishlist
    ADD CONSTRAINT fk_wishlist_on_product FOREIGN KEY (product_id) REFERENCES product (id);

ALTER TABLE wishlist
    ADD CONSTRAINT fk_wishlist_on_user FOREIGN KEY (user_id) REFERENCES user (id);