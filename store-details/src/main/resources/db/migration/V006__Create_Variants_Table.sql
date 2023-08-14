
CREATE TABLE tbl_Product_Variation (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     productID UUID,
                                     variationName VARCHAR(50),
                                     variationValue VARCHAR(50),
                                     quantityAvailable INT,
                                     isDelete BOOLEAN NOT NULL DEFAULT FALSE,
                                     FOREIGN KEY (productID) REFERENCES tbl_Product(id)
);
