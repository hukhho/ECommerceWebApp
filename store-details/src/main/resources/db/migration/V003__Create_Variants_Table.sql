
    CREATE TABLE tbl_Product_Variation (
                                         sku varchar(256) primary key,
                                         productID UUID,
                                         label text not null,
                                         size VARCHAR(128),
                                         color VARCHAR(128),
                                         quantityAvailable INT,
                                         isDelete BOOLEAN NOT NULL DEFAULT FALSE,
                                         FOREIGN KEY (productID) REFERENCES tbl_Product(id)
    );
