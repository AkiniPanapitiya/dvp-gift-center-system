# Deployment Guide - DVP Gift Center

## Overview
This guide provides comprehensive instructions for deploying the DVP Gift Center application in various environments.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Deployment](#development-deployment)
3. [Production Deployment](#production-deployment)
4. [Docker Deployment](#docker-deployment)
5. [Cloud Deployment](#cloud-deployment)
6. [Environment Configuration](#environment-configuration)
7. [Security Considerations](#security-considerations)
8. [Monitoring & Logging](#monitoring--logging)
9. [Backup & Recovery](#backup--recovery)
10. [Troubleshooting](#troubleshooting)

## Prerequisites

### System Requirements
- **Java 17+** (OpenJDK recommended)
- **Node.js 16+** with npm
- **MySQL 8.0+**
- **Git** for version control
- **Maven 3.6+** for backend builds

### Hardware Requirements

#### Development Environment
- **CPU**: 2+ cores
- **RAM**: 4GB minimum, 8GB recommended
- **Storage**: 10GB available space
- **Network**: Stable internet connection

#### Production Environment
- **CPU**: 4+ cores
- **RAM**: 8GB minimum, 16GB recommended
- **Storage**: 50GB+ available space (depends on data volume)
- **Network**: High-speed internet with static IP

## Development Deployment

### 1. Database Setup

#### Windows
```powershell
# Create MySQL database
mysql -u root -p
CREATE DATABASE dvp_gift_center;
exit

# Import schema and data
mysql -u root -p dvp_gift_center < database\dvp_gift_center_schema.sql
```

#### macOS
```bash
# Create MySQL database
mysql -u root -p
CREATE DATABASE dvp_gift_center;
exit

# Import schema and data
mysql -u root -p dvp_gift_center < database/dvp_gift_center_schema.sql
```

### 2. Backend Configuration
Create `backend/src/main/resources/application-dev.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/dvp_gift_center
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
jwt.secret=dev-secret-key-change-in-production
jwt.expiration=86400000

# CORS Configuration
cors.allowed-origins=http://localhost:3000

# Logging
logging.level.com.dvpgiftcenter=DEBUG
logging.level.org.springframework.security=DEBUG
```

### 3. Backend Startup

#### Windows
```powershell
cd backend
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### macOS
```bash
cd backend
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4. Frontend Configuration
Create `frontend/.env.development`:
```
REACT_APP_API_URL=http://localhost:8080
REACT_APP_ENVIRONMENT=development
REACT_APP_VERSION=1.0.0
REACT_APP_DEBUG=true
```

### 5. Frontend Startup

#### Windows
```powershell
cd frontend
npm install
npm start
```

#### macOS
```bash
cd frontend
npm install
npm start
```

The application will be available at:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080

## Production Deployment

### 1. Server Preparation

#### Update System
```bash
sudo apt update && sudo apt upgrade -y
sudo yum update -y  # For CentOS/RHEL
```

#### Install Java 17
```bash
# Ubuntu/Debian
sudo apt install openjdk-17-jdk -y

# CentOS/RHEL
sudo yum install java-17-openjdk-devel -y

# Verify installation
java -version
```

#### Install MySQL
```bash
# Ubuntu/Debian
sudo apt install mysql-server -y

# CentOS/RHEL
sudo yum install mysql-server -y

# Start and enable MySQL
sudo systemctl start mysqld
sudo systemctl enable mysqld

# Secure installation
sudo mysql_secure_installation
```

#### Install Node.js
```bash
# Using NodeSource repository
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Verify installation
node --version
npm --version
```

### 2. Database Configuration

#### Create Production Database
```sql
CREATE DATABASE dvp_gift_center_prod;
```