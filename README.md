# Inventory Management System

## Overview
A JavaFX-based inventory management system for managing club and restaurant items, tracking sales, damages, and providing admin controls.

## Features
- User authentication and role-based access control
- Inventory management for club and restaurant items
- Sales tracking and reporting
- Damage recording and management
- Admin dashboard and controls
- SMS notifications
- PDF report generation

## Project Structure
```
├── build.gradle                    # Gradle build file
├── settings.gradle                 # Project name settings
├── gradle/                         # Gradle wrapper
├── gradlew                         # Gradle wrapper Unix script
├── gradlew.bat                     # Gradle wrapper Windows script
├── src/
│   ├── main/java/com/app/inventory/
│   │   ├── Main.java               # Application entry point
│   │   ├── App.java                # Main application class
│   │   ├── config/                 # Configuration classes
│   │   │   ├── DBConnection.java   # Database connection
│   │   │   └── AppConfig.java      # Application configuration
│   │   ├── auth/                   # Authentication classes
│   │   │   ├── LoginController.java
│   │   │   ├── RegisterController.java
│   │   │   ├── SessionManager.java
│   │   │   └── AccessControl.java
│   │   ├── models/                 # Data models
│   │   │   ├── User.java
│   │   │   ├── Role.java
│   │   │   ├── ClubItem.java
│   │   │   ├── RestaurantItem.java
│   │   │   ├── Sale.java
│   │   │   └── Damage.java
│   │   ├── services/               # Business logic services
│   │   │   ├── AuthService.java
│   │   │   ├── UserService.java
│   │   │   ├── ClubInventoryService.java
│   │   │   ├── RestaurantService.java
│   │   │   ├── DamageService.java
│   │   │   ├── SalesService.java
│   │   │   └── SmsService.java
│   │   ├── controllers/            # FXML controllers
│   │   │   ├── DashboardController.java
│   │   │   ├── AdminController.java
│   │   │   ├── ClubInventoryController.java
│   │   │   ├── RestaurantController.java
│   │   │   ├── SalesController.java
│   │   │   └── DamageController.java
│   │   ├── repository/             # Data access layer
│   │   │   ├── UserRepository.java
│   │   │   ├── ClubInventoryRepository.java
│   │   │   ├── RestaurantRepository.java
│   │   │   ├── SalesRepository.java
│   │   │   └── DamageRepository.java
│   │   ├── utils/                  # Utility classes
│   │   │   ├── Validators.java
│   │   │   ├── DateUtil.java
│   │   │   ├── PDFGenerator.java
│   │   │   └── AlertUtil.java
│   │   └── enums/                  # Enumeration classes
│   │       ├── ItemType.java
│   │       └── DamageType.java
│   └── resources/
│       ├── fxml/                   # FXML UI files
│       │   ├── login.fxml
│       │   ├── dashboard.fxml
│       │   ├── admin.fxml
│       │   ├── club_inventory.fxml
│       │   ├── restaurant.fxml
│       │   ├── sales.fxml
│       │   └── record_damage.fxml
│       ├── css/
│       │   └── main.css
│       └── images/                 # Application images
│           ├── logo-placeholder.txt
│           └── icons/
│               └── placeholder-icons.txt
├── src/test/                       # Test files
└── README.md                       # This file
```

## Getting Started
1. Ensure Java 11+ is installed
2. Import the project into IntelliJ IDEA
3. Run `./gradlew build` to build the project
4. Run `./gradlew run` to start the application

## Technologies Used
- Java 11+
- JavaFX
- Gradle
- IntelliJ IDEA
