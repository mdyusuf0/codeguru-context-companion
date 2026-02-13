# CodeGuru Context Companion

An AI-powered learning assistant for developers navigating complex codebases.

## Overview

The CodeGuru Context Companion leverages Amazon Bedrock (Claude 3) for intelligent code analysis and Amazon Q Developer for automated test generation, providing an interactive, pedagogical approach to code comprehension.

## Features

- Repository analysis and architectural mapping
- Interactive code explanations with mentor mode
- Automated quiz generation for learning verification
- Optional multilingual translation support
- Optional automated unit test generation

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- AWS Account with access to:
  - Amazon Bedrock (Claude 3 models)
  - Amazon Q Developer (optional, for test generation)

## Configuration

Configure AWS credentials using one of the following methods:

1. AWS CLI: `aws configure`
2. Environment variables: `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`
3. IAM role (when running on EC2/ECS)

### Application Properties

Key configuration options in `application.properties`:

```properties
# AWS Configuration
aws.region=us-east-1
aws.bedrock.model-id=anthropic.claude-3-sonnet-20240229-v1:0

# Feature Flags
features.translation.enabled=false
features.test-generation.enabled=false

# Repository Configuration
repository.clone.timeout=300000
repository.token.limit=100000
```

## Building and Running

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Run Tests

```bash
mvn test
```

## API Endpoints

### Health Check
- `GET /actuator/health` - Application health status

### Repository Management (Coming in Task 8)
- `POST /api/v1/repositories/link` - Link a Git repository
- `GET /api/v1/repositories/{repoId}/architecture` - Get architectural map
- `GET /api/v1/repositories/{repoId}/structure` - Get repository structure

### Code Explanation (Coming in Task 12)
- `POST /api/v1/explanations/explain` - Explain code snippet
- `GET /api/v1/explanations/{explanationId}` - Get explanation
- `POST /api/v1/explanations/{explanationId}/translate` - Translate explanation

### Quiz (Coming in Task 12)
- `GET /api/v1/quizzes/explanation/{explanationId}` - Get quiz for explanation
- `POST /api/v1/quizzes/{quizId}/submit` - Submit quiz answers

### Progress (Coming in Task 13)
- `GET /api/v1/progress/user` - Get user learning progress
- `POST /api/v1/progress/save` - Save progress

## Project Structure

```
src/
├── main/
│   ├── java/com/codeguru/contextcompanion/
│   │   ├── config/          # Configuration classes
│   │   ├── controllers/     # REST controllers
│   │   ├── services/        # Business logic
│   │   ├── integration/     # AWS service clients
│   │   ├── repositories/    # Data access
│   │   └── models/          # Domain models
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/codeguru/contextcompanion/
```

## Development

This project follows a layered architecture:
- **Controllers**: REST API endpoints
- **Services**: Business logic and orchestration
- **Integration**: AWS service clients (Bedrock, Q Developer)
- **Repositories**: Data persistence and caching

## Testing Strategy

The project uses a dual testing approach:
- **Unit Tests**: Specific examples and edge cases (JUnit 5)
- **Property-Based Tests**: Universal properties across all inputs (jqwik)

## License

Copyright © 2024 CodeGuru Context Companion
