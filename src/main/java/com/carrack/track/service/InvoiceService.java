package com.carrack.track.service;

import com.carrack.track.dto.InvoiceForm;
import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {

    Page<Invoice> searchInvoices(String keyword, String status, AppUser currentUser, Pageable pageable);

    Invoice getRequiredInvoice(Long id);

    Invoice getRequiredInvoice(Long id, AppUser currentUser);

    Invoice createInvoice(InvoiceForm form, String actorEmail);

    Invoice updateInvoice(Long id, InvoiceForm form, String actorEmail);

    void deleteInvoice(Long id, String actorEmail);
}
