package com.wearewaes.shoppingList

import com.wearewaes.shoppingList.domain.ShoppingList
import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@Api("Shopping List endpoints. Create, update and get shopping list items")
@RestController
@RequestMapping("api")
class ShoppingListController(private val service: ShoppingListService) {
    /**
     * Add an item to ShoppingList. Creates a new list if it doesn't exist
     *
     * @param listId id of a ShoppingList
     * @param description description of new ListItem
     * @param quantity optional with default value 1. Quantity of a ListItem being added
     * @return Updated or created ShoppingList
     */
    @PostMapping("{listId}/add")
    fun addItem(@PathVariable listId: String,
                @RequestParam("description") description: String,
                @RequestParam(value = "quantity", required = false, defaultValue = "1") quantity: Int): Mono<ShoppingList> {
        return service.addItem(listId, description, quantity)
    }

    /**
     * Deletes an item from ShoppingList. Does nothing if no matching ids were found
     *
     * @param listId id of a ShoppingList
     * @param itemId id of a ListItem
     * @return updated ShoppingList or Mono.empty()
     */
    @PutMapping("{listId}/delete/{itemId}")
    fun deleteItem(@PathVariable listId: String, @PathVariable itemId: Long): Mono<ShoppingList> {
        return service.deleteItem(listId, itemId)
    }

    /**
     * Deletes all items from ShoppingList. Does nothing if no matching id were found
     *
     * @param listId id of a ShoppingList
     * @return updated ShoppingList or Mono.empty()
     */
    @PutMapping("{listId}/deleteAll")
    fun deleteAll(@PathVariable listId: String): Mono<ShoppingList> {
        return service.deleteAll(listId)
    }

    /**
     * Updates an item quantity in a ShoppingList. Does nothing if no matching ids were found
     *
     * @param listId id of a ShoppingList
     * @param itemId id of a ListItem
     * @param quantity new quantity. Should be bigger than 0, otherwise Mono.error() returned
     * @return updated ShoppingList or Mono.empty(). Mono.error() in case of non-positive quantity
     */
    @PutMapping("{listId}/updateQuantity")
    fun updateQuantity(@PathVariable listId: String,
                       @RequestParam("itemId") itemId: Long,
                       @RequestParam("quantity") quantity: Int): Mono<ShoppingList> {
        return service.updateQuantity(listId, itemId, quantity)
    }

    /**
     * Finds ShoppingList and returns it sorted.
     *
     * @param listId id of a ShoppingList
     * @param sortBy optional. position by default. property name of a ListItem used for sorting
     * @return ShoppingList or Mono.empty()
     */
    @GetMapping("{listId}")
    fun getList(@PathVariable listId: String,
                @RequestParam(value = "sortBy", required = false, defaultValue = "position") sortBy: String): Mono<ShoppingList> {
        return service.getList(listId, sortBy)
    }
}