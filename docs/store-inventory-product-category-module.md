# Technical Requirement  
## Store Catalog Endpoint (Product + Inventory + Category)
---

# 1. Business Requirement

The system must expose a **Store Catalog endpoint** that allows end users to:

- View all products available in a specific store.
- See stock information (`onHand`, `reserved`) per store.
- Filter products by:
  - Name
  - SKU
  - Price range
  - Stock range
  - Active status
  - Category
- Return products even if no inventory record exists for that store (stock defaults to 0).

This endpoint is **different from the Admin Product CRUD endpoints**, which operate directly on the `Product` entity.

---

# 2. Architectural Analysis

## 2.1 Admin Product Endpoints

Admin endpoints:

- Operate directly on `Product`
- Return `Product` entity or DTO
- Use simple joins (e.g., Product → Category)
- Can use `JpaSpecificationExecutor<Product>`

Example:

```java
@Query("""
  SELECT DISTINCT p
  FROM Product p
  JOIN p.categories c
  WHERE LOWER(c.name) = LOWER(:name)
""")
Page<Product> findProductsByCategoryNameIgnoreCase(@Param("name") String name, Pageable pageable);
```
**Characteristics**

- Entity-based result

- Single-domain focus (Product)

- No Store context

- No computed values

## 2.2 Straightforward Specifications support

**Store Catalog Endpoint (User-Facing)**

The Store Catalog is not entity-based.

It requires combining:

- Product

- Inventory (filtered by storeId)

- Category (for filtering)

- Computed values (COALESCE for null inventory)

**The result is**:

Product
+ Inventory (for specific store)
+ Default stock values if inventory does not exist

This is not a mapped entity, it is a composed dataset.


# 3. Technical Constraint

**Because the result:** Combines multiple tables and show all null products with not stock-inventory, requires LEFT JOIN and Must include computed values:

- Must support filtering without duplicating rows

- Returning Product entity is not appropriate.

- Using Inventory entity is also not appropriate because:

- Products without inventory rows would be excluded.

Therefore:

A projection interface (query-level view) is required.

# 4. Professional Solution
## 4.1 Define a Projection Interface

Example:

```java
public interface ProductStockView {
	Long getProductId();
	String getProductName();
	String getProductDescription();
	Double getProductPrice();
	String getProductImageUrl();
	String getProductSku();
	Integer getOnHand();
	Integer getReserved();
	Instant getCreatedAt();
	Instant getUpdatedAt();
};
```

This interface represents the composed catalog view.

## 4.2 Implement Store Catalog Query
Example:

```java
@Query(
  value = """
    SELECT
      p.id            AS productId,
      p.name          AS productName,
      p.description   AS productDescription,
      p.price         AS productPrice,
      p.image_url     AS productImageUrl,
      p.sku           AS productSku,
      COALESCE(i.on_hand, 0)  AS onHand,
      COALESCE(i.reserved, 0) AS reserved,
      i.created_at    AS createdAt,
      i.updated_at    AS updatedAt
    FROM products p
    LEFT JOIN inventories i
      ON i.product_id = p.id
     AND i.store_id   = :storeId
    WHERE
      (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
      AND (
        :category IS NULL OR EXISTS (
          SELECT 1
          FROM product_categories pc
          JOIN categories c ON c.id = pc.category_id
          WHERE pc.product_id = p.id
            AND LOWER(c.name) = LOWER(:category)
        )
      )
    """,
  countQuery = """
    SELECT COUNT(*)
    FROM products p
    LEFT JOIN inventories i
      ON i.product_id = p.id
     AND i.store_id   = :storeId
    WHERE
      (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
      AND (
        :category IS NULL OR EXISTS (
          SELECT 1
          FROM product_categories pc
          JOIN categories c ON c.id = pc.category_id
          WHERE pc.product_id = p.id
            AND LOWER(c.name) = LOWER(:category)
        )
      )
    """,
  nativeQuery = true
)
Page<ProductStockView> findStoreCatalog(
    Long storeId,
    String name,
    String category,
    Pageable pageable
);
```

# 5. Rationale
## 5.1 Why Not Return Product?

**Because the result includes:**

- Store-specific inventory data

- Computed default values

- Context-specific filtering logic

- This data does not belong to the Product entity.

- Mixing them would violate separation of concerns.

This data does not belong to the Product entity. Mixing them would violate separation of concerns.

## 5.2 Why Not Return Inventory?

**Because:**

- Products without inventory rows must still appear.

- Starting from Inventory excludes missing rows.

- Catalog completeness would be compromised.


## 5.3 Why Projection Is Correct

**Projection:**

- Represents a composed dataset

- Avoids artificial entities

- Supports pagination

- Prevents N+1 query problems

- Maintains performance

- Keeps domain boundaries clean

This approach aligns with professional layered architecture practices.

# 6. Architectural Separation

| Admin Layer      | User Catalog Layer       |
| ---------------- | ------------------------ |
| Product CRUD     | Store-specific catalog   |
| Entity-centric   | DTO-driven              |
| Specifications   | Query projection         |
| Simple JPQL      | Composed LEFT JOIN query |
| No store context | Store context required   |

**Note:** Entity-centric (Product as root aggregate), is the The consultation and persistence model nad the DTO-driven (ProductSummaryDTO, ProductDTO)
 is API contract.
 
# 7. Conclusion

The use of ProductStockView is:

- Architecturally justified

- Technically necessary

- Performance-conscious

- Domain-correct

- Professionally aligned with clean architecture principles

**It ensures that:**

- Admin endpoints remain simple and entity-based.

- User catalog endpoints remain efficient and context-aware.

- The domain model is not polluted with store-specific logic.