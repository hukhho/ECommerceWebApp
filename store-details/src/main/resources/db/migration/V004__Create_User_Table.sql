CREATE TABLE tbl_User (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    fullName VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    roleID VARCHAR(50) NOT NULL,
    status BOOLEAN NOT NULL,
    isDelete BOOLEAN NOT NULL DEFAULT FALSE
);



-- Add more INSERT statements for other users
