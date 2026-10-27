CREATE TABLE project (
                         id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name        VARCHAR(100) NOT NULL,
                         description VARCHAR(500),
                         owner_id    BIGINT NOT NULL,
                         create_time DATETIME NOT NULL,
                         INDEX idx_owner_create (owner_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;