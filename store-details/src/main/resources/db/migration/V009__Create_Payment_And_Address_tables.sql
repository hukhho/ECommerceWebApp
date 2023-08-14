-- Create the Address Table
CREATE TABLE tbl_Address (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            userID UUID NOT NULL,
                            street VARCHAR(255) NULL,
                            ward VARCHAR(255) NULL,
                            district VARCHAR(255) NULL,
                            province VARCHAR(255) NULL,
                            FOREIGN KEY (userID) REFERENCES tbl_User(id)
);

-- Create the Payment Table
CREATE TABLE tbl_Payment (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            orderID UUID NOT NULL,
                            paymentDate TIMESTAMP NULL,
                            paymentMethod VARCHAR(50) NULL,
                            totalAmount DECIMAL(10, 2) NOT NULL,
                            paymentStatus VARCHAR(50) NULL,
                            FOREIGN KEY (orderID) REFERENCES tbl_Order(id)
);