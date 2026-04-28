# Order - OrderItem CRUD API

Brief documentation of the **Order and OrderItem module** in the API.

---

## Overview

The Order module manages the full sales flow in StockFlow. An `Order` is created from a **CartItem session snapshot**, persisting the cart state at the moment of purchase into a permanent `Order` with its associated `OrderItem` records.

Since `Order` and `OrderItem` are in a **composition relationship with cascade ALL**, persisting an `Order` automatically persists all its `OrderItem` children. No separate endpoint is needed to create order items.

> **Note:** The `CartItem` session is not yet implemented. The current design assumes cart data is passed directly in the request body, representing what the session will eventually provide.

---

## How the Sale Flow Works

```
CartItem (session)
    │
    │  snapshot at checkout
    ▼
Order (persisted)
    │  cascade ALL
    ▼
OrderItem (persisted)
    │
    │  references
    ▼
Inventory (stock deducted from onHand)
```

1. The client sends a list of cart items at checkout.
2. The system creates an `Order` linked to a `User` and a `Store`.
3. For each cart item, an `OrderItem` is created capturing `unitPrice` and `quantity` as a **snapshot** — these values will not change even if the product price changes later.
4. Each `OrderItem` holds a reference to the `Inventory` record it was sourced from.
5. The `Inventory.onHand` is decremented accordingly.

---

## Order Entity

The `Order` resource represents a sales transaction with the following attributes:

- `id`
- `orderDate`
- `status` *(OrderStatus enum)*
- `total`
- `user`
- `store`
- `payment`
- `orderItems`

---

## OrderItem Entity

The `OrderItem` resource represents a line item within an order:

- `id`
- `quantity`
- `unitPrice` *(snapshot at time of purchase)*
- `inventory` *(reference to the sourced inventory)*
- `order`

---

## OrderStatus

Orders follow a defined lifecycle:

```
PENDING → CONFIRMED → SHIPPED → DELIVERED
                              ↘ CANCELLED
```

| Status    | Description                              |
|-----------|------------------------------------------|
| PENDING   | Order created, awaiting confirmation     |
| CONFIRMED | Order confirmed, preparing for shipment  |
| SHIPPED   | Order dispatched                         |
| DELIVERED | Order received by customer               |
| CANCELLED | Order cancelled — stock is restored      |

---

## Why There Is No Delete

Orders are **never physically deleted**. Deleting an order would:

- Break the historical sales record
- Leave `OrderItem` records orphaned or cascade-delete financial data
- Make auditing and reporting impossible

Instead, an order is **cancelled** by updating its `status` to `CANCELLED`. On cancellation, the system performs a **stock restoration** by incrementing `Inventory.onHand` for each `OrderItem` in the order.

---

## CRUD Operations

### Create

**POST /store/{storeId}/order**

Creates a new order from a cart snapshot.

- Receives a list of cart items in the request body
- Validates that each referenced `Inventory` exists and belongs to the given store
- Validates sufficient `onHand` stock for each item
- Creates `Order` with `status = PENDING` and `orderDate = now`
- Creates one `OrderItem` per cart entry with:
  - `unitPrice` snapshot from current `Inventory → Product` price
  - `quantity` from cart
- Decrements `Inventory.onHand` for each item
- Cascade ALL persists all `OrderItem` records automatically
- Returns the created `OrderDTO` with its items

---

### Read

**GET /store/{storeId}/orders**
**GET /store/{storeId}/order/{id}**

Retrieve orders:

- Full list of orders scoped to a store
- Single order by `id` including its `OrderItem` list
- Can filter by `status`, `userId`, or date range

---

### Cancel (replaces Delete)

**PATCH /store/{storeId}/orders/{id}/cancel**

Orders are never physically deleted.

Instead:

- `status = CANCELLED`
- `Inventory.onHand` is restored for each `OrderItem`
- The `Order` and all `OrderItem` records remain intact for historical reference

---

## Cascade Behavior

The `Order → OrderItem` relationship is a **composition with cascade ALL**:

- Persisting an `Order` automatically persists all its `OrderItem` children.
- Deleting an `Order` (if ever allowed) would cascade-delete all its `OrderItem` records.
- `OrderItem` cannot exist without an `Order`.

```java
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> orderItems;
```

---

## Snapshot Design

`OrderItem.unitPrice` is a **snapshot**, not a foreign key to the product price. This is intentional:

- Product prices can change after a sale.
- The order must always reflect **what the customer actually paid**.
- Even if the `Inventory` or `Product` is later updated, the historical order remains accurate.

This is why `OrderItem` stores `unitPrice` directly rather than deriving it from `Inventory → Product` at read time.
