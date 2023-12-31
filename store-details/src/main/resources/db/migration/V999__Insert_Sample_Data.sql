-- insert into shoes(id, name, image_url, description, price_in_cents, price_currency) values
--      (gen_random_uuid(), 'Classic sandal', 'img/01','', 2500, 'USD'),
--      (gen_random_uuid(), 'Spring Sneaker', 'img/02', '', 9900, 'EUR'),
--      (gen_random_uuid(), 'Hung Sneaker', 'img/03', '', 9999, 'EUR')
-- ;


INSERT INTO tbl_User (id, username, email, fullName, password, roleID, status)
VALUES ('aac713fd-7dc7-4624-ad50-1ed573cb4c83', 'admin', 'admin@gmail.com', 'Toi la admin', '$2a$12$ABzL8ovPYmA11AGcWjm6M.87G0rkFJkiEWm28v4P5BROkHMaIVtcK', 'AD', true);

INSERT INTO tbl_User (id, username, email, fullName, password, roleID, status)
VALUES ('2fdddbd3-b76c-436f-98df-8a70833042ec', 'Hoadnt', 'Hoadnt@gmail.com', 'Hoa Doan', '$2a$12$ABzL8ovPYmA11AGcWjm6M.87G0rkFJkiEWm28v4P5BROkHMaIVtcK', 'US', true);


--     id uuid primary key,
--     name varchar(256) not null,
--     description text,
--     price_in_cents integer not null,
--     price_currency varchar(3) not null -- e.g. EUR, USD


-- Insert sample data into tblCategory
-- Insert sample data into tblCategory
INSERT INTO tbl_Category (id, name, isDelete)
VALUES
     ('16e79d32-581a-4396-8825-e56df8b36f28', 'Clothing', FALSE),
     ('7a96bd82-f8fe-4df9-9203-13c9ad1e2835', 'Shoes', FALSE);

-- Insert sample data into tblProduct
INSERT INTO tbl_Product (id, categoryID, productCode, name, description, imageURL, price, isDelete)
VALUES
     ('f4c90f31-d58b-4f96-ab8d-1ef7cd8bdb3f', '16e79d32-581a-4396-8825-e56df8b36f28', 'CLTH002', 'Jeans', 'Classic denim jeans for a timeless look.', 'https://img.freepik.com/premium-photo/blue-denim-jeans-pile-jeans-pants-top-view-variety-denim-jean-textiles-black-background_221542-3316.jpg?w=996', 499000, false),
     ('1cca00ae-83aa-4a2c-b400-178ab877abb6', '16e79d32-581a-4396-8825-e56df8b36f28', 'SHOE002', 'T-Shirt', 'Comfortable cotton T-shirt for everyday wear.', 'https://img.freepik.com/premium-photo/black-t-shirts-with-copy-space_53876-102012.jpg?w=996', 199000, false),
     ('4bf4a94d-db86-4a50-a0ac-53a5464b62de', '7a96bd82-f8fe-4df9-9203-13c9ad1e2835', 'SHOE001', 'Boots', 'Nice boots', 'https://img.freepik.com/free-photo/female-model-wearing-black-boots-studio-against-white-background_181624-61478.jpg?size=626&ext=jpg', 39900, false),
     ('ffd3889c-a496-4fa1-bb80-e3ed79ccedb6', '7a96bd82-f8fe-4df9-9203-13c9ad1e2835', 'CLTH003', 'Sneaker', 'Nike Sneaker', 'https://img.freepik.com/free-photo/white-high-top-sneakers-unisex-footwear-fashion_53876-106036.jpg?size=626&ext=jpg', 129000, false);

-- f4c90f31-d58b-4f96-ab8d-1ef7cd8bdb3f,16e79d32-581a-4396-8825-e56df8b36f28,CLTH002,Jeans,Classic denim jeans for a timeless look.,https://img.freepik.com/premium-photo/blue-denim-jeans-pile-jeans-pants-top-view-variety-denim-jean-textiles-black-background_221542-3316.jpg?w=996,49.99,false
-- 1cca00ae-83aa-4a2c-b400-178ab877abb6,16e79d32-581a-4396-8825-e56df8b36f28,SHOE002,T-Shirt,Comfortable cotton T-shirt for everyday wear.,https://img.freepik.com/premium-photo/black-t-shirts-with-copy-space_53876-102012.jpg?w=996,19.99,false
-- 4bf4a94d-db86-4a50-a0ac-53a5464b62de,7a96bd82-f8fe-4df9-9203-13c9ad1e2835,SHOE001,Boots,Nice boots,https://img.freepik.com/free-photo/female-model-wearing-black-boots-studio-against-white-background_181624-61478.jpg?size=626&ext=jpg,39.99,false
-- ffd3889c-a496-4fa1-bb80-e3ed79ccedb6,7a96bd82-f8fe-4df9-9203-13c9ad1e2835,CLTH003,Sneaker,Nike Sneaker,https://img.freepik.com/free-photo/white-high-top-sneakers-unisex-footwear-fashion_53876-106036.jpg?size=626&ext=jpg,129.99,false


-- Insert sample data into tblProductVariation

INSERT INTO tbl_Product_Variation (sku, productID, label, size, color, quantityAvailable, isDelete)
VALUES
     ('SKU007', 'ffd3889c-a496-4fa1-bb80-e3ed79ccedb6', 'Size N/A - Blue', 'N/A', 'Blue', 15, false),
     ('SKU008', '4bf4a94d-db86-4a50-a0ac-53a5464b62de', 'Size 38 - Red', 'VN 42', 'Red', 35, false),
     ('SKU004', '4bf4a94d-db86-4a50-a0ac-53a5464b62de', 'Size 37 - Black', 'VN 41', 'Black', 20, false),
     ('SKU001', '1cca00ae-83aa-4a2c-b400-178ab877abb6', 'Size 42 - White', 'VN 42', 'White', 48, false),
     ('SKU005', '1cca00ae-83aa-4a2c-b400-178ab877abb6', 'Size 41 - Blue', 'VN 41', 'Blue', 38, false),
     ('SKU003', 'ffd3889c-a496-4fa1-bb80-e3ed79ccedb6', 'Size N/A - Red', 'N/A', 'Red', 9, false),
     ('SKU002', 'f4c90f31-d58b-4f96-ab8d-1ef7cd8bdb3f', 'Size L - Red', 'L', 'Red', 24, false),
     ('SKU006', 'f4c90f31-d58b-4f96-ab8d-1ef7cd8bdb3f', 'Size L - Blue', 'L', 'Blue', 18, false);


-- Insert sample data into tbl_Order
-- Insert sample data into tbl_Order
INSERT INTO tbl_Order (id, userID, orderDate, status, receiverName, receiverPhone, note, shippingStreet, shippingWard, shippingDistrict, shippingProvince, productCost, taxAmount, shippingCost, totalAmount, shippingDate, carrier, trackingNumber)
VALUES
     ('49aea874-ce9e-4cb1-82d2-47bf08194251', '2fdddbd3-b76c-436f-98df-8a70833042ec', '2023-08-15 12:00:00', 'Pending', 'John Doe', '1234567890', 'Sample order note', '123 Main St', 'Ward A', 'District X', 'Province Y', 99.99, 9.99, 9.99, 119.97, '2023-08-20 10:00:00', 'Carrier A', 'TRK123');

-- Insert sample data into tbl_Order_Item
INSERT INTO tbl_Order_Item (id, orderID, sku, quantity, subtotal)
VALUES
     ('1bc4c539-9fbc-4f6d-bac4-8be56cafd04b', '49aea874-ce9e-4cb1-82d2-47bf08194251', 'SKU001', 2, 39.98),
     ('fe203f14-1cfb-4f70-9732-a9ecff893e9a', '49aea874-ce9e-4cb1-82d2-47bf08194251', 'SKU002', 1, 0.00);


-- Insert sample data into tbl_Address
INSERT INTO tbl_Address (userID, street, ward, district, province)
VALUES
     ('2fdddbd3-b76c-436f-98df-8a70833042ec', '123 Main St', 'Ward A', 'District X', 'Province Y'),
     ('aac713fd-7dc7-4624-ad50-1ed573cb4c83', '456 Elm St', 'Ward B', 'District Y', 'Province Z');

-- Insert sample data into tbl_Payment
INSERT INTO tbl_Payment (orderID, paymentDate, paymentMethod, totalAmount, paymentStatus)
VALUES
     ('49aea874-ce9e-4cb1-82d2-47bf08194251', '2023-08-15 14:00:00', 'MOMO', 119.97, 'Completed');
