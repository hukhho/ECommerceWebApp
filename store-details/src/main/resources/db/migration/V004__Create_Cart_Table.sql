CREATE TABLE tbl_Cart (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sessionID UUID NULL,
    userID UUID NULL,
    sku varchar(256) NOT NULL,
    quantity INT NOT NULL,
    isDelete BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (userID) REFERENCES tbl_User(id),
    FOREIGN KEY (sku) REFERENCES tbl_Product_Variation(sku),
    UNIQUE (sessionID, userID, sku)
);
