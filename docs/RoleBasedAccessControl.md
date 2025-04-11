# Role-Based Access Control (RBAC) - Documentation

## 📌 Overview

Role-Based Access Control (RBAC) is implemented in this project to control access to various resources based on the user's role within an organization. The RBAC system is multi-tenant aware and integrates seamlessly with Auth0 for authentication.

---

## 🧱 Core Components

### 1. **User**

Represents an authenticated identity within the system.

- Attributes:

  - `id`: UUID
  - `email`
  - `name`
  - `organization_id`

- A user can have multiple roles.
- Belongs to a specific organization (multi-tenancy).

---

### 2. **Role**

Defines a set of permissions.

- Attributes:

  - `id`
  - `name` (e.g., `OrgAdmin`, `ProjectManager`, `Developer`)
  - `description`
  - `organization_id`

- A role is scoped to an organization.
- Can be assigned to multiple users.

---

### 3. **Permission**

Represents an action that can be executed in the system.

- Attributes:

  - `id`
  - `name` (e.g., `create_project`, `delete_issue`, `view_report`)
  - `description`

- Assigned to roles.

---

### 4. **UserRole**

Maps users to roles.

- Attributes:
  - `user_id`
  - `role_id`
  - `organization_id`

---

### 5. **RolePermission**

Maps roles to permissions.

- Attributes:
  - `role_id`
  - `permission_id`

---

## 🏢 Multi-Tenancy Support

- Each user belongs to an organization.
- Roles and permissions are scoped per organization.
- Prevents data leakage between tenants.

---

## 🔐 Auth0 Integration

- Users are authenticated via Auth0.
- Permissions are synchronized with Auth0 Management API for external consistency.
- Tokens contain role information for authorization middleware.

---

## ⚙️ API Responsibility

| Endpoint                   | Access   | Description               |
| -------------------------- | -------- | ------------------------- |
| `GET /permissions`         | OrgAdmin | View all permissions      |
| `POST /roles`              | OrgAdmin | Create a new role         |
| `POST /roles/assign`       | OrgAdmin | Assign role to user       |
| `POST /permissions/create` | OrgAdmin | Add new permissions       |
| `POST /roles/permissions`  | OrgAdmin | Map permissions to role   |
| `GET /user/roles`          | Any      | Get roles of current user |

---

## 🔄 Transition to Microservices (Future Plan)

In microservices architecture:

- `auth-service`: Handles login, signup, and token generation.
- `rbac-service`: Manages users, roles, permissions, and assignments.
- Each service will verify authorization using a shared JWT issued by Auth0.

---
