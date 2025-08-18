package com.hms.rooms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.rooms.entity.Room;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
	Optional<Room> findByCode(String code);
	
}