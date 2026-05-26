CREATE TABLE expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    category_id BIGINT NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    receipt_url VARCHAR(500),
    location_lat DOUBLE,
    location_lng DOUBLE,
    conflict_version INT NOT NULL DEFAULT 1,
    client_id VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
