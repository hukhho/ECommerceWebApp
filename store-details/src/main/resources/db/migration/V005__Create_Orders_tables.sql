CREATE TABLE tbl_Order (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           userID UUID NULL,
                           orderDate TIMESTAMP NOT NULL,
                           status VARCHAR(50) NULL,
                           receiverName VARCHAR(255) NULL,
                           receiverPhone VARCHAR(11) NULL,
                           note VARCHAR(255) NULL,
                           shippingStreet VARCHAR(255) NULL,
                           shippingWard VARCHAR(255) NULL,
                           shippingDistrict VARCHAR(255) NULL,
                           shippingProvince VARCHAR(255) NULL,
                           productCost DECIMAL(10, 2) NOT NULL, -- Total cost of products before tax and shipping
                           taxAmount DECIMAL(10, 2) NOT NULL, -- Tax amount applied to the order
                           shippingCost DECIMAL(10, 2) NOT NULL, -- Cost of shipping
                           totalAmount DECIMAL(10, 2) NOT NULL, -- Total amount including product cost, tax, and shipping
                           shippingDate TIMESTAMP NULL,
                           carrier VARCHAR(100) NULL,
                           trackingNumber VARCHAR(100) NULL,
                           FOREIGN KEY (userID) REFERENCES tbl_User(id)
);


-- Create the Order_Item Table
CREATE TABLE tbl_Order_Item (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               orderID UUID NOT NULL,
                               sku varchar(256) NOT NULL,
                               quantity INT NOT NULL,
                               subtotal DECIMAL(10, 2) NOT NULL,
                               FOREIGN KEY (orderID) REFERENCES tbl_Order(id),
                               FOREIGN KEY (sku) REFERENCES tbl_Product_Variation(sku)
);
