package com.carrack.track.dto;

import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.AuditLog;
import java.util.List;

public record DashboardSnapshot(
        long totalUsers,
        long activeUsers,
        long adminUsers,
        long suspendedUsers,
        long newUsersThisMonth,
        List<AppUser> recentUsers,
        List<AuditLog> recentLogs
) {
}
