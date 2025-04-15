-- users 테이블: 사용자 정보 저장
CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(60)  NOT NULL,
    profile_id UUID,
    CONSTRAINT fk_users_profile
        FOREIGN KEY (profile_id) REFERENCES binary_contents (id)
            ON DELETE SET NULL
);

-- binary_contents 테이블: 프로필 이미지, 첨부파일 등 이진 데이터 저장
CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ  NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes        BYTEA        NOT NULL
);

-- user_statuses 테이블: 사용자의 온라인 상태, 마지막 활동 시간
CREATE TABLE user_statuses
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMPTZ NOT NULL,
    updated_at     TIMESTAMPTZ NOT NULL,
    user_id        UUID        NOT NULL UNIQUE,
    last_active_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_user_statuses_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE
);

-- channels 테이블: 채널 정보 (채팅방 같은 역할)
CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

-- messages 테이블: 채팅 메시지 저장
CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    content    TEXT,
    channel_id UUID        NOT NULL,
    author_id  UUID,
    CONSTRAINT fk_messages_channel
        FOREIGN KEY (channel_id) REFERENCES channels (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_messages_author
        FOREIGN KEY (author_id) REFERENCES users (id)
            ON DELETE SET NULL
);

-- read_statuses 테이블: 유저가 어떤 채널에서 어떤 메시지까지 읽었는지 저장
CREATE TABLE read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ NOT NULL,
    user_id      UUID        NOT NULL,
    channel_id   UUID        NOT NULL,
    last_read_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_read_statuses_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_read_statuses_channel
        FOREIGN KEY (channel_id) REFERENCES channels (id)
            ON DELETE CASCADE,
    CONSTRAINT uk_read_statuses_user_channel UNIQUE (user_id, channel_id)
);

-- message_attachments 테이블: 메시지에 첨부된 파일 연결
CREATE TABLE message_attachments
(
    message_id    UUID NOT NULL,
    attachment_id UUID NOT NULL,
    PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_msg_attach_message
        FOREIGN KEY (message_id) REFERENCES messages (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_msg_attach_content
        FOREIGN KEY (attachment_id) REFERENCES binary_contents (id)
            ON DELETE CASCADE
);
