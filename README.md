# ShopShere – E-Commerce Backend

ShopShere is a full-stack e-commerce project designed to provide a seamless online shopping experience while demonstrating practical backend software engineering concepts. The backend is developed using Java and Spring Boot, with a focus on RESTful API development, database management, authentication, and maintainable application architecture.

## Key Features

**1. User Authentication and Authorization**  
ShopShere includes user registration and login functionality with JWT-based authentication. Protected endpoints require a valid access token, allowing authenticated users to access personalized features securely.

**2. Product Management**  
The application supports product operations, including creating, retrieving, updating, and deleting products. Product information includes name, description, price, category, brand, stock quantity, image URL, and active status. DTOs help control the data exchanged between clients and the backend.

**3. Category Management**  
Category functionality organizes products into meaningful groups, making product discovery and browsing easier for customers.

**4. Shopping Cart**  
Users can add products to their carts, update quantities, and remove items. The backend handles stock availability, product status, quantity validation, item subtotals, and the overall cart total.

**5. Wishlist Management**  
Users can maintain a wishlist of their favorite products, keeping desired items separate from their shopping cart.

## Technology Stack

- **Language:** Java
- **Backend Framework:** Spring Boot
- **Database:** MySQL
- **Data Access:** Spring Data JPA and Hibernate
- **Security:** Spring Security and JWT
- **API Testing:** Postman
- **Build Tool:** Maven
- **Version Control:** Git and GitHub

## Architecture and Development Practices

The project follows a layered architecture that separates controllers, services, repositories, entities, and DTOs. Controllers handle incoming HTTP requests, services implement business logic, repositories interact with the database, and DTOs provide structured API responses.

The application is being developed incrementally, with an emphasis on readable code, validation, reusable components, and clear separation of responsibilities. Future improvements include production deployment, Docker containerization, Redis caching, rate limiting, API gateway integration, and microservices architecture.

ShopShere serves as a practical learning project for strengthening Java backend development skills and understanding how real-world e-commerce applications are designed and deployed.
