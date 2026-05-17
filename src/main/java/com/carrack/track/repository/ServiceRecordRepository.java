// Repository interface for database operations on service records
package com.carrack.track.repository;

import com.carrack.track.entity.ServiceRecord;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long>, JpaSpecificationExecutor<ServiceRecord> {

    List<ServiceRecord> findByVehicleIdOrderByServiceDateDesc(Long vehicleId);

    Page<ServiceRecord> findAllByOrderByServiceDateDesc(Pageable pageable);
}
