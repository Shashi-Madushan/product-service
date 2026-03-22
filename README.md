# Product Catalog Service

A comprehensive Spring Boot microservice for managing product catalogs in a retail POS system. This service handles product information, inventory management, pricing, and provides RESTful APIs for integration with other system components.

## 📋 Project Description

The Product Catalog Service is a core component of a microservices-based retail Point of Sale (POS) system. It provides robust functionality for managing product information, inventory tracking, pricing strategies, and product categorization. The service is built using Spring Boot with Spring Cloud for microservice architecture, featuring MongoDB for data persistence, and comprehensive validation mechanisms.

### Key Features

- **Product Management**: Complete CRUD operations for product catalogs
- **Inventory Tracking**: Real-time stock quantity management and monitoring
- **Pricing Management**: Flexible pricing with cost price and selling price support
- **Product Search**: Advanced search capabilities by name, category, supplier, and barcode
- **Stock Management**: Stock updates, reductions, and low stock alerts
- **Product Variants**: Support for product variants and multiple SKUs
- **Category Management**: Product categorization and filtering
- **Supplier Management**: Supplier information and product sourcing
- **Barcode Support**: Barcode-based product identification
- **Advanced Filtering**: Filter by price range, stock levels, and product status

## 🛠 Technology Stack

### Core Framework
- **Spring Boot**: 4.0.3 - Main application framework
- **Spring Cloud**: 2025.1.0 - Microservice architecture support
- **Spring Data MongoDB**: NoSQL database operations
- **Spring Validation**: Input validation framework

### Database & Persistence
- **MongoDB**: Primary database for product catalog data
- **Spring Data MongoDB**: MongoDB operations and repository support

### Development & Build Tools
- **Java**: 25 - Programming language
- **Maven**: Dependency management and build tool
- **Lombok**: Code generation for boilerplate reduction
- **MapStruct**: 1.6.3 - Bean mapping framework

### Additional Libraries
- **Spring Boot Actuator**: Application monitoring and management
- **Spring Boot DevTools**: Development-time tools
- **Netflix Eureka Client**: Service discovery
- **Spring Cloud Config**: Centralized configuration management
- **Spring Boot Starter Validation**: Bean validation support

## 🚀 Setup / Getting Started Instructions

### Prerequisites

- **Java 25** or higher
- **Maven 3.8+**
- **MongoDB 5.0+**
- **Spring Cloud Config Server** (running on port 9000)
- **Netflix Eureka Service Registry** (optional, for service discovery)

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd Project-Services/product-service
   ```

2. **Database Setup**
   ```bash
   # Start MongoDB
   mongod --dbpath /path/to/your/db
   
   # Create database and collections (automatically created by the application)
   use product_catalog
   ```

3. **Configuration**
   - Ensure Spring Cloud Config Server is running on `http://localhost:9000`
   - Configuration files are located in `src/main/resources/`
   - Default profile: `dev`

4. **Build the Application**
   ```bash
   ./mvnw clean install
   ```

5. **Run the Application**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

6. **Verify Service**
   - The service will start on a dynamic port (registered with Eureka)
   - Health check endpoint: `http://localhost:{port}/actuator/health`

### Docker Setup (Optional)

```bash
# Build Docker image
docker build -t product-service .

# Run with Docker
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e SPRING_CLOUD_CONFIG_URI=http://config-server:9000 \
  product-service
```

## 📚 API Endpoints

### Product Management Endpoints

| Method | Endpoint | Description | Request Type |
|--------|----------|-------------|--------------|
| POST | `/api/v1/products` | Create new product | `application/json` |
| GET | `/api/v1/products` | Get all products | - |
| GET | `/api/v1/products/{productId}` | Get product by ID | - |
| PUT | `/api/v1/products/{productId}` | Update product | `application/json` |
| DELETE | `/api/v1/products/{productId}` | Delete product | - |
| GET | `/api/v1/products/active` | Get active products | - |
| GET | `/api/v1/products/category/{category}` | Get products by category | - |
| GET | `/api/v1/products/supplier/{supplier}` | Get products by supplier | - |
| GET | `/api/v1/products/search` | Search products by name | - |
| GET | `/api/v1/products/barcode/{barcode}` | Get product by barcode | - |
| GET | `/api/v1/products/sku/{sku}` | Get product by SKU | - |
| GET | `/api/v1/products/low-stock` | Get low stock products | - |
| GET | `/api/v1/products/top-selling` | Get top selling products | - |
| GET | `/api/v1/products/price-range` | Get products by price range | - |
| PUT | `/api/v1/products/{productId}/stock` | Update product stock | - |
| PUT | `/api/v1/products/{productId}/stock/reduce` | Reduce product stock | - |

### Product Categories
- `Electronics` - Electronic devices and accessories
- `Clothing` - Apparel and fashion items
- `Food` - Food and beverages
- `Home` - Home and garden items
- `Books` - Books and media
- `Sports` - Sports equipment and accessories

### Product Status
- `ACTIVE` - Active products available for sale
- `INACTIVE` - Inactive products not available for sale

## 🧪 Test Scripts

### Comprehensive Test Suite

The project includes a comprehensive test script (`test-product-service-gateway.sh`) that tests all API endpoints through the API Gateway.

#### Running Tests

```bash
# Make the script executable
chmod +x test-product-service-gateway.sh

# Run all tests
./test-product-service-gateway.sh
```

#### Test Coverage

The test script covers:

1. **Product CRUD Operations**
   - Product creation and management
   - Product updates and deletions
   - Product retrieval operations

2. **Search and Filtering**
   - Search by name, category, supplier
   - Barcode and SKU lookup
   - Price range filtering
   - Stock level queries

3. **Inventory Management**
   - Stock updates and reductions
   - Low stock alerts
   - Top selling products

4. **Edge Case Tests**
   - Invalid ID formats
   - Not found scenarios
   - Validation errors

#### Test Configuration

- **API Gateway URL**: `http://localhost:7001`
- **Test Data**: Automatically generated unique test data
- **Results**: Color-coded output with pass/fail summary

### Manual Testing Examples

#### Create Product
```bash
curl -X POST "http://localhost:7001/api/v1/products" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD123456",
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "category": "Electronics",
    "stockQuantity": 50,
    "barcode": "123456789",
    "supplier": "TechCorp",
    "sku": "LAPTOP-001",
    "isActive": true
  }'
```

#### Search Products
```bash
# Search by name
curl -X GET "http://localhost:7001/api/v1/products/search?name=Laptop"

# Get by category
curl -X GET "http://localhost:7001/api/v1/products/category/Electronics"

# Get by barcode
curl -X GET "http://localhost:7001/api/v1/products/barcode/123456789"
```

## 🏗 Project Structure

```
product-service/
├── src/main/java/lk/ijse/eca/productservice/
│   ├── controller/          # REST API Controllers
│   │   └── ProductController.java
│   ├── dto/                 # Data Transfer Objects
│   │   ├── ProductRequestDTO.java
│   │   └── ProductResponseDTO.java
│   ├── entity/              # MongoDB Entities
│   │   └── Product.java
│   ├── exception/           # Custom Exceptions
│   │   ├── ProductNotFoundException.java
│   │   └── DuplicateProductException.java
│   ├── repository/          # MongoDB Repositories
│   │   └── ProductRepository.java
│   ├── service/             # Service Layer
│   │   ├── ProductService.java
│   │   └── impl/
│   │       └── ProductServiceImpl.java
│   ├── mapper/              # MapStruct Mappers
│   │   └── ProductMapper.java
│   ├── config/              # Configuration Classes
│   │   └── MongoConfig.java
│   ├── validation/          # Custom Validators
│   ├── handler/             # Exception Handlers
│   │   └── GlobalExceptionHandler.java
│   └── ProductServiceApplication.java
├── src/main/resources/
│   ├── application.yaml
│   ├── application-dev.yaml
│   └── uploads/             # File storage directory
├── test-product-service-gateway.sh  # Comprehensive test script
├── pom.xml                  # Maven configuration
└── README.md               # This file
```

## 🔧 Configuration

### Application Properties

Key configuration options:

```yaml
spring:
  application:
    name: product-service
  profiles:
    active: dev
  config:
    import: "configserver:"
  cloud:
    config:
      uri: http://localhost:9000
  data:
    mongodb:
      uri: mongodb://localhost:27017/product_catalog

app:
  storage:
    path: ./uploads
```

### Environment Variables

- `SPRING_PROFILES_ACTIVE`: Active profile (dev/prod)
- `SPRING_CLOUD_CONFIG_URI`: Config server URI
- `MONGODB_URI`: MongoDB connection URI (overrides default)
- `MONGODB_DATABASE`: MongoDB database name (overrides default)

## 📊 Monitoring & Health

### Actuator Endpoints

- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

### Logging

- **Log Level**: Configurable per profile
- **Log Format**: Structured logging with request tracking
- **File Logging**: Configured for production environments

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

## 📝 Student Information

- **Student Name**: Shashi Madushan
- **Student Number**: 2301691002
- **Project**: Product Catalog Service for Retail POS System
- **Course**: Enterprise Computing Architecture

## 📄 License

This project is part of an academic assignment for the Enterprise Computing Architecture course.

## 📞 Support

For any issues or questions regarding this service:

1. Check the test script output for common issues
2. Review the application logs
3. Verify all prerequisite services are running
4. Check the configuration files

---

**Note**: This service is designed to be part of a larger microservices architecture and should be used in conjunction with other services such as API Gateway, Config Server, and Service Registry for full functionality.
