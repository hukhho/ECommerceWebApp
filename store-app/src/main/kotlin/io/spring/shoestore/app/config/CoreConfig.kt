package io.spring.shoestore.app.config


import PostgresUserRepository
import io.spring.shoestore.aws.dynamodb.DynamoDbOrderRepository
import io.spring.shoestore.core.orders.OrderAdminService
import io.spring.shoestore.core.orders.OrderProcessingService
import io.spring.shoestore.core.orders.OrderQueryService
import io.spring.shoestore.core.orders.OrderRepository
import io.spring.shoestore.core.products.ProductService
import io.spring.shoestore.core.products.ShoeService
import io.spring.shoestore.core.security.PrincipalUser
import io.spring.shoestore.core.security.StoreAuthProvider
import io.spring.shoestore.core.users.UserRepository
import io.spring.shoestore.core.users.UserService
import io.spring.shoestore.core.variants.InventoryManagementService
import io.spring.shoestore.core.variants.ProductVariantService
import io.spring.shoestore.postgres.PostgresOrderRepository
import io.spring.shoestore.postgres.PostgresProductRepository
import io.spring.shoestore.postgres.PostgresProductVariantRepository
import io.spring.shoestore.postgres.PostgresShoeRepository
import io.spring.shoestore.redis.RedisInventoryWarehousingRepository
import io.spring.shoestore.security.FakeStoreAuthProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import redis.clients.jedis.Jedis
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
class CoreConfig {

    @Bean
    fun getShoeService(jdbcTemplate: JdbcTemplate): ShoeService {
        // an example of 'hiding' the details implementation, only the shoeservice can be grabbed via DI
        return ShoeService(PostgresShoeRepository(jdbcTemplate))
    }
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
    fun getUserRepository(jdbcTemplate: JdbcTemplate): UserRepository {
        return PostgresUserRepository(jdbcTemplate)
    }
    @Bean
    fun getProductVariantService(jdbcTemplate: JdbcTemplate, jedis: Jedis): ProductVariantService {
        return ProductVariantService(PostgresProductVariantRepository(jdbcTemplate))
    }

    @Bean
    // Kotlin-ified!
    fun getInventoryManagementService(jdbcTemplate: JdbcTemplate, jedis: Jedis) = InventoryManagementService(
            PostgresProductVariantRepository(jdbcTemplate),
            RedisInventoryWarehousingRepository(jedis)
        )

    @Bean
    fun getOrderRepository(jdbcTemplate: JdbcTemplate
                           //, dynamoDbClient: DynamoDbClient
    ): OrderRepository {
        return PostgresOrderRepository(jdbcTemplate)
//        return DynamoDbOrderRepository(dynamoDbClient)
    }

    @Bean
    fun getOrderProcessingService(
        inventoryManagementService: InventoryManagementService,
        shoeService: ShoeService,
        orderRepository: OrderRepository
    ) = OrderProcessingService(inventoryManagementService, shoeService, orderRepository)

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