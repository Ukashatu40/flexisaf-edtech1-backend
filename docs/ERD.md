# Entity Relationship Diagram (ERD)

This document provides a detailed Entity Relationship Diagram (ERD) for the EdTech backend project, based on the current data models.

## Diagram

```mermaid
erDiagram
    User {
        Long id PK
        String name
        String email "Unique"
        String password
        Role role "Enum: STUDENT, TEACHER, ADMIN"
        String status
        String teacherId "Teacher only"
        String schoolName "Teacher only"
        String credentialFilePath "Teacher only"
        List~String~ courses "Student only"
        List~String~ classes "Student only"
        List~String~ managedCourses "Teacher only"
        LocalDateTime createdAt
    }

    Classroom {
        Long id PK
        String name
    }

    Assignment {
        Long id PK
        String title
        String description
        LocalDateTime dueDate
    }

    Submission {
        Long id PK
        String filePath
        String grade
        String feedback
        LocalDateTime submittedAt
    }

    Document {
        Long id PK
        String fileName
        String filePath
        String fileType
        LocalDateTime uploadedAt
    }

    Message {
        Long id PK
        String content
        LocalDateTime sentAt
    }

    Notification {
        Long id PK
        String message
        boolean isRead
        LocalDateTime createdAt
    }

    QuizQuestion {
        Long id PK
        String question
        List~String~ options
        String correctOption
    }

    CareerStory {
        Long id PK
        String title
        String author
        String content
    }

    %% Relationships

    User ||--o{ Document : "uploads (Student)"
    User ||--o{ Message : "sends"
    User ||--o{ Message : "receives"
    User ||--o{ Notification : "receives"
    
    %% Teacher Relationships
    User ||--o{ Assignment : "creates (Teacher)"
    User ||--o{ Classroom : "manages (Teacher)"
    
    %% Student Relationships
    Classroom }|--|{ User : "contains (Students)"
    
    %% Assignment Flow
    Assignment ||--o{ Submission : "has"
    User ||--o{ Submission : "submits (Student)"

```

## detailed Entity Descriptions

### User
Represents all users in the system (Students, Teachers, Admins).
- **id**: Primary Key.
- **role**: Determines the user type (STUDENT, TEACHER, ADMIN).
- **teacherId, schoolName, credentialFilePath**: Specific to Teachers.
- **courses, classes**: Specific to Students (Strings, likely legacy or simple lists).
- **managedCourses**: Specific to Teachers.

### Classroom
Represents a class managed by a teacher and containing students.
- **teacher**: The owner of the classroom.
- **students**: The students enrolled in the classroom.

### Assignment
Tasks created by teachers for students.
- **teacher**: The creator of the assignment.

### Submission
Work submitted by a student for an assignment.
- **assignment**: The related assignment.
- **student**: The student who submitted.
- **filePath**: Link to the submitted file.
- **grade, feedback**: Evaluation details.

### Document
Files uploaded by students (e.g., resources, simple uploads).
- **student**: The uploader.

### Message
Communication between users.
- **sender**: User sending the message.
- **receiver**: User receiving the message.

### Notification
System notifications for users.
- **user**: The recipient.

### QuizQuestion
Standalone entity for quiz questions (currently not linked to other entities).

### CareerStory
Standalone entity for career stories/blogs.
