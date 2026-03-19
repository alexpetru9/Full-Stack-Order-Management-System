# 📦 Full-Stack Order Management System

### 🚀 Overview
This project is an **Enterprise-grade** application designed for managing users, products, and orders. The primary focus was placed on utilizing a **layered architecture** and ensuring data integrity through complex validations on both sides (Client and Server).



## 🛠️ Tech Stack
* **Backend:** Java 17, Spring Boot, Spring Data JPA, Hibernate
* **Frontend:** Angular (TypeScript), HTML5, SCSS
* **Database:** PostgreSQL (Relational)
* **Architecture:** Layered Architecture (Controller-Service-Repository)

## 🏗️ Data Model (ERD)
The system manages complex relationships between entities:
* **Person (1:M) Orders:** A user can place multiple orders.
* **Product (M:M) Category:** Products can belong to multiple categories, managed via a junction table (`category_product`).



## ✨ Key Features
* **Advanced Validation:** Business logic prevents duplicates (identical emails) and enforces security policies for passwords.
* **Global Exception Handling:** Implementation of a global error handler that provides consistent JSON responses to the Frontend.
* **Dynamic Interface:** Modularized Angular application with dedicated services for API calls and protected routes.

## 🚀 How to Run
1. **Backend:** - Configure the database in `application.properties`.
   - Run `./mvnw spring-boot:run`.
2. **Frontend:**
   - `npm install`
   - `ng serve` (accessible at `localhost:4200`).

---
**Author:** Asaftei Alexandru-Petru  
*Project developed as part of a Full-Stack development competency assessment.*
