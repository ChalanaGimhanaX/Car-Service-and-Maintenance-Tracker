package com.carrack.track.repository;

import com.carrack.track.entity.Invoice;
import com.carrack.track.enums.PaymentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumberIgnoreCase(String invoiceNumber);

    boolean existsByServiceRecordId(Long serviceRecordId);

    long countByServiceRecordVehicleOwnerId(Long ownerId);

    List<Invoice> findTop5ByServiceRecordVehicleOwnerIdOrderByInvoiceDateDesc(Long ownerId);

    @Query("""
            select i from Invoice i
            where (:status is null or i.paymentStatus = :status)
              and (:keyword is null
                   or lower(i.invoiceNumber) like lower(concat('%', :keyword, '%'))
                   or lower(i.serviceRecord.vehicle.vehicleNumber) like lower(concat('%', :keyword, '%'))
                   or lower(i.serviceRecord.serviceType) like lower(concat('%', :keyword, '%')))
            """)
    Page<Invoice> searchInvoices(@Param("keyword") String keyword,
                                 @Param("status") PaymentStatus status,
                                 Pageable pageable);
}
