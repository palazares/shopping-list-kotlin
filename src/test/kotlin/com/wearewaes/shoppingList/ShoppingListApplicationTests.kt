package com.wearewaes.shoppingList

import com.wearewaes.shoppingList.domain.ListItem
import com.wearewaes.shoppingList.domain.ShoppingList
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ShoppingListApplicationTests(@Autowired val webClient: WebTestClient,
								   @Autowired val repository: ShoppingListRepository) {
	@LocalServerPort
	private var localPort: Int = 0

	private final val baseUrl = "http://localhost:$localPort/api"

	private final val testListId = "testListId"
	private val testItemId = 234L
	private val testDescription = "testDescription"

	@Test
	fun contextLoads() {
	}

	@Test
	fun shouldCreateListWhenAddingItem() {
		//given
		val description = "myDescr"
		repository.deleteAll().block()
		//when
		val rec: ShoppingList? = webClient.post()
				.uri("$baseUrl/$testListId/add?description=$description")
				.exchange()
				.expectStatus().isOk
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ShoppingList::class.java)
				.returnResult()
				.responseBody

		//then
		assertEquals(testListId, rec!!.id)
		assertEquals(1, rec.list.size)
		assertEquals(description, rec.list.first().description)
	}

	@Test
	fun shouldAddToExistingListWhenAddingItem() {
		//given
		val description = "myDescr"
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		//when
		val rec: ShoppingList? = webClient.post()
				.uri("$baseUrl/$testListId/add?description=$description")
				.exchange()
				.expectStatus().isOk
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ShoppingList::class.java)
				.returnResult()
				.responseBody

		//then
		assertEquals(testListId, rec!!.id)
		assertEquals(3, rec.list.size)
		assertNotNull(rec.list.single { it.description == description })
	}

	@Test
	fun shouldDeleteItem() {
		// given
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		// when
		val rec: ShoppingList? = webClient.put()
				.uri("$baseUrl/$testListId/delete/$testItemId")
				.exchange()
				.expectStatus().isOk
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ShoppingList::class.java)
				.returnResult()
				.responseBody

		//then
		assertEquals(testListId, rec!!.id)
		assertEquals(1, rec.list.size)
		assertEquals(123L, rec.list.first().id)
	}

	@Test
	fun shouldDoNothingWhenDeleteWrongItem() {
		// given
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		// when
		val rec: ShoppingList? = webClient.put()
				.uri("$baseUrl/$testListId/delete/1234")
				.exchange()
				.expectStatus().isOk
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ShoppingList::class.java)
				.returnResult()
				.responseBody

		//then
		assertEquals(testListId, rec!!.id)
		assertEquals(2, rec.list.size)
	}

	@Test
	fun shouldDoNothingWhenDeleteWrongList() {
		// given
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		// when
		val rec: ShoppingList? = webClient.put()
				.uri("$baseUrl/wrongList/delete/$testItemId")
				.exchange()
				.expectStatus().isOk
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ShoppingList::class.java)
				.returnResult()
				.responseBody

		//then
		assertNull(rec)
	}

	@Test
	fun shouldDeleteAll() {
		// given
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		// when
		val rec: ShoppingList? = webClient.put()
				.uri("$baseUrl/$testListId/deleteAll")
				.exchange()
				.expectStatus().isOk
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ShoppingList::class.java)
				.returnResult()
				.responseBody

		//then
		assertEquals(testListId, rec!!.id)
		assertEquals(0, rec.list.size)
	}

	@Test
	fun shouldDoNothingWhenDeleteAllWrongList() {
		// given
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		// when
		val rec: ShoppingList? = webClient.put()
				.uri("$baseUrl/wrongList/deleteAll")
				.exchange()
				.expectStatus().isOk
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ShoppingList::class.java)
				.returnResult()
				.responseBody

		//then
		assertNull(rec)
	}

	@Test
	fun shouldUpdateQuantity() {
		// given
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		// when
		val rec: ShoppingList? = webClient.put()
				.uri("$baseUrl/$testListId/updateQuantity?itemId=$testItemId&quantity=4")
				.exchange()
				.expectStatus().isOk
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ShoppingList::class.java)
				.returnResult()
				.responseBody

		//then
		assertEquals(testListId, rec!!.id)
		assertEquals(2, rec.list.size)
		assertEquals(4, rec.list.single { it.id == testItemId }.quantity)
		assertEquals(3, rec.list.single { it.id != testItemId }.quantity)
	}

	@Test
	fun shouldThrowExceptionWhenZeroQuantity() {
		// given
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		// when
		webClient.put()
				.uri("$baseUrl/$testListId/updateQuantity?itemId=$testItemId&quantity=0")
				.exchange()
				.expectStatus().is5xxServerError
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(Any::class.java)

		//then
	}

	@Test
	fun shouldDoNothingWhenUpdateQuantityWrongItem() {
		// given
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		// when
		val rec: ShoppingList? = webClient.put()
				.uri("$baseUrl/$testListId/updateQuantity?itemId=1234&quantity=4")
				.exchange()
				.expectStatus().isOk
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ShoppingList::class.java)
				.returnResult()
				.responseBody

		//then
		assertEquals(testListId, rec!!.id)
		assertEquals(2, rec.list.size)
		assertEquals(2, rec.list.single { it.id == testItemId }.quantity)
		assertEquals(3, rec.list.single { it.id != testItemId }.quantity)
	}

	@Test
	fun shouldDoNothingWhenUpdateQuantityWrongList() {
		// given
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		// when
		val rec: ShoppingList? = webClient.put()
				.uri("$baseUrl/wrongList/updateQuantity?itemId=$testItemId&quantity=4")
				.exchange()
				.expectStatus().isOk
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ShoppingList::class.java)
				.returnResult()
				.responseBody

		//then
		assertNull(rec)
	}

	@Test
	fun shouldGetItemsDefaultOrder() {
		// given
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		// when
		val rec: ShoppingList? = webClient.get()
				.uri("$baseUrl/$testListId")
				.exchange()
				.expectStatus().isOk
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ShoppingList::class.java)
				.returnResult()
				.responseBody

		//then
		assertEquals(testListId, rec!!.id)
		assertEquals(2, rec.list.size)
		assertEquals(0, rec.list[0].position)
		assertEquals(1, rec.list[1].position)
	}

	@Test
	fun shouldGetItemsDescriptionOrder() {
		// given
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		// when
		val rec: ShoppingList? = webClient.get()
				.uri("$baseUrl/$testListId?sortBy=description")
				.exchange()
				.expectStatus().isOk
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ShoppingList::class.java)
				.returnResult()
				.responseBody

		//then
		assertEquals(testListId, rec!!.id)
		assertEquals(2, rec.list.size)
		assertEquals("random", rec.list[0].description)
		assertEquals(testDescription, rec.list[1].description)
	}

	@Test
	fun shouldThrowExceptionWhenWrongSorting() {
		// given
		val shoppingList = ShoppingList(testListId, mutableListOf(
				ListItem(testDescription, 0, 2, testItemId),
				ListItem("random", 1, 3, 123)))
		repository.deleteAll().block()
		repository.save(shoppingList).block()
		// when
		webClient.get()
				.uri("$baseUrl/$testListId?sortBy=wrongProperty")
				.exchange()
				.expectStatus().is5xxServerError
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(Any::class.java)

		//then
	}
}
