package com.hms.guests.service;

import com.hms.guests.entity.Guest;
import com.hms.guests.repository.GuestRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    // New method to get the last guest ID
    public Long getLastGuestId() {
        return guestRepository.findTopByOrderByIdDesc()
                            .map(Guest::getId)
                            .orElse(0L);
    }

    public Guest addGuest(Guest guest) {
        if (guest.getCode() == null || guest.getCode().isEmpty()) {
            Long nextId = getLastGuestId() + 1;
            guest.setCode("G" + String.format("%03d", nextId)); // Format still works with Long
        }

        Optional<Guest> existingGuest = guestRepository.findByCode(guest.getCode());
        if (existingGuest.isPresent()) {
            throw new GuestAlreadyExistsException("Guest with code " + guest.getCode() + " already exists");
        }
        return guestRepository.save(guest);
    }

    public List<Guest> getAllGuests() {
        return new ArrayList<>(guestRepository.findAll());
    }

    public Guest getGuestById(Long id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException("Guest with ID " + id + " not found"));
    }

    // ✅ New: Get guest by phone number
    public Guest getGuestByPhoneNumber(String phoneNumber) {
        return guestRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new GuestNotFoundException("Guest with phone number " + phoneNumber + " not found"));
    }
    
    public Guest getGuestByCode(String code) {
        return guestRepository.findByCode(code)
                .orElseThrow(() -> new GuestNotFoundException("Guest with code " + code + " not found"));
    }

    public Guest updateGuest(Long id, Guest guest) {
        if (!guestRepository.existsById(id)) {
            throw new GuestNotFoundException("Guest with ID " + id + " not found");
        }
        guest.setId(id);
        return guestRepository.save(guest);
    }

    public void deleteGuest(Long id) {
        if (!guestRepository.existsById(id)) {
            throw new GuestNotFoundException("Guest with ID " + id + " not found");
        }
        guestRepository.deleteById(id);
    }

    // --- Custom Exceptions ---

    public static class GuestNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public GuestNotFoundException(String message) {
            super(message);
        }
    }

    public static class GuestAlreadyExistsException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public GuestAlreadyExistsException(String message) {
            super(message);
        }
    }
}
