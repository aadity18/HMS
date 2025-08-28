package com.hms.guests.repository;

import com.hms.guests.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    Optional<Guest> findByCode(String code);
    Optional<Guest> findByPhoneNumber(String code);
    Optional<Guest> findTopByOrderByIdDesc();
}