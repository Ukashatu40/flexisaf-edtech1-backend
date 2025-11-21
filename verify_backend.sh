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

# 1. Teacher Dashboard
print_header "Teacher: Dashboard Stats"
curl -s "$BASE_URL/teacher/dashboard/stats" | python3 -m json.tool

print_header "Teacher: Get Classes"
curl -s "$BASE_URL/teacher/classes" | python3 -m json.tool

# 2. Student Dashboard
print_header "Student: Dashboard Summary"
curl -s "$BASE_URL/student/dashboard-summary" | python3 -m json.tool

print_header "Student: Get Profile"
curl -s "$BASE_URL/student/profile" | python3 -m json.tool

# 3. Assignments Flow
print_header "Teacher: Create Assignment"
# Capture the response to get the ID if needed, but for now just printing
curl -s -X POST "$BASE_URL/teacher/assignments/create" \
     -H "Content-Type: application/json" \
     -d '{"title": "Physics Project", "description": "Build a model rocket"}' | python3 -m json.tool

print_header "Teacher: List Assignments"
curl -s "$BASE_URL/teacher/assignments" | python3 -m json.tool

print_header "Student: List Assignments"
curl -s "$BASE_URL/student/assignments" | python3 -m json.tool

# We need an assignment ID to submit to. Let's assume ID 1 exists from DataSeeder.
ASSIGNMENT_ID=1
print_header "Student: Submit Assignment (ID: $ASSIGNMENT_ID)"
curl -s -X POST "$BASE_URL/student/assignments/$ASSIGNMENT_ID/submit" \
     -F "file=@test_upload.txt"

# 4. Messaging
print_header "Messages: Get Contacts"
curl -s "$BASE_URL/messages/contacts" | python3 -m json.tool

print_header "Messages: Send Message"
curl -s -X POST "$BASE_URL/messages/send" \
     -H "Content-Type: application/json" \
     -d '{"receiverId": 1, "content": "Hello Teacher, I have a question."}' 

# 5. Notifications
print_header "Notifications: List All"
curl -s "$BASE_URL/notifications" | python3 -m json.tool

# 6. Career Guidance
print_header "Career: Get Quiz Questions"
curl -s "$BASE_URL/career-quiz/questions" | python3 -m json.tool

print_header "Career: Get Stories"
curl -s "$BASE_URL/career-stories" | python3 -m json.tool

# Cleanup
rm test_upload.txt

echo -e "\n\n\033[1;32mTests Completed!\033[0m"
