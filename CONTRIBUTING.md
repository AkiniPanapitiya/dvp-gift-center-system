# Contributing to DVP Gift Center

Thank you for your interest in contributing to the DVP Gift Center project! We welcome contributions from developers of all skill levels.

## Table of Contents
1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Setup](#development-setup)
4. [Contributing Guidelines](#contributing-guidelines)
5. [Pull Request Process](#pull-request-process)
6. [Coding Standards](#coding-standards)
7. [Testing Guidelines](#testing-guidelines)
8. [Documentation](#documentation)
9. [Issue Reporting](#issue-reporting)
10. [Community](#community)

## Code of Conduct

By participating in this project, you agree to abide by our Code of Conduct:

- **Be respectful**: Treat everyone with respect and professionalism
- **Be inclusive**: Welcome newcomers and help them learn
- **Be constructive**: Provide helpful feedback and suggestions
- **Be patient**: Understand that everyone has different skill levels
- **Be collaborative**: Work together towards common goals

## Getting Started

### Prerequisites
Before contributing, ensure you have:
- Java 17+ installed
- Node.js 16+ and npm
- MySQL 8.0+
- Git
- A code editor (VS Code recommended)
- Basic knowledge of Spring Boot and React

### Fork and Clone
1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/dvp-gift-center.git
   cd dvp-gift-center
   ```
3. Add the upstream repository:
   ```bash
   git remote add upstream https://github.com/ORIGINAL_OWNER/dvp-gift-center.git
   ```

## Development Setup

### Backend Setup
```bash
cd backend

# Configure database
mysql -u root -p
CREATE DATABASE dvp_gift_center_dev;
exit

# Import schema
mysql -u root -p dvp_gift_center_dev < ../database/dvp_gift_center_schema.sql

# Configure application-dev.properties
cp src/main/resources/application.properties src/main/resources/application-dev.properties
# Edit application-dev.properties with your database credentials

# Run backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Create environment file
echo "REACT_APP_API_URL=http://localhost:8080" > .env.development.local

# Start development server
npm start
```

### Verify Setup
1. Backend should be running at http://localhost:8080
2. Frontend should be running at http://localhost:3000
3. Test login with: username `admin`, password `admin123`

## Contributing Guidelines

### Types of Contributions
We welcome various types of contributions:

- **Bug fixes**: Fix existing issues
- **Feature enhancements**: Improve existing features
- **New features**: Add new functionality
- **Documentation**: Improve or add documentation
- **Testing**: Add or improve tests
- **Code refactoring**: Improve code quality

### Before You Start
1. **Check existing issues**: Look for existing issues or create a new one
2. **Discuss major changes**: For significant changes, discuss in an issue first
3. **Keep changes focused**: One feature/fix per pull request
4. **Follow coding standards**: Maintain consistency with existing code

## Pull Request Process

### 1. Create a Feature Branch
```bash
# Create and checkout a new branch
git checkout -b feature/your-feature-name
# or
git checkout -b fix/bug-description
```

### 2. Make Your Changes
- Write clear, concise code
- Follow existing code style and conventions
- Add appropriate comments and documentation
- Write or update tests as needed

### 3. Test Your Changes
```bash
# Backend tests
cd backend
mvn test

# Frontend tests
cd frontend
npm test

# Manual testing
# Test all affected functionality
```

### 4. Commit Your Changes
```bash
# Stage your changes
git add .

# Commit with descriptive message
git commit -m "feat: add user profile management feature"

# Or for bug fixes
git commit -m "fix: resolve cart item quantity update issue"
```

### Commit Message Format
We follow conventional commit format:
```
type(scope): description

[optional body]

[optional footer]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**
```
feat(auth): add password reset functionality
fix(cart): resolve quantity update issue
docs(api): update endpoint documentation
test(user): add user registration tests
```

### 5. Push and Create Pull Request
```bash
# Push your branch
git push origin feature/your-feature-name

# Create pull request on GitHub
# Provide clear title and description
# Link related issues
```

### Pull Request Template
```markdown
## Description
Brief description of the changes made.

## Type of Change
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## Testing
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally with my changes
- [ ] I have performed manual testing

## Screenshots (if applicable)
Add screenshots to show the changes.

## Checklist
- [ ] My code follows the style guidelines of this project
- [ ] I have performed a self-review of my own code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
```

## Coding Standards

### Java/Spring Boot Standards

#### Code Style
```java
// Use meaningful variable and method names
public class UserService {
    private final UserRepository userRepository;
    
    public UserDto createUser(CreateUserRequest request) {
        // Validate input
        if (StringUtils.isEmpty(request.getUsername())) {
            throw new ValidationException("Username is required");
        }
        
        // Business logic
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .build();
            
        return userMapper.toDto(userRepository.save(user));
    }
}
```

#### Best Practices
- Use `@Service`, `@Repository`, `@Controller` annotations appropriately
- Implement proper exception handling with custom exceptions
- Use DTOs for API requests/responses
- Follow RESTful API conventions
- Use proper HTTP status codes
- Implement validation using Bean Validation
- Write comprehensive JavaDoc for public APIs

#### Security
```java
// Always validate and sanitize input
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/admin/users")
public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
    // Implementation
}

// Use proper SQL parameterization
@Query("SELECT u FROM User u WHERE u.username = :username")
Optional<User> findByUsername(@Param("username") String username);
```

### JavaScript/React Standards

#### Code Style
```javascript
// Use functional components with hooks
import React, { useState, useEffect } from 'react';

const UserProfile = ({ userId }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchUser();
  }, [userId]);

  const fetchUser = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/users/${userId}`);
      setUser(response.data);
    } catch (error) {
      setError('Failed to load user');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <Spinner />;
  if (error) return <Alert variant="danger">{error}</Alert>;

  return (
    <Card>
      <Card.Body>
        <h5>{user.fullName}</h5>
        <p>{user.email}</p>
      </Card.Body>
    </Card>
  );
};

export default UserProfile;
```

#### Best Practices
- Use functional components and hooks
- Implement proper error handling
- Use meaningful component and variable names
- Follow React best practices for state management
- Use PropTypes or TypeScript for type checking
- Implement proper loading states
- Use consistent import ordering
- Follow accessibility guidelines (ARIA labels, etc.)

### Database Standards

#### Naming Conventions
```sql
-- Table names: lowercase with underscores
CREATE TABLE user_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_profiles_user_id 
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Indexes for performance
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
```

#### Migration Guidelines
- Always use versioned migrations
- Include rollback scripts
- Test migrations on sample data
- Never modify existing migrations in production

## Testing Guidelines

### Backend Testing

#### Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void createUser_WithValidData_ReturnsUserDto() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .username("testuser")
            .email("test@example.com")
            .build();
            
        User savedUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .build();
            
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        UserDto result = userService.createUser(request);
        
        // Then
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }
}
```

#### Integration Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void createUser_WithValidData_Returns201() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        
        // When
        ResponseEntity<UserDto> response = restTemplate.postForEntity(
            "/api/users", request, UserDto.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getUsername()).isEqualTo("newuser");
    }
}
```

### Frontend Testing

#### Component Tests
```javascript
import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { AuthProvider } from '../contexts/AuthContext';
import LoginForm from '../components/LoginForm';

describe('LoginForm', () => {
  test('submits form with valid credentials', async () => {
    const mockLogin = jest.fn();
    
    render(
      <AuthProvider value={{ login: mockLogin }}>
        <LoginForm />
      </AuthProvider>
    );

    fireEvent.change(screen.getByLabelText(/username/i), {
      target: { value: 'testuser' }
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { value: 'password123' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith({
        username: 'testuser',
        password: 'password123'
      });
    });
  });
});
```

#### Test Coverage
- Aim for at least 80% code coverage
- Test both happy path and error scenarios
- Mock external dependencies
- Test user interactions
- Test API integration points

## Documentation

### Code Documentation

#### Java Documentation
```java
/**
 * Service class for managing user operations.
 * 
 * This service handles user creation, updates, and authentication
 * operations while ensuring proper validation and security.
 */
@Service
public class UserService {
    
    /**
     * Creates a new user with the provided information.
     * 
     * @param request The user creation request containing username, email, etc.
     * @return UserDto The created user information
     * @throws ValidationException if the request data is invalid
     * @throws DuplicateUserException if username or email already exists
     */
    public UserDto createUser(CreateUserRequest request) {
        // Implementation
    }
}
```

#### JavaScript Documentation
```javascript
/**
 * Custom hook for managing user authentication state.
 * 
 * @returns {Object} Auth context with user data and auth methods
 * @returns {Object} returns.user - Current authenticated user
 * @returns {Function} returns.login - Login function
 * @returns {Function} returns.logout - Logout function
 * @returns {boolean} returns.isAuthenticated - Authentication status
 */
export const useAuth = () => {
  // Implementation
};
```

### API Documentation
- Update API documentation for new endpoints
- Include request/response examples
- Document error responses
- Update Postman collections

### README Updates
- Update setup instructions for new dependencies
- Add new feature documentation
- Update troubleshooting section
- Keep version information current

## Issue Reporting

### Bug Reports
When reporting bugs, please include:

1. **Clear title**: Descriptive summary of the issue
2. **Environment**: OS, browser, Java/Node versions
3. **Steps to reproduce**: Detailed steps to recreate the bug
4. **Expected behavior**: What should happen
5. **Actual behavior**: What actually happens
6. **Screenshots**: If applicable
7. **Error logs**: Relevant log entries
8. **Additional context**: Any other relevant information

### Feature Requests
When requesting features, please include:

1. **Clear description**: What feature you'd like to see
2. **Use case**: Why this feature would be valuable
3. **Acceptance criteria**: How you'd know it's complete
4. **Implementation ideas**: If you have suggestions
5. **Alternatives**: Other solutions you've considered

### Issue Templates

#### Bug Report Template
```markdown
## Bug Description
A clear and concise description of what the bug is.

## Environment
- OS: [e.g., Windows 10, Ubuntu 20.04]
- Browser: [e.g., Chrome 95, Firefox 94]
- Java Version: [e.g., OpenJDK 17]
- Node Version: [e.g., 16.14.0]

## Steps to Reproduce
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

## Expected Behavior
A clear description of what you expected to happen.

## Actual Behavior
A clear description of what actually happened.

## Screenshots
If applicable, add screenshots to help explain your problem.

## Error Logs
```
Paste any relevant error logs here
```

## Additional Context
Add any other context about the problem here.
```

#### Feature Request Template
```markdown
## Feature Description
A clear and concise description of the feature you'd like to see.

## Problem Statement
Describe the problem this feature would solve.

## Proposed Solution
Describe your proposed solution in detail.

## Use Cases
Describe specific use cases for this feature.

## Acceptance Criteria
- [ ] Criterion 1
- [ ] Criterion 2
- [ ] Criterion 3

## Additional Context
Add any other context, mockups, or examples about the feature request here.
```

## Community

### Communication Channels
- **GitHub Issues**: For bug reports and feature requests
- **GitHub Discussions**: For general questions and discussions
- **Email**: [maintainer@example.com] for security issues

### Getting Help
- Check existing issues and documentation first
- Search closed issues for similar problems
- Provide detailed information when asking for help
- Be patient and respectful when waiting for responses

### Mentoring
New contributors are welcome! We provide mentoring for:
- First-time contributors
- Developers new to Spring Boot or React
- Anyone wanting to learn more about the codebase

### Recognition
We recognize contributors through:
- GitHub contributor recognition
- CONTRIBUTORS.md file
- Release notes mentions
- Community shoutouts

## Release Process

### Version Numbering
We follow [Semantic Versioning](https://semver.org/):
- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

### Release Checklist
- [ ] All tests pass
- [ ] Documentation updated
- [ ] Version numbers updated
- [ ] Release notes prepared
- [ ] Security review completed

## License

By contributing to DVP Gift Center, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to DVP Gift Center! Your efforts help make this project better for everyone. 🎁