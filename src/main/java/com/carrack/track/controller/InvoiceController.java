package com.carrack.track.controller;

import com.carrack.track.dto.InvoiceForm;
import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.Invoice;
import com.carrack.track.entity.ServiceRecord;
import com.carrack.track.enums.PaymentMethod;
import com.carrack.track.enums.PaymentStatus;
import com.carrack.track.service.AppUserPrincipal;
import com.carrack.track.service.InvoiceService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.carrack.track.repository.ServiceRecordRepository;

@Controller
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final ServiceRecordRepository serviceRecordRepository;

    public InvoiceController(InvoiceService invoiceService,
                             ServiceRecordRepository serviceRecordRepository) {
        this.invoiceService = invoiceService;
        this.serviceRecordRepository = serviceRecordRepository;
    }

    @GetMapping("/invoices")
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @AuthenticationPrincipal AppUserPrincipal principal,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "invoiceDate"));
        Page<Invoice> invoicesPage = invoiceService.searchInvoices(q, status, currentUser(principal), pageable);
        model.addAttribute("invoicesPage", invoicesPage);
        model.addAttribute("keyword", q);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statusValues", PaymentStatus.values());
        return "invoices/list";
    }

    @GetMapping("/invoices/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@RequestParam(value = "serviceRecordId", required = false) Long serviceRecordId,
                         Model model) {
        InvoiceForm form = new InvoiceForm();
        if (serviceRecordId != null) {
            form.setServiceRecordId(serviceRecordId);
        }
        model.addAttribute("invoiceForm", form);
        addFormOptions(model);
        model.addAttribute("formMode", "create");
        return "invoices/form";
    }

    @PostMapping("/invoices")
    @PreAuthorize("hasRole('ADMIN')")
    public String store(@Valid @ModelAttribute("invoiceForm") InvoiceForm invoiceForm,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal AppUserPrincipal principal,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (bindingResult.hasErrors()) {
            addFormOptions(model);
            model.addAttribute("formMode", "create");
            return "invoices/form";
        }

        try {
            Invoice saved = invoiceService.createInvoice(invoiceForm, currentActor(principal));
            redirectAttributes.addFlashAttribute("successMessage", "Invoice created.");
            return "redirect:/invoices/" + saved.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            addFormOptions(model);
            model.addAttribute("formMode", "create");
            model.addAttribute("formError", ex.getMessage());
            return "invoices/form";
        }
    }

    @GetMapping("/invoices/{id}")
    public String detail(@PathVariable Long id,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         Model model) {
        model.addAttribute("invoice", invoiceService.getRequiredInvoice(id, currentUser(principal)));
        return "invoices/detail";
    }

    @GetMapping("/invoices/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        Invoice invoice = invoiceService.getRequiredInvoice(id);
        model.addAttribute("invoiceId", id);
        model.addAttribute("invoiceForm", toForm(invoice));
        addFormOptions(model);
        model.addAttribute("formMode", "edit");
        return "invoices/form";
    }

    @PostMapping("/invoices/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("invoiceForm") InvoiceForm invoiceForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("invoiceId", id);
            addFormOptions(model);
            model.addAttribute("formMode", "edit");
            return "invoices/form";
        }

        try {
            invoiceService.updateInvoice(id, invoiceForm, currentActor(principal));
            redirectAttributes.addFlashAttribute("successMessage", "Invoice updated.");
            return "redirect:/invoices/" + id;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("invoiceId", id);
            addFormOptions(model);
            model.addAttribute("formMode", "edit");
            model.addAttribute("formError", ex.getMessage());
            return "invoices/form";
        }
    }

    @PostMapping("/invoices/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal AppUserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        invoiceService.deleteInvoice(id, currentActor(principal));
        redirectAttributes.addFlashAttribute("successMessage", "Invoice deleted.");
        return "redirect:/invoices";
    }

    private void addFormOptions(Model model) {
        List<ServiceRecord> serviceRecords = serviceRecordRepository.findAllByOrderByServiceDateDesc();
        model.addAttribute("serviceRecords", serviceRecords);
        model.addAttribute("paymentStatuses", PaymentStatus.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());
    }

    private InvoiceForm toForm(Invoice invoice) {
        InvoiceForm form = new InvoiceForm();
        form.setServiceRecordId(invoice.getServiceRecord().getId());
        form.setInvoiceNumber(invoice.getInvoiceNumber());
        form.setInvoiceDate(invoice.getInvoiceDate());
        form.setTotalAmount(invoice.getTotalAmount());
        form.setPaymentStatus(invoice.getPaymentStatus());
        form.setPaymentMethod(invoice.getPaymentMethod());
        form.setNotes(invoice.getNotes());
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
}
