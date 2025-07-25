
# Raspberries Auth Service

Simple auth service for microservices arhitecture


## Authentication API

### Register

```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "string",
  "email": "user@example.com",
  "password": "string"
}
```

**Request Fields:**

| Field      | Type   | Required | Description               |
|------------|--------|----------|---------------------------|
| name       | string | ✓        | User display name         |
| email      | string | ✓        | Valid email address      |
| password   | string | ✓        | Min 4 characters         |

**Response (201 Created):**
```json
{
  "accessToken": "string"
}
```

**Response Fields:**

| Field        | Type   | Description                              |
|--------------|--------|------------------------------------------|
| accessToken  | string | JWT token containing userId and roles    |

---

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "string"
}
```

**Request Fields:**

| Field      | Type   | Required | Description               |
|------------|--------|----------|---------------------------|
| email      | string | ✓        | Registered email          |
| password   | string | ✓        | Account password          |

**Response (200 OK):**
```json
{
  "accessToken": "string",
}
```

**Response Fields:**

| Field        | Type   | Description                              |
|--------------|--------|------------------------------------------|
| accessToken  | string | JWT token containing userId and roles    |

---

### Logout (TODO)

```http
POST /api/auth/logout
Authorization: Bearer <access_token>
X-User-Id: 123
X-User-Roles: USER
```

**Headers:**

| Header        | Type   | Required | Description               | Example        |
|--------------|--------|----------|---------------------------|----------------|
| Authorization| string |         | Bearer token              | "Bearer abc.xyz" |
| X-User-Id    | int    | ✓        | Authenticated user ID     | 123            |
| X-User-Roles | string | ✓        | User roles (min "USER")   | "USER,ADMIN"   |

**Response (200 OK):**
```json
{
  
}
```

