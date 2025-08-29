package com.lms.backend.service.impl;

import com.lms.backend.dto.user.CreateUserRequest;
import com.lms.backend.dto.user.UpdateUserRequest;
import com.lms.backend.dto.user.UserResponse;
import com.lms.backend.exception.BadRequestException;
import com.lms.backend.exception.ConflictException;
import com.lms.backend.exception.NotFoundException;
import com.lms.backend.model.entity.Member;
import com.lms.backend.model.entity.User;
import com.lms.backend.model.enums.Role;
import com.lms.backend.model.enums.UserStatus;
import com.lms.backend.repository.MemberRepository;
import com.lms.backend.repository.UserRepository;
import com.lms.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of UserService for administrative user management
 * Provides complete CRUD operations for user accounts
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                   .map(UserResponse::fromEntity)
                   .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        // Validate username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists: " + request.getUsername());
        }
        
        // Validate role (cannot create ADMIN users through this endpoint)
        if (request.getRole() == Role.ADMIN) {
            throw new BadRequestException("Cannot create ADMIN users through this endpoint");
        }
        
        // Create and save User entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        
        // If role is MEMBER, create associated Member entity
        if (request.getRole() == Role.MEMBER) {
            if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
                throw new BadRequestException("Full name is required for MEMBER role");
            }
            
            Member member = new Member();
            member.setUserId(savedUser.getId());
            member.setFullName(request.getFullName());
            member.setEmail(request.getEmail());
            member.setPhone(request.getPhone());
            member.setStatus(UserStatus.ACTIVE);
            
            memberRepository.save(member);
        }
        
        return UserResponse.fromEntity(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));
        
        // Check if username is being changed and if it's already taken
        if (!user.getUsername().equals(request.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new ConflictException("Username already exists: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }
        
        // Validate role change
        if (request.getRole() == Role.ADMIN) {
            throw new BadRequestException("Cannot change role to ADMIN through this endpoint");
        }
        
        // Handle role changes
        Role oldRole = user.getRole();
        Role newRole = request.getRole();
        
        if (oldRole != newRole) {
            user.setRole(newRole);
            
            // If changing from MEMBER to LIBRARIAN, remove Member record
            if (oldRole == Role.MEMBER && newRole == Role.LIBRARIAN) {
                memberRepository.findByUserId(id).ifPresent(memberRepository::delete);
            }
            
            // If changing from LIBRARIAN to MEMBER, create Member record
            if (oldRole == Role.LIBRARIAN && newRole == Role.MEMBER) {
                if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
                    throw new BadRequestException("Full name is required when changing to MEMBER role");
                }
                
                Member member = new Member();
                member.setUserId(user.getId());
                member.setFullName(request.getFullName());
                member.setEmail(request.getEmail());
                member.setPhone(request.getPhone());
                member.setStatus(request.getStatus());
                
                memberRepository.save(member);
            }
        }
        
        // Update user status
        user.setStatus(request.getStatus());
        
        // If user is a member and member details are provided, update them
        if (user.getRole() == Role.MEMBER && 
            (request.getFullName() != null || request.getEmail() != null || request.getPhone() != null)) {
            
            Optional<Member> memberOpt = memberRepository.findByUserId(id);
            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                
                if (request.getFullName() != null) {
                    member.setFullName(request.getFullName());
                }
                if (request.getEmail() != null) {
                    member.setEmail(request.getEmail());
                }
                if (request.getPhone() != null) {
                    member.setPhone(request.getPhone());
                }
                member.setStatus(request.getStatus());
                
                memberRepository.save(member);
            }
        }
        
        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    @Override
    public UserResponse suspendUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));
        
        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("Cannot suspend ADMIN users");
        }
        
        user.setStatus(UserStatus.SUSPENDED);
        
        // If user is a member, also suspend their member record
        if (user.getRole() == Role.MEMBER) {
            memberRepository.findByUserId(id).ifPresent(member -> {
                member.setStatus(UserStatus.SUSPENDED);
                memberRepository.save(member);
            });
        }
        
        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    @Override
    public UserResponse activateUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));
        
        user.setStatus(UserStatus.ACTIVE);
        
        // If user is a member, also activate their member record
        if (user.getRole() == Role.MEMBER) {
            memberRepository.findByUserId(id).ifPresent(member -> {
                member.setStatus(UserStatus.ACTIVE);
                memberRepository.save(member);
            });
        }
        
        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findByRole(String role) {
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            List<User> users = userRepository.findByRole(roleEnum);
            return users.stream().map(UserResponse::fromEntity).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + role + ". Valid roles are: ADMIN, LIBRARIAN, MEMBER");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findByStatus(String status) {
        try {
            UserStatus statusEnum = UserStatus.valueOf(status.toUpperCase());
            List<User> users = userRepository.findByStatus(statusEnum);
            return users.stream().map(UserResponse::fromEntity).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status + ". Valid statuses are: ACTIVE, SUSPENDED");
        }
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));
        
        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("Cannot delete ADMIN users");
        }
        
        // Safety checks for MEMBER
        if (user.getRole() == Role.MEMBER) {
            // Pending loans or pending fines must be zero; rely on repositories in services layer
            // Here we just ensure member exists and let cascade be explicit
            Optional<Member> memberOpt = memberRepository.findByUserId(id);
            if (memberOpt.isPresent()) {
                // Business checks should be enforced in a higher-level service if needed
                memberRepository.delete(memberOpt.get());
            }
        }
        
        userRepository.delete(user);
    }
}
