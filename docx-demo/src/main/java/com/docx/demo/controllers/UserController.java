package com.docx.demo.controllers;

import com.docx.demo.models.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * User management operations controller.
 * 
 * This controller handles all user-related CRUD operations including
 * user creation, retrieval, updates, and deletion. It also provides
 * advanced search and filtering capabilities.
 * 
 * @apiNote This controller follows REST conventions and includes comprehensive validation
 * @author Demo Team
 * @since 1.0.0
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final Map<Long, User> userRepository = new HashMap<>();
    private Long nextId = 1L;

    /**
     * Retrieves a paginated list of all users.
     * 
     * This endpoint supports pagination and optional filtering by username
     * or email. Results are sorted by creation date in descending order.
     * 
     * @param pageable Pagination information including page number, size, and sort
     * @param username Optional username filter for searching users
     * @param email Optional email filter for searching users
     * @param active Optional filter to show only active or inactive users
     * @return Paginated list of users matching the search criteria
     * 
     * @apiExample GET /api/users?page=0&size=10&username=john
     * @apiExample GET /api/users?active=true&sort=createdAt,desc
     * @apiResponse 200 Successfully retrieved user list with pagination metadata
     * @apiResponse 400 Invalid pagination parameters provided
     * @apiResponse 500 Internal server error occurred while fetching users
     */
    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            Pageable pageable,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean active) {
        
        List<User> filteredUsers = userRepository.values().stream()
                .filter(user -> username == null || user.getUsername().toLowerCase().contains(username.toLowerCase()))
                .filter(user -> email == null || user.getEmail().toLowerCase().contains(email.toLowerCase()))
                .filter(user -> active == null || user.isActive() == active)
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredUsers.size());
        List<User> pageContent = filteredUsers.subList(start, end);

        Page<User> userPage = new PageImpl<>(pageContent, pageable, filteredUsers.size());
        return ResponseEntity.ok(userPage);
    }

    /**
     * Retrieves a specific user by their unique identifier.
     * 
     * @param id The unique user identifier (must be positive)
     * @return Complete user details including profile information and metadata
     * @throws UserNotFoundException when user with specified ID is not found
     * 
     * @apiExample GET /api/users/123
     * @apiResponse 200 User successfully retrieved with complete profile data
     * @apiResponse 400 Invalid user ID format provided in request
     * @apiResponse 404 User not found in database or has been deleted
     * @apiResponse 500 Internal server error occurred while processing request
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable @NotNull @Min(value = 1, message = "User ID must be positive") Long id) {
        
        User user = userRepository.get(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Creates a new user in the system.
     * 
     * This endpoint validates all user data including email uniqueness,
     * username availability, and required field validation before creating
     * the user account.
     * 
     * @param user User data for account creation (all required fields must be provided)
     * @return Newly created user with generated ID and timestamp metadata
     * 
     * @apiExample POST /api/users
     * @apiResponse 201 User successfully created with generated ID and timestamps
     * @apiResponse 400 Invalid user data or validation constraints violated
     * @apiResponse 409 Username or email already exists in the system
     * @apiResponse 500 Internal server error occurred during user creation
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        // Check if username or email already exists
        boolean usernameExists = userRepository.values().stream()
                .anyMatch(u -> u.getUsername().equals(user.getUsername()));
        boolean emailExists = userRepository.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));

        if (usernameExists || emailExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        user.setId(nextId++);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.put(user.getId(), user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * Updates an existing user's information.
     * 
     * This endpoint allows partial updates to user data while maintaining
     * data integrity and validation constraints. Username and email uniqueness
     * are validated before applying updates.
     * 
     * @param id The unique identifier of the user to update
     * @param user Updated user data (only provided fields will be updated)
     * @return Updated user with new timestamp metadata
     * 
     * @apiExample PUT /api/users/123
     * @apiResponse 200 User successfully updated with new information
     * @apiResponse 400 Invalid user data or validation constraints violated
     * @apiResponse 404 User not found or has been deleted
     * @apiResponse 409 Updated username or email already exists for another user
     * @apiResponse 500 Internal server error occurred during update operation
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable @NotNull @Min(1) Long id,
            @Valid @RequestBody User user) {
        
        User existingUser = userRepository.get(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        // Check for username/email conflicts with other users
        boolean usernameConflict = userRepository.values().stream()
                .anyMatch(u -> !u.getId().equals(id) && u.getUsername().equals(user.getUsername()));
        boolean emailConflict = userRepository.values().stream()
                .anyMatch(u -> !u.getId().equals(id) && u.getEmail().equals(user.getEmail()));

        if (usernameConflict || emailConflict) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Update user data
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFullName(user.getFullName());
        existingUser.setAge(user.getAge());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setActive(user.isActive());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(existingUser);
    }

    /**
     * Permanently deletes a user from the system.
     * 
     * @param id The unique identifier of the user to delete
     * @return Empty response body with appropriate status code
     * 
     * @apiExample DELETE /api/users/123
     * @apiResponse 204 User successfully deleted from the system
     * @apiResponse 404 User not found or has already been deleted
     * @apiResponse 500 Internal server error occurred during deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable @NotNull @Min(1) Long id) {
        
        User user = userRepository.remove(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Activates or deactivates a user account.
     * 
     * This endpoint provides a soft delete/restore functionality by toggling
     * the user's active status without permanently removing their data.
     * 
     * @param id The unique identifier of the user to activate/deactivate
     * @param active Boolean flag indicating desired activation status
     * @return Updated user with new activation status and timestamp
     * 
     * @apiExample PATCH /api/users/123/status?active=false
     * @apiResponse 200 User status successfully updated
     * @apiResponse 400 Invalid status parameter provided
     * @apiResponse 404 User not found in the system
     * @apiResponse 500 Internal server error occurred during status update
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<User> updateUserStatus(
            @PathVariable @NotNull @Min(1) Long id,
            @RequestParam @NotNull Boolean active) {
        
        User user = userRepository.get(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setActive(active);
        user.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(user);
    }

    /**
     * Searches for users by various criteria.
     * 
     * Advanced search endpoint that supports full-text search across username,
     * email, and full name fields with optional filters for age range and status.
     * 
     * @param query Search query string for full-text search
     * @param minAge Minimum age filter (optional)
     * @param maxAge Maximum age filter (optional)
     * @param active Filter by active status (optional)
     * @return List of users matching the search criteria
     * 
     * @apiExample GET /api/users/search?query=john&minAge=25&maxAge=65&active=true
     * @apiResponse 200 Search completed successfully with matching results
     * @apiResponse 400 Invalid search parameters provided
     * @apiResponse 500 Internal server error occurred during search operation
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam String query,
            @RequestParam(required = false) @Min(0) Integer minAge,
            @RequestParam(required = false) @Min(0) Integer maxAge,
            @RequestParam(required = false) Boolean active) {
        
        List<User> results = userRepository.values().stream()
                .filter(user -> active == null || user.isActive() == active)
                .filter(user -> minAge == null || user.getAge() == null || user.getAge() >= minAge)
                .filter(user -> maxAge == null || user.getAge() == null || user.getAge() <= maxAge)
                .filter(user -> 
                    user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    user.getFullName().toLowerCase().contains(query.toLowerCase())
                )
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .toList();

        return ResponseEntity.ok(results);
    }
}