package com.carrack.track.controller;

import com.carrack.track.dto.MaintenanceForm;
import com.carrack.track.entity.MaintenanceReminder;
import com.carrack.track.enums.ReminderStatus;
import com.carrack.track.service.AppUserPrincipal;
import com.carrack.track.service.MaintenanceReminderService;
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
public class MaintenanceController {

    private final MaintenanceReminderService maintenanceReminderService;

    public MaintenanceController(MaintenanceReminderService maintenanceReminderService) {
        this.maintenanceReminderService = maintenanceReminderService;
    }

    @GetMapping("/maintenance")
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.ASC, "reminderDate"));
        Page<MaintenanceReminder> reminders = maintenanceReminderService.searchReminders(q, status, pageable);
        model.addAttribute("remindersPage", reminders);
        model.addAttribute("keyword", q);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statusValues", ReminderStatus.values());
        return "maintenance/list";
    }

    @GetMapping("/maintenance/new")
    public String create(Model model) {
        model.addAttribute("maintenanceForm", new MaintenanceForm());
        model.addAttribute("statusValues", ReminderStatus.values());
        model.addAttribute("formMode", "create");
        return "maintenance/form";
    }

    @PostMapping("/maintenance")
    public String store(@Valid @ModelAttribute("maintenanceForm") MaintenanceForm maintenanceForm,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal AppUserPrincipal principal,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statusValues", ReminderStatus.values());
            model.addAttribute("formMode", "create");
            return "maintenance/form";
        }

        try {
            MaintenanceReminder saved = maintenanceReminderService.createReminder(maintenanceForm, currentActor(principal));
            redirectAttributes.addFlashAttribute("successMessage", "Maintenance reminder created.");
            return "redirect:/maintenance/" + saved.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("statusValues", ReminderStatus.values());
            model.addAttribute("formMode", "create");
            model.addAttribute("formError", ex.getMessage());
            return "maintenance/form";
        }
    }

    @GetMapping("/maintenance/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("reminder", maintenanceReminderService.getRequiredReminder(id));
        return "maintenance/detail";
    }

    @GetMapping("/maintenance/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        MaintenanceReminder reminder = maintenanceReminderService.getRequiredReminder(id);
        model.addAttribute("reminderId", id);
        model.addAttribute("maintenanceForm", toForm(reminder));
        model.addAttribute("statusValues", ReminderStatus.values());
        model.addAttribute("formMode", "edit");
        return "maintenance/form";
    }

    @PostMapping("/maintenance/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("maintenanceForm") MaintenanceForm maintenanceForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("reminderId", id);
            model.addAttribute("statusValues", ReminderStatus.values());
            model.addAttribute("formMode", "edit");
            return "maintenance/form";
        }

        try {
            maintenanceReminderService.updateReminder(id, maintenanceForm, currentActor(principal));
            redirectAttributes.addFlashAttribute("successMessage", "Maintenance reminder updated.");
            return "redirect:/maintenance/" + id;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("reminderId", id);
            model.addAttribute("statusValues", ReminderStatus.values());
            model.addAttribute("formMode", "edit");
            model.addAttribute("formError", ex.getMessage());
            return "maintenance/form";
        }
    }

    @PostMapping("/maintenance/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        maintenanceReminderService.deleteReminder(id, currentActor(principal));
        redirectAttributes.addFlashAttribute("successMessage", "Maintenance reminder archived.");
        return "redirect:/maintenance";
    }

    private MaintenanceForm toForm(MaintenanceReminder reminder) {
        MaintenanceForm form = new MaintenanceForm();
        form.setVehicleNumber(reminder.getVehicleNumber());
        form.setTitle(reminder.getTitle());
        form.setReminderDate(reminder.getReminderDate());
        form.setLastServiceDate(reminder.getLastServiceDate());
        form.setMileageInterval(reminder.getMileageInterval());
        form.setStatus(reminder.getStatus());
        form.setNotes(reminder.getNotes());
        return form;
    }

    private String currentActor(AppUserPrincipal principal) {
        return principal != null ? principal.getUsername() : "system@local";
    }
}
