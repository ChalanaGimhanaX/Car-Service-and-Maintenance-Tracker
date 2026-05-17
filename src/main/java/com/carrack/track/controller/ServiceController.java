package com.carrack.track.controller;

import com.carrack.track.dto.ServiceForm;
import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.ServiceRecord;
import com.carrack.track.entity.Vehicle;
import com.carrack.track.enums.Role;
import com.carrack.track.enums.ServiceStatus;
import com.carrack.track.repository.VehicleRepository;
import com.carrack.track.service.AppUserPrincipal;
import com.carrack.track.service.ServiceRecordService;
import jakarta.validation.Valid;
import java.util.List;
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
public class ServiceController {

    private final ServiceRecordService serviceRecordService;
    private final VehicleRepository vehicleRepository;

    public ServiceController(ServiceRecordService serviceRecordService,
                             VehicleRepository vehicleRepository) {
        this.serviceRecordService = serviceRecordService;
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping("/services")
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "vehicleId", required = false) Long vehicleId,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @AuthenticationPrincipal AppUserPrincipal principal,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "serviceDate"));
        AppUser currentUser = currentUser(principal);
        Page<ServiceRecord> recordsPage = serviceRecordService.searchServiceRecords(q, vehicleId, currentUser, pageable);
        model.addAttribute("recordsPage", recordsPage);
        model.addAttribute("keyword", q);
        model.addAttribute("selectedVehicleId", vehicleId);
        model.addAttribute("vehicles", visibleVehicles(currentUser));
        model.addAttribute("serviceStatuses", ServiceStatus.values());
        return "services/list";
    }

    @GetMapping("/services/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@RequestParam(value = "vehicleId", required = false) Long vehicleId,
                         Model model) {
        ServiceForm form = new ServiceForm();
        if (vehicleId != null) form.setVehicleId(vehicleId);
        model.addAttribute("serviceForm", form);
        model.addAttribute("vehicles", vehicleRepository.findAll());
        model.addAttribute("serviceStatuses", ServiceStatus.values());
        model.addAttribute("formMode", "create");
        return "services/form";
    }

    @PostMapping("/services")
    @PreAuthorize("hasRole('ADMIN')")
    public String store(@Valid @ModelAttribute("serviceForm") ServiceForm serviceForm,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal AppUserPrincipal principal,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicles", vehicleRepository.findAll());
            model.addAttribute("serviceStatuses", ServiceStatus.values());
            model.addAttribute("formMode", "create");
            return "services/form";
        }

        try {
            ServiceRecord saved = serviceRecordService.createServiceRecord(serviceForm, currentActor(principal));
            redirectAttributes.addFlashAttribute("successMessage", "Service record created.");
            return "redirect:/services/" + saved.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("vehicles", vehicleRepository.findAll());
            model.addAttribute("serviceStatuses", ServiceStatus.values());
            model.addAttribute("formMode", "create");
            model.addAttribute("formError", ex.getMessage());
            return "services/form";
        }
    }

    @GetMapping("/services/{id}")
    public String detail(@PathVariable Long id,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         Model model) {
        model.addAttribute("record", serviceRecordService.getRequiredServiceRecord(id, currentUser(principal)));
        model.addAttribute("serviceStatuses", ServiceStatus.values());
        return "services/detail";
    }

    @GetMapping("/services/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        ServiceRecord record = serviceRecordService.getRequiredServiceRecord(id);
        model.addAttribute("serviceId", id);
        model.addAttribute("serviceForm", toForm(record));
        model.addAttribute("vehicles", vehicleRepository.findAll());
        model.addAttribute("serviceStatuses", ServiceStatus.values());
        model.addAttribute("formMode", "edit");
        return "services/form";
    }

    @PostMapping("/services/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("serviceForm") ServiceForm serviceForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("serviceId", id);
            model.addAttribute("vehicles", vehicleRepository.findAll());
            model.addAttribute("serviceStatuses", ServiceStatus.values());
            model.addAttribute("formMode", "edit");
            return "services/form";
        }

        try {
            serviceRecordService.updateServiceRecord(id, serviceForm, currentActor(principal));
            redirectAttributes.addFlashAttribute("successMessage", "Service record updated.");
            return "redirect:/services/" + id;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("serviceId", id);
            model.addAttribute("vehicles", vehicleRepository.findAll());
            model.addAttribute("serviceStatuses", ServiceStatus.values());
            model.addAttribute("formMode", "edit");
            model.addAttribute("formError", ex.getMessage());
            return "services/form";
        }
    }

    @PostMapping("/services/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        serviceRecordService.deleteServiceRecord(id, currentActor(principal));
        redirectAttributes.addFlashAttribute("successMessage", "Service record deleted.");
        return "redirect:/services";
    }

    @PostMapping("/services/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam("serviceStatus") ServiceStatus serviceStatus,
                               @AuthenticationPrincipal AppUserPrincipal principal,
                               RedirectAttributes redirectAttributes) {
        serviceRecordService.updateServiceStatus(id, serviceStatus, currentActor(principal));
        redirectAttributes.addFlashAttribute("successMessage", "Service progress updated.");
        return "redirect:/services/" + id;
    }

    private ServiceForm toForm(ServiceRecord record) {
        ServiceForm form = new ServiceForm();
        form.setVehicleId(record.getVehicle().getId());
        form.setServiceType(record.getServiceType());
        form.setServiceDate(record.getServiceDate());
        form.setMileageAtService(record.getMileageAtService());
        form.setServiceCenter(record.getServiceCenter());
        form.setServiceStatus(record.getServiceStatus());
        form.setCost(record.getCost());
        form.setNotes(record.getNotes());
        return form;
    }

    private String currentActor(AppUserPrincipal principal) {
        return principal != null ? principal.getUsername() : "system@local";
    }

    private AppUser currentUser(AppUserPrincipal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Signed-in user is required.");
        }
        return principal.getUser();
    }

    private List<Vehicle> visibleVehicles(AppUser currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return vehicleRepository.findAll();
        }
        return vehicleRepository.findByOwnerIdAndDeletedAtIsNullOrderByVehicleNumberAsc(currentUser.getId());
    }
}
