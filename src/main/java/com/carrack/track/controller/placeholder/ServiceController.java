package com.carrack.track.controller.placeholder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServiceController {

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("moduleName", "Service Management");
        model.addAttribute("moduleOwner", "Reserved for the service module owner.");
        return "placeholder/module";
    }
}
