# Design Document: CodeGuru Context Companion

## Overview

The CodeGuru Context Companion is a Java-based Spring Boot application delivered as a VS Code Extension or GitHub App that provides an AI-powered learning assistant for developers navigating complex codebases. The system integrates with Amazon Bedrock for intelligent code analysis, Amazon Bedrock Knowledge Bases and Amazon OpenSearch Serverless for RAG-based retrieval, and Amazon Q Developer for automated test generation, presenting information through an interactive, pedagogical interface.

**Target Audience:** Tier-2 and Tier-3 city students in India (e.g., Bhopal, Indore, Jaipur, Lucknow) who need to understand complex codebases with mandatory multilingual support to bridge the English-language gap.

**Delivery Method:** VS Code Extension or GitHub App that integrates directly into the developer's workspace.

**Architecture Pattern:** The system uses Retrieval-Augmented Generation (RAG) with Amazon Bedrock Knowledge Bases for document chunking and embedding, and Amazon OpenSearch Serverless as the vector database for storing and retrieving codebase representations.

The architecture follows a layered approach with clear separation between the integration layer (VS Code/GitHub), web layer (REST API), service layer (business logic), RAG layer (Knowledge Bases + OpenSearch), integration layer (AWS services), and data layer (session and progress management).

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                   Client Integration Layer                   │
│              (VS Code Extension / GitHub App)                │
└────────────────────────────┬────────────────────────────────┘
                             │ HTTPS/REST
┌────────────────────────────▼────────────────────────────────┐
│                     Spring Boot Backend                      │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              REST Controllers Layer                   │  │
│  │  - RepositoryController                              │  │
│  │  - ExplanationController                             │  │
│  │  - QuizController                                    │  │
│  │  - ProgressController                                │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                      │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │              Service Layer                            │  │
│  │  - RepositoryService                                 │  │
│  │  - ArchitecturalAnalysisService                      │  │
│  │  - CodeExplanationService (RAG-based)                │  │
│  │  - QuizGenerationService                             │  │
│  │  - TranslationService (mandatory for Bharat focus)   │  │
│  │  - TestGenerationService (optional)                  │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                      │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │           RAG Integration Layer                       │  │
│  │  - KnowledgeBaseClient (chunking & embedding)        │  │
│  │  - OpenSearchClient (vector retrieval)               │  │
│  │  - RAGOrchestrator (retrieval + generation)          │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                      │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │           AWS Integration Layer                       │  │
│  │  - BedrockClient                                     │  │
│  │  - AmazonQClient                                     │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                      │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │              Data Layer                               │  │
│  │  - SessionRepository                                 │  │
│  │  - ProgressRepository                                │  │
│  │  - RepositoryCache                                   │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────────┬───────────────────────────────┘
                             │
        ┌────────────────────┼────────────────────┬──────────────────┐
        │                    │                    │                  │
┌───────▼────────┐  ┌────────▼────────┐  ┌───────▼────────┐  ┌─────▼──────┐
│ Amazon Bedrock │  │ Bedrock KB +    │  │   Amazon Q     │  │ Git Repos  │
│  (Claude 3)    │  │ OpenSearch      │  │   Developer    │  │ (GitHub)   │
│                │  │ Serverless      │  │                │  │            │
└────────────────┘  └─────────────────┘  └────────────────┘  └────────────┘
```

### Component Interaction Flow

1. **Repository Linking Flow (RAG-based)**: VS Code/GitHub → RepositoryController → RepositoryService → Git Clone → KnowledgeBaseClient (chunk & embed) → OpenSearchClient (store vectors) → ArchitecturalAnalysisService → RAGOrchestrator (retrieve + generate) → BedrockClient → TranslationService (mandatory) → Response
2. **Code Explanation Flow (RAG-based)**: VS Code/GitHub → ExplanationController → CodeExplanationService → RAGOrchestrator (retrieve context from OpenSearch) → BedrockClient → TranslationService (mandatory) → Response
3. **Quiz Generation Flow**: ExplanationService completion → QuizGenerationService → BedrockClient → TranslationService (mandatory) → Response
4. **Test Generation Flow**: VS Code/GitHub → TestGenerationService → AmazonQClient → Response

## Components and Interfaces

### 1. REST Controllers Layer

#### RepositoryController
```java
@RestController
@RequestMapping("/api/v1/repositories")
public class RepositoryController {
    
    @PostMapping("/link")
    public ResponseEntity<RepositoryLinkResponse> linkRepository(
        @RequestBody RepositoryLinkRequest request,
        @AuthenticationPrincipal UserDetails user
    );
    
    @GetMapping("/{repoId}/architecture")
    public ResponseEntity<ArchitecturalMap> getArchitecturalMap(
        @PathVariable String repoId,
        @AuthenticationPrincipal UserDetails user
    );
    
    @GetMapping("/{repoId}/structure")
    public ResponseEntity<RepositoryStructure> getRepositoryStructure(
        @PathVariable String repoId
    );
}
```

#### ExplanationController
```java
@RestController
@RequestMapping("/api/v1/explanations")
public class ExplanationController {
    
    @PostMapping("/explain")
    public ResponseEntity<CodeExplanation> explainCode(
        @RequestBody ExplainCodeRequest request,
        @AuthenticationPrincipal UserDetails user
    );
    
    @GetMapping("/{explanationId}")
    public ResponseEntity<CodeExplanation> getExplanation(
        @PathVariable String explanationId
    );
    
    @PostMapping("/{explanationId}/translate")
    public ResponseEntity<TranslatedExplanation> translateExplanation(
        @PathVariable String explanationId,
        @RequestParam String targetLanguage
    );
}
```

#### QuizController
```java
@RestController
@RequestMapping("/api/v1/quizzes")
public class QuizController {
    
    @GetMapping("/explanation/{explanationId}")
    public ResponseEntity<Quiz> getQuizForExplanation(
        @PathVariable String explanationId
    );
    
    @PostMapping("/{quizId}/submit")
    public ResponseEntity<QuizResult> submitQuizAnswers(
        @PathVariable String quizId,
        @RequestBody QuizSubmission submission,
        @AuthenticationPrincipal UserDetails user
    );
}
```

#### ProgressController
```java
@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {
    
    @GetMapping("/user")
    public ResponseEntity<LearningProgress> getUserProgress(
        @AuthenticationPrincipal UserDetails user
    );
    
    @PostMapping("/save")
    public ResponseEntity<Void> saveProgress(
        @RequestBody LearningProgress progress,
        @AuthenticationPrincipal UserDetails user
    );
}
```

### 2. Service Layer

#### RepositoryService
```java
@Service
public class RepositoryService {
    
    public RepositoryMetadata cloneRepository(String repoUrl, String userId);
    
    public void validateRepositoryUrl(String repoUrl);
    
    public RepositoryStructure analyzeStructure(String repoId);
    
    public void cleanupRepository(String repoId);
    
    public boolean exceedsTokenLimit(String repoId);
    
    public RepositoryStructure getModuleStructure(String repoId, List<String> selectedModules);
}
```

#### ArchitecturalAnalysisService
```java
@Service
public class ArchitecturalAnalysisService {
    
    private final RAGOrchestrator ragOrchestrator;
    private final TranslationService translationService;
    
    public ArchitecturalMap generateArchitecturalMap(
        String repoId,
        RepositoryStructure structure,
        String preferredLanguage
    );
    
    public ProgressUpdate analyzeWithProgress(
        String repoId,
        ProgressCallback callback
    );
}
```

#### CodeExplanationService
```java
@Service
public class CodeExplanationService {
    
    private final RAGOrchestrator ragOrchestrator;
    private final TranslationService translationService;
    
    public CodeExplanation explainCode(
        String repoId,
        String code,
        String context,
        String userId,
        String preferredLanguage
    );
    
    public CodeExplanation formatAsMentorMode(CodeExplanation explanation);
    
    public List<ExplanationStep> breakIntoSteps(CodeExplanation explanation);
}
```

#### QuizGenerationService
```java
@Service
public class QuizGenerationService {
    
    public Quiz generateQuiz(String explanationId, CodeExplanation explanation);
    
    public QuizResult evaluateQuiz(String quizId, QuizSubmission submission);
}
```

#### TranslationService (Mandatory for Bharat Focus)
```java
@Service
public class TranslationService {
    
    public TranslatedExplanation translate(
        CodeExplanation explanation,
        String targetLanguage
    );
    
    public List<String> getSupportedLanguages();
    
    public String detectUserPreferredLanguage(String userId);
    
    public Map<String, String> translateAllContent(
        Map<String, String> content,
        String targetLanguage
    );
}
```

#### TestGenerationService (Optional)
```java
@Service
@ConditionalOnProperty(name = "features.test-generation.enabled", havingValue = "true")
public class TestGenerationService {
    
    public GeneratedTests generateUnitTests(
        String functionCode,
        String context,
        String testFramework
    );
    
    public List<String> identifyUndocumentedFunctions(String repoId);
}
```

### 3. RAG Integration Layer

#### KnowledgeBaseClient
```java
@Component
public class KnowledgeBaseClient {
    
    private final BedrockAgentRuntimeClient knowledgeBaseClient;
    
    public String createKnowledgeBase(String repoId, String dataSourceS3Path);
    
    public void ingestDocuments(String knowledgeBaseId, List<CodeDocument> documents);
    
    public void chunkAndEmbedRepository(String repoId, RepositoryStructure structure);
    
    public String getKnowledgeBaseId(String repoId);
    
    public boolean isReady(String knowledgeBaseId);
}
```

#### OpenSearchClient
```java
@Component
public class OpenSearchClient {
    
    private final OpenSearchServerlessClient openSearchClient;
    
    public void storeEmbeddings(String repoId, List<CodeEmbedding> embeddings);
    
    public List<RetrievedContext> retrieveRelevantContext(
        String repoId,
        String query,
        int topK
    );
    
    public void deleteRepositoryIndex(String repoId);
    
    public boolean indexExists(String repoId);
}
```

#### RAGOrchestrator
```java
@Component
public class RAGOrchestrator {
    
    private final KnowledgeBaseClient knowledgeBaseClient;
    private final OpenSearchClient openSearchClient;
    private final BedrockClient bedrockClient;
    
    public String retrieveAndGenerate(
        String repoId,
        String query,
        String promptTemplate
    );
    
    public ArchitecturalMap generateArchitecturalMapWithRAG(
        String repoId,
        RepositoryStructure structure
    );
    
    public CodeExplanation explainCodeWithRAG(
        String repoId,
        String code,
        String context
    );
}
```

### 4. AWS Integration Layer

#### BedrockClient
```java
@Component
public class BedrockClient {
    
    private final BedrockRuntimeClient bedrockRuntime;
    
    public String invokeClaudeModel(
        String prompt,
        Map<String, Object> parameters
    );
    
    public String generateArchitecturalSummary(RepositoryStructure structure);
    
    public String generateCodeExplanation(String code, String context);
    
    public String generateQuizQuestions(String explanation);
    
    public String translateText(String text, String targetLanguage);
    
    public boolean isAvailable();
}
```

#### AmazonQClient
```java
@Component
public class AmazonQClient {
    
    private final QDeveloperClient qClient;
    
    public String generateUnitTests(
        String functionCode,
        String language,
        String testFramework
    );
    
    public boolean isAvailable();
}
```

### 5. Data Layer

#### SessionRepository
```java
@Repository
public interface SessionRepository extends JpaRepository<UserSession, String> {
    
    Optional<UserSession> findByUserId(String userId);
    
    void deleteExpiredSessions(LocalDateTime expirationTime);
}
```

#### ProgressRepository
```java
@Repository
public interface ProgressRepository {
    
    LearningProgress findByUserId(String userId);
    
    void save(LearningProgress progress);
    
    void saveLocally(LearningProgress progress);
    
    LearningProgress restoreFromLocal(String userId);
}
```

#### RepositoryCache
```java
@Component
public class RepositoryCache {
    
    public void cacheRepository(String repoId, Path repoPath);
    
    public Optional<Path> getRepository(String repoId);
    
    public void evict(String repoId);
    
    public void cleanupOldRepositories(Duration maxAge);
}
```

## Data Models

### Core Domain Models

```java
public class RepositoryMetadata {
    private String repoId;
    private String repoUrl;
    private String userId;
    private LocalDateTime clonedAt;
    private Path localPath;
    private long sizeInBytes;
    private boolean exceedsTokenLimit;
}

public class RepositoryStructure {
    private String repoId;
    private List<Module> modules;
    private List<Dependency> dependencies;
    private Map<String, Integer> fileCountByType;
    private int totalFiles;
}

public class Module {
    private String name;
    private String path;
    private List<String> files;
    private List<Module> subModules;
}

public class ArchitecturalMap {
    private String repoId;
    private String summary;
    private List<Component> components;
    private List<Relationship> relationships;
    private String visualRepresentation;
}

public class Component {
    private String name;
    private String type;
    private String description;
    private List<String> responsibilities;
}

public class CodeExplanation {
    private String explanationId;
    private String code;
    private String purpose;
    private List<ExplanationStep> steps;
    private List<String> dependencies;
    private List<String> edgeCases;
    private LocalDateTime createdAt;
    private boolean mentorMode;
}

public class ExplanationStep {
    private int stepNumber;
    private String title;
    private String description;
    private String codeHighlight;
    private String visualAid;
}

public class Quiz {
    private String quizId;
    private String explanationId;
    private List<Question> questions;
    private LocalDateTime createdAt;
}

public class Question {
    private int questionNumber;
    private String questionText;
    private QuestionType type;
    private List<String> options;
    private String correctAnswer;
}

public enum QuestionType {
    MULTIPLE_CHOICE,
    SHORT_ANSWER,
    TRUE_FALSE
}

public class QuizSubmission {
    private String quizId;
    private Map<Integer, String> answers;
    private LocalDateTime submittedAt;
}

public class QuizResult {
    private String quizId;
    private int score;
    private int totalQuestions;
    private Map<Integer, QuestionFeedback> feedback;
    private boolean passed;
}

public class QuestionFeedback {
    private boolean correct;
    private String userAnswer;
    private String correctAnswer;
    private String explanation;
}

public class LearningProgress {
    private String userId;
    private List<CompletedExplanation> completedExplanations;
    private List<QuizAttempt> quizAttempts;
    private Map<String, Integer> repositoryProgress;
    private LocalDateTime lastUpdated;
}

public class UserSession {
    private String sessionId;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Map<String, Object> sessionData;
}

public class TranslatedExplanation {
    private String explanationId;
    private String targetLanguage;
    private String translatedContent;
    private LocalDateTime translatedAt;
}

public class CodeDocument {
    private String documentId;
    private String repoId;
    private String filePath;
    private String content;
    private Map<String, String> metadata;
}

public class CodeEmbedding {
    private String embeddingId;
    private String repoId;
    private String documentId;
    private List<Float> vector;
    private Map<String, String> metadata;
}

public class RetrievedContext {
    private String documentId;
    private String content;
    private double relevanceScore;
    private Map<String, String> metadata;
}

public class GeneratedTests {
    private String functionName;
    private String testFramework;
    private String testCode;
    private List<String> testCases;
    private LocalDateTime generatedAt;
}
```

### Request/Response Models

```java
public class RepositoryLinkRequest {
    private String repoUrl;
    private String authToken;
    private List<String> selectedModules;
}

public class RepositoryLinkResponse {
    private String repoId;
    private String status;
    private RepositoryMetadata metadata;
    private boolean requiresModuleSelection;
}

public class ExplainCodeRequest {
    private String repoId;
    private String code;
    private String filePath;
    private int startLine;
    private int endLine;
    private boolean mentorMode;
}

public class ProgressUpdate {
    private String operation;
    private int percentComplete;
    private String currentStep;
    private Long estimatedTimeRemaining;
}
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*


### Property Reflection

After analyzing all acceptance criteria, I've identified several areas where properties can be consolidated:

**Consolidations:**
- Properties 3.1, 3.2, 3.3 (repository cloning, analysis, and map generation) can be combined into a single end-to-end property
- Properties 4.1, 4.2, 4.3, 4.4 (code explanation flow) can be combined into a comprehensive explanation property
- Properties 5.1, 5.2, 5.3 (quiz generation) can be combined into a single quiz generation property
- Properties 6.1, 6.2, 6.3, 6.4 (progress indication) can be combined into a single progress reporting property
- Properties 7.1, 7.2, 7.3, 7.4 (mentor mode features) can be combined into a single mentor mode property
- Properties 9.1, 9.2, 9.3, 9.4 (test generation flow) can be combined into a single test generation property
- Properties 10.1, 10.2, 10.3, 10.4 (large repository handling) can be combined into a single oversized repo property
- Properties 11.1, 11.2, 11.3 (timeout handling) can be combined into a single resilience property
- Properties 12.2, 12.3, 12.4 (API endpoint existence) are examples, not properties, and can be tested together
- Properties 13.1, 13.2 (URL validation and cloning) can be combined with 3.1

**Unique Properties Retained:**
- Session management (1.1, 1.3) - distinct from authentication
- Authentication (1.2) - access control
- Credential security (2.3) - security property
- Quiz evaluation and storage (5.4, 5.5) - distinct from generation
- Translation (8.1, 8.5) - optional feature
- Progress persistence (11.4, 11.5) - distinct from timeout handling
- Concurrent session handling (12.6) - concurrency property
- Repository cleanup (13.4) - resource management
- Error handling (13.5) - error response quality

### Correctness Properties

Property 1: Session State Consistency
*For any* user session and any sequence of operations, retrieving session state should return data consistent with all operations performed in that session.
**Validates: Requirements 1.1, 1.3**

Property 2: Authentication Enforcement
*For any* repository analysis operation, if the request lacks valid authentication credentials, the system should reject the request with an authentication error.
**Validates: Requirements 1.2**

Property 3: Credential Security
*For any* system operation that fails or produces output (logs, responses, errors), the output should never contain AWS credentials or authentication tokens.
**Validates: Requirements 2.3**

Property 4: Repository Analysis Round Trip
*For any* valid Git repository URL, linking the repository should result in: (1) a successful clone, (2) structural analysis data, and (3) an architectural map containing components, modules, and relationships in hierarchical format.
**Validates: Requirements 3.1, 3.2, 3.3, 3.4, 3.5, 13.1, 13.2**

Property 5: Code Explanation Completeness
*For any* code selection request, the generated explanation should contain: (1) the selected code, (2) surrounding context, (3) multiple explanation steps, (4) purpose description, (5) logic flow, (6) dependencies list, and (7) edge cases.
**Validates: Requirements 4.1, 4.2, 4.3, 4.4**

Property 6: Automatic Quiz Generation
*For any* completed code explanation, the system should automatically generate a quiz containing exactly 3 questions, where each question is either multiple-choice, short-answer, or true-false type.
**Validates: Requirements 5.1, 5.2, 5.3**

Property 7: Quiz Evaluation and Persistence
*For any* quiz submission, the system should: (1) evaluate all answers, (2) provide feedback for each question, (3) calculate a score, and (4) store the results in the user's learning progress.
**Validates: Requirements 5.4, 5.5**

Property 8: Progress Reporting Timeliness
*For any* long-running operation (repository analysis or Bedrock requests), the system should emit progress updates at intervals of 2 seconds or less, including percent complete and current step information.
**Validates: Requirements 6.1, 6.2, 6.3, 6.4**

Property 9: Mentor Mode Structure
*For any* code explanation requested in mentor mode, the response should: (1) contain multiple digestible steps, (2) include navigation metadata for forward/backward movement, and (3) provide code highlighting information for each step.
**Validates: Requirements 7.1, 7.2, 7.3, 7.4**

Property 10: Translation Availability
*For any* code explanation, when multilingual support is enabled, requesting translation to a supported language should produce translated content while maintaining access to the original content.
**Validates: Requirements 8.1, 8.5**

Property 11: Test Generation Completeness
*For any* function selected for test generation (when automated testing is enabled), the system should: (1) invoke Amazon Q, (2) generate syntactically valid test code, (3) format tests for the requested framework, and (4) provide save functionality.
**Validates: Requirements 9.1, 9.2, 9.3, 9.4, 9.5**

Property 12: Large Repository Handling
*For any* repository that exceeds the token limit, the system should: (1) detect the size constraint, (2) provide repository structure information, (3) prompt for module selection, and (4) analyze only the selected modules.
**Validates: Requirements 10.1, 10.2, 10.3, 10.4**

Property 13: Timeout Resilience
*For any* operation that experiences an AWS connection timeout, the system should: (1) save current learning progress locally, (2) return a response with retry information, and (3) allow resumption from the saved state on retry.
**Validates: Requirements 11.1, 11.2, 11.3**

Property 14: Progress Persistence
*For any* completed explanation or quiz, the system should immediately persist the learning progress to local storage, and when a user starts a new session, the system should restore the previous progress.
**Validates: Requirements 11.4, 11.5**

Property 15: Request Routing
*For any* API request, the system should route it to the appropriate AWS service based on operation type: Bedrock for explanations/translations/quizzes, Amazon Q for test generation.
**Validates: Requirements 12.5**

Property 16: Concurrent Session Safety
*For any* set of concurrent user sessions performing operations simultaneously, no session's data should be corrupted or mixed with another session's data.
**Validates: Requirements 12.6**

Property 17: Repository Cleanup
*For any* repository analysis that completes (successfully or with error), the system should remove temporary repository clones from the filesystem.
**Validates: Requirements 13.4**

Property 18: Authentication Support
*For any* private Git repository with valid authentication credentials, the system should successfully clone the repository; for any private repository without credentials, the system should fail with an authentication error.
**Validates: Requirements 13.3**

Property 19: Error Message Quality
*For any* repository clone failure, the system should return an error response containing a descriptive message explaining the failure reason.
**Validates: Requirements 13.5**

## Error Handling

### Error Categories and Handling Strategies

#### 1. Repository Access Errors
- **Invalid URL Format**: Return 400 Bad Request with validation error details
- **Repository Not Found**: Return 404 Not Found with descriptive message
- **Authentication Failed**: Return 401 Unauthorized with authentication guidance
- **Clone Timeout**: Return 408 Request Timeout with retry information
- **Network Errors**: Return 503 Service Unavailable with retry-after header

#### 2. AWS Service Errors
- **Bedrock Timeout**: Save progress locally, return 504 Gateway Timeout with retry prompt
- **Bedrock Rate Limit**: Return 429 Too Many Requests with exponential backoff guidance
- **Amazon Q Unavailable**: Return 503 Service Unavailable, disable test generation feature temporarily
- **Invalid Credentials**: Log security event, return 500 Internal Server Error (never expose credential details)
- **Token Limit Exceeded**: Return 413 Payload Too Large with module selection prompt

#### 3. Session and Authentication Errors
- **Session Expired**: Return 401 Unauthorized with re-authentication prompt
- **Invalid Session**: Return 401 Unauthorized, clear client-side session data
- **Concurrent Session Conflict**: Use optimistic locking, return 409 Conflict with retry guidance

#### 4. Data Persistence Errors
- **Local Storage Full**: Return 507 Insufficient Storage, prompt user to clear space
- **Database Connection Lost**: Retry with exponential backoff (3 attempts), then return 503
- **Corrupted Progress Data**: Log error, return clean slate, notify user of data loss

#### 5. Input Validation Errors
- **Invalid Code Selection**: Return 400 Bad Request with selection requirements
- **Unsupported Language**: Return 400 Bad Request with list of supported languages
- **Invalid Quiz Submission**: Return 400 Bad Request with validation details

### Error Response Format

All error responses follow a consistent structure:

```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable error description",
    "details": {
      "field": "Additional context",
      "suggestion": "Recommended action"
    },
    "timestamp": "2024-01-15T10:30:00Z",
    "requestId": "unique-request-id"
  }
}
```

### Retry and Resilience Patterns

1. **Exponential Backoff**: For transient AWS service errors, retry with delays: 1s, 2s, 4s
2. **Circuit Breaker**: After 5 consecutive AWS failures, open circuit for 60 seconds
3. **Graceful Degradation**: If optional features (translation, test generation) fail, continue with core functionality
4. **Local Fallback**: Always save progress locally before attempting remote operations
5. **Timeout Configuration**: 
   - Repository clone: 5 minutes
   - Bedrock API calls: 30 seconds
   - Amazon Q API calls: 45 seconds
   - Database operations: 10 seconds

## Testing Strategy

### Dual Testing Approach

The system requires both unit testing and property-based testing for comprehensive coverage:

**Unit Tests** focus on:
- Specific examples of valid and invalid inputs
- Edge cases (empty repositories, single-file repos, deeply nested structures)
- Error conditions (network failures, invalid credentials, malformed responses)
- Integration points between components
- Mock AWS service responses

**Property-Based Tests** focus on:
- Universal properties that hold for all inputs
- Comprehensive input coverage through randomization
- Invariants that must be maintained across operations
- Round-trip properties (serialize/deserialize, save/restore)

### Property-Based Testing Configuration

**Framework**: Use **jqwik** for Java property-based testing

**Configuration**:
- Minimum 100 iterations per property test (due to randomization)
- Each test must reference its design document property
- Tag format: `@Tag("Feature: codeguru-context-companion, Property {number}: {property_text}")`

**Example Property Test Structure**:

```java
@Property
@Tag("Feature: codeguru-context-companion, Property 1: Session State Consistency")
void sessionStateConsistency(@ForAll("validSessions") UserSession session,
                             @ForAll("operationSequences") List<Operation> operations) {
    // Arrange: Set up session
    sessionRepository.save(session);
    
    // Act: Perform operations
    operations.forEach(op -> op.execute(session.getUserId()));
    
    // Assert: Verify consistency
    UserSession retrieved = sessionRepository.findByUserId(session.getUserId()).get();
    assertThat(retrieved.getSessionData()).isConsistentWith(operations);
}
```

### Test Coverage Requirements

1. **Unit Test Coverage**: Minimum 80% line coverage for service and controller layers
2. **Property Test Coverage**: One property test per correctness property (19 total)
3. **Integration Tests**: End-to-end tests for each major user flow
4. **Performance Tests**: Load testing for concurrent sessions (target: 100 concurrent users)
5. **Security Tests**: Penetration testing for authentication and credential handling

### Testing Priorities

**High Priority** (Must test before release):
- Authentication and session management (Properties 1, 2)
- Credential security (Property 3)
- Repository analysis flow (Property 4)
- Error handling and resilience (Properties 13, 19)

**Medium Priority** (Should test before release):
- Code explanation and quiz generation (Properties 5, 6, 7)
- Progress reporting and persistence (Properties 8, 14)
- Concurrent session safety (Property 16)

**Low Priority** (Can defer to post-MVP):
- Optional features: translation (Property 10), test generation (Property 11)
- Mentor mode specifics (Property 9)
- Large repository handling (Property 12)

### Mock and Test Data Strategy

**AWS Service Mocking**:
- Use **LocalStack** for local AWS service emulation during development
- Use **WireMock** for stubbing Bedrock and Amazon Q responses in unit tests
- Create fixture files with sample Bedrock responses for consistent testing

**Test Repository Strategy**:
- Maintain a set of test repositories with known structures:
  - `tiny-repo`: 5 files, single module (fast tests)
  - `medium-repo`: 50 files, 3 modules (realistic tests)
  - `large-repo`: 500+ files, 10+ modules (token limit tests)
  - `malformed-repo`: Invalid structure (error handling tests)

**Test Data Generators** (for property-based tests):
- Random valid Git URLs
- Random code snippets with varying complexity
- Random session data with different states
- Random quiz questions and answers
- Random repository structures

### Continuous Integration

**CI Pipeline Steps**:
1. Compile and build
2. Run unit tests (fast feedback)
3. Run property-based tests (100 iterations each)
4. Run integration tests
5. Generate coverage reports
6. Security scanning (dependency check, credential scanning)
7. Performance baseline tests

**Quality Gates**:
- All tests must pass
- Code coverage ≥ 80%
- No high-severity security vulnerabilities
- No credentials in code or logs
- API response times < 2 seconds (95th percentile)
