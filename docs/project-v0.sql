CREATE TABLE project (
                         id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name        VARCHAR(100) NOT NULL,
                         description VARCHAR(500),
                         owner_id    BIGINT NOT NULL,
                         create_time DATETIME NOT NULL,
                         INDEX idx_owner_create (owner_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- 项目与普通成员的关系表。一行表示一个用户加入了一个项目。
CREATE TABLE project_member (
    -- 这条成员关系自己的编号，由 MySQL 自动生成。
                                id BIGINT NOT NULL AUTO_INCREMENT,
    -- 对应 project 表的 id，说明加入的是哪个项目。
                                project_id BIGINT NOT NULL,
    -- 对应 user 表的 id，说明是谁加入了项目。
                                user_id BIGINT NOT NULL,
    -- 加入时间由 Java 后端在添加成员时填入。
                                join_time DATETIME NOT NULL,

                                PRIMARY KEY (id),
    -- 同一个用户只能在同一个项目里有一条成员关系。
                                UNIQUE KEY uk_project_member_project_user (project_id, user_id),
    -- 按用户编号查“我加入了哪些项目”时，可以从 user_id 开始定位。
                                KEY idx_project_member_user_project (user_id, project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;