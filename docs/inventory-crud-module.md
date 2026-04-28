# Inventory CRUD API

Brief documentation of the **Inventory CRUD module** in the API.

---

## Inventory CRUD

Supports full inventory lifecycle management:

- Create inventories
- Update inventory details
- Retrieve active inventory items
- No deletion, instead an update on stock is only necessary and prevents violation of referential integrity and keep records on sales.

### Problem on Delete

Deleting an `Inventory` record causes any associated `OrderItem` entities to hold a **dangling reference**, since `OrderItem` maintains a direct foreign key to `Inventory`. This violates referential integrity, as `OrderItem` represents a historical sales record that depends on inventory data at the time of purchase.

### Solution

Instead of deleting the `Inventory` record, the system performs an **update on the `onHand` field**, restoring the stock quantity to reflect the cancelled or reversed order. This approach preserves referential integrity by keeping the `Inventory` record intact, while accurately reestablishing the available stock for the associated product.

---

## Why InventoryId Acts as a Bridge

The `Inventory` entity uses a composite identity (`inventoryId`) that encapsulates both a `productId` and a `storeId`. This design makes `Inventory` a **bridge entity** between `Store` and `Product`.

Rather than querying inventory in isolation, all operations are scoped to a specific store context. The system determines which inventory record to work with by combining:

- The **store** where the user is currently positioned (derived from the active store context or route parameter).
- The **product** to which stock needs to be assigned or updated.

This means queries such as "give this product X units of stock" are always resolved within the context of a specific store, avoiding ambiguity when the same product exists across multiple stores with different stock levels.

---

## Inventory Entity

The `Inventory` resource represents inventory entries with the following attributes:

- `inventoryId` *(bridges `storeId` + `productId`)*
- `store`
- `product`
- `onHand`
- `reserved`
- `createdAt`

---

## CRUD Operations

### Create

**POST /store/{id}/product/{id}/inventory**

Creates a new inventory entry.

- Validates basic data (e.g., non-negative `onHand` and `reserved` via `@Min()` annotation)
- Returns the created `InventoryCreateResponseDTO`

**Example - Request: Create a stock for in a specific store with a specific product id**
```json
{
    "onHand": 155,
    "reserved": 100
}
```



**Example -  Response: Returns the object**
```json
{
    "message": "Inventory created successfully",
    "inventory": {
        "productId": 3,
        "storeId": 1,
        "onHand": 155,
        "reserved": 100,
        "createdAt": "2026-04-28T19:21:07.369339Z",
        "updatedAt": null
    }
}
```

---

### Read

**GET /store/{id}/inventories**
**GET /store/{id}/inventories/{id}**

Retrieves inventories:

- Full list of inventory items for a given store
- Single inventory item by `id`
---

### Update (Put)

**PUT /store/{id}/product/{id}/inventory**

Partial update of an inventory entry:

- Only fields present in the DTO are updated
- Prevents overwriting existing values with `null`
- `createdAt` is not modified
- Validates basic data (e.g., non-negative `onHand` and `reserved` via `@Min()` annotation)
- Returns the created `InventoryUpdateResponseDTO`

**Example - Request: PUT - Update the stock**
```json
{
    "onHand": 2,
    "reserved": 100
}
```

**Example - Response: Returns the object**
```json
{
    "message": "Inventory have been updated successfully",
    "inventory": {
        "productId": 3,
        "storeId": 1,
        "onHand": 2,
        "reserved": 100
    }
}
```

---