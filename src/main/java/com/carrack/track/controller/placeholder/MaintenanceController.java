package com.carrack.track.controller.placeholder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MaintenanceController {

    @GetMapping("/maintenance")
    public String maintenance(Model model) {
        model.addAttribute("moduleName", "Maintenance Reminder");
        model.addAttribute("moduleOwner", "Reserved for the maintenance module owner.");
        return "placeholder/module";
    }
}
