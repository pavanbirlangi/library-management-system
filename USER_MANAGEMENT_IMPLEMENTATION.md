# User Management System Implementation

## Overview
This document describes the complete implementation of the administrative user management system for the Library Management System. The system provides ADMIN-only endpoints for managing users, including creating LIBRARIAN and MEMBER accounts.

## Components Implemented

### 1. Service Layer

#### UserService Interface (`/service/UserService.java`)
- **Purpose**: Service interface for administrative user operations
- **Key Methods**:
  - `findAll(Pageable)` - Get all users with pagination
  - `findAll()` - Get all users without pagination
  - `findById(Long)` - Find user by ID
  - `createUser(CreateUserRequest)` - Create new LIBRARIAN/MEMBER users
  - `updateUser(Long, UpdateUserRequest)` - Update existing users
  - `suspendUser(Long)` - Suspend user accounts
  - `activateUser(Long)` - Activate suspended accounts
  - `isUsernameAvailable(String)` - Check username availability
  - `findByRole(String, Pageable)` - Filter users by role
  - `findByStatus(String, Pageable)` - Filter users by status

#### UserServiceImpl Class (`/service/impl/UserServiceImpl.java`)
- **Purpose**: Implementation of user management business logic
- **Key Features**:
  - Password encoding using BCrypt
  - Automatic Member entity creation for MEMBER role users
  - Username uniqueness validation
  - Role change handling (LIBRARIAN ↔ MEMBER)
  - Status synchronization between User and Member entities
  - Business rules enforcement (cannot create/suspend ADMIN users)
- **Dependencies**: UserRepository, MemberRepository, PasswordEncoder

### 2. Repository Layer

#### UserRepository Interface (`/repository/UserRepository.java`)
- **Added Methods**:
  - `findByRole(Role, Pageable)` - Find users by role with pagination
  - `findByStatus(UserStatus, Pageable)` - Find users by status with pagination
  - `findByRoleAndStatus(Role, UserStatus, Pageable)` - Combined filtering

### 3. DTO Layer

#### UpdateUserRequest Class (`/dto/user/UpdateUserRequest.java`)
- **Purpose**: DTO for updating user information by administrators
- **Fields**:
  - `username` - Required, validated
  - `role` - Required (LIBRARIAN or MEMBER)
  - `status` - Required (ACTIVE or SUSPENDED)
  - `fullName`, `email`, `phone` - Optional member details
- **Validation**: Jakarta Validation annotations for data integrity

### 4. Controller Layer

#### UserController Class (`/controller/UserController.java`)
- **Purpose**: REST API endpoints for administrative user management
- **Security**: All endpoints require ADMIN role (`@PreAuthorize("hasRole('ADMIN')")`)
- **Key Endpoints**:

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users with pagination |
| GET | `/api/users/all` | Get all users without pagination |
| GET | `/api/users/{id}` | Get user by ID |
| POST | `/api/users` | Create new user |
| PUT | `/api/users/{id}` | Update existing user |
| PATCH | `/api/users/{id}/suspend` | Suspend user account |
| PATCH | `/api/users/{id}/activate` | Activate user account |
| GET | `/api/users/username/{username}/available` | Check username availability |
| GET | `/api/users/role/{role}` | Get users by role |
| GET | `/api/users/status/{status}` | Get users by status |

## Business Rules Implemented

### User Creation Rules
1. **ADMIN Role Restriction**: Cannot create ADMIN users through this system
2. **Username Uniqueness**: Usernames must be unique across the system
3. **Member Requirements**: When creating MEMBER users, `fullName` is required
4. **Auto-Member Creation**: MEMBER role users automatically get a Member entity created

### User Update Rules
1. **Role Restrictions**: Cannot change any user to ADMIN role
2. **Username Changes**: Validates uniqueness when changing usernames
3. **Role Transitions**:
   - MEMBER → LIBRARIAN: Deletes associated Member record
   - LIBRARIAN → MEMBER: Creates new Member record (requires fullName)
4. **Status Synchronization**: User and Member status kept in sync

### Security Rules
1. **ADMIN Only**: All endpoints require ADMIN role authentication
2. **Cannot Suspend ADMIN**: ADMIN users cannot be suspended
3. **JWT Required**: All endpoints require valid JWT token

## Integration with Existing System

### Database Schema Compliance
- Follows existing schema with `users` and `members` tables
- Maintains foreign key relationship: `members.user_id → users.id`
- Respects existing enum constraints for Role and UserStatus

### Password Security
- Uses existing BCrypt password encoder from AuthService
- Passwords are never returned in responses (UserResponse excludes password)

### Member System Integration
- Automatically manages Member entities when dealing with MEMBER role users
- Synchronizes status between User and Member records
- Handles role transitions properly

### Authentication Integration
- Leverages existing Spring Security configuration
- Uses JWT tokens from existing authentication system
- Respects role-based access control

## API Documentation
- Complete Swagger/OpenAPI documentation with security requirements
- Comprehensive parameter descriptions and response codes
- JWT authentication integration for API testing

## Error Handling
- Proper exception handling with appropriate HTTP status codes
- Validation error responses for malformed requests
- Clear error messages for business rule violations

## Testing Considerations
- All methods are transactional for data consistency
- Read-only methods marked with `@Transactional(readOnly = true)`
- Proper error propagation for unit testing
- Comprehensive validation for edge cases

## Usage Examples

### Creating a Librarian
```json
POST /api/users
{
    "username": "librarian1",
    "password": "securePassword123",
    "role": "LIBRARIAN"
}
```

### Creating a Member
```json
POST /api/users
{
    "username": "member1",
    "password": "memberPassword123",
    "role": "MEMBER",
    "fullName": "John Doe",
    "email": "john.doe@email.com",
    "phone": "+1234567890"
}
```

### Updating a User
```json
PUT /api/users/5
{
    "username": "updatedUsername",
    "role": "MEMBER",
    "status": "ACTIVE",
    "fullName": "Updated Name"
}
```

This implementation provides a complete, secure, and feature-rich administrative user management system that integrates seamlessly with the existing Library Management System.
