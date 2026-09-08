# Billora — GST Billing & Invoice Management System

<p align="center">
  <strong>Billora</strong> is a GST Billing & Invoice Management System designed to simplify customer billing, GST invoice generation, financial reporting, audit tracking, and secure API access.
</p>

<p align="center">
  <em>Spring Boot • Java 17 • MySQL • Spring Security • JWT • GitHub Actions • Microsoft Azure</em>
</p>

---

## 📌 About Billora

**Billora** is a full-stack billing and invoice management project developed to provide businesses with a centralized platform for creating and managing GST invoices.

The backend is built with **Java 17 and Spring Boot 3.5.5**, with **MySQL** used for data persistence. Security is implemented using **Spring Security and JWT authentication**. The application also supports PDF invoice generation, email delivery, financial reports, and audit logging.

The project is maintained as a single repository containing both the frontend and backend applications.

Billora also includes a **GitHub Actions CI/CD pipeline** for automated build and testing and is deployed to **Microsoft Azure**.

---

## ✨ Key Features

### 🧾 GST Invoice Generation

- Create GST invoices
- Add customer information
- Store invoice information
- Generate unique invoice numbers/IDs
- Calculate GST at 18%
- Include customer GSTIN
- Include customer email
- Calculate invoice totals
- Generate invoice PDFs

### 📋 Invoice Management

- View invoices
- Sort invoices by:
  - Customer name
  - Invoice date
  - Invoice amount
- Sort in ascending or descending order
- Filter invoices by:
  - Invoice ID
  - Customer name
  - Invoice date

### 📊 Financial Reports

Billora supports financial reporting for different time periods:

- Monthly profit reports
- Quarterly profit reports
- Annual profit reports

### 🔎 Audit Trail

The system maintains activity information for important operations, including:

- Invoice creation
- Invoice updates
- User activities
- Authentication events

### 📄 PDF Invoice

- Generate invoice as PDF
- Download invoice PDF
- Print invoice
- Include GST and customer information in the invoice

### 📧 Email Invoice

- Send invoice through email
- Attach generated PDF invoice
- Use Spring Boot Mail / Java Mail support

### 🔐 Authentication & Authorization

- User login
- JWT token generation
- JWT-based API authentication
- Spring Security integration
- Password encryption
- Role-based authorization
- Protected REST APIs

---

# 🏗️ System Architecture

Billora is organized as a full-stack application with separate frontend and backend applications.

```text
                         ┌─────────────────────┐
                         │       User          │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │      Frontend       │
                         │   Billora Web App   │
                         └──────────┬──────────┘
                                    │
                              REST / HTTP
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │      Backend        │
                         │ Spring Boot API     │
                         └──────────┬──────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │                     │                     │
              ▼                     ▼                     ▼
       ┌─────────────┐       ┌─────────────┐       ┌─────────────┐
       │    MySQL    │       │   Security  │       │    Mail     │
       │  Database   │       │ JWT / Auth  │       │   Service   │
       └─────────────┘       └─────────────┘       └─────────────┘
                                    │
                                    ▼
                            ┌─────────────┐
                            │  PDFBox     │
                            │ PDF Invoice │
                            └─────────────┘
```

---

# 📂 Repository Structure

```text
billora/
│
├── backend/
│   │
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── ...
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   │
│   │   └── test/
│   │
│   ├── pom.xml
│   └── README.md
│
├── frontend/
│   │
│   ├── src/
│   ├── public/
│   └── ...
│
├── .github/
│   └── workflows/
│       ├── backend-ci-cd.yml
│       └── frontend-ci-cd.yml
│
├── .gitignore
└── README.md
```

> The exact frontend and workflow filenames may differ depending on the current project configuration.

---

# ⚙️ Backend Structure

The Billora backend follows a layered Spring Boot architecture.

```text
backend/
│
├── config/
│   └── Security & application configuration
│
├── controller/
│   └── REST API controllers
│
├── dao/
│   └── Data access / repository layer
│
├── dto/
│   └── Data Transfer Objects
│
├── helper/
│   └── Utility and helper classes
│
├── models/
│   └── Entity / model classes
│
├── service/
│   └── Business logic
│
└── GstBillingApplication.java
```

### Main Layers

| Layer | Responsibility |
|---|---|
| Controller | Handles HTTP requests and REST APIs |
| Service | Contains business logic |
| DAO / Repository | Handles database operations |
| DTO | Transfers data between application layers |
| Models | Represents database entities |
| Config | Security and application configuration |
| Helper | Reusable utility functionality |

---

# 🛠️ Technology Stack

## Backend

| Technology | Version / Details |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.5 |
| Spring Web | REST APIs |
| Spring Data JPA | Database access |
| Spring Security | Authentication & authorization |
| JWT | 0.11.5 |
| Maven | 3.x |
| MySQL | 8.x |
| Lombok | Latest |
| Spring Boot Mail | Email service |
| Apache PDFBox | 2.0.31 |

## DevOps & Cloud

| Technology | Purpose |
|---|---|
| Git | Version control |
| GitHub | Source code repository |
| GitHub Actions | CI/CD automation |
| Microsoft Azure | Cloud deployment |

## Development Tools

- Eclipse IDE
- IntelliJ IDEA
- Visual Studio Code
- MySQL
- Maven
- Git

---

# 🚀 Getting Started

## Prerequisites

Before running Billora locally, install:

- Java 17
- Maven 3.x
- MySQL 8.x
- Git
- An IDE such as Eclipse or IntelliJ IDEA

---

## 1. Clone the Repository

```bash
git clone https://github.com/yogikh2005/GST_Billing_And_Invoice_Software.git
```

Then navigate to the project:

```bash
cd GST_Billing_And_Invoice_Software
```

If the repository is renamed to `billora`, use:

```bash
cd billora
```

---

## 2. Configure MySQL

Create the database:

```sql
CREATE DATABASE gstbilling;
```

Update the backend configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gstbilling
spring.datasource.username=root
spring.datasource.password=your_password
```

---

## 3. Build the Backend

Navigate to the backend directory:

```bash
cd backend
```

Build the project:

```bash
mvn clean install
```

---

## 4. Run the Backend

Run using Maven:

```bash
mvn spring-boot:run
```

Or run:

```text
GstBillingApplication.java
```

from your IDE.

---

# 🔄 CI/CD with GitHub Actions

Billora uses **GitHub Actions** to automate the Continuous Integration and Continuous Deployment process.

The CI/CD pipeline is designed to automatically build, test, and deploy the application.

## CI — Continuous Integration

When code is pushed to the repository, GitHub Actions can perform tasks such as:

1. Checkout the source code
2. Set up Java 17
3. Install dependencies
4. Build the backend
5. Run automated tests
6. Package the application

## CD — Continuous Deployment

After the CI process succeeds, the deployment workflow can publish the application to Microsoft Azure.

### Pipeline

```text
Developer
    │
    │ git push
    ▼
GitHub Repository
    │
    ▼
GitHub Actions
    │
    ├── Checkout
    ├── Setup Java 17
    ├── Install Dependencies
    ├── Build
    ├── Test
    └── Package
          │
          ▼
   Microsoft Azure
          │
          ▼
   Deployed Application
```

---

# ☁️ Microsoft Azure Deployment

Billora has been configured for deployment on **Microsoft Azure**.

The project uses GitHub Actions to automate deployment to the Azure environment.

### Deployment Flow

```text
GitHub
   │
   │ Push / Merge
   ▼
GitHub Actions
   │
   ├── Build
   ├── Test
   └── Package
        │
        ▼
Microsoft Azure
        │
        ▼
Billora Application
```

### Cloud Technologies

- Microsoft Azure
- GitHub Actions
- Java 17
- Spring Boot
- Maven

> Azure resource type and deployment configuration depend on the environment used for the project.

---

# 🔧 Environment Configuration

For local development, application configuration can be stored in:

```text
backend/src/main/resources/application.properties
```
---

# 📁 GitHub Repository

The project source code is maintained on GitHub.

**Repository:**

`GST_Billing_And_Invoice_Software`

The repository contains:

```text
Frontend
   +
Backend
   +
GitHub Actions CI/CD
   +
Azure Deployment Configuration
```

---

# 🌐 Deployment

The application is deployed to Microsoft Azure through the configured CI/CD workflow.

For the production URL, add the Azure application URL here:

```text
Production URL:
https://<your-azure-application-url>
```
---

# 📄 License

This project is developed for **educational and learning purposes**.

---

# 👨‍💻 Author

## Yogiraj Mohan Khaladkar

**Engineering Student | Java Backend Developer**

Interested in:

- Java
- Spring Boot
- Backend Development
- REST APIs
- Database Management
- Spring Security
- JWT Authentication
- CI/CD
- Cloud Deployment
