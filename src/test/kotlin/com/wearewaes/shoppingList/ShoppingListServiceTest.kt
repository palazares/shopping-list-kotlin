package com.wearewaes.shoppingList

import com.wearewaes.shoppingList.domain.ListItem
import com.wearewaes.shoppingList.domain.ShoppingList
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class ShoppingListServiceTest {
    @Mock
    private lateinit var repository: ShoppingListRepository
    @InjectMocks
    private lateinit var service: ShoppingListService
    @Captor
    private lateinit var listCaptor: ArgumentCaptor<ShoppingList>

    private val testListId = "testListId"
    private val testItemId = 234L
    private val testDescription = "testDescription"

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenAddQuantityIsZero() {
        // given
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.empty())
        // when
        StepVerifier
                .create(service.addItem(testListId, testDescription, 0))
                .expectError(IllegalArgumentException::class.java)
                .verify()
    }

    @Test
    fun shouldAddItemAndCreateList() {
        // given
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.empty())
        `when`(repository.save(any(ShoppingList::class.java))).thenReturn(Mono.just(ShoppingList(testListId)))
        // when
        StepVerifier
                .create(service.addItem(testListId, testDescription, 2))
                .expectNextCount(1)
                .verifyComplete()

        //then
        verify(repository).save(listCaptor.capture())

        val shoppingList = listCaptor.value
        assertEquals(testListId, shoppingList.id)
        assertNotNull(shoppingList.list.single { it.description == testDescription })
        assertEquals(2, shoppingList.list.single { it.description == testDescription }.quantity)
    }

    @Test
    fun shouldDeleteItem() {
        // given
        val shoppingList = ShoppingList(testListId, mutableListOf(
                ListItem(testDescription, 0, 2, testItemId),
                ListItem("random", 1, 3, 123)))
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.just(shoppingList))
        `when`(repository.save(any(ShoppingList::class.java))).thenReturn(Mono.just(shoppingList))
        // when
        StepVerifier
                .create(service.deleteItem(testListId, testItemId))
                .expectNextCount(1)
                .verifyComplete()

        // then
        verify(repository).save(listCaptor.capture())

        val result = listCaptor.value
        assertEquals(testListId, result.id)
        assertTrue(result.list.none { it.id == testItemId })
        assertTrue(result.list.size > 0)
    }

    @Test
    fun shouldDeleteNothingWhenWrongItemId() {
        // given
        val shoppingList = ShoppingList(testListId, mutableListOf(
                ListItem(testDescription, 0, 2, testItemId),
                ListItem("random", 1, 3, 123)))
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.just(shoppingList))
        `when`(repository.save(any(ShoppingList::class.java))).thenReturn(Mono.just(shoppingList))
        // when
        StepVerifier
                .create(service.deleteItem(testListId, 1234))
                .expectNextCount(1)
                .verifyComplete()

        // then
        verify(repository).save(listCaptor.capture())

        val result = listCaptor.value
        assertEquals(testListId, result.id)
        assertNotNull(result.list.single { it.id == testItemId })
        assertNotNull(result.list.single { it.id == 123L })
        assertTrue(result.list.size == 2)
    }

    @Test
    fun shouldDeleteNothingWhenWrongListId() {
        // given
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.empty())
        // when
        StepVerifier
                .create(service.deleteItem("wrongId", testItemId))
                .verifyComplete()

        // then
    }

    @Test
    fun shouldReturnEmptyWhenDeleteWrongId() {
        // given
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.empty())
        // when
        StepVerifier
                .create(service.deleteItem(testListId, testItemId))
                .verifyComplete()

        // then
        verify(repository, times(0)).save(any())
    }

    @Test
    fun shouldUpdateNothingWhenWrongListId() {
        // given
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.empty())
        // when
        StepVerifier
                .create(service.updateQuantity("wrongId", testItemId, 2))
                .verifyComplete()

        // then
    }

    @Test
    fun shouldDeleteAll() {
        // given
        val shoppingList = ShoppingList(testListId, mutableListOf(
                ListItem(testDescription, 0, 2, testItemId),
                ListItem("random", 1, 3, 123)))
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.just(shoppingList))
        `when`(repository.save(any(ShoppingList::class.java))).thenReturn(Mono.just(shoppingList))
        // when
        StepVerifier
                .create(service.deleteAll(testListId))
                .expectNextCount(1)
                .verifyComplete()

        // then
        verify(repository).save(listCaptor.capture())

        val result = listCaptor.value
        assertEquals(testListId, result.id)
        assertTrue(result.list.size == 0)
    }

    @Test
    fun shouldDeleteNothingWhenDeleteWrongId() {
        // given
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.empty())
        // when
        StepVerifier
                .create(service.deleteAll(testListId))
                .verifyComplete()

        // then
        verify(repository, times(0)).save(any())
    }

    @Test
    fun shouldReturnEmptyWhenDeleteAllWrongId() {
        // given
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.empty())
        // when
        StepVerifier
                .create(service.deleteAll(testListId))
                .verifyComplete()

        // then
        verify(repository, times(0)).save(any())
    }

    @Test
    fun shouldUpdateQuantity() {
        // given
        val shoppingList = ShoppingList(testListId, mutableListOf(
                ListItem(testDescription, 0, 2, testItemId),
                ListItem("random", 1, 3, 123)))
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.just(shoppingList))
        `when`(repository.save(any(ShoppingList::class.java))).thenReturn(Mono.just(shoppingList))
        // when
        StepVerifier
                .create(service.updateQuantity(testListId, testItemId, 1))
                .expectNextCount(1)
                .verifyComplete()

        // then
        verify(repository).save(listCaptor.capture())

        val result = listCaptor.value
        assertEquals(testListId, result.id)
        assertTrue(result.list.size == 2)
        assertEquals(1, shoppingList.list.single { it.id == testItemId }.quantity)
    }

    @Test
    fun shouldUpdateNothingWhenWrongItemId() {
        // given
        val shoppingList = ShoppingList(testListId, mutableListOf(
                ListItem(testDescription, 0, 2, testItemId),
                ListItem("random", 1, 3, 123)))
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.just(shoppingList))
        `when`(repository.save(any(ShoppingList::class.java))).thenReturn(Mono.just(shoppingList))
        // when
        StepVerifier
                .create(service.updateQuantity(testListId, 1234, 1))
                .expectNextCount(1)
                .verifyComplete()

        // then
        verify(repository).save(listCaptor.capture())

        val result = listCaptor.value
        assertEquals(testListId, result.id)
        assertTrue(result.list.size == 2)
        assertEquals(2, shoppingList.list.single { it.id == testItemId }.quantity)
        assertEquals(3, shoppingList.list.single { it.id == 123L }.quantity)
    }

    @Test
    fun shouldReturnEmptyWhenUpdateWrongId() {
        // given
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.empty())
        // when
        StepVerifier
                .create(service.updateQuantity(testListId, testItemId, 1))
                .verifyComplete()

        // then
        verify(repository, times(0)).save(any())
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenUpdateQuantityIsZero() {
        // given
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.just(ShoppingList(testListId)))
        // when
        StepVerifier
                .create(service.updateQuantity(testListId, testItemId, 0))
                .expectError(IllegalArgumentException::class.java)
                .verify()
    }

    @Test
    fun shouldGetItemsWithPositionSorting() {
        // given
        val shoppingList = ShoppingList(testListId, mutableListOf(
                ListItem(testDescription, 0, 2, testItemId),
                ListItem("random", 1, 3, 123)))
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.just(shoppingList))
        // when
        StepVerifier
                .create(service.getList(testListId, ListItem::position.name))
                .expectNextMatches { it.list.size == 2 && it.list[0].id == testItemId && it.list[1].id == 123L }
                .verifyComplete()
    }

    @Test
    fun shouldGetItemsWithDescriptionSorting() {
        // given
        val shoppingList = ShoppingList(testListId, mutableListOf(
                ListItem(testDescription, 0, 2, testItemId),
                ListItem("random", 1, 3, 123)))
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.just(shoppingList))
        // when
        StepVerifier
                .create(service.getList(testListId, ListItem::description.name))
                .expectNextMatches { it.list.size == 2 && it.list[1].id == testItemId && it.list[0].id == 123L }
                .verifyComplete()
    }

    @Test
    fun shouldReturnEmptyListWhenWrongId() {
        // given
        `when`(repository.findById(any(String::class.java))).thenReturn(Mono.empty())
        // when
        StepVerifier
                .create(service.getList(testListId, ListItem::description.name))
                .verifyComplete()
    }
}