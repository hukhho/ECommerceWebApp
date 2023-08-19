package io.spring.shoestore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
class SpringShoeStoreApplication

fun main(args: Array<String>) {
	runApplication<SpringShoeStoreApplication>(*args)
}
