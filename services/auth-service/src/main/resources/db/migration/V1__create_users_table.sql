create table users
(
    id            bigserial primary key,
    username      varchar(255) not null unique,
    password      varchar(100) not null,
    firstname     varchar(255) not null,
    lastname      varchar(255) not null,
    email         varchar(255) not null unique,
    role          varchar(50)  not null check (role in ('ADMIN', 'USER')),
    registered_at timestamptz  not null default now(),
    updated_at    timestamptz  not null default now()
);