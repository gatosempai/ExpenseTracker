CREATE TABLE sync_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    client_id VARCHAR(100) NOT NULL,
    operation_type VARCHAR(10) NOT NULL,
    entity_type VARCHAR(20) NOT NULL,
    entity_id BIGINT,
    client_entity_id VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    conflict_occurred BOOLEAN NOT NULL DEFAULT FALSE,
    resolution VARCHAR(10),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
