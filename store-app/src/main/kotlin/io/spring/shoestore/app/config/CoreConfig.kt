package io.spring.shoestore.app.config


import PostgresCartRepository
import PostgresUserRepository
import io.spring.shoestore.core.cart.CartService
import io.spring.shoestore.core.orders.OrderAdminService
import io.spring.shoestore.core.orders.OrderProcessingService
import io.spring.shoestore.core.orders.OrderQueryService
import io.spring.shoestore.core.orders.OrderRepository
import io.spring.shoestore.core.products.ProductService
import io.spring.shoestore.core.security.PrincipalUser
import io.spring.shoestore.core.security.StoreAuthProvider
import io.spring.shoestore.core.users.UserRepository
import io.spring.shoestore.core.users.UserService
import io.spring.shoestore.core.variants.ProductVariantService
import io.spring.shoestore.postgres.PostgresOrderRepository
import io.spring.shoestore.postgres.PostgresProductRepository
import io.spring.shoestore.postgres.PostgresProductVariantRepository
import io.spring.shoestore.redis.RedisInventoryWarehousingRepository
import io.spring.shoestore.security.FakeStoreAuthProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import redis.clients.jedis.Jedis

@Configuration
class CoreConfig {
//    @Bean
//    fun validator(): LocalValidatorFactoryBean {
//        return LocalValidatorFactoryBean()
//    }

//    @Bean
//    fun getShoeService(jdbcTemplate: JdbcTemplate): ShoeService {
//        // an example of 'hiding' the details implementation, only the shoeservice can be grabbed via DI
//        return ShoeService(PostgresShoeRepository(jdbcTemplate))
//    }
    @Bean
    fun getProductService(jdbcTemplate: JdbcTemplate): ProductService {
        // an example of 'hiding' the details implementation, only the shoeservice can be grabbed via DI
        return ProductService(PostgresProductRepository(jdbcTemplate))
    }
    @Bean
    fun getUserService(jdbcTemplate: JdbcTemplate): UserService {
        // an example of 'hiding' the details implementation, only the shoeservice can be grabbed via DI
        return UserService(PostgresUserRepository(jdbcTemplate))
    }

    @Bean
    fun getCartService(jdbcTemplate: JdbcTemplate): CartService {
        // an example of 'hiding' the details implementation, only the shoeservice can be grabbed via DI
        return CartService(PostgresCartRepository(jdbcTemplate))
    }

    @Bean
    fun getUserRepository(jdbcTemplate: JdbcTemplate): UserRepository {
        return PostgresUserRepository(jdbcTemplate)
    }
    @Bean
    fun getProductVariantService(jdbcTemplate: JdbcTemplate, jedis: Jedis): ProductVariantService {
        return ProductVariantService(PostgresProductVariantRepository(jdbcTemplate))
    }

//    @Bean
//    // Kotlin-ified!
//    fun getInventoryManagementService(jdbcTemplate: JdbcTemplate, jedis: Jedis) = InventoryManagementService(
//            PostgresProductVariantRepository(jdbcTemplate),
//            RedisInventoryWarehousingRepository(jedis)
//    )

    @Bean
    fun getOrderRepository(jdbcTemplate: JdbcTemplate
                           //, dynamoDbClient: DynamoDbClient
    ): OrderRepository {
        return PostgresOrderRepository(jdbcTemplate)
//        return DynamoDbOrderRepository(dynamoDbClient)
    }

    @Bean
    fun getRedisInventoryWarehousingRepository(jedis: Jedis,
                                               orderRepository: OrderRepository
    ): RedisInventoryWarehousingRepository {
        return RedisInventoryWarehousingRepository(jedis, orderRepository)
    }

    @Bean
    fun getOrderProcessingService(
        cartService: CartService,
        orderRepository: OrderRepository
    ) = OrderProcessingService(cartService, orderRepository)

    @Bean
    fun getOrderAdminService(orderRepository: OrderRepository) = OrderAdminService(orderRepository)

    @Bean
    fun getOrderQueryService(orderRepository: OrderRepository): OrderQueryService = OrderQueryService(orderRepository)

    @Bean
    fun getStoreAuthProvider(): StoreAuthProvider {
        val provider = FakeStoreAuthProvider()
        provider.login(PrincipalUser("Sam Testington", "stestington@test.com"))
        return provider
    }
}