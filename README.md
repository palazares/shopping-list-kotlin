# shopping-list-kotlin
Reactive SpringBoot REST Shopping list implementation

## Requirements

* Add an item to a shopping list
* Delete an item from a shopping list
* Delete all items from a shopping list
* Increase / Decrease quantity
* Sort on the order the items are added (default)
* Sort on description alphabetically


## Libs used

* Kotlin 1.3
* Maven
* Spring Boot 2
* WebFlux
* Swagger 2
* MongoDB

## Build and run

Clone git repo:

```
git clone https://github.com/palazares/shopping-list-kotlin.git
```

Build application:

```
mvn install 
```

Run


```
mvn spring-boot:run
```

## Tests

Run unit and integration tests:

```
mvn test
```

## API details
```
ListItem {
    description*	string
    id*	            integer($int64)
    position*	    integer($int64)
    quantity*	    integer($int32)
}
```
```
ShoppingList {
     id*	    string
     list*	    [...]
     version*	integer($int64)
}
```
URI | HTTP Method | Description
--- | --- | ---
`<host>/api/<ID>/add?description=<description>&quantity=<quantity>` | POST | Add an item to shopping list. quantity should be positive and is1 by default
`<host>/api/<ID>?sortBy=<property_name>` | GET | Get list details. sortBy is optional. By default position is used
`<host>/api/<ID>/updateQuantity?itemId=<ItemID>&quantity=<quantity>` | PUT | Updates the quantity of particular item in list. Should be positive
`<host>/api/<ID>/deleteAll` | PUT | Deletes all items from list 
`<host>/api/<ID>/delete/<ItemID>` | PUT | Deletes an item from list 

