# Learning Management System

1. Project Title:  Learning Management System (LMS)

2. Overview / Description: This is a full-stack Learning Management System (LMS) built using Spring Boot, Thymeleaf, and MySQL.It supports three roles — Admin, Instructor, and Student — each with their own dashboards, role-based access, and features such as course management, lesson creation, and student registration.

3. Features : 
            User Authentication (Login & Registration)

            Role-based Dashboards (Admin, Instructor, Student)

            Add and Manage Lessons

            Secure Authentication using Spring Security & JWT

            Responsive and Minimal UI (Bootstrap + Thymeleaf)


4. Tech Stack:

            Backend: Spring Boot, Spring Security, JWT

            Frontend: Thymeleaf, HTML, Bootstrap

            Database: MySQL

            Language: Java

            Build Tool: Maven


5. Setup Instructions: 
            # 1. Clone the repository
            git clone https://github.com/Velocityy19/Learning-Management-System.git

            # 2. Navigate into the project
            cd Learning-Management-System

            # 3. Configure database in application.properties
            spring.datasource.url=jdbc:mysql://localhost:3306/lms
            spring.datasource.username=root
            spring.datasource.password=yourpassword

            # 4. Run the app
            mvn spring-boot:run


6. Known Issues / Limitations:

    Firebase Storage integration could not be completed due to payment verification issues.

    Currently, local storage or manual uploads are used instead for media and file handling.

    Future updates may include Firebase or AWS S3 integration once payment setup is resolved.


7. Future Enhancements

    Integrate Firebase Storage or AWS S3 for file uploads

    Add password reset feature

    Enable email notifications for users


8. Author:
    Developed by: Vasanth M
    GitHub: Velocityy19
