# RewardPointCalculatorUpdated
This is the service to calculate reward points

RewardPointCalculator
Overview
The RewardPointCalculator is a Spring Boot-based application that helps a retailer manage a rewards program. Customers earn reward points based on their purchase transactions, with points awarded as follows:

2 points for each dollar spent over $100.
1 point for each dollar spent between $50 and $100.
The application exposes RESTful APIs for managing customer data, transactions, and reward points.

Key Features
Customer Registration & Login: Allows customers to register, log in, and authenticate via JWT tokens.
Transaction Management: Customers can add, edit, and delete their transactions.
Reward Points Calculation: Points are calculated for each transaction and can be retrieved by month or year.
Reports: Customers can view their total reward points for a specific month or year.
API Endpoints
Customer APIs:

POST /customers/register: Register a new customer.
POST /customers/login: Customer login.
Transaction APIs:

GET /transactions/{customerId}: Get all transactions for a customer.
POST /transactions/{customerId}: Add a new transaction.
PUT /transactions/{customerId}/{transactionId}: Edit a transaction.
DELETE /transactions/{customerId}/{transactionId}: Delete a transaction.
Reward Points APIs:

GET /reward-points/{month}/{year}: Get reward points for a specific month and year.
GET /reward-points/all: Get all reward points for a customer.
Technologies Used
Spring Boot for backend development.
Spring Security for authentication (JWT-based).
PostgreSQL for database storage.
JUnit 5 & Mockito for unit testing.
Features and Performance
Async Operations: Reward points updates are handled asynchronously to improve performance.
Optimized Database Queries: Indexing and efficient data retrieval for faster query performance.
Error Handling: Standard HTTP error codes and detailed error messages for smooth error tracking.
Installation & Setup
Clone the repository.
Set up the PostgreSQL database.
Configure application.properties with database credentials.
Run the application using mvn spring-boot:run.
Testing
Unit tests are provided using JUnit 5 and Mockito.
Postman is used for API testing.
