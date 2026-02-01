# Product CRUD API

Brief documentation of the **Product CRUD module** in the API.

---

## Product Entity

The `Product` resource represents inventory/catalog products with attributes such as:

- `id`
- `name`
- `description`
- `price`
- `sku`
- `imageUrl`
- `stock`
- `active`
- `createdAt`
- `discontinuedAt`

---

##  CRUD Operations

###  Create
**POST /products**

Creates a new product.

- Validates basic data (e.g., non-negative price)
- Initializes `active = true` by default
- Returns the created `ProductDTO`

---

###  Read
**GET /products**  
**GET /products/{id}**

Retrieve products:

- Full list of products
- Single product by `id`
- Can filter only `active = true` products for public catalog views

---

###  Update
**PATCH /products/{id}**

Partial update of a product:

- Only fields present in the DTO are updated
- Prevents overwriting existing values with `null`
- `createdAt` is not modified

Example 1:
```json
{
  "price": 19.99,
  "stock": 10
}

Example 2:
```json
{
  "name": "Gold Minimalist Emeral Ring",
  "description": "18k gold plated minimalist ring with smooth finish and emeral stone of 20k",
  "price": 680.29,
  "sku": "JWL-RNG-001",
  "imageUrl": "https://example.com/images/gold-ring.jpg",
  "stock": 25.00
}

## Delete (Soft Delete)

**PATCH /products/{id}/discontinue (example)**

Products are not physically deleted.

Instead:

- active = false

- Optionally set discontinuedAt = now
