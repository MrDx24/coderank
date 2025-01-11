# CodeRank

> Transform your coding workflow with our powerful online code execution platform

CodeRank is an advanced distributed system that allows developers to compile and execute code across multiple programming languages in a secure, scalable environment. Built with a modern microservices architecture, it provides real-time code execution capabilities with robust error handling and detailed performance metrics.

## 🌟 Core Features

- **Multi-Language Support**: Execute code in Java, Python, JavaScript, and Ruby
- **Real-Time Execution**: Get immediate output on your code execution
- **Secure Environment**: Run code in isolated containers for maximum security
- **Scalable Architecture**: Built to handle concurrent executions efficiently
- **Resource Management**: Track execution time, cpu usage and memory usage

## 🏗️ Architecture Overview

CodeRank employs a sophisticated microservices architecture, with each component playing a crucial role in the execution pipeline:

- **Service Registry (Eureka)**: Manages service discovery and registration
- **API Gateway**: Handles request routing and acts as the system's entry point
- **Authentication Service**: Ensures secure access with JWT-based authentication
- **Task Queue Service**: Orchestrates code execution requests
- **Execution Service**: Manages the actual code compilation and running
- **PostgreSQL Database**: Stores user code metadata and execution history
- **Amazon S3**: Stores actual input code and execution result 
- **RabbitMQ**: Enables asynchronous communication between services
- **Docker**: For containerization of the services

## 🚀 Getting Started

### Prerequisites

Before diving in, ensure you have the following installed:

```bash
- Docker Desktop
- Git
- IntelliJ IDEA Community Edition
```

### Installation Steps

1. Clone the repository:
```bash
git clone https://github.com/yourusername/coderank.git
cd coderank
```

2. Launch the services using Docker Compose:
```bash
docker-compose up -d
```

This will start all necessary services in the correct order, with proper dependency management.

## 🛠️ Technology Stack

### Backend Services
- **Spring Boot**: Powers our microservices infrastructure
- **Docker**: Ensures consistent environments across services
- **RabbitMQ**: Manages asynchronous message queuing and concurrency
- **PostgreSQL and Amazon S3**: Provides reliable data persistence
- **Eureka**: Enables dynamic service discovery
- **JWT**: Handles secure authentication

### Deployment
- **Docker Compose**: Orchestrates local development environment

## 🔍 Service Details

### Service Registry (Port: 8761)
The backbone of our microservices architecture, providing service discovery capabilities and maintaining a real-time registry of all active services.

### API Gateway (Port: 8081)
The entry point for all client requests, handling authentication verification and request routing to appropriate services.

### Authentication Service (Port: 8082)
Manages user authentication and authorization, generating and validating JWT tokens for secure access.

### Task Queue Service (Port: 8083)
Orchestrates code execution requests through RabbitMQ, ensuring efficient task distribution and management.

### Execution Service (Port: 8084)
Handles the core functionality of code execution in isolated containers, supporting multiple programming languages.

## 📊 Database Schema

Our PostgreSQL database maintains several key entities:

- User details for authentication and job submission
- Code metadata and execution logs
- AWS and Google Gemini API secret keys 

## 🔐 Security

CodeRank prioritizes security through:

- Containerized execution environments
- JWT-based authentication
- Resource limiting and request validation
- Secure communication between services

*Built by Deep Patadiya*