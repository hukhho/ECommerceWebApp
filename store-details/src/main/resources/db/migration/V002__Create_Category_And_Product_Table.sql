CREATE TABLE tbl_Category (
                             id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
                             name VARCHAR(100),
                             isDelete BOOLEAN NOT NULL DEFAULT FALSE
);


CREATE TABLE tbl_Product (
                            id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
                            categoryID UUID,
                            productCode VARCHAR(50) UNIQUE,
                            name VARCHAR(255),
                            description TEXT,
                            imageURL VARCHAR(2000),
                            price DECIMAL(10, 2),
                            isDelete BOOLEAN NOT NULL DEFAULT FALSE,
                            FOREIGN KEY (categoryID) REFERENCES tbl_Category(id)
);
