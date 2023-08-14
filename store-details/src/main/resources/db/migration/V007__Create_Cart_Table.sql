CREATE TABLE tbl_Cart (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sessionID UUID NULL,
    userID UUID NULL,
    productVariationID UUID NOT NULL,
    quantity INT NOT NULL,
    isDelete BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (userID) REFERENCES tbl_User(id),
    FOREIGN KEY (productVariationID) REFERENCES tbl_Product_Variation(id)
);
