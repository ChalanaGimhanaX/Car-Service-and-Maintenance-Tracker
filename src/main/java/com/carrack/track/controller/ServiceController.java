package com.carrack.track.controller;

import com.carrack.track.dto.ServiceForm;
import com.carrack.track.entity.ServiceRecord;
import com.carrack.track.enums.ServiceStatus;
import com.carrack.track.service.AppUserPrincipal;
import com.carrack.track.service.ServiceRecordService;
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
public class ServiceController {

    private final ServiceRecordService serviceRecordService;

    public ServiceController(ServiceRecordService serviceRecordService) {
        this.serviceRecordService = serviceRecordService;
    }

    @GetMapping("/services")
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "serviceDate"));
        Page<ServiceRecord> services = serviceRecordService.searchServices(q, status, pageable);
        model.addAttribute("servicesPage", services);
        model.addAttribute("keyword", q);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statusValues", ServiceStatus.values());
        return "services/list";
    }

    @GetMapping("/services/new")
    public String create(Model model) {
        model.addAttribute("serviceForm", new ServiceForm());
        model.addAttribute("statusValues", ServiceStatus.values());
        model.addAttribute("formMode", "create");
        return "services/form";
    }

    @PostMapping("/services")
    public String store(@Valid @ModelAttribute("serviceForm") ServiceForm serviceForm,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal AppUserPrincipal principal,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statusValues", ServiceStatus.values());
            model.addAttribute("formMode", "create");
            return "services/form";
        }

        try {
            ServiceRecord saved = serviceRecordService.createServiceRecord(serviceForm, currentActor(principal));
            redirectAttributes.addFlashAttribute("successMessage", "Service record created.");
            return "redirect:/services/" + saved.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("statusValues", ServiceStatus.values());
            model.addAttribute("formMode", "create");
            model.addAttribute("formError", ex.getMessage());
            return "services/form";
        }
    }

    @GetMapping("/services/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("serviceRecord", serviceRecordService.getRequiredServiceRecord(id));
        return "services/detail";
    }

    @GetMapping("/services/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        ServiceRecord record = serviceRecordService.getRequiredServiceRecord(id);
        model.addAttribute("serviceId", id);
        model.addAttribute("serviceForm", toForm(record));
        model.addAttribute("statusValues", ServiceStatus.values());
        model.addAttribute("formMode", "edit");
        return "services/form";
    }

    @PostMapping("/services/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("serviceForm") ServiceForm serviceForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("serviceId", id);
            model.addAttribute("statusValues", ServiceStatus.values());
            model.addAttribute("formMode", "edit");
            return "services/form";
        }

        try {
            serviceRecordService.updateServiceRecord(id, serviceForm, currentActor(principal));
            redirectAttributes.addFlashAttribute("successMessage", "Service record updated.");
            return "redirect:/services/" + id;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("serviceId", id);
            model.addAttribute("statusValues", ServiceStatus.values());
            model.addAttribute("formMode", "edit");
            model.addAttribute("formError", ex.getMessage());
            return "services/form";
        }
    }

    @PostMapping("/services/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        serviceRecordService.deleteServiceRecord(id, currentActor(principal));
        redirectAttributes.addFlashAttribute("successMessage", "Service record archived.");
        return "redirect:/services";
    }

    private ServiceForm toForm(ServiceRecord record) {
        ServiceForm form = new ServiceForm();
        form.setServiceCode(record.getServiceCode());
        form.setVehicleNumber(record.getVehicleNumber());
        form.setCustomerName(record.getCustomerName());
        form.setServiceType(record.getServiceType());
        form.setServiceDate(record.getServiceDate());
        form.setCost(record.getCost());
        form.setTechnicianName(record.getTechnicianName());
        form.setStatus(record.getStatus());
        form.setNotes(record.getNotes());
        return form;
    }

    private String currentActor(AppUserPrincipal principal) {
        return principal != null ? principal.getUsername() : "system@local";
    }
}
