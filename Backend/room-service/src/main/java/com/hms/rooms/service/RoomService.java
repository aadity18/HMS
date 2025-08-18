package com.hms.rooms.service;

import com.hms.rooms.entity.Room;
import com.hms.rooms.exception.RoomNotFoundException;
import com.hms.rooms.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room addRoom(Room room) {
        Optional<Room> existingRoom = roomRepository.findByCode(room.getCode());
        if (existingRoom.isPresent()) {
            throw new RuntimeException("Room with code " + room.getCode() + " already exists");
        }
        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(roomRepository.findAll());
    }

    public Room getRoomById(Long id) {
        Optional<Room> room = roomRepository.findById(id);
        if (room.isPresent()) {
            return room.get();
        }
        throw new RoomNotFoundException("Room with ID " + id + " not found");
    }

    public Room getRoomByCode(String code) {
        Optional<Room> room = roomRepository.findByCode(code);
        if (room.isPresent()) {
            return room.get();
        }
        throw new RoomNotFoundException("Room with code " + code + " not found");
    }

    public Room updateRoom(Long id, Room room) {
        Optional<Room> existingRoom = roomRepository.findById(id);
        if (existingRoom.isPresent()) {
            room.setId(id);
            return roomRepository.save(room);
        }
        throw new RoomNotFoundException("Room with ID " + id + " not found");
    }

    public void deleteRoom(Long id) {
        Optional<Room> room = roomRepository.findById(id);
        if (room.isPresent()) {
            roomRepository.deleteById(id);
        } else {
            throw new RoomNotFoundException("Room with ID " + id + " not found");
        }
    }

    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : roomRepository.findAll()) {
            if ("Available".equals(room.getStatus())) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    public Room setRate(Long id, Double rate) {
        Optional<Room> room = roomRepository.findById(id);
        if (room.isPresent()) {
            Room r = room.get();
            r.setPrice(rate);
            return roomRepository.save(r);
        }
        throw new RoomNotFoundException("Room with ID " + id + " not found");
    }
}
