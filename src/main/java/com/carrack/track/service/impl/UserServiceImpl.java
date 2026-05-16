package com.carrack.track.service.impl;

import com.carrack.track.dto.RegistrationRequest;
import com.carrack.track.dto.ProfileForm;
import com.carrack.track.dto.UserForm;
import com.carrack.track.entity.AppUser;
import com.carrack.track.enums.AccountStatus;
import com.carrack.track.enums.AuditAction;
import com.carrack.track.enums.Role;
import com.carrack.track.repository.AppUserRepository;
import com.carrack.track.service.AuditService;
import com.carrack.track.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;
    private final AuditService auditService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(AppUserRepository userRepository, AuditService auditService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUser register(RegistrationRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalStateException("Email already exists.");
        }

        AppUser user = new AppUser();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(StringUtils.hasText(request.getPhone()) ? request.getPhone().trim() : null);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CLIENT);
        user.setStatus(AccountStatus.ACTIVE);

        AppUser saved = userRepository.save(user);
        auditService.log(AuditAction.REGISTER, saved.getEmail(), saved.getEmail(), "New client account registered.");
        return saved;
    }

    @Override
    public AppUser updateOwnProfile(String email, ProfileForm form) {
        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        user.setFullName(form.getFullName().trim());
        user.setPhone(StringUtils.hasText(form.getPhone()) ? form.getPhone().trim() : null);
        AppUser saved = userRepository.save(user);
        auditService.log(AuditAction.USER_UPDATED, saved.getEmail(), saved.getEmail(), "Profile updated by the logged-in user.");
        return saved;
    }

    @Override
    public Page<AppUser> searchUsers(String keyword, String status, Pageable pageable) {
        String cleanedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        AccountStatus parsedStatus = StringUtils.hasText(status)
                ? AccountStatus.valueOf(status.trim().toUpperCase())
                : null;
        return userRepository.searchUsers(cleanedKeyword, parsedStatus, pageable);
    }

    @Override
    public List<AppUser> listAssignableClients() {
        return userRepository.findByRoleAndStatusNotOrderByFullNameAsc(Role.CLIENT, AccountStatus.DELETED);
    }

    @Override
    public AppUser getRequiredUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    @Override
    public AppUser updateUser(Long id, UserForm form, String actorEmail) {
        AppUser user = getRequiredUser(id);
        AppUser byEmail = userRepository.findByEmailIgnoreCase(form.getEmail().trim()).orElse(null);
        if (byEmail != null && !byEmail.getId().equals(id)) {
            throw new IllegalStateException("Email already exists.");
        }

        user.setFullName(form.getFullName().trim());
        user.setEmail(form.getEmail().trim().toLowerCase());
        user.setPhone(StringUtils.hasText(form.getPhone()) ? form.getPhone().trim() : null);
        user.setRole(form.getRole());
        user.setStatus(form.getStatus());
        if (form.getStatus() == AccountStatus.DELETED) {
            user.setDeletedAt(LocalDateTime.now());
        } else if (user.getDeletedAt() != null && form.getStatus() != AccountStatus.DELETED) {
            user.setDeletedAt(null);
        }

        AppUser saved = userRepository.save(user);
        auditService.log(AuditAction.USER_UPDATED, actorEmail, saved.getEmail(), "User profile was updated from admin panel.");
        return saved;
    }

    @Override
    public void suspendUser(Long id, String actorEmail) {
        changeStatus(id, AccountStatus.SUSPENDED, actorEmail, AuditAction.USER_SUSPENDED, "Account suspended from admin panel.");
    }

    @Override
    public void restoreUser(Long id, String actorEmail) {
        changeStatus(id, AccountStatus.ACTIVE, actorEmail, AuditAction.USER_RESTORED, "Account restored from admin panel.");
    }

    @Override
    public void deleteUser(Long id, String actorEmail) {
        AppUser user = getRequiredUser(id);
        user.setStatus(AccountStatus.DELETED);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
        auditService.log(AuditAction.USER_DELETED, actorEmail, user.getEmail(), "Account archived with soft delete.");
    }

    @Override
    public void markLastLogin(String email) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            auditService.log(AuditAction.LOGIN, user.getEmail(), user.getEmail(), "Successful sign-in recorded.");
        });
    }

    private void changeStatus(Long id, AccountStatus status, String actorEmail, AuditAction action, String details) {
        AppUser user = getRequiredUser(id);
        user.setStatus(status);
        if (status == AccountStatus.DELETED) {
            user.setDeletedAt(LocalDateTime.now());
        } else if (status == AccountStatus.ACTIVE) {
            user.setDeletedAt(null);
        }
        userRepository.save(user);
        auditService.log(action, actorEmail, user.getEmail(), details);
    }
}
