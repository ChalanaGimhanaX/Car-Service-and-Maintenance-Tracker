package com.carrack.track.service.impl;

import com.carrack.track.dto.InvoiceForm;
import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.Invoice;
import com.carrack.track.entity.ServiceRecord;
import com.carrack.track.enums.AuditAction;
import com.carrack.track.enums.PaymentStatus;
import com.carrack.track.enums.Role;
import com.carrack.track.repository.InvoiceRepository;
import com.carrack.track.repository.ServiceRecordRepository;
import com.carrack.track.service.AuditService;
import com.carrack.track.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ServiceRecordRepository serviceRecordRepository;
    private final AuditService auditService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              ServiceRecordRepository serviceRecordRepository,
                              AuditService auditService) {
        this.invoiceRepository = invoiceRepository;
        this.serviceRecordRepository = serviceRecordRepository;
        this.auditService = auditService;
    }

    @Override
    public Page<Invoice> searchInvoices(String keyword, String status, AppUser currentUser, Pageable pageable) {
        String cleanedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        PaymentStatus parsedStatus = null;
        if (StringUtils.hasText(status)) {
            try {
                parsedStatus = PaymentStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid payment status.");
            }
        }
        if (!isAdmin(currentUser)) {
            return invoiceRepository.searchInvoicesByOwner(currentUser.getId(), cleanedKeyword, parsedStatus, pageable);
        }
        return invoiceRepository.searchInvoices(cleanedKeyword, parsedStatus, pageable);
    }

    @Override
    public Invoice getRequiredInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found."));
    }

    @Override
    public Invoice getRequiredInvoice(Long id, AppUser currentUser) {
        Invoice invoice = getRequiredInvoice(id);
        if (!isAdmin(currentUser)
                && !invoice.getServiceRecord().getVehicle().getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Invoice not found.");
        }
        return invoice;
    }

    @Override
    public Invoice createInvoice(InvoiceForm form, String actorEmail) {
        String invoiceNumber = normalizeInvoiceNumber(form.getInvoiceNumber());
        if (invoiceRepository.findByInvoiceNumberIgnoreCase(invoiceNumber).isPresent()) {
            throw new IllegalStateException("Invoice number already exists.");
        }
        if (invoiceRepository.existsByServiceRecordId(form.getServiceRecordId())) {
            throw new IllegalStateException("This service record already has an invoice.");
        }

        ServiceRecord serviceRecord = getRequiredServiceRecord(form.getServiceRecordId());
        Invoice invoice = new Invoice();
        invoice.setServiceRecord(serviceRecord);
        applyForm(invoice, form, invoiceNumber);
        Invoice saved = invoiceRepository.save(invoice);
        auditService.log(AuditAction.INVOICE_CREATED, actorEmail, saved.getInvoiceNumber(), "Invoice created.");
        return saved;
    }

    @Override
    public Invoice updateInvoice(Long id, InvoiceForm form, String actorEmail) {
        Invoice invoice = getRequiredInvoice(id);
        String invoiceNumber = normalizeInvoiceNumber(form.getInvoiceNumber());
        Invoice existing = invoiceRepository.findByInvoiceNumberIgnoreCase(invoiceNumber).orElse(null);
        if (existing != null && !existing.getId().equals(id)) {
            throw new IllegalStateException("Invoice number already exists.");
        }

        ServiceRecord serviceRecord = getRequiredServiceRecord(form.getServiceRecordId());
        if (!invoice.getServiceRecord().getId().equals(serviceRecord.getId())
                && invoiceRepository.existsByServiceRecordId(serviceRecord.getId())) {
            throw new IllegalStateException("This service record already has an invoice.");
        }

        PaymentStatus previousStatus = invoice.getPaymentStatus();
        invoice.setServiceRecord(serviceRecord);
        applyForm(invoice, form, invoiceNumber);
        Invoice saved = invoiceRepository.save(invoice);

        AuditAction action = saved.getPaymentStatus() == PaymentStatus.CANCELLED
                && previousStatus != PaymentStatus.CANCELLED
                ? AuditAction.INVOICE_CANCELLED
                : AuditAction.INVOICE_UPDATED;
        String details = action == AuditAction.INVOICE_CANCELLED ? "Invoice cancelled." : "Invoice updated.";
        auditService.log(action, actorEmail, saved.getInvoiceNumber(), details);
        return saved;
    }

    @Override
    public void deleteInvoice(Long id, String actorEmail) {
        Invoice invoice = getRequiredInvoice(id);
        String invoiceNumber = invoice.getInvoiceNumber();
        invoiceRepository.delete(invoice);
        auditService.log(AuditAction.INVOICE_DELETED, actorEmail, invoiceNumber, "Invoice deleted.");
    }

    private ServiceRecord getRequiredServiceRecord(Long id) {
        return serviceRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service record not found."));
    }

    private void applyForm(Invoice invoice, InvoiceForm form, String invoiceNumber) {
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setInvoiceDate(form.getInvoiceDate());
        invoice.setTotalAmount(form.getTotalAmount());
        invoice.setPaymentStatus(form.getPaymentStatus());
        invoice.setPaymentMethod(form.getPaymentMethod());
        invoice.setNotes(StringUtils.hasText(form.getNotes()) ? form.getNotes().trim() : null);
    }

    private String normalizeInvoiceNumber(String invoiceNumber) {
        return invoiceNumber.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    private boolean isAdmin(AppUser user) {
        return user != null && user.getRole() == Role.ADMIN;
    }
}
