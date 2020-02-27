package com.wearewaes.shoppingList

import io.swagger.annotations.Api
import org.reactivestreams.Publisher
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.config.EnableMongoAuditing
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux

@SpringBootApplication
@EnableSwagger2WebFlux
@EnableMongoAuditing
class ShoppingListApplication {

	/**
	 * Enables Swagger API for controllers marked with [Api] annotation
	 *
	 * @return Docket bean responsible for Swagger configuration
	 */
	@Bean
	fun api(): Docket = Docket(DocumentationType.SWAGGER_2)
			.genericModelSubstitutes(Mono::class.java, Flux::class.java, Publisher::class.java)
			.select()
			.apis(RequestHandlerSelectors.withClassAnnotation(Api::class.java))
			.paths(PathSelectors.any())
			.build()
			.apiInfo(ApiInfoBuilder()
					.title("Personal shopping list API")
					.build())
}

fun main(args: Array<String>) {
	runApplication<ShoppingListApplication>(*args)
}
