# Dashboard Analytics

The Dashboard provides a centralized view of sales performance and inventory status. It combines key business indicators (KPIs) with operational metrics, allowing administrators to monitor revenue, sales activity, and stock health from a single interface.

---

# Dashboard Components

## Year Filter

### Purpose

The year selector allows users to analyze sales metrics for a specific year.

Unlike a static dropdown, the available years are loaded dynamically from confirmed sales records, ensuring users can only select years containing data.

### Query

```java
@Query("""
    SELECT DISTINCT YEAR(o.orderDate)
    FROM Order o
    WHERE o.orderStatus = 'CONFIRMED'
    ORDER BY YEAR(o.orderDate) DESC
""")
List<Integer> findAvailableSalesYears();
```

### UI

Located in the dashboard header.

```
Year
[ 2027 ▼ ]
```

### Benefits

- Eliminates hardcoded years.
- Prevents empty reports.
- Automatically includes new years as sales are generated.

---

# KPI Cards

## Average Sale Value

### Purpose

Displays the average value of every confirmed order placed during the selected year.

This KPI helps understand customer purchasing behavior and average spending.

### Query

```java
@Query("""
    SELECT AVG(o.total)
    FROM Order o
    WHERE YEAR(o.orderDate) = :year
      AND o.orderStatus = :status
""")
Double getAverageOrderValue(...);
```

### Formula

```
Average Sale Value =
Total Revenue / Number of Confirmed Orders
```

### Dashboard Card

```
Average Sale Value

$184.75

Per confirmed order
```

---

## Total Revenue

### Purpose

Displays the total revenue generated from confirmed orders during the selected year.

It represents the company's gross sales before any additional business calculations.

### Query

```java
@Query("""
    SELECT SUM(o.total)
    FROM Order o
    WHERE orderStatus = 'CONFIRMED'
      AND YEAR(orderDate) = :year
""")
Double totalRevenueByYear(...);
```

### Dashboard Card

```
Total Revenue

$254,830.50

Per confirmed order
```

---

## Products Sold

### Purpose

Shows the total number of product units sold during the selected year.

Unlike counting orders, this metric sums every product quantity contained within confirmed orders.

### Query

```java
@Query("""
    SELECT COALESCE(SUM(oi.quantity), 0)
    FROM Order o
    JOIN o.orderItems oi
    WHERE YEAR(o.orderDate) = :year
      AND o.orderStatus = :status
""")
Long getProductsSold(...);
```

### Dashboard Card

```
Products Sold

1,245

Units sold
```

---

## Low Stock Items

### Purpose

Displays how many products currently require replenishment.

A product is considered low in stock when its available quantity falls below the configured minimum stock level.

### Business Rule

```
onHand < minStock
```

Products exactly at the minimum stock level are considered acceptable.

### Query

```java
@Query("""
    SELECT new LowStockProductDTO(
        i.product.name,
        i.onHand,
        i.minStock
    )
    FROM Inventory i
    WHERE i.onHand < i.minStock
    ORDER BY i.onHand ASC
""")
List<LowStockProductDTO> findLowStockProducts(...);
```

### Dashboard Card

```
Low Stock Items

5

Need restocking
```

---

## Inventory Health

### Purpose

Measures the percentage of products whose current inventory satisfies the configured minimum stock requirement.

This KPI provides a quick overview of the overall health of the inventory.

### Business Rule

A product is considered healthy when:

```
onHand >= minStock
```

### Queries

Healthy products

```java
@Query("""
    SELECT COUNT(i)
    FROM Inventory i
    WHERE i.onHand >= i.minStock
""")
Long healthyProducts();
```

Total inventory records

```java
@Query("""
    SELECT COUNT(i)
    FROM Inventory i
""")
Long totalProducts();
```

### Formula

```
Inventory Health (%) =
Healthy Products / Total Products × 100
```

### Status Colors

| Health | Status | Color |
|---------|--------|--------|
| 80% - 100% | Healthy | 🟢 Green |
| 50% - 79% | Warning | 🟡 Yellow |
| Below 50% | Critical | 🔴 Red |

### Dashboard Card

```
Inventory

Health 86%

Healthy
```

The icon color changes automatically according to the calculated health percentage.

---

# Dashboard Charts

## Sales by Month

### Purpose

Visualizes monthly sales performance throughout the selected year.

The chart displays:

- Total confirmed orders.
- Revenue generated each month.

This visualization helps identify seasonal trends, growth patterns, and fluctuations in business activity.

### Query

```java
@Query("""
    SELECT new OrdersByMonthDTO(
        YEAR(o.orderDate),
        MONTH(o.orderDate),
        COUNT(o),
        SUM(o.total)
    )
    FROM Order o
    WHERE YEAR(o.orderDate) = :year
    GROUP BY YEAR(o.orderDate), MONTH(o.orderDate)
    ORDER BY YEAR(o.orderDate), MONTH(o.orderDate)
""")
List<OrdersByMonthDTO> countOrdersByMonth(...);
```

### Returned Data

| Field | Description |
|--------|-------------|
| Year | Selected year |
| Month | Month number |
| Orders | Number of confirmed orders |
| Revenue | Revenue generated during the month |

### Dashboard

```
Sales by Month

Jan ────────
Feb ───────────────
Mar ─────────────────────
...
```

The chart updates automatically whenever the selected year changes.

---

# Inventory Monitoring

## Top 10 Products to Restock

### Purpose

Displays the ten products with the lowest stock levels that require immediate replenishment.

Products are ordered from the lowest available stock to the highest priority for restocking.

### Business Rule

```
onHand < minStock
```

### Query

```java
@Query("""
    SELECT new LowStockProductDTO(
        i.product.name,
        i.onHand,
        i.minStock
    )
    FROM Inventory i
    WHERE i.onHand < i.minStock
    ORDER BY i.onHand ASC
""")
List<LowStockProductDTO> findLowStockProducts(Pageable pageable);
```

### Information Displayed

Each product includes:

- Product name
- Current stock
- Stock percentage relative to the minimum stock
- Visual progress indicator

Example

```
Silver Heart Necklace

8 units

Stock Level
80%

██████████████
```

### Progress Bar Colors

| Stock Level | Color | Meaning |
|--------------|--------|----------|
| 80% - 100% | 🟢 Green | Healthy stock |
| 50% - 79% | 🟡 Yellow | Monitor stock |
| Below 50% | 🔴 Red | Immediate restocking required |

If no products require replenishment, the dashboard displays:

```
✔ Inventory levels are healthy
```

instead of the product list.