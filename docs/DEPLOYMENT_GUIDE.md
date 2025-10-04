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
CREATE USER 'dvp_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON dvp_gift_center_prod.* TO 'dvp_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Import Schema (provided SQL; no runtime migrations)
```bash
mysql -u dvp_user -p dvp_gift_center_prod < database/dvp_gift_center_schema.sql
```

### 3. Backend Production Build

#### Create Production Properties
`backend/src/main/resources/application-prod.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/dvp_gift_center_prod
spring.datasource.username=dvp_user
spring.datasource.password=secure_password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
jwt.secret=production-secret-key-very-secure-change-this
jwt.expiration=86400000

# CORS Configuration
cors.allowed-origins=https://yourdomain.com

# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Business
# Configurable tax rate used at checkout (5% example)
app.tax.rate=0.05

# Logging Configuration
logging.level.com.dvpgiftcenter=INFO
logging.file.name=logs/application.log
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.total-size-cap=100MB

# Security Headers
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
```

#### Build and Deploy
```bash
cd backend
mvn clean package -Pprod
sudo cp target/dvp-gift-center-backend-1.0.0.jar /opt/dvp-gift-center/
```

#### Create Systemd Service
`/etc/systemd/system/dvp-gift-center.service`:
```ini
[Unit]
Description=DVP Gift Center Backend
After=mysql.service

[Service]
Type=simple
User=dvp-service
Group=dvp-service
WorkingDirectory=/opt/dvp-gift-center
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod dvp-gift-center-backend-1.0.0.jar
Restart=always
RestartSec=10

# Environment variables
Environment=JAVA_OPTS="-Xms512m -Xmx1024m"
Environment=SPRING_PROFILES_ACTIVE=prod

# Logging
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

#### Start Service
```bash
sudo systemctl daemon-reload
sudo systemctl enable dvp-gift-center
sudo systemctl start dvp-gift-center
sudo systemctl status dvp-gift-center
```

### 4. Frontend Production Build

#### Build React Application
```bash
cd frontend
npm ci --production
npm run build
```

#### Configure Nginx
`/etc/nginx/sites-available/dvp-gift-center`:
```nginx
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;

    ssl_certificate /path/to/ssl/cert.pem;
    ssl_certificate_key /path/to/ssl/private.key;

    # SSL Configuration
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    ssl_prefer_server_ciphers off;

    # Frontend
    location / {
        root /var/www/dvp-gift-center;
        index index.html;
        try_files $uri $uri/ /index.html;
        
        # Cache static files
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }

    # Backend API
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # CORS headers
        add_header Access-Control-Allow-Origin "https://yourdomain.com" always;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
        add_header Access-Control-Allow-Headers "Authorization, Content-Type" always;
    }

    # Security Headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
}
```

#### Deploy Frontend
```bash
sudo cp -r frontend/build/* /var/www/dvp-gift-center/
sudo chown -R www-data:www-data /var/www/dvp-gift-center/
sudo nginx -t
sudo systemctl reload nginx
```

## Docker Deployment

> Note on schema migrations
>
> This project uses Flyway. Ensure `spring.jpa.hibernate.ddl-auto=none` and Flyway is enabled so migrations run first. See `docs/MIGRATION_AND_SECURITY.md` for details on the `BIGINT UNSIGNED` alignment migration and how to verify successful application.

### 1. Backend Dockerfile
`backend/Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim

# Install required packages
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /app

# Copy the built jar
COPY target/dvp-gift-center-backend-1.0.0.jar app.jar

# Create non-root user
RUN groupadd -r dvp && useradd -r -g dvp dvp
RUN chown -R dvp:dvp /app
USER dvp

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=docker"]
```

### 2. Frontend Dockerfile
`frontend/Dockerfile`:
```dockerfile
# Multi-stage build
FROM node:18-alpine as builder

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy build files
COPY --from=builder /app/build /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Create non-root user
RUN addgroup -g 1001 -S nginx-group && \
    adduser -S nginx-user -u 1001 -G nginx-group

# Change ownership
RUN chown -R nginx-user:nginx-group /usr/share/nginx/html
RUN chown -R nginx-user:nginx-group /var/cache/nginx
RUN chown -R nginx-user:nginx-group /var/log/nginx

USER nginx-user

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### 3. Docker Compose
`docker-compose.yml`:
```yaml
version: '3.8'

services:
  database:
    image: mysql:8.0
    container_name: dvp-mysql
    environment:
      MYSQL_DATABASE: dvp_gift_center
      MYSQL_USER: dvp_user
      MYSQL_PASSWORD: secure_password
      MYSQL_ROOT_PASSWORD: root_password
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database/dvp_gift_center_schema.sql:/docker-entrypoint-initdb.d/schema.sql
    ports:
      - "3306:3306"
    networks:
      - dvp-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 30s
      timeout: 10s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: dvp-backend
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:mysql://database:3306/dvp_gift_center
      SPRING_DATASOURCE_USERNAME: dvp_user
      SPRING_DATASOURCE_PASSWORD: secure_password
      JWT_SECRET: docker-secret-key-change-in-production
    ports:
      - "8080:8080"
    depends_on:
      database:
        condition: service_healthy
    networks:
      - dvp-network
    volumes:
      - backend_logs:/app/logs
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: dvp-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - dvp-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:80"]
      interval: 30s
      timeout: 10s
      retries: 3

networks:
  dvp-network:
    driver: bridge

volumes:
  mysql_data:
  backend_logs:
```

### 4. Deploy with Docker
```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Scale services
docker-compose up -d --scale backend=2

# Stop services
docker-compose down

# Clean up volumes (WARNING: This will delete data)
docker-compose down -v
```

## Cloud Deployment

### AWS Deployment

#### 1. EC2 Instance Setup
```bash
# Launch EC2 instance (t3.medium or larger)
# Configure security groups:
# - HTTP (80) from anywhere
# - HTTPS (443) from anywhere
# - SSH (22) from your IP
# - MySQL (3306) internal only

# Connect to instance
ssh -i your-key.pem ec2-user@your-instance-ip
```

#### 2. RDS Database Setup
```bash
# Create RDS MySQL instance
# Configure security group to allow access from EC2
# Note the endpoint URL for configuration
```

#### 3. Application Deployment
```bash
# Follow production deployment steps
# Update database URL to RDS endpoint
spring.datasource.url=jdbc:mysql://your-rds-endpoint:3306/dvp_gift_center
```

### Azure Deployment

#### 1. App Service Setup
```bash
# Create App Service Plan
az appservice plan create --name dvp-plan --resource-group dvp-rg --sku B2

# Create Web App
az webapp create --name dvp-gift-center --resource-group dvp-rg --plan dvp-plan --runtime "JAVA|17-java17"
```

#### 2. Database Setup
```bash
# Create Azure Database for MySQL
az mysql flexible-server create --resource-group dvp-rg --name dvp-mysql --admin-user dvpadmin --admin-password SecurePassword123!
```

### Google Cloud Deployment

#### 1. App Engine Setup
`app.yaml`:
```yaml
runtime: java17
service: default

env_variables:
  SPRING_PROFILES_ACTIVE: gcp
  SPRING_DATASOURCE_URL: jdbc:mysql://google/dvp_gift_center?socketFactory=com.google.cloud.sql.mysql.SocketFactory&cloudSqlInstance=your-project:region:instance-name
  SPRING_DATASOURCE_USERNAME: dvp_user
  SPRING_DATASOURCE_PASSWORD: secure_password

automatic_scaling:
  min_instances: 1
  max_instances: 10
  target_cpu_utilization: 0.6

resources:
  cpu: 2
  memory_gb: 4
```

## Environment Configuration

### Environment Variables

#### Backend
```bash
# Database
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/dvp_gift_center
export SPRING_DATASOURCE_USERNAME=dvp_user
export SPRING_DATASOURCE_PASSWORD=secure_password

# JWT
export JWT_SECRET=your-secure-secret-key
export JWT_EXPIRATION=86400000

# CORS
export CORS_ALLOWED_ORIGINS=https://yourdomain.com

# Profiles
export SPRING_PROFILES_ACTIVE=prod
```

#### Frontend
```bash
export REACT_APP_API_URL=https://api.yourdomain.com
export REACT_APP_ENVIRONMENT=production
export REACT_APP_VERSION=1.0.0
```

### Configuration Management

#### Using External Config Server
```properties
# application.properties
spring.config.import=configserver:https://config.yourdomain.com/
spring.cloud.config.uri=https://config.yourdomain.com/
```

#### Using Kubernetes ConfigMaps
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: dvp-config
data:
  application.properties: |
    spring.datasource.url=jdbc:mysql://mysql-service:3306/dvp_gift_center
    jwt.secret=kubernetes-secret-key
```

## Security Considerations

### 1. Database Security
```sql
-- Create specific user with limited permissions
CREATE USER 'dvp_app'@'localhost' IDENTIFIED BY 'complex_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON dvp_gift_center.* TO 'dvp_app'@'localhost';

-- Enable SSL
ALTER USER 'dvp_app'@'localhost' REQUIRE SSL;
```

### 2. Application Security
```properties
# Enable security headers
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=strict

# SSL Configuration
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
```

### 3. Network Security
```bash
# Firewall configuration (UFW)
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw deny 8080/tcp  # Block direct backend access
sudo ufw enable
```

### 4. Secrets Management
```bash
# Using environment variables
export $(cat .env | grep -v '#' | xargs)

# Using Docker secrets
docker secret create jwt_secret jwt_secret.txt
```

## Monitoring & Logging

### 1. Application Metrics
```properties
# Enable Actuator endpoints
management.endpoints.web.exposure.include=health,metrics,info,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```

### 2. Log Configuration
```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

### 3. Health Checks
```bash
# Backend health check
curl -f http://localhost:8080/actuator/health

# Frontend health check
curl -f http://localhost:3000

# Database health check
mysqladmin ping -h localhost -u dvp_user -p
```

## Backup & Recovery

### 1. Database Backup
```bash
# Daily backup script
#!/bin/bash
BACKUP_DIR="/backups/mysql"
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u dvp_user -p dvp_gift_center > "$BACKUP_DIR/dvp_backup_$DATE.sql"

# Keep only last 7 days
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
```

### 2. Application Backup
```bash
# Backup configuration and logs
tar -czf dvp_backup_$(date +%Y%m%d).tar.gz \
    /opt/dvp-gift-center/ \
    /etc/nginx/sites-available/dvp-gift-center \
    /etc/systemd/system/dvp-gift-center.service \
    /var/log/dvp-gift-center/
```

### 3. Automated Backups
```bash
# Add to crontab
crontab -e

# Daily database backup at 2 AM
0 2 * * * /scripts/backup_database.sh

# Weekly full backup at 3 AM on Sundays
0 3 * * 0 /scripts/backup_full.sh
```

## Troubleshooting

### Common Issues

#### 1. Backend Won't Start
```bash
# Check Java version
java -version

# Check port availability
netstat -tlnp | grep 8080

# Check database connection
mysql -u dvp_user -p -h localhost

# View application logs
tail -f logs/application.log

# Check system service
sudo systemctl status dvp-gift-center
sudo journalctl -u dvp-gift-center -f
```

#### 2. Frontend Build Issues
```bash
# Clear npm cache
npm cache clean --force

# Remove node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Check Node.js version
node --version
npm --version
```

#### 3. Database Connection Issues
```bash
# Test connection
mysql -u dvp_user -p -h localhost dvp_gift_center

# Check MySQL service
sudo systemctl status mysql

# Check MySQL logs
sudo tail -f /var/log/mysql/error.log
```

#### 4. SSL/HTTPS Issues
```bash
# Verify certificate
openssl x509 -in certificate.crt -text -noout

# Test SSL connection
openssl s_client -connect yourdomain.com:443

# Check nginx configuration
sudo nginx -t
```

### Performance Tuning

#### 1. JVM Tuning
```bash
# JVM options for production
JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

#### 2. Database Tuning
```sql
-- MySQL optimization
SET GLOBAL innodb_buffer_pool_size = 2147483648;  -- 2GB
SET GLOBAL query_cache_size = 268435456;  -- 256MB
```

#### 3. Nginx Tuning
```nginx
# nginx.conf
worker_processes auto;
worker_connections 1024;

# Enable gzip compression
gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_types text/css text/javascript application/javascript;

# Enable browser caching
location ~* \.(js|css|png|jpg|jpeg|gif|ico)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

This deployment guide provides comprehensive instructions for deploying the DVP Gift Center application in various environments with proper security, monitoring, and maintenance procedures.

## Development Setup Instructions

### Windows

#### 1. Install Prerequisites
- **Java 17+**: Download and install from [Adoptium](https://adoptium.net/).
- **Node.js 16+**: Download and install from [Node.js official website](https://nodejs.org/).
- **MySQL 8.0+**: Download and install from [MySQL official website](https://www.mysql.com/).
- **Git**: Download and install from [Git official website](https://git-scm.com/).
- **Maven 3.6+**: Download and install from [Apache Maven official website](https://maven.apache.org/).

#### 2. Set Up Environment Variables
```bash
# Database
setx SPRING_DATASOURCE_URL "jdbc:mysql://localhost:3306/dvp_gift_center"
setx SPRING_DATASOURCE_USERNAME "dvp_user"
setx SPRING_DATASOURCE_PASSWORD "secure_password"

# JWT
setx JWT_SECRET "your-secure-secret-key"
setx JWT_EXPIRATION "86400000"

# CORS
setx CORS_ALLOWED_ORIGINS "https://yourdomain.com"

# Profiles
setx SPRING_PROFILES_ACTIVE "prod"
```

#### 3. Install and Configure MySQL
- During installation, configure the root password and note it down.
- Create a new database and user for the application:
```sql
CREATE DATABASE dvp_gift_center;
CREATE USER 'dvp_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON dvp_gift_center.* TO 'dvp_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 4. Build and Run the Application
```bash
# Clone the repository
git clone https://github.com/your-repo/dvp-gift-center.git
cd dvp-gift-center

# Build the backend
cd backend
mvn clean install

# Run the backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# In a new terminal, build and run the frontend
cd frontend
npm install
npm start
```

### macOS

#### 1. Install Prerequisites
```bash
# Install Homebrew if not installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java 17
brew install openjdk@17

# Install Node.js 16
brew install node@16

# Install MySQL 8
brew install mysql@8

# Install Git
brew install git

# Install Maven 3.6
brew install maven
```

#### 2. Set Up Environment Variables
```bash
# Database
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/dvp_gift_center
export SPRING_DATASOURCE_USERNAME=dvp_user
export SPRING_DATASOURCE_PASSWORD=secure_password

# JWT
export JWT_SECRET=your-secure-secret-key
export JWT_EXPIRATION=86400000

# CORS
export CORS_ALLOWED_ORIGINS=https://yourdomain.com

# Profiles
export SPRING_PROFILES_ACTIVE=prod
```

#### 3. Install and Configure MySQL
- Start MySQL service:
```bash
brew services start mysql@8
```
- Secure MySQL installation:
```bash
mysql_secure_installation
```
- Create a new database and user for the application:
```sql
CREATE DATABASE dvp_gift_center;
CREATE USER 'dvp_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON dvp_gift_center.* TO 'dvp_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 4. Build and Run the Application
```bash
# Clone the repository
git clone https://github.com/your-repo/dvp-gift-center.git
cd dvp-gift-center

# Build the backend
cd backend
mvn clean install

# Run the backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# In a new terminal, build and run the frontend
cd frontend
npm install
npm start
```