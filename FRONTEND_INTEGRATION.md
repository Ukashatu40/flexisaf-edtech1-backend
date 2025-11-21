# Frontend Integration Guide

This guide provides step-by-step instructions for frontend developers to integrate with the EduLink360 Backend.

## Base URL
All API requests should be made to:
`http://localhost:8080`

## 1. Authentication Flow
The application uses **JWT (JSON Web Tokens)**. You must authenticate a user to get a token, which is then used for all subsequent requests.

### Step 1: Login
**Endpoint**: `POST /auth/login`
**Payload**:
```json
{
  "email": "student@test.com",
  "password": "password"
}
```
**Response**:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "student@test.com",
  "email": "student@test.com",
  "roles": ["STUDENT"]
}
```
> **Action**: Save the `token` in `localStorage` or a secure cookie.

### Step 2: Authenticated Requests
For **ALL** other endpoints, you must include the token in the `Authorization` header:
```http
Authorization: Bearer <your_token_here>
```

---

## 2. User Roles
The backend supports two primary roles: `TEACHER` and `STUDENT`.
- **Teacher**: Can create assignments, view class stats, grade submissions.
- **Student**: Can view assignments, submit work, view profile.

> **Tip**: Use the `roles` array from the login response to conditionally render UI elements.

---

## 3. Key Workflows

### A. Dashboard
- **Teacher**: `GET /teacher/dashboard-stats`
    - Returns: `{ "totalStudents": 30, "pendingReviews": 5, ... }`
- **Student**: `GET /student/dashboard-summary`
    - Returns: `{ "completedAssignments": 10, "pendingAssignments": 2, "averageGrade": 92.5 }`

### B. Assignments
#### 1. List Assignments
- **Teacher**: `GET /teacher/assignments` (Lists assignments created by them)
- **Student**: `GET /student/assignments` (Lists all assignments assigned to them)

#### 2. Create Assignment (Teacher Only)
- **Endpoint**: `POST /teacher/assignments/create`
- **Payload**:
    ```json
    {
      "title": "Physics Project",
      "description": "Build a rocket.",
      "dueDate": "2023-12-31T23:59:00"
    }
    ```

#### 3. Submit Assignment (Student Only)
- **Endpoint**: `POST /student/assignments/{id}/submit`
- **Format**: `multipart/form-data`
- **Fields**:
    - `file`: The file object to upload.

### C. Messaging
- **Get Contacts**: `GET /messages/contacts`
    - Returns a list of users (teachers/students) you can chat with.
- **Send Message**: `POST /messages/send`
    - **Payload**:
        ```json
        {
          "receiverId": 2,
          "content": "Hello, I have a question."
        }
        ```

### D. Career Guidance
- **Get Quiz**: `GET /career-quiz/questions`
- **Get Stories**: `GET /career-stories`

---

## 4. Error Handling
The API returns standard HTTP status codes:
- `200 OK`: Success.
- `400 Bad Request`: Invalid input.
- `401 Unauthorized`: Missing or invalid token.
- `403 Forbidden`: Valid token but insufficient permissions (e.g., Student trying to create assignment).
- `404 Not Found`: Resource doesn't exist.
- `500 Internal Server Error`: Something went wrong on the server.

## 5. Testing with Data
The backend comes with pre-seeded data for testing:
- **Teacher**: `teacher@test.com` / `password`
- **Student**: `student@test.com` / `password`

Use these credentials to test the full flow immediately.
