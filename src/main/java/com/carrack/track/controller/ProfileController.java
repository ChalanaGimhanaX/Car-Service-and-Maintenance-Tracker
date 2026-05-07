package com.carrack.track.controller;

import com.carrack.track.dto.ProfileForm;
import com.carrack.track.service.AppUserPrincipal;
import com.carrack.track.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        ProfileForm form = new ProfileForm();
        form.setFullName(principal.getUser().getFullName());
        form.setPhone(principal.getUser().getPhone());
        model.addAttribute("profileForm", form);
        model.addAttribute("profile", principal.getUser());
        return "profile";
    }

    @PostMapping("/profile")
    public String update(@AuthenticationPrincipal AppUserPrincipal principal,
                         @Valid @ModelAttribute("profileForm") ProfileForm profileForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("profile", principal.getUser());
            return "profile";
        }

        userService.updateOwnProfile(principal.getUsername(), profileForm);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated.");
        return "redirect:/profile";
    }
}
