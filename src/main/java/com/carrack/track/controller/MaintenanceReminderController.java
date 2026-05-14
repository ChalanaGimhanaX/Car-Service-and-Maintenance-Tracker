package com.carrack.track.controller;

import com.carrack.track.dto.MaintenanceReminderForm;
import com.carrack.track.entity.MaintenanceReminder;
import com.carrack.track.enums.MaintenancePriority;
import com.carrack.track.enums.MaintenanceStatus;
import com.carrack.track.service.MaintenanceReminderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class MaintenanceReminderController {

    private final MaintenanceReminderService reminderService;

    public MaintenanceReminderController(MaintenanceReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping("/maintenance")
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {
        String selectedStatus = status == null ? MaintenanceStatus.UPCOMING.name() : status;
        Pageable pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.ASC, "dueDate"));
        Page<MaintenanceReminder> reminders = reminderService.searchReminders(q, selectedStatus, pageable);
        model.addAttribute("remindersPage", reminders);
        model.addAttribute("keyword", q);
        model.addAttribute("selectedStatus", selectedStatus);
        model.addAttribute("statusValues", MaintenanceStatus.values());
        model.addAttribute("overdueCount", reminderService.countOverdueReminders());
        return "maintenance/list";
    }

    @GetMapping("/maintenance/new")
    public String create(Model model) {
        model.addAttribute("maintenanceReminderForm", new MaintenanceReminderForm());
        addFormOptions(model);
        model.addAttribute("formMode", "create");
        return "maintenance/form";
    }

    @PostMapping("/maintenance")
    public String store(@Valid @ModelAttribute("maintenanceReminderForm") MaintenanceReminderForm form,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (bindingResult.hasErrors()) {
            addFormOptions(model);
            model.addAttribute("formMode", "create");
            return "maintenance/form";
        }

        try {
            MaintenanceReminder saved = reminderService.createReminder(form);
            redirectAttributes.addFlashAttribute("successMessage", "Maintenance reminder created.");
            return "redirect:/maintenance/" + saved.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            addFormOptions(model);
            model.addAttribute("formMode", "create");
            model.addAttribute("formError", ex.getMessage());
            return "maintenance/form";
        }
    }

    @GetMapping("/maintenance/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("reminder", reminderService.getRequiredReminder(id));
        return "maintenance/detail";
    }

    @GetMapping("/maintenance/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        MaintenanceReminder reminder = reminderService.getRequiredReminder(id);
        model.addAttribute("reminderId", id);
        model.addAttribute("maintenanceReminderForm", toForm(reminder));
        addFormOptions(model);
        model.addAttribute("formMode", "edit");
        return "maintenance/form";
    }

    @PostMapping("/maintenance/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("maintenanceReminderForm") MaintenanceReminderForm form,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("reminderId", id);
            addFormOptions(model);
            model.addAttribute("formMode", "edit");
            return "maintenance/form";
        }

        try {
            reminderService.updateReminder(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Maintenance reminder updated.");
            return "redirect:/maintenance/" + id;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("reminderId", id);
            addFormOptions(model);
            model.addAttribute("formMode", "edit");
            model.addAttribute("formError", ex.getMessage());
            return "maintenance/form";
        }
    }

    @PostMapping("/maintenance/{id}/complete")
    public String complete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reminderService.markCompleted(id);
        redirectAttributes.addFlashAttribute("successMessage", "Maintenance reminder marked as completed.");
        return "redirect:/maintenance/" + id;
    }

    private void addFormOptions(Model model) {
        model.addAttribute("vehicles", reminderService.listSelectableVehicles());
        model.addAttribute("statusValues", MaintenanceStatus.values());
        model.addAttribute("priorityValues", MaintenancePriority.values());
    }

    private MaintenanceReminderForm toForm(MaintenanceReminder reminder) {
        MaintenanceReminderForm form = new MaintenanceReminderForm();
        form.setTitle(reminder.getTitle());
        form.setVehicleId(reminder.getVehicle().getId());
        form.setLastServiceDate(reminder.getLastServiceDate());
        form.setIntervalDays(reminder.getIntervalDays());
        form.setDueDate(reminder.getDueDate());
        form.setStatus(reminder.getStatus());
        form.setPriority(reminder.getPriority());
        form.setNote(reminder.getNote());
        return form;
    }
}
