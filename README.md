Members Discount Management System
-----------------------------------

Overview
--------
The Members Discount Management System is a Spring Boot–based backend solution designed to automate member management, discount eligibility, payment reminders, and email communication.
It integrates with external REST APIs and uses MySQL for persistent data storage, reducing manual administration and improving operational efficiency.

Core Features
--------------
- Fetches and stores member data from an external API on a scheduled basis.
- Calculates a 15% discount for long-term, paid members (3 or more years).
- Tracks paid and unpaid members in a local MySQL database.
- Sends payment reminder emails to members nearing their deadlines.
- Notifies eligible members of discounts automatically via email.
- Provides RESTful APIs documented using Swagger UI for easy testing and integration.

Technology Stack
----------------
Backend Framework: Spring Boot (Java 17+)
Database: MySQL with Spring Data JPA (Hibernate ORM)
Email: Spring Mail (JavaMailSender with Gmail SMTP)
API Documentation: Springdoc OpenAPI + Swagger UI
Scheduling: Spring Scheduler for automated tasks
Testing: JUnit 5, Mockito, MockMvc
Build Tool: Maven
Logging: SLF4J / Logback

Architecture Overview
---------------------
The application follows a layered architecture:

Controller Layer  →  Service Layer  →  Repository Layer  →  Database
        ↓                  ↓                   ↓
     REST API        Business Logic       MySQL Storage
        ↓
  External API (Synchronization)
        ↓
  Email Notifications (Spring Mail)

Key Highlights
---------------
- Layered architecture for maintainability and separation of concerns.
- Integration with external APIs using RestTemplate.
- Centralized exception handling with @RestControllerAdvice for consistent error responses.
- Scheduled automation for data synchronization and reminder emails.
- Unit and controller testing with JUnit and Mockito for reliability.

Installation and Setup
----------------------
Prerequisites:
- Java 17 or higher
- Maven 3.8 or higher
- MySQL Server 8 or higher

Steps:
1. Clone the project repository.
2. Configure the following properties in application.properties:
   spring.datasource.url=jdbc:mysql://localhost:3306/membersdb
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=create-drop
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   external.api.get.url=https://api-generator.retool.com/VRlTnt/data
   external.api.post.url=https://api-generator.retool.com/VRlTnt/data
3. Build the project using:
   mvn clean install
4. Run the application using:
   mvn spring-boot:run
5. Access the Swagger UI at:
   http://localhost:8080/integration

API Endpoints
--------------
GET    /members                       - Retrieve all members
GET    /members/fetchAndSave          - Fetch and save members from external API
POST   /members/eligibleFordiscount   - Send eligible discounted members to external API
GET    /members/unpaidMembers         - Retrieve members who have not paid
GET    /members/paidMembers           - Retrieve members who have paid
POST   /members/reminders             - Send payment reminder emails
POST   /members/sendDiscountToMembers - Send discount notification emails

Business Logic Summary
----------------------
- Discount Eligibility: Member must have paid and been registered for more than 3 years.
- Discount Calculation: 15% reduction on the member’s current price.
- Payment Reminder Trigger: Sent to members whose deadline is within 3 days and have not paid.
- Scheduled Jobs:
    • Fetch members: 3 AM on the 25th of every month.
    • Send reminders: 9 AM daily.

Testing
-------
Unit and controller tests are included to verify core functionality.

Key Tests:
- DiscountServiceTest: Validates discount calculation, eligibility, and reminder logic.
- DiscountControllerTest: Tests REST endpoints and expected responses using MockMvc.

Run tests using:
   mvn test

Email Integration
-----------------
Emails are sent using Spring Mail (JavaMailSender) through Gmail’s SMTP server.

Email Templates:
- Discount Notification: Sent to members eligible for a 15% discount.
- Payment Reminder: Sent to members who have upcoming payment deadlines.

Automation and Scheduling
-------------------------
- Fetch Members Job: Runs at 3:00 AM on the 25th of each month.
- Reminder Job: Runs daily at 9:00 AM to notify members about upcoming deadlines.

Business Value
---------------
This system enables organizations to streamline member engagement by automating discount eligibility, payment reminders, and data synchronization.

Benefits:
- Increases member retention through loyalty discounts.
- Reduces manual administrative workload.
- Improves on-time payments.
- Provides transparent and efficient communication.
- Ensures consistent data management through automation.

Future Enhancements
-------------------
- Replace RestTemplate with Spring WebClient (Reactive approach).
- Add integration tests using Testcontainers for database validation.
- Introduce DTO mapping to separate internal entities from API responses.
- Implement HTML-based email templates using Thymeleaf.
- Add Spring Security for authentication and role-based authorization.

License
-------
This project is open source and may be used, modified, or distributed under the terms of the MIT License.

