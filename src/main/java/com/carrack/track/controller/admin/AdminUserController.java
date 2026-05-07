package com.carrack.track.controller.admin;

import com.carrack.track.dto.UserForm;
import com.carrack.track.entity.AppUser;
import com.carrack.track.enums.AccountStatus;
import com.carrack.track.enums.Role;
import com.carrack.track.service.AppUserPrincipal;
import com.carrack.track.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AppUser> users = userService.searchUsers(q, status, pageable);
        model.addAttribute("usersPage", users);
        model.addAttribute("keyword", q);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statusValues", AccountStatus.values());
        return "admin/users";
    }

    @GetMapping("/admin/users/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        AppUser user = userService.getRequiredUser(id);
        UserForm form = new UserForm();
        form.setFullName(user.getFullName());
        form.setEmail(user.getEmail());
        form.setPhone(user.getPhone());
        form.setRole(user.getRole());
        form.setStatus(user.getStatus());
        model.addAttribute("userId", id);
        model.addAttribute("userForm", form);
        model.addAttribute("roleValues", Role.values());
        model.addAttribute("statusValues", AccountStatus.values());
        return "admin/edit-user";
    }

    @PostMapping("/admin/users/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("userForm") UserForm userForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", id);
            model.addAttribute("roleValues", Role.values());
            model.addAttribute("statusValues", AccountStatus.values());
            return "admin/edit-user";
        }

        try {
            userService.updateUser(id, userForm, principal != null ? principal.getUsername() : "admin@system.local");
            redirectAttributes.addFlashAttribute("successMessage", "User details updated.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("userId", id);
            model.addAttribute("roleValues", Role.values());
            model.addAttribute("statusValues", AccountStatus.values());
            model.addAttribute("formError", ex.getMessage());
            return "admin/edit-user";
        }
        return "redirect:/admin/users/" + id + "/edit";
    }

    @PostMapping("/admin/users/{id}/suspend")
    public String suspend(@PathVariable Long id,
                          @AuthenticationPrincipal AppUserPrincipal principal,
                          RedirectAttributes redirectAttributes) {
        userService.suspendUser(id, principal != null ? principal.getUsername() : "admin@system.local");
        redirectAttributes.addFlashAttribute("successMessage", "User suspended.");
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/restore")
    public String restore(@PathVariable Long id,
                          @AuthenticationPrincipal AppUserPrincipal principal,
                          RedirectAttributes redirectAttributes) {
        userService.restoreUser(id, principal != null ? principal.getUsername() : "admin@system.local");
        redirectAttributes.addFlashAttribute("successMessage", "User restored.");
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        userService.deleteUser(id, principal != null ? principal.getUsername() : "admin@system.local");
        redirectAttributes.addFlashAttribute("successMessage", "User archived with soft delete.");
        return "redirect:/admin/users";
    }
}
