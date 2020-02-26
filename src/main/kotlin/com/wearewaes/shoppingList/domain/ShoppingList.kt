package com.wearewaes.shoppingList.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*


data class ListItem(val description: String, val position: Long, var quantity: Int,
                    val id: Long = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE)

@Document(collection = "shoppingList")
class ShoppingList(@Id val id: String,
                   val list: MutableList<ListItem> = mutableListOf(),
                   @Version var version: Long = 0)