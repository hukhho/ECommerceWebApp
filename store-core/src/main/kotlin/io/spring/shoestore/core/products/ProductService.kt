package io.spring.shoestore.core.products

class ProductService(private val repository: ProductRepository) {

    // parse query

    fun get(id: ProductId): Product? = repository.findById(id)

    fun search(query: ProductLookupQuery): List<Product> {
        if (query.isEmpty()) {
            return repository.list()
        } else {
            val nameResults = repository.findByName(query.byName ?: "")
            // todo: price
            return nameResults
        }
    }
}





