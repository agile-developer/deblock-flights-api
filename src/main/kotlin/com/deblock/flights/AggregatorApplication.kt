package com.deblock.flights

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestClient

@SpringBootApplication
class AggregatorApplication {

	@Bean
	fun restClient(): RestClient {
		return RestClient.create()
	}
}

fun main(args: Array<String>) {
	runApplication<com.deblock.flights.AggregatorApplication>(*args)
}
