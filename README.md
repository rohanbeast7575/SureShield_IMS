
# SureShield IMS

SureShield Insurance Management System (IMS) is a comprehensive solution designed to manage all aspects of insurance operations, from user management to policy creation and claims processing. Built with a microservices architecture using Spring Boot, the system is highly scalable, reliable, and optimized for performance.

## Features
- Eureka Server: Manages service discovery.
- API Gateway: Central point for routing requests to respective services.
- User Service: Manages user-related operations.
- Policy Service: Manages insurance policies with Redis for enhanced performance.
- Claims Service: Handles insurance claims efficiently.
- MySQL Database: Persistent storage for user, policy, and claims data.
- Redis: Caching layer for frequently accessed policy data, improving response times.
## Tech Stack

**Backend:**
- Spring Boot (Microservices)
- Spring Data JPA
- Spring Cloud (Eureka, API Gateway)
- MySQL (Relational Database)
- Redis (Caching)

**Frontend:** React (UI for user interactions, policy management, and claims processing)

**Containerization:** 
- Docker (Containerization of services)
- Docker Compose (Multi-container orchestration)

**Others:** 
- Feign Client (Inter-service communication)
- RESTful APIs (For external integrations)
## Documentation

[Documentation](https://linktodocumentation)



![App Screenshot](https://github.com/user-attachments/assets/ed531efc-d321-49b8-abbf-d6a43d33cf11)

![App Screenshot](https://github.com/user-attachments/assets/787429c6-99ac-4c71-a5f2-a4186e986584)



- User Management:
   -  User Registration and Login: Users can register for the system via the Frontend, with requests being handled by the User Service.
    - User data, including contact details, is stored in the User Service's MySQL database.
    - User Profile Management: After logging in, users can view their policies and manage their profiles (update details, view policies, etc.).

- Policy Application Process:
   - Admin Adds Policy: As shown in the second image, the admin creates and manages policies. This is done via the Policy Service, and the data is stored in the MySQL database.
  - Customer Applies for Policy: The customer views policies via the Frontend (React).When a customer applies for a policy, the request is routed through the API Gateway to the Policy Service.The application is saved in the Policy Service's MySQL database.
   - Approval by Admin:The admin approves or rejects the policy application, completing the policy creation workflow.

- Claims Submission and Processing:
  - Customer Submits a Claim:When a customer experiences an event covered by their policy, they can submit a claim through the Frontend.The claim request is routed via the API Gateway to the Claim Service.The claim details are saved in the Claim Service's MySQL database.
  - Assigning a Surveyor:According to the business flow image, after a claim is submitted, a surveyor is assigned to inspect the claim.This assignment is managed by the Claim Service and the surveyor’s details are linked to the claim.
  - Surveyor Inspects and Approves/Rejects the Claim: The surveyor conducts an investigation and either approves or rejects the claim based on their findings. The claim status is updated in the Claim Service and stored in its database.
  - Customer Notified: If approved, the customer is notified and the settlement process is triggered. If rejected, the customer is informed of the rejection and the claim is closed.
## API Reference

### User Service

#### Register Admin

```http
  POST /api/user/admin/register
```

| Parameter             | Type     | Description                              |
| :-------------------- | :------- | :--------------------------------------- |
| `userRegisterDto`      | `object` | **Required**. Admin details to register. |

#### Register User

```http
  POST /api/user/register
```

| Parameter             | Type     | Description                              |
| :-------------------- | :------- | :--------------------------------------- |
| `userRegisterDto`      | `object` | **Required**. User details to register.  |

#### User Login

```http
  POST /api/user/login
```

| Parameter             | Type     | Description                          |
| :-------------------- | :------- | :----------------------------------- |
| `userLoginRequest`     | `object` | **Required**. User credentials for login. |

#### Fetch User

```http
  GET /api/user/fetch
```

| Parameter  | Type      | Description                    |
| :--------- | :-------- | :----------------------------- |
| `userId`   | `integer` | **Required**. ID of user to fetch. |

#### Delete User

```http
  DELETE /api/user/delete
```

| Parameter  | Type      | Description                    |
| :--------- | :-------- | :----------------------------- |
| `userId`   | `integer` | **Required**. ID of user to delete. |

#### Update User

```http
  PUT /api/user/update
```

| Parameter         | Type     | Description                             |
| :---------------- | :------- | :-------------------------------------- |
| `userRegisterDto`  | `object` | **Required**. Updated details of user.  |

#### Fetch All Users

```http
  GET /api/user/fetch/all
```

| Parameter  | Type     | Description                            |
| :--------- | :------- | :------------------------------------- |
| `role`     | `string` | **Required**. Role to filter users.     |

---

### Policy Service

#### Add Policy

```http
  POST /api/policy/add
```

| Parameter      | Type     | Description                     |
| :------------- | :------- | :------------------------------ |
| `request`      | `object` | **Required**. Policy details to add. |

#### Fetch All Policies

```http
  GET /api/policy/fetch/all
```

#### Fetch Policy by ID

```http
  GET /api/policy/fetch
```

| Parameter  | Type      | Description                    |
| :--------- | :-------- | :----------------------------- |
| `policyId` | `integer` | **Required**. ID of policy to fetch. |

#### Delete Policy

```http
  DELETE /api/policy/delete
```

| Parameter  | Type      | Description                    |
| :--------- | :-------- | :----------------------------- |
| `policyId` | `integer` | **Required**. ID of policy to delete. |

#### Add Policy Application

```http
  POST /api/policy/application/add
```

| Parameter      | Type     | Description                              |
| :------------- | :------- | :--------------------------------------- |
| `request`      | `object` | **Required**. Policy application details. |

#### Fetch All Policy Applications

```http
  GET /api/policy/application/fetch/all
```

#### Fetch Policy Application by ID

```http
  GET /api/policy/application/fetch
```

| Parameter      | Type      | Description                                  |
| :------------- | :-------- | :------------------------------------------- |
| `applicationId`| `integer` | **Required**. ID of policy application to fetch. |

#### Fetch Policy Application by Customer

```http
  GET /api/policy/application/fetch/customer-wise
```

| Parameter      | Type      | Description                                  |
| :------------- | :-------- | :------------------------------------------- |
| `customerId`   | `integer` | **Required**. Customer ID to fetch applications. |

#### Update Application Status

```http
  PUT /api/policy/application/status/update
```

| Parameter      | Type     | Description                                   |
| :------------- | :------- | :-------------------------------------------- |
| `request`      | `object` | **Required**. Status update request.          |

#### Fetch Policy Coverage

```http
  GET /api/policy/coverage/type/fetch
```

#### Fetch Policy Plans

```http
  GET /api/policy/plans/fetch
```

---

### Claim Service

#### Add Claim

```http
  POST /api/claim/add
```

| Parameter      | Type     | Description                     |
| :------------- | :------- | :------------------------------ |
| `request`      | `object` | **Required**. Claim details to add. |

#### Fetch All Claims

```http
  GET /api/claim/fetch/all
```

#### Fetch Claims by Customer

```http
  GET /api/claim/fetch/customer-wise
```

| Parameter   | Type      | Description                           |
| :---------- | :-------- | :------------------------------------ |
| `customerId`| `integer` | **Required**. Customer ID to fetch claims. |

#### Fetch Claims by Surveyor

```http
  GET /api/claim/fetch/surveyor-wise
```

| Parameter   | Type      | Description                           |
| :---------- | :-------- | :------------------------------------ |
| `surveyorId`| `integer` | **Required**. Surveyor ID to fetch claims. |

#### Assign Surveyor for Claim

```http
  PUT /api/claim/assign/surveyor
```

| Parameter  | Type     | Description                           |
| :--------- | :------- | :------------------------------------ |
| `request`  | `object` | **Required**. Surveyor assignment details. |

#### Update Claim by Surveyor

```http
  PUT /api/claim/surveyor/update
```

| Parameter  | Type     | Description                           |
| :--------- | :------- | :------------------------------------ |
| `request`  | `object` | **Required**. Updated claim details from surveyor. |

#### Customer Claim Response

```http
  PUT /api/claim/customer/response
```

| Parameter  | Type     | Description                           |
| :--------- | :------- | :------------------------------------ |
| `request`  | `object` | **Required**. Customer's response to claim. |

## Run Locally

Clone the project

```bash
  git clone https://github.com/rohanbeast7575/SureShield_IMS.git
```

Go to the project directory

```bash
  cd SureShield_IMS/backend
```

Docker command to build and run the images

```bash
 docker-compose build -t SureShield_IMS .

```

```bash
 docker-compose run  sureshieldims_IMS

```
Navigate to the frontend 
```bash
  cd .. 
  cd frontend/cms-frontend
```
```bash
  docker build -t sureshieldims_ui
```

run the docker

```bash
  docker run sureshieldims_ui
```


## 🚀 About Me
I am a passionate Java Backend Developer with hands-on experience in designing, developing, and deploying Spring Boot microservices for large-scale applications. I have worked extensively with Spring Boot, Spring Security, and JPA, building secure, efficient RESTful APIs. My skill set includes working with Docker and Docker Compose to containerize and orchestrate microservices, enabling seamless deployment and scaling of applications.

Throughout my career, I've been involved in creating distributed systems, leveraging tools like Eureka for service discovery and API Gateway for centralized routing. I am also adept at handling inter-service communication using RestTemplate, Feign Client, and message brokers like RabbitMQ and Kafka for asynchronous communication.

In addition, I have experience with Redis for caching and session management in distributed systems, as well as integrating cloud storage solutions like AWS S3 into Spring Boot applications. I am always focused on optimizing performance, enhancing scalability, and ensuring the security of applications.

Currently, I am deepening my expertise in Docker to streamline the deployment process for microservices architectures and to manage infrastructure more efficiently.

I am always eager to learn new technologies and work on challenging projects that require innovative solutions. If you're looking for someone who is passionate about backend technologies and containerization, let’s connect!


## 🔗 Links
[![portfolio](https://img.shields.io/badge/my_portfolio-000?style=for-the-badge&logo=ko-fi&logoColor=white)](https://katherineoelsner.com/)
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/deepakadik/)

