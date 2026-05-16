package com.carrack.track.dto;

import com.carrack.track.enums.PaymentMethod;
import com.carrack.track.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class InvoiceForm {

    @NotNull(message = "Service record is required.")
    private Long serviceRecordId;

    @NotBlank(message = "Invoice number is required.")
    @Size(max = 40, message = "Invoice number must be 40 characters or fewer.")
    private String invoiceNumber;

    @NotNull(message = "Invoice date is required.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;

    @NotNull(message = "Total amount is required.")
    @DecimalMin(value = "0.00", message = "Amount cannot be negative.")
    @Digits(integer = 8, fraction = 2, message = "Amount must be a valid money value.")
    private BigDecimal totalAmount;

    @NotNull(message = "Payment status is required.")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private PaymentMethod paymentMethod;

    @Size(max = 1000, message = "Notes must be 1000 characters or fewer.")
    private String notes;

    public Long getServiceRecordId() {
        return serviceRecordId;
    }

    public void setServiceRecordId(Long serviceRecordId) {
        this.serviceRecordId = serviceRecordId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
