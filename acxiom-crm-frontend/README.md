# Acxiom CRM Frontend

React + Vite + Tailwind CSS frontend for the Acxiom CRM Spring Boot API.

## 1. Install

```bash
npm install
```

## 2. Configure backend URL

Create `.env`:

```env
VITE_API_URL=http://localhost:8080
```

## 3. Run

```bash
npm run dev
```

Frontend: http://localhost:5173

## Included

- JWT login and Axios bearer interceptor
- Dashboard
- Customers CRUD + pagination/search endpoint
- Leads CRUD + pagination/search + conversion endpoint wiring
- Opportunities CRUD + pagination/search
- Follow-Ups CRUD + pagination/search
- Activities CRUD
- Admin user management: create/update/activate/deactivate/reset-password/unlock
- Audit log filters
- Reports: customers, leads, opportunities, followups, pipeline, conversion, users, audit
- Responsive sidebar and Tailwind UI

## Backend endpoints used

Authentication: `/api/auth/register`, `/api/auth/login`

Customers: `/api/customers`, `/api/customers/page`

Leads: `/api/leads`, `/api/leads/page`, `/api/leads/{id}/convert`

Opportunities: `/api/opportunities`, `/api/opportunities/page`

Follow-ups: `/api/followups`, `/api/followups/page`

Activities: `/api/activities`

Dashboard: `/api/dashboard`

Users: `/api/users`, `/api/users/{id}/activate`, `/deactivate`, `/reset-password`, `/unlock`

Audit: `/api/audit`

Reports: `/api/reports/customers`, `/leads`, `/opportunities`, `/followups`, `/pipeline`, `/conversion`, `/users`, `/audit`
