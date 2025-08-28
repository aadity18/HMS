package com.hms.guests.service;

import com.hms.guests.entity.Guest;
import com.hms.guests.repository.GuestRepository;
import com.hms.guests.service.GuestService.GuestAlreadyExistsException;
import com.hms.guests.service.GuestService.GuestNotFoundException;
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
class GuestServiceTest {

    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private GuestService guestService;

    private Guest sampleGuest;

    @BeforeEach
    void setUp() {
        sampleGuest = new Guest(
                1L,
                "G001",
                "1234567890",
                "Alice Smith",
                "alice@mail.com",
                "Female",
                "123 Main St"
        );
    }

    /* ---------- getLastGuestId ---------- */

    @Test
    @DisplayName("getLastGuestId() returns 0 when no guests exist")
    void getLastGuestId_emptyRepo_returnsZero() {
        given(guestRepository.findTopByOrderByIdDesc()).willReturn(Optional.empty());

        Long lastId = guestService.getLastGuestId();

        assertThat(lastId).isZero();
    }

    @Test
    @DisplayName("getLastGuestId() returns the highest id")
    void getLastGuestId_whenGuestsExist_returnsHighestId() {
        Guest lastGuest = new Guest(42L, null, null, null, null, null, null);
        given(guestRepository.findTopByOrderByIdDesc()).willReturn(Optional.of(lastGuest));

        Long lastId = guestService.getLastGuestId();

        assertThat(lastId).isEqualTo(42L);
    }

    /* ---------- addGuest ---------- */

    @Test
    @DisplayName("addGuest() generates code when none provided")
    void addGuest_generatesCode() {
        Guest input = new Guest();
        input.setName("Bob");
        given(guestRepository.findTopByOrderByIdDesc()).willReturn(Optional.empty()); // last id = 0
        given(guestRepository.findByCode("G001")).willReturn(Optional.empty());
        given(guestRepository.save(any(Guest.class))).willAnswer(inv -> inv.getArgument(0));

        Guest saved = guestService.addGuest(input);

        assertThat(saved.getCode()).isEqualTo("G001");
        then(guestRepository).should().save(input);
    }

    @Test
    @DisplayName("addGuest() throws when code already exists")
    void addGuest_duplicateCode_throws() {
        Guest input = new Guest();
        input.setCode("G001");
        given(guestRepository.findByCode("G001")).willReturn(Optional.of(sampleGuest));

        assertThatThrownBy(() -> guestService.addGuest(input))
                .isInstanceOf(GuestAlreadyExistsException.class)
                .hasMessageContaining("G001");

        then(guestRepository).should(never()).save(any());
    }

    /* ---------- getAllGuests ---------- */

    @Test
    @DisplayName("getAllGuests() returns list from repository")
    void getAllGuests() {
        given(guestRepository.findAll()).willReturn(List.of(sampleGuest));

        List<Guest> guests = guestService.getAllGuests();

        assertThat(guests).containsExactly(sampleGuest);
    }

    /* ---------- getGuestById ---------- */

    @Test
    @DisplayName("getGuestById() returns guest when found")
    void getGuestById_found() {
        given(guestRepository.findById(1L)).willReturn(Optional.of(sampleGuest));

        Guest found = guestService.getGuestById(1L);

        assertThat(found).isEqualTo(sampleGuest);
    }

    @Test
    @DisplayName("getGuestById() throws when not found")
    void getGuestById_notFound() {
        given(guestRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> guestService.getGuestById(99L))
                .isInstanceOf(GuestNotFoundException.class)
                .hasMessage("Guest with ID 99 not found");
    }

    /* ---------- getGuestByPhoneNumber ---------- */

    @Test
    @DisplayName("getGuestByPhoneNumber() returns guest when found")
    void getGuestByPhoneNumber_found() {
        given(guestRepository.findByPhoneNumber("1234567890"))
                .willReturn(Optional.of(sampleGuest));

        Guest guest = guestService.getGuestByPhoneNumber("1234567890");

        assertThat(guest).isEqualTo(sampleGuest);
    }

    @Test
    @DisplayName("getGuestByPhoneNumber() throws when not found")
    void getGuestByPhoneNumber_notFound() {
        given(guestRepository.findByPhoneNumber("000")).willReturn(Optional.empty());

        assertThatThrownBy(() -> guestService.getGuestByPhoneNumber("000"))
                .isInstanceOf(GuestNotFoundException.class)
                .hasMessage("Guest with phone number 000 not found");
    }

    /* ---------- getGuestByCode ---------- */

    @Test
    @DisplayName("getGuestByCode() returns guest when found")
    void getGuestByCode_found() {
        given(guestRepository.findByCode("G001")).willReturn(Optional.of(sampleGuest));

        Guest guest = guestService.getGuestByCode("G001");

        assertThat(guest).isEqualTo(sampleGuest);
    }

    @Test
    @DisplayName("getGuestByCode() throws when not found")
    void getGuestByCode_notFound() {
        given(guestRepository.findByCode("G999")).willReturn(Optional.empty());

        assertThatThrownBy(() -> guestService.getGuestByCode("G999"))
                .isInstanceOf(GuestNotFoundException.class)
                .hasMessage("Guest with code G999 not found");
    }

    /* ---------- updateGuest ---------- */

    @Test
    @DisplayName("updateGuest() saves and returns updated guest")
    void updateGuest_success() {
        Guest updateData = new Guest();
        updateData.setName("Updated Name");

        given(guestRepository.existsById(1L)).willReturn(true);
        given(guestRepository.save(any(Guest.class))).willAnswer(inv -> inv.getArgument(0));

        Guest updated = guestService.updateGuest(1L, updateData);

        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("updateGuest() throws when id does not exist")
    void updateGuest_notFound() {
        given(guestRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> guestService.updateGuest(99L, new Guest()))
                .isInstanceOf(GuestNotFoundException.class)
                .hasMessage("Guest with ID 99 not found");
    }

    /* ---------- deleteGuest ---------- */

    @Test
    @DisplayName("deleteGuest() deletes when guest exists")
    void deleteGuest_success() {
        given(guestRepository.existsById(1L)).willReturn(true);

        guestService.deleteGuest(1L);

        then(guestRepository).should().deleteById(1L);
    }

    @Test
    @DisplayName("deleteGuest() throws when guest does not exist")
    void deleteGuest_notFound() {
        given(guestRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> guestService.deleteGuest(99L))
                .isInstanceOf(GuestNotFoundException.class)
                .hasMessage("Guest with ID 99 not found");
    }
}