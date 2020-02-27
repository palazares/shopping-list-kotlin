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

    fun addItem(listId: String, description: String, quantity: Int): Mono<ShoppingList> =
            findAndUpdate(listId) { l ->
                l.defaultIfEmpty(ShoppingList(listId))
                        .map {
                            require(quantity > 0)
                            it.apply { this.list.add(ListItem(description, this.version, quantity)) }
                        }
            }.doOnError { logger.debug("Error during adding an item $description to list $listId: ", it) }

    fun deleteItem(listId: String, itemId: Long): Mono<ShoppingList> =
            findAndUpdate(listId) { l -> l.map { it.apply { this.list.removeIf { i -> i.id == itemId } } } }
                    .doOnError { logger.debug("Error during deleting an item $itemId from list $listId: : ", it) }

    fun deleteAll(listId: String): Mono<ShoppingList> =
            findAndUpdate(listId) { l -> l.map { it.apply { this.list.clear() } } }
                    .doOnError { logger.debug("Error during deleting all items from list $listId: : ", it) }

    fun updateQuantity(listId: String, itemId: Long, quantity: Int): Mono<ShoppingList> =
            findAndUpdate(listId) { l ->
                l.map {
                    require(quantity > 0)
                    it.apply { this.list.firstOrNull { i -> i.id == itemId }?.quantity = quantity }
                }
            }.doOnError { logger.debug("Error during updating quantity for item $itemId in list $listId: : ", it) }

    fun getList(listId: String, sortBy: String): Mono<ShoppingList> =
            repo.findById(listId)
                    .map {
                        ShoppingList(it.id, it.list.toMutableList().apply {
                            this.sortBy { li ->
                                val prop = ListItem::class.memberProperties.single { p -> p.name == sortBy }
                                prop.get(li) as Comparable<Any>
                            }
                        })
                    }.doOnError { logger.debug("Error during getting items of list $listId: : ", it) }

    private fun findAndUpdate(listId: String, update: (Mono<ShoppingList>) -> Mono<ShoppingList>) =
            update(repo.findById(listId)).flatMap { repo.save(it) }
}