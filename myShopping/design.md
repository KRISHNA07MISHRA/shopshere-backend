# Design Reference and Decision Log

This is the file-level design reference for the current Maven project. It records what each source/config/resource file does, how it is implemented, the algorithm or framework behavior it uses, and why the design appears to exist. Where the repository does not state the original author's reason, the rationale is labeled **inferred** rather than treated as fact.

## Scope and accuracy

- The inventory covers application Java files, tests, build/runtime configuration, and static assets currently under `src/` and the project root.
- `target/` is generated Maven output, not hand-maintained source; it is ignored and intentionally not inventoried file by file.
- `.env` is a private local configuration file. Its contents are intentionally not documented here. Never put its values in this file, source control, screenshots, or logs.
- When implementation changes, update the affected file entry and append a dated decision entry below for meaningful changes to dependencies, design, API, data, security, or behavior.

## How to add a decision

For a non-trivial change, append an entry with: **date, status, context, decision, reason, algorithm/library, alternatives and accepted trade-off, consequences, changed files, and verification**. Distinguish explicitly documented rationale from inference. Preserve superseded decisions and mark them as superseded.

Suggested format:

```text
## YYYY-MM-DD — Short decision name
- Status: Accepted | Superseded | Proposed
- Context:
- Decision:
- Reason:
- Algorithm/library:
- Alternatives and accepted trade-offs:
- Consequences:
- Files:
- Verification:
```

## Application and configuration files

### Entry point and Spring configuration

| File | What it does / approach | Why it exists; algorithm or trade-off |
|---|---|---|
| `src/main/java/com/ShopNow/myShopping/MyShoppingApplication.java` | Declares the `@SpringBootApplication` class and `main`; calls `SpringApplication.run`. | Standard Spring Boot JVM entry point. Component scanning and auto-configuration are framework algorithms; no domain algorithm is implemented here. |
| `src/main/java/com/ShopNow/myShopping/Config/SecurityConfig.java` | Defines the Spring Security filter chain, URL authorization, stateless sessions, CORS integration, BCrypt password encoder, authentication manager, and JWT role-claim converter. | Centralizes HTTP security policy. Uses BCrypt with work factor 12; JWT roles from claim `role` get prefix `ROLE_` to align with `hasRole`. Disables CSRF and sessions for this stateless bearer-token API. This is convenient for APIs but requires careful token and browser-origin handling. The source comment cites the public registration REST endpoint as CSRF context; global CSRF disable is broader than that comment. |
| `src/main/java/com/ShopNow/myShopping/Config/JwtConfig.java` | Reads `app.jwt.secret`, Base64-decodes it, creates an HMAC `SecretKey`, then exposes Nimbus `JwtEncoder` and `JwtDecoder` configured for HS256. | Same symmetric key signs and verifies tokens; compact and simple to operate for a single API. Trade-off: the shared secret must remain confidential anywhere tokens are signed or verified; key rotation invalidates old tokens. Base64 is an encoding, not encryption. |
| `src/main/java/com/ShopNow/myShopping/Config/CorsConfig.java` | Implements `WebMvcConfigurer`; allows configured HTTP methods for all paths from `http://localhost:5173`. | Enables the local browser frontend to call the API cross-origin. A fixed development origin is safer than wildcard origins with credentials, but deployment origins must be configured deliberately. |
| `src/main/resources/application.properties` | Names the app; imports optional project-root `.env` using Spring's properties extension hint; binds DB credentials and JWT secret from properties/environment; configures MySQL, Hibernate, SQL logging, and 900,000 ms JWT expiry. | Keeps secret values outside tracked application properties. Environment variables can supply the same names in deployment. `ddl-auto=update` is convenient for development but is not a substitute for reviewed schema migrations; SQL display is useful while developing but can be noisy or expose query details in logs. |

### HTTP controllers

| File | What it does / approach | Why it exists; algorithm or trade-off |
|---|---|---|
| `src/main/java/com/ShopNow/myShopping/Controller/AuthController.java` | Exposes `POST /auth/register`, `POST /auth/login`, and `GET /auth/me`. Delegates register/login to `AuthService`; `/me` returns email subject and `name` claim from authenticated JWT. Request records on register/login use `@Valid`. | Keeps HTTP routing/status/body work separate from authentication business logic. `/register` returns 201; login returns 200. `/me` depends on the security filter having verified a bearer token first. |
| `src/main/java/com/ShopNow/myShopping/Controller/ProductController.java` | Exposes product list, get-by-id, create, full update, partial update, and delete routes under `/product`; delegates to `ProductService`. | REST-style CRUD routes. DTO use avoids directly returning the Product entity for this API. `PUT` sends the full request model; `PATCH` uses a partial model. `RequestParam` and `Product` imports are not used by active handlers and appear to be leftovers. |
| `src/main/java/com/ShopNow/myShopping/Controller/CategoryController.java` | Exposes list/create/update/patch/delete under `/category`; delegates operations to `CategoryService`, except it binds request data to `Category`/`Map` and returns entities. | Provides category CRUD. Returning entities is simpler but couples response shape to persistence model; a DTO layer would reduce that coupling. |
| `src/main/java/com/ShopNow/myShopping/Controller/Cart/CartController.java` | Exposes `GET /cart`; maps authenticated principal name to `AppUser` via `AppUserRepo`, then asks `CartService` for summary DTO. | Resolves ownership from identity established by Spring Security, not from a caller-supplied user ID. This handler expects an existing cart and currently returns 404 if absent. |
| `src/main/java/com/ShopNow/myShopping/Controller/Cart/CartIemController.java` | (Class/file spelling is `CartIem`, as currently named.) Exposes list/add/update/remove under `/cart/items`; helper checks authentication and looks up the user; delegates to `CartItemService`. Defines request records for add and quantity update. | Keeps cart-item routes grouped and passes a resolved user into service methods. Item lookup later includes cart ID, preventing a user from updating another user's item by guessing its ID. |
| `src/main/java/com/ShopNow/myShopping/Controller/Wishlist/WishlistController.java` | Exposes list/add/remove under `/wishlist`; resolves user from authentication, looks up products directly through `productRepo`, then delegates wishlist operations. | Identity is derived from authenticated principal. Product existence lookup currently resides in the controller, unlike the stricter controller/service/repository layering used by products. Responses expose `Wishlist` entities. |

### Services and use cases

| File | What it does / approach | Why it exists; algorithm or trade-off |
|---|---|---|
| `src/main/java/com/ShopNow/myShopping/Service/AuthService.java` | `register` rejects duplicate email, hashes password, assigns customer role/active flag, saves user, returns response. `login` authenticates through Spring Security, reloads user, builds JWT claims, signs token, returns login DTO. | Centralizes identity use cases. Uses Spring Security authentication instead of comparing password strings manually; token expiry uses current time plus configured duration. Role assignment is server-side. Duplicate check is user-friendly but can race under concurrent registration; the database unique email constraint remains important. |
| `src/main/java/com/ShopNow/myShopping/Service/CustomUserDetailsService.java` | Implements `UserDetailsService.loadUserByUsername(email)`: finds the account, adapts hash/role/active state into Spring Security `UserDetails`. | Adapter between the application's `AppUser` model and Spring Security's username/password authentication provider. Email acts as username. |
| `src/main/java/com/ShopNow/myShopping/Service/ProductService.java` | Implements CRUD. Uses `ProductMapper`, repository operations, existence checks and `@Transactional` boundaries. | Separates use-case operations from request routing and persistence interfaces. Mapping is explicit. Missing records currently throw generic `RuntimeException` in some paths, so error status/message consistency is a known trade-off. |
| `src/main/java/com/ShopNow/myShopping/Service/CategoryService.java` | Implements list/save/full update/partial update/delete. Patch checks field types and nonblank category names before applying only supplied keys. | The patch algorithm is a key-presence check followed by per-field validation. It avoids overwriting omitted fields; runtime `Map<String,Object>` input is flexible but less type-safe than a patch DTO. |
| `src/main/java/com/ShopNow/myShopping/Service/Cart/CartService.java` | `getOrCreateCart` searches by user and creates if absent. `getCartDetails` requires an existing cart, maps items, computes per-line subtotal, total item quantity and total amount. | Uses `BigDecimal` for money arithmetic to avoid binary floating-point currency errors; quantity totals are integer sums. Note `getCartDetails` and `getOrCreateCart` intentionally have different absent-cart behavior. |
| `src/main/java/com/ShopNow/myShopping/Service/Cart/CartItemService.java` | Lists, adds, changes quantity, removes items. Validates positive quantity, checks product active/stock, merges same product into one cart row, checks requested quantity against stock, and scopes item lookup by cart. Creates response DTOs with subtotal. | The add algorithm is find-or-create then quantity accumulation; update replaces quantity. Transactional methods keep related cart writes together. `unitPrice` is set to the product's current price on add/update, so the implementation refreshes price rather than preserving an immutable historical price despite the entity comment. |
| `src/main/java/com/ShopNow/myShopping/Service/WishList/WishlistService.java` | Lists by user, adds only if user/product pair is not already present, removes a matching row or fails. | Find-or-return-existing makes repeated add calls idempotent in normal sequential use. A unique database constraint protects the pair against duplicates; concurrent inserts still require handling constraint conflicts if exposed in practice. |

### Mapper, DTOs, entities, and repositories

| File | What it does / data/algorithm | Why it exists; trade-off |
|---|---|---|
| `src/main/java/com/ShopNow/myShopping/Mapper/ProductMapper.java` | `toEntity`, `updateEntity`, `patchEntity`, and `toResponse` copy product fields. Create defaults `active` to false when omitted. Patch changes non-null fields only. | Handwritten mapping avoids a mapper dependency and makes field behavior visible. Null-as-omitted means a PATCH request cannot clear a field to null. |
| `src/main/java/com/ShopNow/myShopping/DTO/ProductRequestDTO.java` | Mutable product create/full-update input: name, description, price, stock, category, brand, image URL, active, trending; Lombok getter/setter. | Prevents direct entity binding for product writes. No validation annotations are currently present. |
| `src/main/java/com/ShopNow/myShopping/DTO/ProductPatchRequestDTO.java` | Same editable fields as product input, all nullable to represent omitted changes. | Supports partial updates through mapper null checks; trade-off: cannot distinguish omitted from explicit null. |
| `src/main/java/com/ShopNow/myShopping/DTO/ProductResponseDTO.java` | Mutable outward product shape, including generated ID and product fields. | Separates product response contract from entity persistence details. Lombok generates accessors. |
| `src/main/java/com/ShopNow/myShopping/DTO/Login/LoginRequest.java` | Immutable record for email/password; validates not blank, email format, email max 254, password length 8–72. | Record is a compact value carrier; bean validation rejects malformed login input before service execution. |
| `src/main/java/com/ShopNow/myShopping/DTO/Login/LoginResponse.java` | Immutable record returning ID, name, email, role and JWT token. | Makes login response shape explicit. The token is intentionally returned to the client for bearer use. |
| `src/main/java/com/ShopNow/myShopping/DTO/Register/RegisterRequest.java` | Immutable record for name/email/password; validates nonblank values, name max 100, email format/max 254, password length 8–72 characters. | Explicit request contract and early validation. The password limit is expressed in characters; BCrypt's effective input limit is byte-based, so Unicode inputs may need separate treatment. |
| `src/main/java/com/ShopNow/myShopping/DTO/Register/RegisterResponse.java` | Immutable record returning ID/name/email/role, not password/hash. | Explicit response and avoids returning the credential field. |
| `src/main/java/com/ShopNow/myShopping/DTO/Cart/CartItemResponseDTO.java` | Immutable item response: IDs, product name/image, quantity, unit price and computed subtotal. | Prevents exposing the full JPA cart-item/product graph and carries display-ready line data. |
| `src/main/java/com/ShopNow/myShopping/DTO/Cart/CartResponseDTO.java` | Immutable cart summary: cart ID, item DTO list, quantity total and monetary total. | Aggregated response avoids requiring clients to calculate totals from raw persistence records. |
| `src/main/java/com/ShopNow/myShopping/Entity/AppUser.java` | JPA table `app_users`; identity ID; required name/password hash/role/active; unique required email; role stored as enum string. Lombok accessors. | Represents account persistence. Unique email supports identity lookup; storing enum names avoids ordinal coupling. The password field is a hash despite its property name. |
| `src/main/java/com/ShopNow/myShopping/Entity/Role.java` | Enum values `USER_ADMIN`, `USER_CUSTOMER`. | Restricts roles to known constants rather than free-form strings; names coordinate with JWT role claims/authorization. |
| `src/main/java/com/ShopNow/myShopping/Entity/Product.java` | JPA entity with identity ID, name/description, `BigDecimal` price, stock, category/brand/image URL, active/trending. Lombok accessors. | Product data model. `BigDecimal` supports exact decimal prices. Category is currently a string field, not a JPA relation to `Category`. |
| `src/main/java/com/ShopNow/myShopping/Entity/Category.java` | JPA entity with identity ID, name, image URL, active flag; Lombok accessors. | Category persistence model. No explicit `@Table` or relationship mapping; default JPA naming/mapping applies. |
| `src/main/java/com/ShopNow/myShopping/Entity/Cart/Cart.java` | `carts` entity; lazy one-to-one required link to `AppUser`; unique user ID. | Enforces one cart per user at the database mapping level; lazy fetch avoids always loading user details with cart. |
| `src/main/java/com/ShopNow/myShopping/Entity/Cart/CartItem.java` | `cart_items`; required lazy many-to-one links to cart/product; unique `(cart_id, product_id)`; required quantity and decimal unit price. | Keeps one row per product in a cart. Decimal precision is explicit. Price comment says captured on add, while service currently updates it to current price on add/update—keep documentation/behavior aligned when deciding price policy. |
| `src/main/java/com/ShopNow/myShopping/Entity/Wishlist/Wishlist.java` | `wishlists`; required lazy user/product links; unique `(user_id, product_id)`. | Models a user/product association and guards duplicate logical entries at the database level. |
| `src/main/java/com/ShopNow/myShopping/Repository/AppUserRepo.java` | Spring Data JPA repository; adds `findByEmail` and `existsByEmail`. | Derived-query naming delegates SQL generation to Spring Data; used by registration, login, user lookup and security. |
| `src/main/java/com/ShopNow/myShopping/Repository/CategoryRepo.java` | `JpaRepository<Category,Long>` with inherited CRUD. | No custom query is needed for current category use cases. |
| `src/main/java/com/ShopNow/myShopping/Repository/productRepo.java` | `JpaRepository<Product,Long>` with inherited CRUD. | No custom query currently; class name begins lowercase in existing source. |
| `src/main/java/com/ShopNow/myShopping/Repository/Cart/CartRepo.java` | `JpaRepository<Cart,Long>` plus derived `findByUser`. | Retrieves the unique cart for an account. |
| `src/main/java/com/ShopNow/myShopping/Repository/Cart/CartItemRepo.java` | `JpaRepository<CartItem,Long>` plus `findByCartId`, `findByCartAndProduct`, and ownership-scoped `findByIdAndCartId`. | Derived queries cover list, duplicate-product lookup, and item ownership checks without handwritten SQL. |
| `src/main/java/com/ShopNow/myShopping/Repository/Wishlist/WishlistRepo.java` | `JpaRepository<Wishlist,Long>` plus find-by-user and find-by-user/product. | Supports user's list and idempotent add/remove lookup. |

### Tests and resources

| File/folder | What it does | Reason/limits |
|---|---|---|
| `src/test/java/com/ShopNow/myShopping/MyShoppingApplicationTests.java` | A `@SpringBootTest` with one `contextLoads()` test. | Smoke-checks Spring context creation; it does not assert endpoints, business rules, security, or database contents. |
| `src/main/resources/static/images/Hero/hero.jpg` | Hero/banner image served as a static resource. | Visual frontend asset, not processed by Java business logic. |
| `src/main/resources/static/images/Categories/` | Category images: `apparel.jpg`, `Beauty.jpg`, `computer.jpg`, `electronics.jpg`, `fitness.jpg`, `furniture.jpg`, `grocery.jpg`, `kitchenware.jpg`, `Sports.jpg`, `wearable_Tech.jpg`. | Static visual assets; paths can be referenced by category image URLs. Case-sensitive filesystems preserve the actual capitalization. |
| `src/main/resources/static/images/products/` | Product images: `bottle.jpg`, `chair.jpg`, `cokies.jpg`, `cream.jpg`, `jacket.jpg`, `keyboard.jpg`, `mat.jpg`, `pan.jpg`, `Speaker.jpg`, `watch.jpg`. | Static visual assets; product records can refer to them through `imageUrl`. Filenames/capitalization are as present in source. |
| `src/main/resources/templates/` | Currently empty. | No server-rendered template flow is implemented in this project snapshot. |

## Root/build files

| File | What it does | Why it exists / trade-off |
|---|---|---|
| `pom.xml` | Maven coordinates, Spring Boot parent, Java 21 setting, dependencies, Spring Boot and compiler plugins. Dependencies include Spring MVC, Spring Data JPA, MySQL driver, Spring Security, OAuth2 resource-server/JWT integration, Jakarta validation, Lombok, and Spring test starters. | Maven resolves/builds the app. The dependency stack aligns with REST, persistence, authentication, validation and tests used in code. Lombok reduces accessor boilerplate; handwritten product mapping avoids a mapper package. Exact original library-selection rationale is not recorded, so these purposes are inferred from usage. |
| `mvnw` / `mvnw.cmd` | POSIX and Windows Maven wrapper launchers. | Allow Maven commands without a separately installed Maven. |
| `.mvn/wrapper/maven-wrapper.properties` | Pins Maven Wrapper 3.3.4 configuration and Maven 3.9.16 distribution URL. | Makes wrapper download/build version reproducible. |
| `.gitattributes` | Sets LF for `mvnw` and CRLF for `*.cmd`. | Avoids platform-specific line-ending damage to shell and Windows scripts. |
| `.gitignore` | Excludes Maven output, IDE metadata and `.env`; keeps generated files/local secrets out of normal commits. | Prevents build artifacts and local credentials from being tracked. If `.env` was already tracked, ignore rules alone do not untrack it. |
| `.env.example` | Safe names-only template for `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`. | Shows required local configuration keys without storing actual values. |
| `.env` | Local-only configuration consumed by Spring's optional config import. | Contains private machine-specific values; ignored and deliberately not enumerated/read into this document. |
| `HELP.md` | Generated Spring Boot getting-started links and note about Maven parent metadata overrides. | Reference material; does not participate in runtime execution. |
| `design.md` | This file: file-level design reference and decision log. | Keeps future modifications explainable and provides rationale/trade-offs in addition to code descriptions. |
| `flow.md` | Companion architecture/call-flow map. | Explains runtime order and file/function interactions; update whenever behavior or call paths change. |

## Dependency/library purposes and trade-offs

The build currently uses Spring Boot 4.1.1 and Java 21. The concrete dependency declarations are in `pom.xml`:

- **Spring MVC/Web:** REST annotations, request binding, response serialization and static resource serving.
- **Spring Data JPA + Hibernate:** repository abstraction, ORM mapping and transaction integration. Trade-off: ORM conventions/schema updates can obscure SQL and require migrations for controlled production evolution.
- **MySQL Connector/J:** runtime JDBC connection to MySQL.
- **Spring Security + OAuth2 resource server:** filter chain, authentication/authorization and bearer JWT processing.
- **Nimbus JOSE/JWT integration:** provided through the security JWT stack for HS256 encode/decode APIs used in `JwtConfig`.
- **Jakarta Validation:** validates login/registration records when controller methods invoke `@Valid`.
- **Lombok:** generates getters/setters in selected mutable DTOs/entities. Trade-off: compile-time code generation can hide methods from casual readers/IDE setups.
- **JUnit/Spring test starters:** Spring context test support; current coverage is only context startup.

The source does not state why these versions/libraries were originally selected. Descriptions above are code-grounded purposes, not claims of recorded historical intent.

## Current accepted decisions

### DEC-001 — Layered Spring REST application

- **Status:** Current; rationale inferred.
- **Decision:** Route HTTP through controllers, business operations through services, and persistence through Spring Data repositories.
- **Reason:** Separates transport binding, use-case rules and database access; dependencies are constructor-injected.
- **Accepted trade-off:** More classes/indirection than a small direct controller-to-repository app. Some current paths bypass this consistency (wishlist product lookup, category entity responses).

### DEC-002 — DTOs and explicit product mapper

- **Status:** Current; rationale inferred.
- **Decision:** Use request/response DTOs and a handwritten mapper for product operations.
- **Reason:** Makes product API and persistence representations independently evolvable; field conversions/defaults are explicit.
- **Accepted trade-off:** Repetitive mapping code; null-as-omitted PATCH cannot clear a field. Other APIs are less isolated from entity changes.

### DEC-003 — Stateless HS256 JWT authentication

- **Status:** Current; stateless session policy is explicit; original library rationale inferred.
- **Decision:** Issue and verify HS256 JWTs with a shared environment-provided secret; tokens expire after 15 minutes; server keeps no HTTP session.
- **Reason:** Supports bearer authentication across REST requests with low server-side session state.
- **Accepted trade-off:** Signing and verification depend on protecting the same secret; rotation invalidates existing tokens. Symmetric keys are simpler but do not separate signer and verifier roles.

### DEC-004 — Externalized local secrets

- **Status:** Current.
- **Decision:** Bind datasource/JWT values from environment or imported local `.env`; ignore `.env` and track only `.env.example`.
- **Reason:** Avoids placing credentials in `application.properties` committed to source control.
- **Accepted trade-off:** Developers must configure local variables; optional file import does not make required values optional. Git ignore cannot clean secrets from prior history.

### DEC-005 — Database constraints backstop application checks

- **Status:** Current; rationale inferred.
- **Decision:** Use uniqueness constraints for user email, one cart per user, one cart item per cart/product, and one wishlist row per user/product.
- **Reason:** Enforces identity/association invariants in the database as well as application code.
- **Accepted trade-off:** Concurrent attempts may surface uniqueness exceptions; services do not currently translate every conflict into a domain-specific response.

## Open decisions / review points

1. Decide whether production schema changes should use versioned migrations instead of Hibernate `ddl-auto=update`.
2. Decide whether SQL logging is development-only and make it environment-specific if needed.
3. Clarify cart price semantics: stored comment says price captured at add, service refreshes on add/update.
4. Standardize entity-to-DTO boundaries for category and wishlist endpoints.
5. Consider typed category patch DTOs and consistent request validation/error handling.
6. Add behavioral/security/repository tests beyond context startup.
7. For every future dependency or algorithm change, log why that library/algorithm is appropriate and why the accepted alternative/trade-off is reasonable.
