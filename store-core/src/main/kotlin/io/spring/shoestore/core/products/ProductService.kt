package io.spring.shoestore.core.products

import org.slf4j.LoggerFactory

class ProductService(private val repository: ProductRepository) {

    // parse query

    fun get(id: ProductId): Product? = repository.findById(id)

    fun search(query: ProductLookupQuery): List<Product> {
        if (query.isEmpty()) {
            return repository.list()
        } else {
            val keywordResults = repository.findByKeyword(query.byKeyword ?: "")
            //search price still not implement
            return keywordResults
        }
    }

    // Create a new product
    fun create(product: Product): Boolean {
        return try {
            repository.create(product)
        } catch (e: Exception) {
            log.info("create product: ${e.stackTrace}")
            false
        }
    }


    // Update an existing product
    fun update(product: Product): Boolean {
        return repository.update(product)
    }

    // Soft delete a product by its ID
    fun delete(id: ProductId): Boolean {
        return repository.delete(id)
    }

    // Restore a soft-deleted product by its ID
    fun restore(id: ProductId): Boolean {
        return repository.restore(id)
    }

    fun getAllCategories(): List<Category> {
        return repository.listAllCategories()
    }
    companion object {
        private val log = LoggerFactory.getLogger(ProductService::class.java)
    }
}





