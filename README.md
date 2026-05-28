# College Ride Share System

A multi-college, desktop-based ride booking and ride sharing system built with **JavaFX** to help students who miss their college bus reach college on time.

---

## Problem Statement

Students who miss their college bus are forced to wait for the next bus or arrive late. At the same time, other students from the same or nearby colleges travel using their own vehicles with available seats. This system provides a centralized, secure platform to connect these students.

---

## Features

### Authentication & Access Control
- Login using **Roll Number + Password**
- Users must select their **college** during registration
- Only registered college students can access the system
- Passwords are securely hashed using **BCrypt**

### Ride Booking (Seeker)
- Browse all available rides from students across colleges
- View ride details: route, departure time, vehicle type, fare, available seats
- Send booking request to ride provider
- Cancel booking at any time

### Ride Offering (Provider)
- Offer rides with vehicle type, route, departure time, seats, and fare
- Manage incoming booking requests (Accept / Reject)
- Cancel ride (notifies all booked passengers)

### Vehicle & Seat Logic
| Vehicle Type   | Max Passengers |
|---------------|---------------|
| Bike          | 1             |
| Car           | Multiple      |
| Light Vehicle | Multiple      |

- Bike: Only **1** passenger allowed
- Car/Light Vehicle: Provider sets number of available seats
- Accepting a booking decreases available seats
- When seats reach zero, ride status changes to **FULL**
- Cancellation restores the seat

### Notification System
- In-app notifications for:
  - New booking request
  - Ride accepted / rejected
  - Ride cancelled (by provider)
  - Booking cancelled (by seeker)
- Unread notification count shown in sidebar

### Safety & Emergency
- Emergency college contact numbers displayed in the system
- All registered colleges with their addresses and contact numbers

### Payment
- **Cash-based only** (no online payment)
- Fare is **negotiable** between rider and provider

---

## Tech Stack

| Component    | Technology               |
|-------------|--------------------------|
| Language    | Java 17                  |
| UI Framework| JavaFX 17                |
| Architecture| MVC (Model-View-Controller) |
| Database    | MySQL 8.x                |
| Build Tool  | Maven                    |
| Security    | BCrypt password hashing  |
| Notifications| JavaMail (email-ready)  |

---

## Project Structure (MVC Architecture)

```
final-phase/
├── pom.xml                          # Maven build configuration
├── database/
│   └── schema.sql                   # Database schema + sample data
├── README.md
└── src/main/java/
    ├── module-info.java             # Java module descriptor
    └── com/rideshare/
        ├── App.java                 # Main entry point
        │
        ├── model/                   # MODEL - Data entities & DB connection
        │   ├── DatabaseConnection.java
        │   ├── College.java
        │   ├── User.java
        │   ├── Ride.java
        │   ├── Booking.java
        │   └── Notification.java
        │
        ├── dao/                     # Data Access Objects (DB operations)
        │   ├── CollegeDAO.java
        │   ├── UserDAO.java
        │   ├── RideDAO.java
        │   ├── BookingDAO.java
        │   └── NotificationDAO.java
        │
        ├── controller/              # CONTROLLER - Business logic
        │   ├── SessionManager.java
        │   ├── AuthController.java
        │   ├── RideController.java
        │   └── NotificationController.java
        │
        └── view/                    # VIEW - JavaFX UI screens
            ├── ViewHelper.java
            ├── LoginView.java
            ├── DashboardView.java
            ├── BookRideView.java
            ├── OfferRideView.java
            ├── MyRidesView.java
            ├── MyBookingsView.java
            ├── NotificationsView.java
            └── EmergencyContactsView.java
```

---

## Prerequisites

1. **Java 17** (JDK 17 or later)
2. **Maven 3.6+**
3. **MySQL 8.x** (running on localhost)

---

## Setup Instructions

### Step 1: Clone the Repository

```bash
git clone https://github.com/bhandaripiyush3399-lang/final-phase.git
cd final-phase
```

### Step 2: Set Up MySQL Database

1. Start MySQL server
2. Run the schema script:

```bash
mysql -u root -p < database/schema.sql
```

This creates the `college_rideshare` database with all tables and sample data.

### Step 3: Configure Database Connection

Edit the connection details in `src/main/java/com/rideshare/model/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/college_rideshare?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER = "root";        // Your MySQL username
private static final String PASSWORD = "root";     // Your MySQL password
```

### Step 4: Build the Project

```bash
mvn clean compile
```

### Step 5: Run the Application

```bash
mvn javafx:run
```

---

## Sample Login Credentials

| Roll Number | Password      | Name           | College |
|------------|---------------|----------------|---------|
| PICT001    | password123   | Rahul Sharma   | PICT    |
| COEP002    | password123   | Priya Patil    | COEP    |
| VIT003     | password123   | Amit Deshmukh  | VIT     |

---

## Database Schema

### Tables

1. **colleges** - Registered colleges with emergency contacts
2. **users** - Student accounts (roll number, password hash, college)
3. **rides** - Offered rides (route, vehicle, seats, fare, status)
4. **bookings** - Ride booking requests (pending/accepted/rejected/cancelled)
5. **notifications** - In-app notification messages

### Entity Relationships

```
colleges (1) ──── (N) users
users    (1) ──── (N) rides      (as provider)
users    (1) ──── (N) bookings   (as seeker)
rides    (1) ──── (N) bookings
users    (1) ──── (N) notifications
```

---

## Application Screens

1. **Login Screen** - Roll number + password authentication
2. **Registration Screen** - New student registration with college selection
3. **Dashboard** - Welcome screen with user info
4. **Book a Ride** - Browse and book available rides
5. **Offer a Ride** - Create a new ride offer
6. **My Offered Rides** - Manage rides and handle booking requests
7. **My Bookings** - Track booking status
8. **Notifications** - View all notifications
9. **Emergency Contacts** - College emergency numbers

---

## Ride Flow

### Booking Flow
1. Student A offers a ride (sets route, time, vehicle, seats, fare)
2. Student B browses available rides
3. Student B sends a booking request
4. Student A receives notification of the request
5. Student A accepts or rejects the request
6. Student B receives notification of the decision

### Cancellation Flow
- **Seeker cancels**: Seat is restored, provider is notified
- **Provider cancels ride**: All active bookings are cancelled, all seekers are notified

---

## Corner Cases Handled

- Bike allows only 1 passenger
- Cannot book your own ride
- Cannot book same ride twice
- Ride closes when seats reach zero
- Past departure times are rejected
- Cancelled rides cannot be booked
- Cancellation restores seat count

---

## Academic Requirements Met

- MVC Architecture (Model / DAO / Controller / View)
- Java 17 with JavaFX
- MySQL relational database with constraints and foreign keys
- Complete CRUD operations
- Input validation and error handling
- Password security with BCrypt hashing
- Notification system for all ride events

---

## License

This project is for academic purposes (Full Semester PBL).
