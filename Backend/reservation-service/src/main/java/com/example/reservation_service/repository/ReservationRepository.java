package com.example.reservation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Query;

import com.example.reservation_service.model.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	 List<Reservation> findByGuestId(Long guestId);
	 
	 @Query("SELECT r FROM Reservation r " +
		       "WHERE r.roomCode = :roomCode " +
		       "AND r.checkOutDate > :checkInDate " +
		       "AND r.checkInDate < :checkOutDate " +
		       "AND (r.status = 'PENDING' OR r.status = 'BOOKED')")
		List<Reservation> findOverlappingReservations(
		    @Param("roomCode") String roomCode,
		    @Param("checkInDate") LocalDate checkInDate,
		    @Param("checkOutDate") LocalDate checkOutDate
		);
	 Optional<Reservation> findTopByOrderByIdDesc(); // New method for getting last staff
	 
	 Optional<Reservation> findByCode(String code);

}
