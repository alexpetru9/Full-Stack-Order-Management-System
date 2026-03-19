# 📦 Full-Stack Order Management System

### 🚀 Prezentare Generală
Acest proiect reprezintă o aplicație de tip **Enterprise** pentru gestionarea utilizatorilor, produselor și comenzilor. Accentul principal a fost pus pe utilizarea unei **arhitecturi stratificate** și pe asigurarea integrității datelor prin validări complexe pe ambele părți (Client și Server).

## 🛠️ Stack Tehnologic
* **Backend:** Java 17, Spring Boot, Spring Data JPA, Hibernate
* **Frontend:** Angular (TypeScript), HTML5, SCSS
* **Bază de date:** PostgreSQL (Relational)
* **Arhitectură:** Layered Architecture (Controller-Service-Repository)

## 🏗️ Modelul de Date (ERD)
Sistemul gestionează relații complexe între entități:
* **Person (1:M) Orders:** Un utilizator poate plasa mai multe comenzi.
* **Product (M:M) Category:** Produsele pot aparține mai multor categorii, gestionate printr-o tabelă de joncțiune (`category_product`).

## ✨ Funcționalități Cheie
* **Validare Avansată:** Logica de business previne duplicatele (email-uri identice) și impune politici de securitate pentru parole.
* **Global Exception Handling:** Implementarea unui handler global pentru erori care oferă răspunsuri JSON consistente către Frontend.
* **Interfață Dinamică:** Aplicație Angular modularizată cu servicii dedicate pentru apeluri API și rute protejate.

## 🚀 Cum se rulează
1. **Backend:** - Configurează baza de date în `application.properties`.
   - Rulează `./mvnw spring-boot:run`.
2. **Frontend:**
   - `npm install`
   - `ng serve` (accesibil la `localhost:4200`).

---
**Autor:** Asaftei Alexandru-Petru  
*Proiect realizat ca parte a evaluării pentru competențe de dezvoltare Full-Stack.*
