#!/bin/bash

BASE_URL="http://localhost:8080"

echo "========================================"
echo "   Testing EdTech1 Backend Endpoints"
echo "========================================"

# Function to print section headers
print_header() {
    echo -e "\n\033[1;34m--- $1 ---\033[0m"
}

# Create a dummy file for upload tests
echo "This is a test assignment submission." > test_upload.txt

# 1. Authentication & Setup
echo "1. Authentication..."

# Login Teacher (Seeded)
echo "Logging in Teacher (Seeded)..."
TEACHER_LOGIN=$(curl -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"teacher@test.com", "password":"password"}' -s)

TEACHER_TOKEN=$(echo $TEACHER_LOGIN | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Teacher Token: $TEACHER_TOKEN"

# Login Student (Seeded)
echo "Logging in Student (Seeded)..."
STUDENT_LOGIN=$(curl -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"student@test.com", "password":"password"}' -s)

STUDENT_TOKEN=$(echo $STUDENT_LOGIN | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Student Token: $STUDENT_TOKEN"

# 2. Teacher Dashboard
echo -e "\n2. Teacher Dashboard..."
curl -H "Authorization: Bearer $TEACHER_TOKEN" "$BASE_URL/teacher/dashboard-stats" -s | grep "totalStudents" && echo "Stats OK" || echo "Stats Failed"
curl -H "Authorization: Bearer $TEACHER_TOKEN" "$BASE_URL/teacher/classes" -s | grep "name" && echo "Classes OK" || echo "Classes Failed"

# 3. Student Dashboard
echo -e "\n3. Student Dashboard..."
curl -H "Authorization: Bearer $STUDENT_TOKEN" "$BASE_URL/student/dashboard-summary" -s | grep "completedAssignments" && echo "Summary OK" || echo "Summary Failed"
curl -H "Authorization: Bearer $STUDENT_TOKEN" "$BASE_URL/student/profile" -s | grep "email" && echo "Profile OK" || echo "Profile Failed"

# 4. Assignments
echo -e "\n4. Assignments..."
# Create Assignment (Teacher)
curl -X POST "$BASE_URL/teacher/assignments/create" \
  -H "Authorization: Bearer $TEACHER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Math HW", "description":"Solve problems", "dueDate":"2023-12-31", "teacherId":1}' -s > /dev/null
echo "Assignment Created"

# List Assignments (Student)
ASSIGNMENT_ID=$(curl -H "Authorization: Bearer $STUDENT_TOKEN" "$BASE_URL/student/assignments" -s | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "Assignment ID: $ASSIGNMENT_ID"

# Submit Assignment (Student)
if [ ! -z "$ASSIGNMENT_ID" ]; then
  curl -X POST "$BASE_URL/student/assignments/$ASSIGNMENT_ID/submit" \
    -H "Authorization: Bearer $STUDENT_TOKEN" \
    -F "file=@test_upload.txt" \
    -s > /dev/null
  echo "Assignment Submitted"
fi

# 5. Messaging
echo -e "\n5. Messaging..."
curl -H "Authorization: Bearer $STUDENT_TOKEN" "$BASE_URL/messages/contacts" -s | grep "id" && echo "Contacts OK" || echo "Contacts Failed"
curl -X POST "$BASE_URL/messages/send" \
  -H "Authorization: Bearer $STUDENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"receiverId":1, "content":"Hello Teacher"}' -s > /dev/null
echo "Message Sent"

# 6. Notifications
echo -e "\n6. Notifications..."
curl -H "Authorization: Bearer $STUDENT_TOKEN" "$BASE_URL/notifications" -s | grep "message" && echo "Notifications OK" || echo "Notifications Failed"

# 7. Career
echo -e "\n7. Career..."
curl -H "Authorization: Bearer $STUDENT_TOKEN" "$BASE_URL/career-quiz/questions" -s | grep "question" && echo "Quiz Questions OK" || echo "Quiz Questions Failed"
curl -H "Authorization: Bearer $STUDENT_TOKEN" "$BASE_URL/career-stories" -s | grep "title" && echo "Stories OK" || echo "Stories Failed"

# Cleanup
rm test_upload.txt

echo -e "\nVerification Complete!\033[0m"
