package io.spring.shoestore.postgres

import io.spring.shoestore.core.products.Category
import io.spring.shoestore.core.products.Product
import io.spring.shoestore.core.products.ProductId
import io.spring.shoestore.core.products.ProductRepository
import io.spring.shoestore.postgres.mappers.CategoryMapper
import io.spring.shoestore.postgres.mappers.ProductMapper
import org.springframework.jdbc.core.JdbcTemplate
import java.math.BigDecimal

class PostgresProductRepository(private val jdbcTemplate: JdbcTemplate) : ProductRepository {

    private val productMapper = ProductMapper()

    override fun create(product: Product): Boolean {
        val affectedRows = jdbcTemplate.update(
            "INSERT INTO tbl_Product (id, categoryID, productCode, name, description, imageURL, price) VALUES (?, ?, ?, ?, ?, ?, ?);",
            product.id?.value,
            product.categoryID.value,
            product.productCode,
            product.name,
            product.description,
            product.imageURL,
            product.price
        )
        return affectedRows > 0
    }

    override fun findById(id: ProductId): Product? {
        return jdbcTemplate.queryForObject(
            "SELECT p.*, c.name AS categoryName FROM tbl_Product p " +
                    "INNER JOIN tbl_Category c ON p.categoryID = c.id " +
                    "WHERE p.id = ? AND p.isDelete = FALSE LIMIT 1;",
            productMapper,
            id.value
        )
    }

    override fun list(): List<Product> {
        return jdbcTemplate.query(
            "SELECT p.*, c.name AS categoryName FROM tbl_Product p " +
                    "INNER JOIN tbl_Category c ON p.categoryID = c.id " +
                    "WHERE p.isDelete = FALSE;",
            productMapper
        )
    }

    override fun update(product: Product): Boolean {
        val affectedRows = jdbcTemplate.update(
            "UPDATE tbl_Product " +
                    "SET categoryID = ?, " +
                    "productCode = ?, " +
                    "name = ?, " +
                    "description = ?, " +
                    "imageURL = ?, " +
                    "price = ? " +
                    " WHERE id = ? " +
                    " AND isDelete = FALSE;",
            product.categoryID.value,
            product.productCode,
            product.name,
            product.description,
            product.imageURL,
            product.price,
            product.id?.value
        )
        return affectedRows > 0
    }

    override fun delete(id: ProductId): Boolean {
        val affectedRows = jdbcTemplate.update(
            "UPDATE tbl_Product SET isDelete = TRUE WHERE id = ?;",
            id.value
        )
        return affectedRows > 0
    }

    override fun restore(id: ProductId): Boolean {
        val affectedRows = jdbcTemplate.update(
            "UPDATE tbl_Product SET isDelete = FALSE WHERE id = ?;",
            id.value
        )
        return affectedRows > 0
    }

    override fun listAllCategories(): List<Category> {
        val categoryMapper = CategoryMapper()
        return jdbcTemplate.query(
            "SELECT id, name FROM tbl_Category;",
            categoryMapper
        )
    }

    override fun findByKeyword(keywordPartial: String): List<Product> {
        return jdbcTemplate.query(
            "SELECT p.*, c.name AS categoryName " +
                    "FROM tbl_Product p " +
                    "INNER JOIN tbl_Category c ON p.categoryID = c.id " +
                    "WHERE " +
                    "    (p.name ILIKE ? " +
                    "    OR p.description ILIKE ? " +
                    "    OR c.name ILIKE ? \n" +
                    "    OR p.productCode ILIKE ?)" +
                    "AND p.isDelete = FALSE;",
            productMapper,
            "%$keywordPartial%",
            "%$keywordPartial%",
            "%$keywordPartial%",
            "%$keywordPartial%"
        )
    }

    override fun findByPriceUnder(upperPrice: BigDecimal): List<Product> {
        return jdbcTemplate.query(
            "SELECT p.*, c.name AS categoryName FROM tbl_Product p " +
                    "INNER JOIN tbl_Category c ON p.categoryID = c.id " +
                    "WHERE p.price <= ? AND p.isDelete = FALSE;",
            productMapper,
            upperPrice
        )
    }
}
