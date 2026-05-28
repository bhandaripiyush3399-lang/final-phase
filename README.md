# College Ride Share System

A multi-college ride booking and ride sharing system built with **Java** to help students who miss their college bus reach college on time.

> **Two versions available:**
> - **Web Application** (Spring Boot) - Deployable online, accessible via browser
> - **Desktop Application** (JavaFX) - Runs locally on your machine

---

## Live Demo (Web Version)

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/bhandaripiyush3399-lang/final-phase)

**To deploy your own instance:**
1. Click the "Deploy to Render" button above (or go to [render.com](https://render.com))
2. Connect your GitHub account
3. Select this repository
4. The `render.yaml` file will auto-configure everything
5. Wait for the build to complete and get your live URL

**Sample Login:** `PICT001` / `password123`

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
- Accepting a booking decreases available seats (seats checked before accepting)
- When seats reach zero, ride status changes to **FULL**
- Cancellation restores the seat only if booking was ACCEPTED

### Notification System
- In-app notifications for:
  - New booking request
  - Ride accepted / rejected
  - Ride cancelled (by provider)
  - Booking cancelled (by seeker)
- Unread notification count shown in navigation

### Safety & Emergency
- Emergency college contact numbers displayed in the system
- All registered colleges with their addresses and contact numbers

### Payment
- **Cash-based only** (no online payment)
- Fare is **negotiable** between rider and provider

---

## Tech Stack

### Web Application (Spring Boot)
| Component    | Technology                    |
|-------------|-------------------------------|
| Language    | Java 17                       |
| Framework   | Spring Boot 3.2               |
| Frontend    | Thymeleaf + Bootstrap 5       |
| Database    | H2 (embedded, no setup needed)|
| Security    | Spring Security + BCrypt      |
| Architecture| MVC                           |
| Deployment  | Docker / Render               |

### Desktop Application (JavaFX)
| Component    | Technology               |
|-------------|--------------------------|
| Language    | Java 17                  |
| UI Framework| JavaFX 17                |
| Architecture| MVC                      |
| Database    | MySQL 8.x                |
| Build Tool  | Maven                    |
| Security    | BCrypt password hashing  |

---

## Project Structure

```
final-phase/
├── README.md
├── render.yaml                      # Render deployment config
│
├── web-app/                         # WEB APPLICATION (Spring Boot)
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/rideshare/
│       ├── RideShareWebApplication.java
│       ├── model/                   # JPA Entities
│       ├── repository/              # Spring Data repositories
│       ├── service/                 # Business logic
│       ├── controller/              # Web controllers
│       └── config/                  # Security & data init
│
├── pom.xml                          # DESKTOP APP (JavaFX)
├── database/
│   └── schema.sql                   # MySQL schema + sample data
└── src/main/java/com/rideshare/     # JavaFX desktop app
    ├── App.java
    ├── model/
    ├── dao/
    ├── controller/
    └── view/
```

---

## Quick Start - Web Application

### Option 1: Run Locally (No database setup needed!)

```bash
cd web-app
mvn clean package -DskipTests
java -jar target/college-ride-share-web-1.0-SNAPSHOT.jar
```

Open **http://localhost:8080** in your browser.

### Option 2: Deploy to Render (Free hosting)

1. Push this repo to your GitHub
2. Go to [render.com](https://render.com) → New → Web Service
3. Connect your GitHub repo
4. Render auto-detects the `render.yaml` and deploys
5. Get your live URL!

### Option 3: Docker

```bash
cd web-app
docker build -t college-rideshare .
docker run -p 8080:8080 college-rideshare
```

---

## Quick Start - Desktop Application (JavaFX)

### Step 1: Set Up MySQL Database

```bash
mysql -u root -p < database/schema.sql
```

### Step 2: Configure Database Connection

Edit `src/main/java/com/rideshare/model/DatabaseConnection.java`:

```java
private static final String USER = "root";        // Your MySQL username
private static final String PASSWORD = "root";     // Your MySQL password
```

### Step 3: Build & Run

```bash
mvn clean compile
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
3. **Dashboard** - Welcome screen with user info and quick actions
4. **Book a Ride** - Browse and book available rides
5. **Offer a Ride** - Create a new ride offer
6. **My Offered Rides** - Manage rides and handle booking requests (accept/reject)
7. **My Bookings** - Track booking status and cancel if needed
8. **Notifications** - View all notifications with unread badges
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
- **Seeker cancels**: Seat restored only if booking was ACCEPTED, provider notified
- **Provider cancels ride**: All active bookings cancelled, all seekers notified

---

## Corner Cases Handled

- Bike allows only 1 passenger
- Cannot book your own ride
- Cannot book same ride twice
- Seat availability checked before accepting a booking (prevents overbooking)
- Cancelling a PENDING booking does not increment seats (only ACCEPTED bookings do)
- Ride closes when seats reach zero
- Past departure times are rejected
- Cancelled rides cannot be booked
- Cancellation restores seat count correctly

---

## Academic Requirements Met

- MVC Architecture (Model / DAO-Repository / Controller / View)
- Java 17 with JavaFX and Spring Boot
- MySQL (desktop) and H2 (web) relational databases with constraints
- Complete CRUD operations
- Input validation and error handling
- Password security with BCrypt hashing
- Notification system for all ride events
- Deployable web version

---

## License

This project is for academic purposes (Full Semester PBL).
