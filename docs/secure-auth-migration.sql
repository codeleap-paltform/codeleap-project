-- Run this once before using the upgraded authentication flow.
-- BCrypt hashes are 60 characters, so the password column must not be shorter than 60.
ALTER TABLE user MODIFY COLUMN password VARCHAR(100) NOT NULL;

-- Add this constraint only if the user table does not already have a unique username index.
ALTER TABLE user ADD CONSTRAINT uk_user_username UNIQUE (username);
