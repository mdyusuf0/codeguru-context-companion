# Requirements Document: CodeGuru Context Companion

## Introduction

The CodeGuru Context Companion is an AI-powered, interactive learning assistant designed to help developers navigate, understand, and contribute to complex or legacy codebases. The system leverages Amazon Bedrock (Claude 3 models) for intelligent code analysis, Amazon Q Developer for automated testing, and provides an interactive, pedagogical approach to code comprehension.

**Target Audience:** This tool is specifically designed for Tier-2 and Tier-3 city students in India (e.g., Bhopal, Indore, Jaipur, Lucknow) who are learning software development and need to understand complex codebases. The system addresses the English-language gap by providing mandatory multilingual support for all codebase explanations.

**Delivery Method:** The system is delivered as a VS Code Extension or GitHub App that integrates directly into the developer's existing workspace, providing seamless access to code analysis and learning features without requiring a separate web application.

## Glossary

- **System**: The CodeGuru Context Companion application (delivered as VS Code Extension or GitHub App)
- **Repository**: A Git-based code repository linked by the user
- **Architectural_Map**: A plain-English visualization of repository structure and component relationships
- **Code_Explanation**: A step-by-step pedagogical breakdown of selected code
- **Mentor_Mode**: An interactive, guided tutorial format for presenting explanations
- **Quiz_Module**: A 3-question interactive assessment generated after code explanations
- **Amazon_Bedrock**: AWS service providing access to Claude 3 foundation models
- **Amazon_Bedrock_Knowledge_Bases**: AWS service for document chunking, embedding, and retrieval using RAG pattern
- **Amazon_OpenSearch_Serverless**: Vector database for storing codebase embeddings and representations
- **Amazon_Q**: AWS Developer tool for automated code analysis and test generation
- **Session_State**: User authentication and progress data maintained during active sessions
- **Token_Limit**: Maximum input size for AI model processing
- **Learning_Progress**: User's completion status for explanations and quizzes
- **RAG_Pattern**: Retrieval-Augmented Generation architecture for enhanced code understanding
- **Bharat_Focus**: Design principle emphasizing accessibility for Indian Tier-2/Tier-3 city students

## Ubiquitous Rules

The following rules apply to ALL requirements and MUST be enforced throughout the system:

1. **Mandatory Multilingual Support (Bharat Focus)**: ALL code explanations, architectural maps, quiz questions, and learning content MUST be available in Hindi, Tamil, and other regional Indian languages. The system MUST NOT provide English-only explanations. This is non-negotiable and addresses the English-language gap for Tier-2 and Tier-3 city students in India.

2. **Workspace Integration**: The system MUST be delivered as a VS Code Extension or GitHub App. It MUST NOT be a standalone web application. All features MUST integrate directly into the developer's existing workspace.

3. **RAG Architecture**: The system MUST use Retrieval-Augmented Generation (RAG) pattern with Amazon Bedrock Knowledge Bases for document chunking and embedding, and Amazon OpenSearch Serverless as the vector database for storing codebase representations.

## Requirements

### Requirement 1: Authentication and Session Management

**User Story:** As a developer, I want secure authentication and session management, so that my learning progress and repository access are protected.

#### Acceptance Criteria

1. THE System SHALL manage secure session state for all active user sessions
2. THE System SHALL authenticate users before granting access to repository analysis features
3. THE System SHALL maintain user context across multiple interactions within a session

### Requirement 2: AWS Service Integration

**User Story:** As a system administrator, I want reliable AWS service integration with RAG architecture, so that the system can leverage Amazon Bedrock, Knowledge Bases, OpenSearch, and Amazon Q capabilities.

#### Acceptance Criteria

1. THE System SHALL use AWS SDK for Java to manage all interactions with Amazon Bedrock
2. THE System SHALL use AWS SDK for Java to manage all interactions with Amazon Q Developer
3. THE System SHALL use Amazon Bedrock Knowledge Bases for document chunking and embedding
4. THE System SHALL use Amazon OpenSearch Serverless as the vector database for storing codebase embeddings
5. THE System SHALL implement Retrieval-Augmented Generation (RAG) pattern for code analysis
6. THE System SHALL handle AWS service credentials securely

### Requirement 3: Repository Analysis and Architectural Mapping (RAG-based)

**User Story:** As a developer (The Learner), I want to see a high-level architectural overview of a codebase in my preferred language, so that I can understand its structure before diving into details.

#### Acceptance Criteria

1. WHEN a user links a valid code repository, THE System SHALL clone the repository to a temporary workspace
2. WHEN a repository is successfully cloned, THE System SHALL chunk and embed the codebase using Amazon Bedrock Knowledge Bases
3. WHEN embeddings are created, THE System SHALL store them in Amazon OpenSearch Serverless vector database
4. WHEN repository analysis completes, THE System SHALL use RAG pattern to retrieve relevant context and generate a plain-English architectural map using Amazon Bedrock
5. THE Architectural_Map SHALL identify major components, modules, and their relationships
6. THE Architectural_Map SHALL present information in a hierarchical, easy-to-understand format
7. THE Architectural_Map SHALL be available in Hindi, Tamil, and other regional languages (mandatory)

### Requirement 4: Interactive Code Explanation (RAG-based)

**User Story:** As a developer (The Learner), I want detailed explanations of specific code sections in my native language, so that I can understand complex or unfamiliar implementations.

#### Acceptance Criteria

1. WHEN a user highlights code and clicks "Explain" in VS Code or GitHub, THE System SHALL extract the selected code with surrounding context
2. WHEN code is selected for explanation, THE System SHALL use RAG pattern to retrieve relevant context from OpenSearch vector database
3. WHEN context is retrieved, THE System SHALL send the code and retrieved context to Amazon Bedrock for analysis
4. WHEN Amazon Bedrock returns analysis, THE System SHALL generate a step-by-step pedagogical breakdown
5. THE Code_Explanation SHALL include purpose, logic flow, dependencies, and potential edge cases
6. THE Code_Explanation SHALL use clear, beginner-friendly language
7. THE Code_Explanation SHALL be available in Hindi, Tamil, and other regional languages (mandatory)

### Requirement 5: Quiz Generation and Assessment

**User Story:** As a developer (The Learner), I want to test my understanding after learning about code, so that I can verify my comprehension.

#### Acceptance Criteria

1. WHEN a user completes a code explanation module, THE System SHALL automatically generate a 3-question interactive quiz
2. THE Quiz_Module SHALL use Amazon Bedrock to create questions based on the explained code
3. THE Quiz_Module SHALL include multiple-choice or short-answer questions
4. WHEN a user submits quiz answers, THE System SHALL evaluate responses and provide immediate feedback
5. THE System SHALL store quiz results as part of Learning_Progress

### Requirement 6: Real-Time Progress Indication

**User Story:** As a developer, I want to see progress indicators during long operations, so that I know the system is working and not frozen.

#### Acceptance Criteria

1. WHILE analyzing a large repository, THE System SHALL display a real-time progress indicator
2. WHILE waiting for Amazon Bedrock responses, THE System SHALL show processing status
3. THE System SHALL update progress indicators at least every 2 seconds during active operations
4. THE System SHALL display estimated time remaining when available

### Requirement 7: Mentor Mode Presentation

**User Story:** As a developer (The Learner), I want explanations presented as interactive tutorials, so that I can learn at my own pace with guided instruction.

#### Acceptance Criteria

1. WHILE in Mentor_Mode, THE System SHALL format responses as interactive, guided tutorials
2. WHILE in Mentor_Mode, THE System SHALL break complex explanations into digestible steps
3. WHILE in Mentor_Mode, THE System SHALL allow users to navigate forward and backward through explanation steps
4. WHILE in Mentor_Mode, THE System SHALL highlight relevant code sections for each explanation step

### Requirement 8: Mandatory Multilingual Support (Bharat Focus)

**User Story:** As a Tier-2 or Tier-3 city student in India, I need technical explanations in my regional language, so that I can learn effectively without the English-language barrier.

#### Acceptance Criteria

1. THE System SHALL provide ALL technical explanations in Hindi, Tamil, and other regional Indian languages (mandatory, not optional)
2. THE System SHALL use Amazon Bedrock for translation
3. THE System SHALL support at minimum: Hindi, Tamil, Telugu, Kannada, Malayalam, Bengali, Marathi, Gujarati
4. THE System SHALL preserve technical terminology accuracy during translation
5. THE System SHALL allow users to switch between languages
6. THE System SHALL default to the user's preferred language (detected from VS Code or GitHub settings)
7. THE System SHALL NOT provide English-only explanations without regional language alternatives

### Requirement 9: Automated Unit Test Generation

**User Story:** As a developer, I want automated unit test generation for undocumented functions, so that I can understand expected behavior and improve code coverage.

#### Acceptance Criteria

1. WHERE automated testing is enabled, THE System SHALL identify functions without existing unit tests
2. WHERE automated testing is enabled, WHEN a user selects an undocumented function, THE System SHALL invoke Amazon Q Developer
3. WHERE automated testing is enabled, THE Amazon_Q SHALL generate runnable unit tests for the selected function
4. WHERE automated testing is enabled, THE System SHALL present generated tests in the appropriate testing framework format
5. WHERE automated testing is enabled, THE System SHALL allow users to save generated tests to the repository

### Requirement 10: Large Repository Handling

**User Story:** As a developer working with large codebases, I want the system to handle repositories that exceed processing limits, so that I can still analyze specific parts of interest.

#### Acceptance Criteria

1. IF a repository exceeds the Token_Limit for analysis, THEN THE System SHALL detect the size constraint
2. IF a repository exceeds the Token_Limit, THEN THE System SHALL prompt the user to select specific modules or directories
3. IF a repository exceeds the Token_Limit, THEN THE System SHALL display repository structure to aid module selection
4. IF a repository exceeds the Token_Limit, THEN THE System SHALL analyze only the user-selected portions

### Requirement 11: Connection Resilience and Progress Persistence

**User Story:** As a developer, I want my learning progress saved locally, so that I don't lose work if the connection to AWS services is interrupted.

#### Acceptance Criteria

1. IF an AWS connection times out during operation, THEN THE System SHALL save current Learning_Progress locally
2. IF an AWS connection times out, THEN THE System SHALL display a retry prompt to the user
3. IF an AWS connection times out, THEN THE System SHALL allow users to resume from the last saved state
4. THE System SHALL persist Learning_Progress to local storage after each completed explanation or quiz
5. WHEN a user returns to the System, THE System SHALL restore previous Learning_Progress from local storage

### Requirement 12: API and Backend Architecture (VS Code Extension / GitHub App)

**User Story:** As a system architect, I want a robust backend architecture integrated with VS Code or GitHub, so that the system can handle multiple users and integrate with AWS services reliably.

#### Acceptance Criteria

1. THE System SHALL implement the backend using Java and Spring Boot
2. THE System SHALL be delivered as a VS Code Extension OR GitHub App (not a standalone web application)
3. THE System SHALL provide RESTful API endpoints for repository management
4. THE System SHALL provide RESTful API endpoints for code explanation requests
5. THE System SHALL provide RESTful API endpoints for quiz generation and evaluation
6. THE System SHALL route requests to appropriate AWS services based on operation type
7. THE System SHALL integrate with VS Code Extension API or GitHub App API
8. THE System SHALL handle concurrent user sessions without data corruption

### Requirement 13: Git Repository Management

**User Story:** As a developer, I want to link various Git repositories, so that I can analyze different codebases without manual setup.

#### Acceptance Criteria

1. WHEN a user provides a Git repository URL, THE System SHALL validate the URL format
2. WHEN a valid repository URL is provided, THE System SHALL clone the repository using Git commands
3. THE System SHALL support public and private repositories with appropriate authentication
4. THE System SHALL clean up temporary repository clones after analysis completion
5. THE System SHALL handle repository clone failures gracefully with descriptive error messages
