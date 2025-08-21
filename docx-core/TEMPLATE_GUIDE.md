# API Documentation Template Guide

This guide explains how to use the modern HTML template for API documentation generation.

## Template Features

The enhanced HTML template includes:

### 🎨 Modern UI/UX
- Responsive design with sidebar navigation
- Interactive API testing panel
- Real-time search functionality
- Keyboard shortcuts (⌘K for search)
- Modern color scheme with customizable primary colors

### 🔧 API Testing
- Built-in API request testing
- Support for path variables, query parameters, and request bodies
- Authentication token support
- Mock response simulation
- Request/response history

### 📝 Code Generation
- Dynamic code snippet generation in multiple languages:
  - cURL
  - JavaScript (fetch)
  - Python (requests)
  - Java (OkHttp)
  - PHP (cURL)
- AI-powered code generation with Gemini API integration
- Fallback to static templates if AI is unavailable

### 🔍 Advanced Features
- Full-text search across endpoints
- Auto-complete and filtering
- Endpoint categorization by controllers
- Response status code documentation
- Parameter validation display

## Configuration

### Basic Setup

```java
// In your Spring Boot application
@Configuration
public class DocxConfig {
    
    @Bean
    public TemplateConfig templateConfig() {
        TemplateConfig config = new TemplateConfig();
        config.setTitle("My API Documentation");
        config.setPrimaryColor("#4F46E5"); // Indigo
        config.setLogoText("API");
        config.setEnableApiTester(true);
        config.setEnableCodeGeneration(true);
        return config;
    }
}
```

### Gemini AI Integration

To enable AI-powered code generation, set your Gemini API key:

```java
TemplateConfig config = new TemplateConfig();
config.setGeminiApiKey("your-gemini-api-key");
config.setEnableCodeGeneration(true);
```

### Custom Styling

You can customize the template appearance by modifying the CSS variables:

```java
// Custom primary color
config.setPrimaryColor("#FFD700"); // Gold
config.setLogoText("DOCS");
```

## Usage Examples

### 1. Controller Documentation

```java
/**
 * Product catalog management operations.
 * 
 * This controller manages the complete product lifecycle including inventory
 * management, pricing updates, category organization, and availability tracking.
 * 
 * @author Demo Team  
 * @since 1.0.0
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    /**
     * Retrieves a paginated catalog of all products.
     * 
     * Supports advanced filtering by category, price range, availability status,
     * and full-text search across product names and descriptions.
     * 
     * @param category Optional category filter
     * @param minPrice Minimum price threshold  
     * @param maxPrice Maximum price threshold
     * @param available Filter by availability status
     * @param search Full-text search query
     * @return Paginated product catalog
     * 
     * @apiExample GET /api/products?category=electronics&minPrice=100
     * @apiResponse 200 Product catalog retrieved successfully
     * @apiResponse 400 Invalid filter parameters provided
     * @apiResponse 500 Internal server error
     */
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @Min(0) BigDecimal minPrice,
            @RequestParam(required = false) @Min(0) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String search) {
        // Implementation
    }
}
```

### 2. Request Body Documentation

```java
/**
 * Creates a new product.
 * 
 * @param product Complete product information
 * @return Newly created product with generated ID
 * 
 * @apiExample POST /api/products
 * @apiResponse 201 Product successfully created
 * @apiResponse 400 Invalid product data provided
 */
@PostMapping
public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
    // Implementation
}
```

The template will automatically generate JSON examples like:

```json
{
  "name": "New Product",
  "price": 99.99,
  "category": "Electronics",
  "available": true,
  "description": "Product description",
  "sku": "PROD-001",
  "stockQuantity": 100
}
```

## Template Structure

The generated documentation includes:

### Homepage
- API overview with title, version, and description
- Controller cards showing endpoint counts
- Quick navigation to each controller

### Endpoint Pages
- Detailed endpoint information
- Parameter tables with types and validation
- Request/response examples
- Status code documentation
- Interactive testing form

### Utility Panel
- Authentication section for API tokens
- Parameter input forms
- Request body editor
- Code snippet generator
- Response viewer

## Best Practices

### 1. Comprehensive Documentation
```java
/**
 * Brief endpoint description.
 * 
 * Detailed explanation of what the endpoint does,
 * including business logic and use cases.
 * 
 * @param id The unique identifier
 * @return Response description
 * 
 * @apiExample GET /api/resource/{id}
 * @apiResponse 200 Success response description
 * @apiResponse 404 Resource not found
 * @apiResponse 500 Internal server error
 */
```

### 2. Parameter Documentation
- Always include type information
- Provide example values
- Document validation constraints
- Explain business rules

### 3. Response Documentation
- Document all possible status codes
- Provide example responses
- Explain error conditions
- Include headers when relevant

## Troubleshooting

### Common Issues

1. **Missing Examples**: Ensure your model classes have proper JavaDoc
2. **Styling Issues**: Check CSS variable definitions
3. **API Testing Fails**: Verify CORS configuration
4. **Search Not Working**: Ensure JavaScript is enabled

### Debug Mode

Enable debug logging to troubleshoot issues:

```properties
logging.level.com.docx=DEBUG
```

This will show detailed information about template generation and API scanning.

## Advanced Customization

For advanced customization, you can extend the `HtmlTemplateEngine` class:

```java
@Component
public class CustomTemplateEngine extends HtmlTemplateEngine {
    
    @Override
    protected String getStyles() {
        // Return custom CSS
        return super.getStyles() + "\n" + getCustomStyles();
    }
    
    private String getCustomStyles() {
        return """
            :root {
                --custom-color: #your-color;
            }
            .custom-class {
                /* your styles */
            }
            """;
    }
}
```

This template provides a complete, modern API documentation solution with interactive testing capabilities and beautiful styling.