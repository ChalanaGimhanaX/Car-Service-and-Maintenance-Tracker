package com.carrack.track.controller;

import com.carrack.track.dto.VehicleForm;
import com.carrack.track.entity.Vehicle;
import com.carrack.track.enums.FuelType;
import com.carrack.track.enums.VehicleStatus;
import com.carrack.track.service.AppUserPrincipal;
import com.carrack.track.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/vehicles")
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Vehicle> vehicles = vehicleService.searchVehicles(q, status, pageable);
        model.addAttribute("vehiclesPage", vehicles);
        model.addAttribute("keyword", q);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statusValues", VehicleStatus.values());
        return "vehicles/list";
    }

    @GetMapping("/vehicles/new")
    public String create(Model model) {
        model.addAttribute("vehicleForm", new VehicleForm());
        addFormOptions(model);
        model.addAttribute("formMode", "create");
        return "vehicles/form";
    }

    @PostMapping("/vehicles")
    public String store(@Valid @ModelAttribute("vehicleForm") VehicleForm vehicleForm,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal AppUserPrincipal principal,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (bindingResult.hasErrors()) {
            addFormOptions(model);
            model.addAttribute("formMode", "create");
            return "vehicles/form";
        }

        try {
            Vehicle saved = vehicleService.createVehicle(vehicleForm, currentActor(principal));
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle created.");
            return "redirect:/vehicles/" + saved.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            addFormOptions(model);
            model.addAttribute("formMode", "create");
            model.addAttribute("formError", ex.getMessage());
            return "vehicles/form";
        }
    }

    @GetMapping("/vehicles/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("vehicle", vehicleService.getRequiredVehicle(id));
        return "vehicles/detail";
    }

    @GetMapping("/vehicles/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.getRequiredVehicle(id);
        model.addAttribute("vehicleId", id);
        model.addAttribute("vehicleForm", toForm(vehicle));
        addFormOptions(model);
        model.addAttribute("formMode", "edit");
        return "vehicles/form";
    }

    @PostMapping("/vehicles/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("vehicleForm") VehicleForm vehicleForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicleId", id);
            addFormOptions(model);
            model.addAttribute("formMode", "edit");
            return "vehicles/form";
        }

        try {
            vehicleService.updateVehicle(id, vehicleForm, currentActor(principal));
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle details updated.");
            return "redirect:/vehicles/" + id;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("vehicleId", id);
            addFormOptions(model);
            model.addAttribute("formMode", "edit");
            model.addAttribute("formError", ex.getMessage());
            return "vehicles/form";
        }
    }

    @PostMapping("/vehicles/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        vehicleService.deleteVehicle(id, currentActor(principal));
        redirectAttributes.addFlashAttribute("successMessage", "Vehicle archived with soft delete.");
        return "redirect:/vehicles";
    }

    private void addFormOptions(Model model) {
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("statusValues", VehicleStatus.values());
    }

    private VehicleForm toForm(Vehicle vehicle) {
        VehicleForm form = new VehicleForm();
        form.setVehicleNumber(vehicle.getVehicleNumber());
        form.setModel(vehicle.getModel());
        form.setType(vehicle.getType());
        form.setBrand(vehicle.getBrand());
        form.setFuelType(vehicle.getFuelType());
        form.setYear(vehicle.getYear());
        form.setMileage(vehicle.getMileage());
        form.setStatus(vehicle.getStatus());
        form.setNotes(vehicle.getNotes());
        return form;
    }

    private String currentActor(AppUserPrincipal principal) {
        return principal != null ? principal.getUsername() : "system@local";
    }
}
