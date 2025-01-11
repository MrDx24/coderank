# Coderank - Capstone Project

## A Bird's-Eye View

**Coderank** takes flight as an online code execution platform, empowering developers to compile and run code across various programming languages. Built with a modular microservices architecture for scalability and maintainability, Coderank boasts seven core services, each playing a vital role in the execution symphony:

1. **ServiceRegistry (Eureka):** The maestro, keeping track of all services for seamless discovery.
2. **API Gateway:** The facade, fielding incoming requests and routing them to the designated service.
3. **AuthService:** The gatekeeper, ensuring user authentication and authorization.
4. **TaskQueueService:** The conductor, orchestrating the task queue for code execution.
5. **ExecutionService:** The virtuoso, executing code in diverse programming languages (Java, Python, JavaScript, Ruby!).
6. **PostgreSQL:** The data vault, securely storing user information and execution records.
7. **RabbitMQ:** The messenger, enabling asynchronous communication between services.

Docker provides containerization magic, and Docker Compose orchestrates the multi-container masterpiece. Deployment happens on AWS Elastic Beanstalk, ensuring scalability and effortless management.

---

## Delving Deeper (Table of Contents)

1. [Technologies Used](#technologies-used)
2. [Architectural Marvel](#architecture)
3. [Getting Started Guide](#setup-and-installation)
4. [Service Spotlight](#service-descriptions)

---

## Technological Powerhouse](#technologies-used)

- **Spring Boot:** The robust backend framework for crafting microservices.
- **Docker:** The containerization champion for packaging and deploying applications.
- **RabbitMQ:** The reliable messaging broker for inter-service communication.
- **PostgreSQL:** The trusted relational database for user and task data.
- **Eureka (Service Registry):** The maestro of service discovery.
- **AWS Elastic Beanstalk:** The platform-as-a-service (PaaS) for deploying Dockerized applications.
- **Docker Compose:** The conductor for defining and running multi-container applications.

---

## Architectural Brilliance](#architecture)

Coderank embraces a **microservices architecture**, a symphony of components working together:

1. **ServiceRegistry (Eureka):** The central registry, acting as the service discovery maestro for all other services.
2. **API Gateway:** The facade, a reverse proxy that routes requests to the appropriate service. It handles user requests, performs authentication, and forwards them to the designated service endpoint.
3. **AuthService:** The gatekeeper, safeguarding the system through user authentication and generating JWT tokens for authorized access.
4. **TaskQueueService:** The conductor, managing the task queue for code execution requests. It receives user requests and pushes them onto the `code-execution-queue` for processing.
5. **ExecutionService:** The virtuoso, a master of code execution. It consumes tasks from the `code-execution-queue`, executes the code in the requested language (Java, Python, JavaScript, or Ruby!), and pushes the response to the `execution-response-queue`.
6. **PostgreSQL:** The data vault, securely storing user information, task/code execution results, and other relevant data (execution logs/metadata).
7. **RabbitMQ:** The messenger, enabling asynchronous communication between services. TaskQueueService and ExecutionService leverage RabbitMQ to process execution requests seamlessly.

(Architecture%20daigram.png)  ---

## Getting Started Guide](#setup-and-installation)

### Prerequisites

Before embarking on your coding adventure, ensure you have these tools installed:

- **Docker Desktop** (for containerization magic)
- **Git** (for version control)
- **Intellij Idea Community Edition** (your trusty code IDE)

### Cloning the Repository

To initiate your coding journey, clone the project repository to your local machine:

```bash
git clone [https://github.com/yourusername/coderank.git](https://github.com/yourusername/coderank.git)
cd coderank