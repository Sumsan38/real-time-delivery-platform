create table products (
    id bigint auto_increment primary key,
    name varchar(255) not null,
    price int not null,
    stock int not null,
    created_at timestamp,
    updated_at timestamp
);