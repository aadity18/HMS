package com.hms.staff.service;

import com.hms.staff.entity.Staff;
import com.hms.staff.exception.StaffAlreadyExistsException;
import com.hms.staff.exception.StaffNotFoundException;
import com.hms.staff.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private StaffService staffService;

    private Staff sampleStaff;

    @BeforeEach
    void setUp() {
        sampleStaff = new Staff();
        sampleStaff.setId(1L);
        sampleStaff.setCode("S001");
        sampleStaff.setName("Alice Smith");
        sampleStaff.setEmail("alice@mail.com");
    }

    /* ---------- getLastStaffId ---------- */

    @Test
    @DisplayName("getLastStaffId() returns 0 when no staff exist")
    void getLastStaffId_emptyRepo_returnsZero() {
        given(staffRepository.findTopByOrderByIdDesc()).willReturn(Optional.empty());

        Long lastId = staffService.getLastStaffId();

        assertThat(lastId).isZero();
    }

    @Test
    @DisplayName("getLastStaffId() returns highest id")
    void getLastStaffId_whenStaffExist_returnsHighestId() {
        Staff last = new Staff();
        last.setId(42L);
        given(staffRepository.findTopByOrderByIdDesc()).willReturn(Optional.of(last));

        Long lastId = staffService.getLastStaffId();

        assertThat(lastId).isEqualTo(42L);
    }

    /* ---------- addStaff ---------- */

    @Test
    @DisplayName("addStaff() auto-generates code when none provided")
    void addStaff_generatesCode() {
        Staff input = new Staff();
        input.setName("Bob");
        given(staffRepository.findTopByOrderByIdDesc()).willReturn(Optional.empty()); // last id = 0
        given(staffRepository.findByCode("S001")).willReturn(Optional.empty());
        given(staffRepository.save(any(Staff.class))).willAnswer(inv -> inv.getArgument(0));

        Staff saved = staffService.addStaff(input);

        assertThat(saved.getCode()).isEqualTo("S001");
        then(staffRepository).should().save(input);
    }

    @Test
    @DisplayName("addStaff() throws when code already exists")
    void addStaff_duplicateCode_throws() {
        Staff staff = new Staff();
        staff.setCode("S001");
        given(staffRepository.findByCode("S001")).willReturn(Optional.of(sampleStaff));

        assertThatThrownBy(() -> staffService.addStaff(staff))
                .isInstanceOf(StaffAlreadyExistsException.class)
                .hasMessage("Staff with code S001 already exists");
    }

    /* ---------- getAllStaff ---------- */

    @Test
    @DisplayName("getAllStaff() returns list from repository")
    void getAllStaff() {
        given(staffRepository.findAll()).willReturn(List.of(sampleStaff));

        List<Staff> staffList = staffService.getAllStaff();

        assertThat(staffList).containsExactly(sampleStaff);
    }

    /* ---------- getStaffById ---------- */

    @Test
    @DisplayName("getStaffById() returns staff when found")
    void getStaffById_found() {
        given(staffRepository.findById(1L)).willReturn(Optional.of(sampleStaff));

        Staff staff = staffService.getStaffById(1L);

        assertThat(staff).isEqualTo(sampleStaff);
    }

    @Test
    @DisplayName("getStaffById() throws when not found")
    void getStaffById_notFound() {
        given(staffRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.getStaffById(99L))
                .isInstanceOf(StaffNotFoundException.class)
                .hasMessage("Staff with ID 99 not found");
    }

    /* ---------- getStaffByCode ---------- */

    @Test
    @DisplayName("getStaffByCode() returns staff when found")
    void getStaffByCode_found() {
        given(staffRepository.findByCode("S001")).willReturn(Optional.of(sampleStaff));

        Staff staff = staffService.getStaffByCode("S001");

        assertThat(staff).isEqualTo(sampleStaff);
    }

    @Test
    @DisplayName("getStaffByCode() throws when not found")
    void getStaffByCode_notFound() {
        given(staffRepository.findByCode("S999")).willReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.getStaffByCode("S999"))
                .isInstanceOf(StaffNotFoundException.class)
                .hasMessage("Staff with code S999 not found");
    }

    /* ---------- updateStaff ---------- */

    @Test
    @DisplayName("updateStaff() saves and returns updated staff")
    void updateStaff_success() {
        Staff updateData = new Staff();
        updateData.setName("Updated Name");

        given(staffRepository.existsById(1L)).willReturn(true);
        given(staffRepository.save(any(Staff.class))).willAnswer(inv -> inv.getArgument(0));

        Staff updated = staffService.updateStaff(1L, updateData);

        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("updateStaff() throws when id does not exist")
    void updateStaff_notFound() {
        given(staffRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> staffService.updateStaff(99L, new Staff()))
                .isInstanceOf(StaffNotFoundException.class)
                .hasMessage("Staff with ID 99 not found");
    }

    /* ---------- deleteStaff ---------- */

    @Test
    @DisplayName("deleteStaff() deletes when staff exists")
    void deleteStaff_success() {
        given(staffRepository.existsById(1L)).willReturn(true);

        staffService.deleteStaff(1L);

        then(staffRepository).should().deleteById(1L);
    }

    @Test
    @DisplayName("deleteStaff() throws when staff does not exist")
    void deleteStaff_notFound() {
        given(staffRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> staffService.deleteStaff(99L))
                .isInstanceOf(StaffNotFoundException.class)
                .hasMessage("Staff with ID 99 not found");
    }
}