# Acxiom CRM

A full-stack Customer Relationship Management application built with **Spring Boot + React + Tailwind CSS**.

## Project Structure

```text
acxiom-crm/
├── backend/       # Spring Boot REST API
├── frontend/      # React + Vite + Tailwind CSS
├── README.md
└── .gitignore
```

## Tech Stack

### Backend

- Java
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven

### Frontend

- React
- Vite
- Tailwind CSS
- Axios
- React Router

---

# 1. Prerequisites

Install:

- Java 21 or the Java version configured in the backend project
- Maven (or use the Maven wrapper if included)
- Node.js and npm
- PostgreSQL
- Git

---

# 2. Run the Backend

Go to the backend folder:

```bash
cd acxiomcrm
```

Configure the PostgreSQL database and the required application properties/environment variables in the backend.

Then run:

```bash
mvn spring-boot:run
```

Or, if the Maven wrapper is included:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

---

# 3. Run the Frontend

Open another terminal:

```bash
cd acxiom-crm-frontend
```

Install dependencies:

```bash
npm install
```

Create a `.env` file if required:

```env
VITE_API_URL=http://localhost:8080
```

Start the frontend:

```bash
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

---

# 4. Authentication

Most APIs require a JWT token.

First register or login using the authentication APIs. The frontend stores the JWT and sends it with protected API requests.

Authorization header:

```http
Authorization: Bearer <JWT_TOKEN>
```

The Postman collection uses the backend base URL `http://localhost:8080` and bearer authentication.

---

# 5. API Endpoints

Base URL:

```text
http://localhost:8080
```

## Authentication

| Method | Endpoint             | Description                |
| ------ | -------------------- | -------------------------- |
| POST   | `/api/auth/register` | Register a new user        |
| POST   | `/api/auth/login`    | Login user and receive JWT |

### Register

```json
{
  "name": "John",
  "email": "john@gmail.com",
  "password": "Password@123",
  "role": "SALES_EXECUTIVE"
}
```

### Login

```json
{
  "email": "admin@acxiomcrm.com",
  "password": "Admin@123"
}
```

---

# 6. Customers

| Method | Endpoint              | Description           |
| ------ | --------------------- | --------------------- |
| POST   | `/api/customers`      | Create customer       |
| GET    | `/api/customers`      | Get all customers     |
| GET    | `/api/customers/{id}` | Get customer by ID    |
| PUT    | `/api/customers/{id}` | Update customer       |
| DELETE | `/api/customers/{id}` | Delete customer       |
| GET    | `/api/customers/page` | Pagination and search |

### Create / Update Customer

```json
{
  "customerName": "ABC Technologies",
  "email": "contact@abc.com",
  "phone": "9876543210",
  "companyName": "ABC Technologies Pvt Ltd",
  "address": "MVP Colony",
  "city": "Visakhapatnam",
  "state": "Andhra Pradesh",
  "status": "ACTIVE",
  "assignedTo": 3
}
```

### Pagination / Search

```text
GET /api/customers/page?page=0&size=10&sortBy=customerName&direction=asc&search=abc
```

---

# 7. Leads

| Method | Endpoint                  | Description           |
| ------ | ------------------------- | --------------------- |
| POST   | `/api/leads`              | Create lead           |
| GET    | `/api/leads`              | Get all leads         |
| GET    | `/api/leads/{id}`         | Get lead by ID        |
| PUT    | `/api/leads/{id}`         | Update lead           |
| DELETE | `/api/leads/{id}`         | Delete lead           |
| GET    | `/api/leads/page`         | Pagination and search |
| POST   | `/api/leads/{id}/convert` | Convert lead          |

### Create / Update Lead

```json
{
  "leadName": "Rahul Sharma",
  "email": "rahul@gmail.com",
  "phone": "9876543210",
  "companyName": "XYZ Solutions",
  "source": "WEBSITE",
  "status": "NEW",
  "priority": "HIGH",
  "expectedValue": 500000,
  "assignedTo": 3
}
```

### Pagination / Search

```text
GET /api/leads/page?page=0&size=10&search=rahul
```

### Convert Lead

```json
{
  "opportunityName": "XYZ Enterprise Deal",
  "amount": 600000,
  "stage": "PROPOSAL",
  "probability": 60,
  "expectedCloseDate": "2026-12-15",
  "notes": "Enterprise software requirement"
}
```

```text
POST /api/leads/1/convert
```

---

# 8. Opportunities

| Method | Endpoint                  | Description           |
| ------ | ------------------------- | --------------------- |
| POST   | `/api/opportunities`      | Create opportunity    |
| GET    | `/api/opportunities`      | Get all opportunities |
| GET    | `/api/opportunities/{id}` | Get opportunity by ID |
| PUT    | `/api/opportunities/{id}` | Update opportunity    |
| DELETE | `/api/opportunities/{id}` | Delete opportunity    |
| GET    | `/api/opportunities/page` | Pagination and search |

### Create / Update Opportunity

```json
{
  "opportunityName": "Enterprise Software Deal",
  "customerId": 1,
  "leadId": 1,
  "assignedTo": 3,
  "amount": 750000,
  "stage": "PROPOSAL",
  "probability": 70,
  "expectedCloseDate": "2026-12-20",
  "status": "OPEN",
  "notes": "Large enterprise opportunity"
}
```

### Pagination / Search

```text
GET /api/opportunities/page?page=0&size=10&search=enterprise
```

---

# 9. Follow-Ups

| Method | Endpoint              | Description           |
| ------ | --------------------- | --------------------- |
| POST   | `/api/followups`      | Create follow-up      |
| GET    | `/api/followups`      | Get all follow-ups    |
| GET    | `/api/followups/{id}` | Get follow-up by ID   |
| PUT    | `/api/followups/{id}` | Update follow-up      |
| DELETE | `/api/followups/{id}` | Delete follow-up      |
| GET    | `/api/followups/page` | Pagination and search |

### Create Follow-Up

```json
{
  "customerId": 1,
  "leadId": 1,
  "followUpDate": "2026-10-15T10:30:00",
  "followUpType": "CALL",
  "subject": "Product discussion",
  "remarks": "Discuss pricing and requirements",
  "status": "PENDING",
  "assignedTo": 3
}
```

### Pagination / Search

```text
GET /api/followups/page?page=0&size=10&search=meeting
```

---

# 10. Activities

| Method | Endpoint               | Description        |
| ------ | ---------------------- | ------------------ |
| POST   | `/api/activities`      | Create activity    |
| GET    | `/api/activities`      | Get all activities |
| GET    | `/api/activities/{id}` | Get activity by ID |
| PUT    | `/api/activities/{id}` | Update activity    |
| DELETE | `/api/activities/{id}` | Delete activity    |

### Create Activity

```json
{
  "activityType": "CALL",
  "subject": "Customer follow-up",
  "description": "Discussed product requirements",
  "activityDate": "2026-10-07T15:30:00",
  "customerId": 1,
  "leadId": null,
  "status": "COMPLETED",
  "assignedTo": 3
}
```

---

# 11. Dashboard

| Method | Endpoint                                                                  | Description                  |
| ------ | ------------------------------------------------------------------------- | ---------------------------- |
| GET    | `/api/dashboard?rangeType=THIS_MONTH`                                     | Dashboard for standard range |
| GET    | `/api/dashboard?rangeType=CUSTOM&startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` | Dashboard for custom range   |

### Example

```text
GET /api/dashboard?rangeType=THIS_MONTH
```

Custom range:

```text
GET /api/dashboard?rangeType=CUSTOM&startDate=2026-10-01&endDate=2026-10-07
```

---

# 12. User Management (Admin)

| Method | Endpoint                         | Description     |
| ------ | -------------------------------- | --------------- |
| POST   | `/api/users`                     | Create user     |
| GET    | `/api/users`                     | Get all users   |
| GET    | `/api/users/{id}`                | Get user by ID  |
| PUT    | `/api/users/{id}`                | Update user     |
| PATCH  | `/api/users/{id}/activate`       | Activate user   |
| PATCH  | `/api/users/{id}/deactivate`     | Deactivate user |
| PATCH  | `/api/users/{id}/reset-password` | Reset password  |
| PATCH  | `/api/users/{id}/unlock`         | Unlock user     |

### Create User

```json
{
  "name": "Sales User",
  "email": "sales1@gmail.com",
  "password": "Password@123",
  "role": "SALES_EXECUTIVE"
}
```

### Update User

```json
{
  "name": "Sales User Updated",
  "email": "updated@gmail.com",
  "role": "SALES_EXECUTIVE"
}
```

### Reset Password

```json
{
  "newPassword": "NewPassword@123"
}
```

---

# 13. Audit Logs

| Method | Endpoint     | Description           |
| ------ | ------------ | --------------------- |
| GET    | `/api/audit` | Get/filter audit logs |

### Example

```text
GET /api/audit?userId=3&action=CREATE&entityName=Customer&start=2026-10-01&end=2026-10-07
```

Supported filters in the provided API collection:

- `userId`
- `action`
- `entityName`
- `start`
- `end`

---

# 14. Reports

| Method | Endpoint                     | Description          |
| ------ | ---------------------------- | -------------------- |
| GET    | `/api/reports/customers`     | Customer report      |
| GET    | `/api/reports/leads`         | Lead report          |
| GET    | `/api/reports/opportunities` | Opportunity report   |
| GET    | `/api/reports/followups`     | Follow-up report     |
| GET    | `/api/reports/pipeline`      | Pipeline report      |
| GET    | `/api/reports/conversion`    | Conversion report    |
| GET    | `/api/reports/users`         | User activity report |
| GET    | `/api/reports/audit`         | Audit report         |

---

# 15. Quick Start

Run the backend first:

```bash
cd acxiomcrm
.\mvnw spring-boot:run
```

Then run the frontend in another terminal:

```bash
cd acxiom-crm-frontend
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

---

# 16. Important Notes

- The backend must be running on port `8080` unless the frontend API URL is changed.
- PostgreSQL must be configured before starting the backend.
- Protected APIs require a valid JWT token.
- Do not commit passwords, JWT secrets, database credentials, or `.env` files to GitHub.
- Use `.env.example` or example configuration files for sharing configuration safely.

---

# 17. API Testing

The complete API collection can be imported into **Postman** for testing.

Base URL:

```text
http://localhost:8080
```

After login, use the returned JWT as a Bearer token for protected endpoints.
