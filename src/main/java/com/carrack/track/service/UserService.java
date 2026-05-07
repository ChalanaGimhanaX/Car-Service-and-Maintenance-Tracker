package com.carrack.track.service;

import com.carrack.track.dto.RegistrationRequest;
import com.carrack.track.dto.ProfileForm;
import com.carrack.track.dto.UserForm;
import com.carrack.track.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    AppUser register(RegistrationRequest request);

    AppUser updateOwnProfile(String email, ProfileForm form);

    Page<AppUser> searchUsers(String keyword, String status, Pageable pageable);

    AppUser getRequiredUser(Long id);

    AppUser updateUser(Long id, UserForm form, String actorEmail);

    void suspendUser(Long id, String actorEmail);

    void restoreUser(Long id, String actorEmail);

    void deleteUser(Long id, String actorEmail);

    void markLastLogin(String email);
}
