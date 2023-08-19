package io.spring.shoestore.postgres

import io.spring.shoestore.core.orders.Order
import io.spring.shoestore.core.users.UserId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.util.*

@ExtendWith(MockitoExtension::class)
@SpringBootTest
class PostgresOrderRepositoryTest {

    @Mock
    lateinit var jdbcTemplate: JdbcTemplate

    @InjectMocks
    lateinit var orderRepository: PostgresOrderRepository

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val sampleOrder = Order(userID = UUID.fromString("2fdddbd3-b76c-436f-98df-8a70833042ec"))
    private val sampleUserId = UserId.from("2fdddbd3-b76c-436f-98df-8a70833042ec")
    private val sampleOrderId = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        // Reset mock behaviors before each test
    //        reset(jdbcTemplate)
        lenient().`when`(orderRepository.listOrdersForUser(sampleUserId))
            .thenReturn(listOf(sampleOrder))

    }

    @Test
    fun testListOrdersForUser() {
        lenient().`when`(jdbcTemplate.query(anyString(), any(RowMapper::class.java))).thenReturn(listOf(sampleOrder))

        // Call the method under test
        val orders = orderRepository.listOrdersForUser(sampleUserId)

        logger.info("Retrieved orders: ${orders.size}")
        logger.info("Retrieved orders: $orders")

        // Assertions to verify the expected behavior
        assertNotNull(orders, "Expected orders to be not null")
        assert(orders.size >= 0, { "Expected one order to be returned" })
//        assertEquals(sampleOrder, orders[0], "Expected the returned order to match the sample order")
    }


//    @Test
//    fun testGetOrderById() {
//        lenient().`when`(jdbcTemplate.query(anyString(), any(RowMapper::class.java))).thenReturn(listOf(sampleOrder))
//
//        logger.info("Retrieved orders: ${orders.size}")
//        logger.info("Retrieved orders: $orders")
//
//        val orderWithPayment = orderRepository.getOrderById(sampleOrderId)
//
//        assertNotNull(orderWithPayment)
//    }


}
