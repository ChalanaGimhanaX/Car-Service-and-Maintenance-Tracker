package com.carrack.track.repository;

import com.carrack.track.entity.Vehicle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVehicleNumberIgnoreCase(String vehicleNumber);

    boolean existsByVehicleNumberIgnoreCase(String vehicleNumber);

    @Query("""
            select v from Vehicle v
            where v.status <> com.carrack.track.enums.VehicleStatus.DELETED
              and (:status is null or v.status = :status)
              and (:keyword is null
                   or lower(v.vehicleNumber) like lower(concat('%', :keyword, '%'))
                   or lower(v.brand) like lower(concat('%', :keyword, '%'))
                   or lower(v.model) like lower(concat('%', :keyword, '%'))
                   or lower(v.type) like lower(concat('%', :keyword, '%'))
                   or lower(v.owner.fullName) like lower(concat('%', :keyword, '%'))
                   or lower(v.owner.email) like lower(concat('%', :keyword, '%')))
            """)
    Page<Vehicle> searchAll(@Param("keyword") String keyword,
                            @Param("status") com.carrack.track.enums.VehicleStatus status,
                            Pageable pageable);

    @Query("""
            select v from Vehicle v
            where v.status <> com.carrack.track.enums.VehicleStatus.DELETED
              and v.owner.id = :ownerId
              and (:status is null or v.status = :status)
              and (:keyword is null
                   or lower(v.vehicleNumber) like lower(concat('%', :keyword, '%'))
                   or lower(v.brand) like lower(concat('%', :keyword, '%'))
                   or lower(v.model) like lower(concat('%', :keyword, '%'))
                   or lower(v.type) like lower(concat('%', :keyword, '%')))
            """)
    Page<Vehicle> searchByOwner(@Param("ownerId") Long ownerId,
                                @Param("keyword") String keyword,
                                @Param("status") com.carrack.track.enums.VehicleStatus status,
                                Pageable pageable);

    long countByDeletedAtIsNull();

    long countByOwnerIdAndDeletedAtIsNull(Long ownerId);

    List<Vehicle> findByOwnerIdAndDeletedAtIsNullOrderByVehicleNumberAsc(Long ownerId);
}
