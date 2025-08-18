package com.hms.staff.service;

import com.hms.staff.entity.Staff;
import com.hms.staff.exception.StaffAlreadyExistsException;
import com.hms.staff.exception.StaffNotFoundException;
import com.hms.staff.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    // New method to get last staff ID
    public Long getLastStaffId() {
        return staffRepository.findTopByOrderByIdDesc()
                            .map(Staff::getId)
                            .orElse(0L); // Default to 0 if no staff exists
    }

    // Updated addStaff to auto-generate code if not provided
    public Staff addStaff(Staff staff) {
        // Auto-generate code if empty (e.g., "S006")
        if (staff.getCode() == null || staff.getCode().isEmpty()) {
            Long nextId = getLastStaffId() + 1;
            staff.setCode("S" + String.format("%03d", nextId));
        }

        Optional<Staff> existingStaff = staffRepository.findByCode(staff.getCode());
        if (existingStaff.isPresent()) {
            throw new StaffAlreadyExistsException("Staff with code " + staff.getCode() + " already exists");
        }
        return staffRepository.save(staff);
    }

    public List<Staff> getAllStaff() {
        return new ArrayList<>(staffRepository.findAll());
    }

    public Staff getStaffById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new StaffNotFoundException("Staff with ID " + id + " not found"));
    }

    public Staff getStaffByCode(String code) {
        return staffRepository.findByCode(code)
                .orElseThrow(() -> new StaffNotFoundException("Staff with code " + code + " not found"));
    }

    public Staff updateStaff(Long id, Staff staff) {
        if (!staffRepository.existsById(id)) {
            throw new StaffNotFoundException("Staff with ID " + id + " not found");
        }
        staff.setId(id);
        return staffRepository.save(staff);
    }

    public void deleteStaff(Long id) {
        if (!staffRepository.existsById(id)) {
            throw new StaffNotFoundException("Staff with ID " + id + " not found");
        }
        staffRepository.deleteById(id);
    }
}
