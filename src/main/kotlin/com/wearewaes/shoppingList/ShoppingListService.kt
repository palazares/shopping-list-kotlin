package com.wearewaes.shoppingList

import com.wearewaes.shoppingList.domain.ListItem
import com.wearewaes.shoppingList.domain.ShoppingList
import mu.KotlinLogging
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import kotlin.reflect.full.memberProperties


@Repository
interface ShoppingListRepository : ReactiveCrudRepository<ShoppingList, String>

@Service
class ShoppingListService(private val repo: ShoppingListRepository) {
    private val logger = KotlinLogging.logger {}

    fun addItem(listId: String, description: String, quantity: Int): Mono<ShoppingList> {
        require(quantity > 0)
        return repo.findById(listId).defaultIfEmpty(ShoppingList(listId))
                .map { it.apply { this.list.add(ListItem(description, this.version, quantity)) } }
                .flatMap { repo.save(it) }
    }

    fun deleteItem(listId: String, itemId: Long): Mono<ShoppingList> =
            repo.findById(listId)
                    .map { it.apply { this.list.removeIf { i -> i.id == itemId } } }
                    .flatMap { repo.save(it) }


    fun deleteAll(listId: String): Mono<ShoppingList> =
            repo.findById(listId)
                    .map { it.apply { this.list.clear() } }
                    .flatMap { repo.save(it) }

    fun updateQuantity(listId: String, itemId: Long, quantity: Int): Mono<ShoppingList> {
        require(quantity > 0)
        return repo.findById(listId)
                .map { it.apply { this.list.single { i -> i.id == itemId }.quantity = quantity } }
                .flatMap { repo.save(it) }
    }

    fun getItems(listId: String, sortBy: String): Mono<List<ListItem>> =
            repo.findById(listId)
                    .map {
                        it.list.toMutableList().apply {
                            this.sortBy { li ->
                                val prop = ListItem::class.memberProperties.single { p -> p.name == sortBy }
                                prop.get(li) as Comparable<Any>
                            }
                        }
                    }
}