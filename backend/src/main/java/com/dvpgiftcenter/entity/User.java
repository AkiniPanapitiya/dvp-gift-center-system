package com.dvpgiftcenter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "USERS")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "username", unique = true)
    private String username;
    
    @NotBlank
    @Size(max = 255)
    @Column(name = "password_hash")
    private String passwordHash;
    
    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "email", unique = true)
    private String email;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "full_name")
    private String fullName;
    
    @Size(max = 20)
    @Column(name = "phone")
    private String phone;
    
    @NotBlank
    @Size(max = 20)
    @Column(name = "role")
    private String role;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<CustomerAddress> addresses;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<SessionToken> sessionTokens;
    
    // Constructors
    public User() {}
    
    public User(String username, String passwordHash, String email, String fullName, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.isActive = true;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public List<CustomerAddress> getAddresses() { return addresses; }
    public void setAddresses(List<CustomerAddress> addresses) { this.addresses = addresses; }
    
    public List<SessionToken> getSessionTokens() { return sessionTokens; }
    public void setSessionTokens(List<SessionToken> sessionTokens) { this.sessionTokens = sessionTokens; }
}