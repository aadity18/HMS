package com.hms.staff.repository;

import com.hms.staff.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByCode(String code);
    Optional<Staff> findTopByOrderByIdDesc(); // New method for getting last staff
}