package com.carrack.track.controller.dashboard;

import com.carrack.track.enums.Role;
import com.carrack.track.service.AppUserPrincipal;
import com.carrack.track.service.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        if (principal != null && principal.getUser().getRole() == Role.ADMIN) {
            model.addAttribute("snapshot", dashboardService.adminSnapshot());
            return "dashboard/index";
        }

        model.addAttribute("snapshot", dashboardService.clientSnapshot(principal.getUser()));
        return "dashboard/client";
    }
}
