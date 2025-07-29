
# Raspberries Auth Service

Simple auth service for marketplace.


## Authentication API

### Register User

```http
POST /api/auth/register/user
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
| password   | string | ✓        | Min 8 characters         |

**Response (201 Created):**
```json
{
  "accessToken": "string"
}
```
Sends an AccountRegisteredEvent object to the **user-registered** topic in kafka.
### Register Company

```http
POST /api/auth/register/company
Content-Type: application/json

{
  "name": "string",
  "email": "company@example.com",
  "password": "string"
}
```

**Request Fields:**

| Field      | Type   | Required | Description               |
|------------|--------|----------|---------------------------|
| name       | string | ✓        | Company display name     |
| email      | string | ✓        | Valid email address      |
| password   | string | ✓        | Min 8 characters         |
| taxId      | string | ✓        | Min 10 characters        |


**Response (201 Created):**
```json
{
  "accessToken": "string"
}
```
Sends an AccountRegisteredEvent object to the **company-registered** topic in kafka.

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

| Header       | Type   | Required | Description               |
|--------------|--------|----------|---------------------------|
| Authorization| string |          | Bearer token              |
| X-User-Id    | int    | ✓        | Authenticated user ID     |
| X-User-Roles | string | ✓        | User role (any)           |

**Response (200 OK):**
```json
{
  
}
```


## Related

Here are other services of my project:

- [**Raspberries Gateway**](https://github.com/rvfw/Raspberries_Gateway)
- [Raspberries User Service](https://github.com/rvfw/Raspberries_UserService)

