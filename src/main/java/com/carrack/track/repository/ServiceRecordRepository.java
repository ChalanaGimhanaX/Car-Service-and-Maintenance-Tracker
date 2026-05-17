package com.carrack.track.repository;

import com.carrack.track.entity.ServiceRecord;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {

    List<ServiceRecord> findByVehicleIdOrderByServiceDateDesc(Long vehicleId);

    List<ServiceRecord> findAllByOrderByServiceDateDesc();

    Page<ServiceRecord> findAllByOrderByServiceDateDesc(Pageable pageable);

    long countByVehicleOwnerId(Long ownerId);

    List<ServiceRecord> findTop5ByVehicleOwnerIdOrderByServiceDateDesc(Long ownerId);

    @Query("""
            select s from ServiceRecord s
            where (:vehicleId is null or s.vehicle.id = :vehicleId)
              and (:keyword is null
                   or lower(s.serviceType) like lower(concat('%', :keyword, '%'))
                   or lower(coalesce(s.serviceCenter, '')) like lower(concat('%', :keyword, '%'))
                   or lower(s.vehicle.vehicleNumber) like lower(concat('%', :keyword, '%')))
            """)
    Page<ServiceRecord> searchRecords(@Param("keyword") String keyword,
                                      @Param("vehicleId") Long vehicleId,
                                      Pageable pageable);

    @Query("""
            select s from ServiceRecord s
            where s.vehicle.owner.id = :ownerId
              and (:vehicleId is null or s.vehicle.id = :vehicleId)
              and (:keyword is null
                   or lower(s.serviceType) like lower(concat('%', :keyword, '%'))
                   or lower(coalesce(s.serviceCenter, '')) like lower(concat('%', :keyword, '%'))
                   or lower(s.vehicle.vehicleNumber) like lower(concat('%', :keyword, '%')))
            """)
    Page<ServiceRecord> searchRecordsByOwner(@Param("ownerId") Long ownerId,
                                             @Param("keyword") String keyword,
                                             @Param("vehicleId") Long vehicleId,
                                             Pageable pageable);
}
