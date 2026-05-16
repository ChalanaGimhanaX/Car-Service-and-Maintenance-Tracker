package com.carrack.track.service;

import com.carrack.track.dto.ClientDashboardSnapshot;
import com.carrack.track.dto.DashboardSnapshot;
import com.carrack.track.entity.AppUser;

public interface DashboardService {

    DashboardSnapshot adminSnapshot();

    ClientDashboardSnapshot clientSnapshot(AppUser user);
}
