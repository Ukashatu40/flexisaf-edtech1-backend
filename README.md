# EduLink360 Backend

## Overview
EduLink360 is a comprehensive educational technology platform designed to bridge the gap between students, teachers, and career guidance. This backend service, built with **Spring Boot**, powers the platform by providing robust APIs for user management, classroom activities, assignments, messaging, and career exploration.

## Features
- **User Authentication**: Secure JWT-based authentication for Students and Teachers.
- **Role-Based Access Control**: Distinct features and data access for different roles.
- **Dashboard**: Real-time statistics and summaries for teachers and students.
- **Classroom Management**: Manage classes and student enrollments.
- **Course Selection**: Students can select courses and classes; Teachers can manage their course load.
- **Assignment System**: Create, list, submit, and grade assignments with file support.
- **Messaging**: Internal messaging system between students and teachers.
- **Notifications**: Real-time updates for important events (new assignments, grades).
- **Career Guidance**: Interactive career quizzes and inspiring career stories.
- **Digital Portfolio**: Students can build and share their academic portfolios.

## Technology Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL (Render Managed)
- **Security**: Spring Security + JWT (JSON Web Tokens)
- **Deployment**: Docker + Render
- **Build Tool**: Maven

## Getting Started

### Prerequisites
- Java 17 SDK
- Maven 3.x
- Docker (optional, for containerization)

### Installation
1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd edtech1-backend
    ```

2.  **Configure Database**:
    - The project is configured to use a remote Render database by default.
    - To run locally with a local DB, update `src/main/resources/application.properties`.

3.  **Build & Run (Local)**:
    ```bash
    mvn spring-boot:run
    ```
    The server will start on `http://localhost:8080`.

4.  **Build & Run (Docker)**:
    ```bash
    docker build -t edtech1-backend .
    docker run -p 8080:8080 edtech1-backend
    ```

### Data Seeding
The application includes a `DataSeeder` that automatically populates the database with test users and data on startup if it's empty.
- **Teacher**: `teacher@test.com` / `password`
- **Student**: `student@test.com` / `password`

## API Documentation
For detailed integration guides, please refer to [FRONTEND_INTEGRATION.md](FRONTEND_INTEGRATION.md).

## Project Structure
```
src/main/java/com/edtech1/edtech1_backend
├── config/          # Security and App configurations
├── controller/      # REST Controllers (API Endpoints)
├── model/           # JPA Entities
├── repository/      # Data Access Layer
├── security/        # JWT and Auth logic
├── service/         # Business Logic (File Storage, etc.)
└── util/            # Utilities (Data Seeder)
```

## Security
This project uses **JWT (JSON Web Tokens)** for security.
- **Login**: POST `/auth/login` returns a `token`.
- **Authenticated Requests**: All other requests must include the header:
    `Authorization: Bearer <your_token>`

## License
[MIT](LICENSE)