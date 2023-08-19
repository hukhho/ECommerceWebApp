package io.spring.shoestore.app.lifecycle

import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component


@Component
class OnStartupMessaging(
    //private val dynamoDbClient: DynamoDbClient
): ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        println("App started!")
        //DynamoTableManager(dynamoDbClient).establishOrderTable()
    }
}