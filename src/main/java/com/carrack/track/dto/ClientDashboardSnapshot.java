package com.carrack.track.dto;

import com.carrack.track.entity.Invoice;
import com.carrack.track.entity.MaintenanceReminder;
import com.carrack.track.entity.ServiceRecord;
import java.util.List;

public record ClientDashboardSnapshot(
        long totalVehicles,
        long totalServices,
        long totalReminders,
        long totalInvoices,
        List<ServiceRecord> recentServices,
        List<MaintenanceReminder> upcomingReminders,
        List<Invoice> recentInvoices
) {
}
