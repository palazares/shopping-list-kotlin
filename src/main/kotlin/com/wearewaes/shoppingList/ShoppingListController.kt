package com.wearewaes.shoppingList

import com.wearewaes.shoppingList.domain.ShoppingList
import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@Api("Shopping List endpoints. Create, update and get shopping list items")
@RestController
@RequestMapping("api")
class ShoppingListController(private val service: ShoppingListService) {
    @PostMapping("{listId}/add")
    fun addItem(@PathVariable listId: String,
                @RequestParam("description") description: String,
                @RequestParam(value = "quantity", required = false, defaultValue = "1") quantity: Int): Mono<ShoppingList> {
        return service.addItem(listId, description, quantity)
    }

    @PutMapping("{listId}/delete/{itemId}")
    fun deleteItem(@PathVariable listId: String, @PathVariable itemId: Long): Mono<ShoppingList> {
        return service.deleteItem(listId, itemId)
    }

    @PutMapping("{listId}/deleteAll")
    fun deleteAll(@PathVariable listId: String): Mono<ShoppingList> {
        return service.deleteAll(listId)
    }

    @PutMapping("{listId}/updateQuantity")
    fun updateQuantity(@PathVariable listId: String,
                       @RequestParam("itemId") itemId: Long,
                       @RequestParam("quantity") quantity: Int): Mono<ShoppingList> {
        return service.updateQuantity(listId, itemId, quantity)
    }

    @GetMapping("{listId}")
    fun getList(@PathVariable listId: String,
                @RequestParam(value = "sortBy", required = false, defaultValue = "position") sortBy: String): Mono<ShoppingList> {
        return service.getList(listId, sortBy)
    }
}