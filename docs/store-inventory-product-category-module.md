# Store · Inventory · Product — Module

---

# 1. Business Requirement

The idea came from thinking about how a real physical store works. A customer walks in, asks if a product is available, the store checks its stock, and if there is inventory the purchase moves forward. That's exactly what this module models.

From the admin side, the goal is simple: be able to manage what products a store has, and how much stock is available for each one — `Store → Inventory → Product`.

By keeping this isolated, the client-facing catalog can be built separately on top of the same data, without mixing the two concerns.

---

# 2. The Store Catalog Constraint

One important requirement: **every product must show up**, even if no inventory record exists for that store yet. The admin needs to know what products have stock and which ones don't.

That's why a `LEFT JOIN` between `Product` and `Inventory` was necessary. A regular `INNER JOIN` would exclude products with no inventory row — which is exactly what we don't want.

```sql
FROM products p
LEFT JOIN inventories i
  ON i.product_id = p.id
 AND i.store_id   = :storeId
```

And because `LEFT JOIN` can return `null` for inventory columns when no row exists, `COALESCE` is used to default those values to `0`:

```sql
COALESCE(i.on_hand, 0)  AS onHand,
COALESCE(i.reserved, 0) AS reserved,
```

This way, a product with no inventory still returns `onHand: 0` instead of `null` — which makes it easier to handle on the frontend. The UI can then decide: if `onHand <= 0`, show **Out of Stock**; otherwise show **In Stock**.

---

# 3. Why a Projection Interface?

Since the result combines data from `Product`, `Inventory`, and computed values, it doesn't map to any single entity. Returning a `Product` would be wrong because it doesn't carry store-specific stock. Returning an `Inventory` would be wrong because products with no inventory would be excluded.

The solution is a **projection interface** — it just defines the shape of what the query returns, nothing more.

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
}
```

---

# 4. Store Catalog vs Client Catalog

Both endpoints serve similar data but for different audiences with different needs.

| | Store Catalog (Admin) | Client Catalog *(in progress)* |
|---|---|---|
| Who uses it | Admin | End user / customer |
| Purpose | Manage stock per store | Browse available products |
| Shows out-of-stock | Yes — admin needs full visibility | TBD — likely filtered or flagged |
| `onHand` meaning | Raw stock value | Drives In Stock / Out of Stock label |
| `COALESCE` | Returns `0` when no inventory row | Same — prevents null on frontend |
| Filters | Name, SKU, price, stock, category | TBD — likely name, category, availability |
| Stock deduction logic | Manual via admin | To be analyzed — triggered on purchase |

> The stock deduction flow (when a customer buys a product, `onHand` decreases) is still pending analysis. That logic will live here once the purchase flow is defined.

---

# 5. From Native Query to Specifications

The first approach was writing the query manually with `@Query`. It worked, but as filters stacked and relationships grew, the SQL became long, hard to read, and fragile — one typo and it only breaks at runtime.

**Native query approach:**

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

That's when **JPA Specifications** came in. Instead of one big SQL string, each filter becomes its own small Java method. Hibernate takes those, builds the SQL automatically, and runs it — no raw SQL, no typos slipping through unnoticed.

```
Your code → Specification → Hibernate (ORM) → JDBC → Database
```

**Specification approach:**

```java
Specification<Product> spec =
    Specification.where(ProductSpecification.nameContains(filter.getName()))
        .and(ProductSpecification.hasId(filter.getId()))
        .and(ProductSpecification.hasSku(filter.getSku()))
        .and(ProductSpecification.minPrice(filter.getMinPrice()))
        .and(ProductSpecification.maxPrice(filter.getMaxPrice()))
        .and(ProductSpecification.minStock(filter.getMinStock()))
        .and(ProductSpecification.maxStock(filter.getMaxStock()))
        .and(ProductSpecification.isActive(filter.getActive()))
        .and(ProductSpecification.discontinuedBefore(filter.getDiscontinuedAt()))
        .and(ProductSpecification.hasCategory(filter.getCategory()));

Page<Product> page = repo.findAll(spec, pageable);
return page.map(Mapper::toSummaryDTO);
```

Each filter handles its own null check internally — if a filter wasn't sent in the request, it's ignored automatically.

> Specifications replaced the native query for the admin product endpoints. The native query is still used for the `ProductStockView` projection because that result combines multiple tables with `LEFT JOIN` and computed values — Specifications only work when the result maps to a single entity.
