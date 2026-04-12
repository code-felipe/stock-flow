# Product CRUD API

Brief documentation of the **Product CRUD module** in the API.

---

## Product CRUD

Supports full product lifecycle management:

- Create products
- Update product details
- Soft delete products (logical deletion)
- Retrieve active catalog items

Soft deletion helps preserve historical integrity and prevents accidental data loss.

---

## Product Entity

The `Product` resource represents inventory/catalog products with attributes such as:

- `id`
- `name`
- `description`
- `price`
- `sku`
- `imageUrl`
- `active`
- `createdAt`
- `discontinuedAt`
- `Category`

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
- `categoryIds` is required and replaces existing categories
- SKU uniqueness is validated before update

Example 1:
```json
{
  "price": 19.99,
  "stock": 10, // about to be deleted - inventory has been added to this
  "categoryIds": [1, 3]
  
}

Example 2:
```json
{
  "name": "Gold Minimalist Emerald Ring",
  "description": "18k gold plated minimalist ring with smooth finish and emerald stone of 20k",
  "price": 680.29,
  "sku": "JWL-RNG-001",
  "imageUrl": "https://example.com/images/gold-ring.jpg",
  "stock": 25.00,// about to be deleted - inventory has been added to this
  "categoryIds": [1, 2]
}

**SKU Update Algorithm (Unique Constraint Handling)**

SKU is:

- Required

- Unique

- Editable

- Must allow keeping the same value during update

 To prevent duplicate constraint violations, the system:
 
1. Trims the incoming SKU

2. Compares it with the current product SKU

3. Only checks database uniqueness if the SKU changed

4. Throws a controlled exception if already used by another product

Example 3:
```java
if (hasText(dto.getSku())) {
	String newSku = dto.getSku().trim();
		if (!newSku.equals(product.getSku())) {
			if (repo.existsBySkuAndIdNot(newSku, product.getId())) {
				throw new IllegalArgumentException("SKU already in use: " + newSku);
				}
        product.setSku(newSku);
    }
}

This prevents:

- False duplicate errors when keeping the same SKU

- Raw database constraint exceptions

- Race condition exposure (DB constraint remains as final safety layer)

Category Update (Bidirectional Many-to-Many):

**Relationship**

- Product ↔ Category

- Bidirectional

- Managed through product_categories join table

- Product is the owning side

**Rules**

- categoryIds is required

- The list replaces existing categories

- All IDs must exist in the database

- Validation occurs before persisting

**Algorithm**
```java
Set<Long> ids = dto.getCategoryIds();

// Retrieve categories
List<Category> cats = catRepo.findAllById(ids);

// Validate existence
if (cats.size() != ids.size()) {
	Set<Long> foundIds = cats.stream()
		.map(Category::getId)
		.collect(Collectors.toSet());
	Set<Long> missing = ids.stream()
		.filter(id -> !foundIds.contains(id))
		.collect(Collectors.toSet());
	throw new ResourceNotFoundException("Category not found: " + missing);
}
// Replace relationship
product.getCategories().clear();
product.getCategories().addAll(cats);

## Delete (Soft Delete)

**PATCH /products/{id}/discontinue (example)**

Products are not physically deleted.

Instead:

- active = false

- Optionally set discontinuedAt = now
