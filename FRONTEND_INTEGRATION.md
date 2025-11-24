# Frontend Integration Guide

This guide provides a comprehensive, step-by-step walkthrough for integrating the EduLink360 Backend. It covers authentication, role-based flows, and detailed API specifications.

## Base URL
**Production**: `https://flexisaf-edulink360-backend.onrender.com` (or your specific Render URL)
**Local**: `http://localhost:8080`

---

## 1. Authentication Flow (The Entry Point)
**All** interactions start here. You must register or login to get a **JWT Token**. This token is your key to accessing the rest of the system.

### A. Registration
**Endpoint**: `POST /auth/register`
**Content-Type**: `multipart/form-data`
**Description**: Creates a new user account. Teachers can upload credentials.

**Payload (Form Data):**
| Key | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `name` | Text | Yes | Full Name (e.g., "John Doe") |
| `email` | Text | Yes | Valid email address |
| `password` | Text | Yes | Secure password |
| `role` | Text | Yes | `STUDENT` or `TEACHER` |
| `file` | File | No | Credential document (Required for Teachers) |

**Success Response (200 OK):**
```text
User registered successfully
```

### B. Login
**Endpoint**: `POST /auth/login`
**Content-Type**: `application/json`
**Description**: Authenticates a user and returns a JWT token.

**Payload (JSON):**
```json
{
  "email": "student@test.com",
  "password": "password"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": 1,
    "name": "John Student",
    "email": "student@test.com",
    "role": "STUDENT",
    "status": "ACTIVE"
  }
}
```

> **CRITICAL STEP**:
> 1.  **Save the `token`** in `localStorage` or a secure cookie.
> 2.  **Save the `user` object** (especially `id` and `role`) to manage UI state (e.g., show "Teacher Dashboard" vs "Student Dashboard").

### C. Authenticated Requests
For **ALL** endpoints below, you **MUST** include the token in the header:
```http
Authorization: Bearer <your_token_here>
```

---

## 2. Teacher Workflow

### A. Dashboard
**Endpoint**: `GET /teacher/dashboard-stats`
**Response**:
```json
{
  "totalStudents": 30,
  "pendingReviews": 5,
  "progressRate": 85.5,
  "recentSubmissions": []
}
```

### B. Manage Classes
**Endpoint**: `GET /teacher/classes`
**Response**:
```json
[
  {
    "id": 1,
    "name": "Math 101",
    "students": [ ... ]
  }
]
```

### C. Assignments
#### 1. Create Assignment
**Endpoint**: `POST /teacher/assignments/create`
**Payload**:
```json
{
  "title": "Physics Project",
  "description": "Build a model rocket.",
  "dueDate": "2023-12-31T23:59:00" // Optional, backend defaults to 7 days
}
```
**Response**: Returns the created `Assignment` object.

#### 2. View Assignment & Submissions
**Endpoint**: `GET /teacher/assignments/{id}`
**Response**:
```json
{
  "assignment": { "id": 1, "title": "Physics Project", ... },
  "submissions": [
    {
      "id": 101,
      "student": { "name": "John Doe" },
      "filePath": "uploads/file.pdf",
      "submittedAt": "..."
    }
  ]
}
```

#### 3. Grade Submission
**Endpoint**: `POST /teacher/assignments/{id}/feedback`
**Payload**:
```json
{
  "submissionId": "101",
  "grade": "A",
  "feedback": "Great work!"
}
```
**Response**: `Feedback submitted`

---

## 3. Student Workflow

### A. Dashboard
**Endpoint**: `GET /student/dashboard-summary`
**Response**:
```json
{
  "completedAssignments": 10,
  "pendingAssignments": 2,
  "averageGrade": 92.5
}
```

### B. Profile
**Endpoint**: `GET /student/profile`
**Response**: Returns `User` object.

**Update Profile**:
**Endpoint**: `PATCH /student/profile`
**Payload**:
```json
{
  "name": "Johnathan Doe"
}
```

### C. Assignments
#### 1. List Assignments
**Endpoint**: `GET /student/assignments`
**Response**: List of all assignments.

#### 2. Submit Assignment
**Endpoint**: `POST /student/assignments/{id}/submit`
**Content-Type**: `multipart/form-data`
**Payload**:
| Key | Type | Description |
| :--- | :--- | :--- |
| `file` | File | The homework file to upload |

**Response**: `Assignment submitted: <filename>`

#### 3. View Feedback
**Endpoint**: `GET /student/assignments/{id}/feedback`
**Response**: Returns `Submission` object (with `grade` and `feedback`).

---

## 4. Common Features (Both Roles)

### A. Messaging
#### 1. Get Contacts
**Endpoint**: `GET /messages/contacts`
**Response**: List of users you can chat with.

#### 2. Send Message
**Endpoint**: `POST /messages/send`
**Payload**:
```json
{
  "receiverId": 2,
  "content": "Hello, I have a question."
}
```

#### 3. Get Chat History
**Endpoint**: `GET /messages/student/{id}` (for Teachers) or `/messages/teacher/{id}` (for Students)
**Response**: List of message objects sorted by time.

### B. Notifications
**Endpoint**: `GET /notifications`
**Response**: List of notifications.

**Mark as Read**:
**Endpoint**: `PATCH /notifications/mark-read/{id}`
**Response**: `Marked as read`

### C. Career Guidance
**Endpoint**: `GET /career-quiz/questions`
**Response**: List of quiz questions.

**Endpoint**: `GET /career-stories`
**Response**: List of career stories.

### D. Digital Portfolio
#### 1. Upload Document
**Endpoint**: `POST /portfolio/upload-document`
**Content-Type**: `multipart/form-data`
**Payload**: `file` (The document)

#### 2. View Portfolio
**Endpoint**: `GET /portfolio`
**Response**: List of uploaded documents.

#### 3. Delete Document
**Endpoint**: `DELETE /portfolio/{documentId}`
**Response**: `Deleted`

---

## 5. Error Handling
- **401 Unauthorized**: Token is missing or invalid. Redirect to Login.
- **403 Forbidden**: You are trying to access a Teacher endpoint as a Student (or vice versa).
- **500 Internal Server Error**: Something went wrong. Check the response body for `"error": "message"`.
