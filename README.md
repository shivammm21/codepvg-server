# CodePVG Server

A comprehensive Spring Boot-based coding platform that provides an online judge system for competitive programming and coding practice. The platform integrates with Judge0 for secure code execution and supports 60+ programming languages.

## 🚀 Features

### Core Functionality
- **Multi-language Code Execution**: Support for 60+ programming languages via Judge0 integration
- **Problem Management**: Create, import, and manage coding problems with test cases
- **User Management**: Role-based access control (Admin/Student) with approval workflow
- **Real-time Code Judging**: Instant feedback with execution time, memory usage, and detailed error messages
- **Comprehensive Analytics**: Leaderboards, performance tracking, and detailed statistics

### Advanced Features
- **Excel Import**: Bulk problem import from Excel spreadsheets
- **Multiple Execution Modes**: Run, debug, and submit code with different feedback levels
- **Rich Problem Structure**: Examples, test cases, constraints, and difficulty levels
- **Academic Integration**: Student profiles with year, branch, and academic information
- **Social Features**: LinkedIn and GitHub profile integration

## 🛠 Technology Stack

- **Backend**: Spring Boot 3.5.4, Java 17
- **Security**: Spring Security with JWT authentication
- **Database**: MongoDB with Spring Data MongoDB
- **Code Execution**: Judge0 API integration
- **Build Tool**: Maven
- **File Processing**: Apache POI for Excel handling

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB 4.4+
- Docker and Docker Compose (for Judge0)
- Ubuntu 22.04 (recommended for Judge0)

## 🔧 Installation & Setup

### CORS Configuration
The application is configured to accept requests only from `http://localhost:3000` for security. This is where your frontend React/Vue/Angular application should be running.

### 1. Clone the Repository

```bash
git clone <repository-url>
cd codepvg-server
```

### 2. Database Setup

**Option A: Local MongoDB**
```bash
# Install MongoDB
sudo apt update
sudo apt install -y mongodb

# Start MongoDB service
sudo systemctl start mongodb
sudo systemctl enable mongodb
```

**Option B: MongoDB Atlas (Production)**
- Create a MongoDB Atlas cluster
- Update `application-prod.properties` with your connection string

### 3. Judge0 Setup

Judge0 provides secure, sandboxed code execution. Follow these steps for deployment:

#### System Requirements
- Ubuntu 22.04 (recommended)
- Docker and Docker Compose
- Minimum 2GB RAM, 10GB disk space

#### GRUB Configuration (Ubuntu 22.04)
```bash
# Open GRUB configuration
sudo nano /etc/default/grub

# Add to GRUB_CMDLINE_LINUX variable:
GRUB_CMDLINE_LINUX="systemd.unified_cgroup_hierarchy=0"

# Apply changes
sudo update-grub
sudo reboot
```

#### Install Docker and Docker Compose
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Logout and login again for group changes to take effect
```

#### Deploy Judge0
```bash
# Download Judge0
wget https://github.com/judge0/judge0/releases/download/v1.13.1/judge0-v1.13.1.zip
unzip judge0-v1.13.1.zip
cd judge0-v1.13.1

# Generate secure passwords
# Visit https://www.random.org/passwords/?num=1&len=32&format=html&rnd=new

# Update judge0.conf with generated passwords
nano judge0.conf
# Set REDIS_PASSWORD=<your-generated-password>
# Set POSTGRES_PASSWORD=<your-generated-password>

# Start Judge0 services
docker-compose up -d db redis
sleep 10s
docker-compose up -d
sleep 5s

# Verify installation
curl http://localhost:2358/docs
```

#### Judge0 Configuration for Production
```bash
# For production, update judge0.conf:
nano judge0.conf

# Key settings:
REDIS_PASSWORD=your-secure-redis-password
POSTGRES_PASSWORD=your-secure-postgres-password
JUDGE0_HOMEPAGE=http://your-domain.com
JUDGE0_SOURCE_CODE=https://github.com/your-org/judge0
```

### 4. Application Configuration

#### Development Environment
```bash
# Copy and configure application properties
cp src/main/resources/application-dev.properties src/main/resources/application.properties

# Update configuration
nano src/main/resources/application.properties
```

**Key Configuration Properties:**
```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/codepvg

# Judge0 Configuration
judge0.api.url=http://localhost:2358

# JWT Configuration (Generate a secure secret)
jwt.secret=your-256-bit-secret-key-here
jwt.expiration=86400000

# Admin Configuration
admin.access.code=CODEPVG_ADMIN_2024_SECURE

# Server Configuration
server.port=8080
```

#### Production Environment
```properties
# MongoDB Atlas
spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/codepvg

# Judge0 Production URL
judge0.api.url=http://your-judge0-server:2358

# Secure JWT Secret
jwt.secret=your-production-jwt-secret-256-bits
```

### 5. Build and Run

```bash
# Build the application
mvn clean compile

# Run in development mode
mvn spring-boot:run

# Or build JAR and run
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## 🔐 Admin Setup

### Admin Registration
Admins can register using the dedicated admin registration endpoint with the following requirements:

**Admin Access Code:** `CODEPVG_ADMIN_2024_SECURE`

**Registration Fields:**
- First Name & Last Name
- Department (e.g., Computer Science, Information Technology)
- Admin Access Code (required for verification)
- Email Address
- Password (minimum 8 characters)
- Confirm Password

**⚠️ Important**: 
- Change the admin access code in production environments
- Admin accounts are automatically approved upon successful registration
- The admin access code is configured in `application.properties`

## 📚 API Documentation

### Authentication Endpoints
```
POST /api/auth/register - Student registration
POST /api/auth/register/admin - Admin registration
POST /api/auth/login - User login
GET  /api/auth/status/{username} - Check user status by username
GET  /api/auth/status/email/{email} - Check user status by email
```

### Admin Endpoints
```
GET  /api/admin/users/pending - Get pending user approvals
POST /api/admin/users/{id}/approve - Approve user
POST /api/admin/problems/create - Create problem
POST /api/admin/problems/import - Import problems from Excel
GET  /api/admin/leaderboard - Get detailed leaderboard
```

### Student Endpoints
```
GET  /api/student/problems - Get all problems
POST /api/student/submissions - Submit code
POST /api/student/submissions/run - Run code against examples
GET  /api/student/dashboard - Get student dashboard
```

## 📊 Excel Import Format

For bulk problem import, use this Excel format:

| Column | Field | Description |
|--------|-------|-------------|
| A | Title | Problem title |
| B | Description | Problem description |
| C | Difficulty | EASY/MEDIUM/HARD |
| D | Topics | Comma-separated (array,tree,dp) |
| E | Example Input | Sample input |
| F | Example Output | Expected output |
| G | Test Input | Test case input |
| H | Test Output | Test case output |
| I | Constraints | Problem constraints |

## 🐳 Docker Deployment

### Application Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose Setup
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/codepvg
      - JUDGE0_API_URL=http://judge0:2358
    depends_on:
      - mongo
      - judge0

  mongo:
    image: mongo:5.0
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db

  judge0:
    # Use Judge0 docker-compose.yml from their release

volumes:
  mongo_data:
```

## 🔧 Troubleshooting

### Common Issues

**1. Judge0 Connection Failed**
```bash
# Check Judge0 status
docker-compose -f judge0-v1.13.1/docker-compose.yml ps

# Check logs
docker-compose -f judge0-v1.13.1/docker-compose.yml logs
```

**2. MongoDB Connection Issues**
```bash
# Check MongoDB status
sudo systemctl status mongodb

# Test connection
mongo --eval "db.adminCommand('ismaster')"
```

**3. Application Won't Start**
```bash
# Check Java version
java -version

# Check port availability
netstat -tulpn | grep :8080

# View application logs
tail -f logs/spring.log
```

### Performance Tuning

**Judge0 Optimization:**
```bash
# In judge0.conf, adjust:
JUDGE0_MAX_QUEUE_SIZE=100
JUDGE0_MAX_WORKERS_PER_QUEUE=10
JUDGE0_ENABLE_WAIT_RESULT=true
```

**Application Tuning:**
```properties
# In application.properties:
spring.data.mongodb.max-connection-idle-time=60000
spring.data.mongodb.max-connection-life-time=120000
server.tomcat.max-threads=200
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Acknoswledgments

- [Judge0](https://judge0.com) for providing the robust code execution API
- [Spring Boot](https://spring.io/projects/spring-boot) for the excellent framework
- [MongoDB](https://www.mongodb.com) for the flexible database solution

