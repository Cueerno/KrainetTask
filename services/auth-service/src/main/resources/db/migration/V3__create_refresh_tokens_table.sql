create table if not exists refresh_tokens
(
    id         bigserial primary key,
    token_hash varchar(64) not null,
    revoked    boolean     not null default false,
    jti        varchar(36) not null unique,
    created_at timestamptz not null default now(),
    expires_at timestamptz not null,

    user_id    bigint      not null references users (id)
);