package com.carrack.track.controller.placeholder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VehicleController {

    @GetMapping("/vehicles")
    public String vehicles(Model model) {
        model.addAttribute("moduleName", "Vehicle Management");
        model.addAttribute("moduleOwner", "Reserved for the vehicle module owner.");
        return "placeholder/module";
    }
}
