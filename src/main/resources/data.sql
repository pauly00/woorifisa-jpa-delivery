CREATE DATABASE IF NOT EXISTS jpa_delivery_db;
USE jpa_delivery_db;

DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menu;
DROP TABLE IF EXISTS store;
DROP TABLE IF EXISTS member;

CREATE TABLE member (
    member_id   BIGINT       NOT NULL AUTO_INCREMENT,
    address     VARCHAR(255),
    username    VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(50)  NOT NULL,
    PRIMARY KEY (member_id)
);

CREATE TABLE store (
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    name    VARCHAR(100),
    address VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE menu (
    menu_id     BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100),
    price       INT          NOT NULL,
    store_id    BIGINT,
    PRIMARY KEY (menu_id),
    CONSTRAINT fk_menu_store FOREIGN KEY (store_id) REFERENCES store (id)
);

CREATE TABLE orders (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    order_date  DATETIME,
    status      VARCHAR(20),
    member_id   BIGINT,
    menu_id     BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_orders_member FOREIGN KEY (member_id) REFERENCES member (member_id),
    CONSTRAINT fk_orders_menu   FOREIGN KEY (menu_id)   REFERENCES menu (menu_id)
);
