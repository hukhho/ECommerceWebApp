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
     ('7a96bd82-f8fe-4df9-9203-13c9ad1e2835', 'Pans', FALSE);

-- Insert sample data into tblProduct
INSERT INTO tbl_Product (id, categoryID, productCode, name, description, imageURL, price, isDelete)
VALUES
     ('1cca00ae-83aa-4a2c-b400-178ab877abb6', '16e79d32-581a-4396-8825-e56df8b36f28', 'CLTH001', 'T-Shirt', 'Â Ê Đ Comfortable cotton T-shirt for everyday wear.', 'https://example.com/tshirt.jpg', 19.99, FALSE),
     ('f4c90f31-d58b-4f96-ab8d-1ef7cd8bdb3f', '16e79d32-581a-4396-8825-e56df8b36f28', 'CLTH002', 'Jeans', 'Classic denim jeans for a timeless look.', 'https://example.com/jeans.jpg', 49.99, FALSE),
     ('ffd3889c-a496-4fa1-bb80-e3ed79ccedb6', '7a96bd82-f8fe-4df9-9203-13c9ad1e2835', 'PAN001', 'Non-Stick Cookware Set', 'Complete set of non-stick cookware for your kitchen.', 'https://example.com/cookware.jpg', 129.99, FALSE),
     ('4bf4a94d-db86-4a50-a0ac-53a5464b62de', '7a96bd82-f8fe-4df9-9203-13c9ad1e2835', 'PAN002', 'Frying Pan', 'Versatile frying pan for all your cooking needs.', 'https://example.com/fryingpan.jpg', 39.99, FALSE);



-- Insert sample data into tblProductVariation
INSERT INTO tbl_Product_Variation (id, productID, variationName, variationValue, quantityAvailable, isDelete)
VALUES
     ('49aea874-ce9e-4cb1-82d2-47bf08194251', '1cca00ae-83aa-4a2c-b400-178ab877abb6', 'Size', 'M', 50, FALSE),
     ('1bc4c539-9fbc-4f6d-bac4-8be56cafd04b', 'f4c90f31-d58b-4f96-ab8d-1ef7cd8bdb3f', 'Size', 'L', 30, FALSE),
     ('fe203f14-1cfb-4f70-9732-a9ecff893e9a', 'ffd3889c-a496-4fa1-bb80-e3ed79ccedb6', 'Size', 'N/A', 10, FALSE),
     ('c2471140-7a7b-4b93-ab91-676692406d32', '4bf4a94d-db86-4a50-a0ac-53a5464b62de', 'Size', 'S', 20, FALSE),
     ('8cc6ef5e-948c-4be1-bae5-5a872cced97b', '1cca00ae-83aa-4a2c-b400-178ab877abb6', 'Color', 'Red', 40, FALSE),
     ('9051c15e-1073-41b8-95fe-1f152ddc2e2c', 'f4c90f31-d58b-4f96-ab8d-1ef7cd8bdb3f', 'Color', 'Blue', 25, FALSE),
     ('99ab952d-a68c-4102-acb2-6a10cb38ddec', 'ffd3889c-a496-4fa1-bb80-e3ed79ccedb6', 'Color', 'Black', 15, FALSE),
     ('0868f223-3689-4288-89b6-6eed16503fe2', '4bf4a94d-db86-4a50-a0ac-53a5464b62de', 'Color', 'Green', 35, FALSE);



-- Insert sample data into tbl_Order
-- Insert sample data into tbl_Order
INSERT INTO tbl_Order (id, userID, orderDate, status, receiverName, receiverPhone, note, shippingStreet, shippingWard, shippingDistrict, shippingProvince, productCost, taxAmount, shippingCost, totalAmount, shippingDate, carrier, trackingNumber)
VALUES
     ('49aea874-ce9e-4cb1-82d2-47bf08194251', '2fdddbd3-b76c-436f-98df-8a70833042ec', '2023-08-15 12:00:00', 'Pending', 'John Doe', '1234567890', 'Sample order note', '123 Main St', 'Ward A', 'District X', 'Province Y', 99.99, 9.99, 9.99, 119.97, '2023-08-20 10:00:00', 'Carrier A', 'TRK123');

-- Insert sample data into tbl_Order_Item
INSERT INTO tbl_Order_Item (id, orderID, productVariationID, quantity, subtotal)
VALUES
     ('1bc4c539-9fbc-4f6d-bac4-8be56cafd04b', '49aea874-ce9e-4cb1-82d2-47bf08194251', '49aea874-ce9e-4cb1-82d2-47bf08194251', 2, 39.98),
     ('fe203f14-1cfb-4f70-9732-a9ecff893e9a', '49aea874-ce9e-4cb1-82d2-47bf08194251', 'fe203f14-1cfb-4f70-9732-a9ecff893e9a', 1, 0.00);
