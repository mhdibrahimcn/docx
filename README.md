# Docx - Spring Boot JavaDoc API Documentation Generator

[![Maven Central](https://img.shields.io/maven-central/v/com.docx/docx-parent.svg)](https://search.maven.org/artifact/com.docx/docx-parent)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://img.shields.io/github/actions/workflow/status/docx/docx/ci.yml?branch=main)](https://github.com/docx/docx/actions)

**Docx** is a powerful Spring Boot dependency that automatically generates beautiful API documentation from JavaDoc comments and Spring annotations, featuring a Laravel Scramble-inspired UI with multi-page navigation and dark mode support.

## ✨ Features

### 🔍 **Zero-Annotation Documentation**
- Pure JavaDoc-based documentation generation
- No additional annotations required on your controllers
- Automatic discovery of all `@RestController` and `@Controller` classes

### 🎨 **Beautiful Laravel Scramble-Inspired UI**
- Multi-page navigation with collapsible sections
- Dark/Light mode toggle with system preference detection
- Responsive design that works on all devices
- Interactive search functionality across all endpoints
- Copy-to-clipboard for code examples

### 📋 **Comprehensive JavaDoc Support**
- **Standard Tags**: `@param`, `@return`, `@throws`, `@author`, `@since`, `@version`
- **Custom API Tags**: 
  - `@apiNote` - Additional notes about the endpoint
  - `@apiDescription` - Detailed endpoint description
  - `@apiResponse 200 Success message` - Manual response documentation
  - `@apiError 404 Error description` - Error response documentation
  - `@apiExample GET /api/users/123` - Usage examples

### 🚀 **Smart Annotation Processing**
- Automatic HTTP method extraction from Spring mappings
- Path variable and query parameter detection
- Request/Response body analysis
- Validation constraint documentation (`@NotNull`, `@Size`, `@Email`, etc.)

### 🔄 **AOP-Based Example Generation**
- Real-time capture of actual API calls during development
- Automatic generation of realistic examples
- Path variable and query parameter examples
- Smart sanitization of sensitive data

### ⚙️ **Flexible Configuration**
```yaml
docx:
  enabled: true
  base-path: /docx
  theme: auto # light, dark, auto
  title: "My API Documentation"
  branding:
    color: "#3B82F6"
    logo-url: "/assets/logo.png"
  scan:
    auto-discover: true
    include-validation: true
    include-test-controllers: false
  features:
    search: true
    export-openapi: true
    auto-generate-examples: true
```

## 🚀 Quick Start

### 1. Add Dependency

**Maven:**
```xml
<dependency>
    <groupId>com.docx</groupId>
    <artifactId>docx-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle:**
```gradle
implementation 'com.docx:docx-spring-boot-starter:1.0.0'
```

### 2. Enable Docx

```java
@SpringBootApplication
@EnableDocx
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. Document Your Controllers

```java
/**
 * User management operations
 * @apiNote This controller handles all user-related CRUD operations
 * @author John Doe
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    /**
     * Retrieves a user by their unique identifier
     * @param id The unique user identifier
     * @return User details with profile information
     * @throws UserNotFoundException when user is not found
     * @apiExample GET /api/users/123
     * @apiResponse 200 User successfully retrieved with complete profile data
     * @apiResponse 404 User not found in database or has been deactivated
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
        @PathVariable @NotNull @Min(1) Long id) {
        // implementation
    }
}
```

### 4. View Documentation

Start your application and visit: `http://localhost:8080/docx`

## 📖 Documentation Structure

Docx generates a comprehensive documentation site with the following structure:

```
/docx/
├── index.html                 # Main dashboard with API overview
├── controllers/               # Controller-specific pages
│   ├── user-controller.html
│   ├── product-controller.html
│   └── ...
├── endpoints/                 # Individual endpoint pages
│   ├── get-users.html
│   ├── create-user.html
│   └── ...
├── models/                    # DTO/Model documentation
│   ├── user-dto.html
│   ├── product-dto.html
│   └── ...
└── openapi.json              # OpenAPI 3.0 specification
```

## 🛠️ Build-Time Generation (Maven Plugin)

For build-time documentation generation, add the Maven plugin:

```xml
<plugin>
    <groupId>com.docx</groupId>
    <artifactId>docx-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <outputDirectory>${project.build.directory}/docx</outputDirectory>
        <title>My API Documentation</title>
        <autoScanControllers>true</autoScanControllers>
        <excludePackages>
            <package>com.example.internal</package>
        </excludePackages>
    </configuration>
</plugin>
```

Run with: `mvn docx:generate`

## 🎯 Advanced Features

### Custom Response Documentation

Use manual `@apiResponse` and `@apiError` tags for detailed response documentation:

```java
/**
 * Creates a new user account
 * @apiResponse 201 User successfully created with generated ID and timestamps
 * @apiResponse 400 Invalid user data or validation constraints violated
 * @apiResponse 409 Username or email already exists in the system
 * @apiError 500 Internal server error occurred during user creation
 */
@PostMapping
public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
    // implementation
}
```

### Validation Constraint Documentation

Docx automatically documents validation constraints:

```java
public class UserDto {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50)
    private String username;
    
    @Email(message = "Must be a valid email address")
    private String email;
    
    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;
}
```

### AOP Example Generation

Enable automatic example generation to capture real API calls:

```yaml
docx:
  features:
    auto-generate-examples: true
```

Examples are automatically generated from actual requests during development.

## 📋 Configuration Reference

| Property | Default | Description |
|----------|---------|-------------|
| `docx.enabled` | `true` | Enable/disable Docx |
| `docx.base-path` | `/docx` | Documentation base URL |
| `docx.theme` | `auto` | UI theme (light, dark, auto) |
| `docx.title` | `API Documentation` | Documentation title |
| `docx.branding.color` | `#3B82F6` | Primary brand color |
| `docx.scan.auto-discover` | `true` | Auto-scan all controllers |
| `docx.scan.include-validation` | `true` | Include validation constraints |
| `docx.features.search` | `true` | Enable search functionality |
| `docx.features.export-openapi` | `true` | Export OpenAPI specification |

## 🏗️ Project Structure

```
docx/
├── docx-core/                 # Core JavaDoc parsing and generation
├── docx-spring-boot-starter/  # Spring Boot auto-configuration
├── docx-maven-plugin/         # Maven plugin for build-time generation
└── docx-demo/                # Demo application
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

```bash
git clone https://github.com/docx/docx.git
cd docx
mvn clean install
```

### Running the Demo

```bash
cd docx-demo
mvn spring-boot:run
```

Visit `http://localhost:8080/docx` to see the documentation.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Inspired by [Laravel Scramble](https://scramble.dedoc.co/) for the beautiful UI design
- Built with Spring Boot and Maven ecosystem
- Uses Handlebars for templating

## 📞 Support

- 📧 Email: support@docx.dev
- 💬 Discord: [Join our community](https://discord.gg/docx)
- 🐛 Issues: [GitHub Issues](https://github.com/docx/docx/issues)
- 📖 Documentation: [Full Documentation](https://docs.docx.dev)

---

**Made with ❤️ by the Docx team**