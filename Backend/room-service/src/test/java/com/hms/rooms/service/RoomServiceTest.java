package com.hms.rooms.service;

import com.hms.rooms.entity.Room;
import com.hms.rooms.exception.RoomNotFoundException;
import com.hms.rooms.repository.RoomRepository;
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
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private Room sampleRoom;

    @BeforeEach
    void setUp() {
        sampleRoom = new Room();
        sampleRoom.setId(1L);
        sampleRoom.setCode("R101");
        sampleRoom.setStatus("Available");
        sampleRoom.setPrice(100.0);
    }

    /* ---------- addRoom ---------- */

    @Test
    @DisplayName("addRoom() saves new room when code is unique")
    void addRoom_success() {
        Room newRoom = new Room();
        newRoom.setCode("R102");
        given(roomRepository.findByCode("R102")).willReturn(Optional.empty());
        given(roomRepository.save(newRoom)).willReturn(newRoom);

        Room saved = roomService.addRoom(newRoom);

        assertThat(saved).isSameAs(newRoom);
    }

    @Test
    @DisplayName("addRoom() throws when code already exists")
    void addRoom_duplicateCode_throws() {
        Room room = new Room();
        room.setCode("R101");
        given(roomRepository.findByCode("R101")).willReturn(Optional.of(sampleRoom));

        assertThatThrownBy(() -> roomService.addRoom(room))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Room with code R101 already exists");
    }

    /* ---------- getAllRooms ---------- */

    @Test
    @DisplayName("getAllRooms() returns list from repository")
    void getAllRooms() {
        given(roomRepository.findAll()).willReturn(List.of(sampleRoom));

        List<Room> rooms = roomService.getAllRooms();

        assertThat(rooms).containsExactly(sampleRoom);
    }

    /* ---------- getRoomById ---------- */

    @Test
    @DisplayName("getRoomById() returns room when found")
    void getRoomById_found() {
        given(roomRepository.findById(1L)).willReturn(Optional.of(sampleRoom));

        Room room = roomService.getRoomById(1L);

        assertThat(room).isEqualTo(sampleRoom);
    }

    @Test
    @DisplayName("getRoomById() throws when not found")
    void getRoomById_notFound() {
        given(roomRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoomById(99L))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("Room with ID 99 not found");
    }

    /* ---------- getRoomByCode ---------- */

    @Test
    @DisplayName("getRoomByCode() returns room when found")
    void getRoomByCode_found() {
        given(roomRepository.findByCode("R101")).willReturn(Optional.of(sampleRoom));

        Room room = roomService.getRoomByCode("R101");

        assertThat(room).isEqualTo(sampleRoom);
    }

    @Test
    @DisplayName("getRoomByCode() throws when not found")
    void getRoomByCode_notFound() {
        given(roomRepository.findByCode("R999")).willReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoomByCode("R999"))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("Room with code R999 not found");
    }

    /* ---------- updateRoom ---------- */

    @Test
    @DisplayName("updateRoom() saves and returns updated room")
    void updateRoom_success() {
        Room updateData = new Room();
        updateData.setCode("R101-updated");

        given(roomRepository.findById(1L)).willReturn(Optional.of(sampleRoom));
        given(roomRepository.save(any(Room.class))).willAnswer(invocation -> {
            Room merged = invocation.getArgument(0);
            return merged;   // merged already contains the new values
        });

        Room updated = roomService.updateRoom(1L, updateData);

        assertThat(updated.getCode()).isEqualTo("R101-updated");
    }

    @Test
    @DisplayName("updateRoom() throws when room does not exist")
    void updateRoom_notFound() {
        given(roomRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.updateRoom(99L, new Room()))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("Room with ID 99 not found");
    }

    /* ---------- deleteRoom ---------- */

    @Test
    @DisplayName("deleteRoom() deletes when room exists")
    void deleteRoom_success() {
        given(roomRepository.findById(1L)).willReturn(Optional.of(sampleRoom));

        roomService.deleteRoom(1L);

        then(roomRepository).should().deleteById(1L);
    }

    @Test
    @DisplayName("deleteRoom() throws when room does not exist")
    void deleteRoom_notFound() {
        given(roomRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.deleteRoom(99L))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("Room with ID 99 not found");
    }

    /* ---------- getAvailableRooms ---------- */

    @Test
    @DisplayName("getAvailableRooms() returns only rooms with status 'Available'")
    void getAvailableRooms() {
        Room occupied = new Room();
        occupied.setStatus("Occupied");

        given(roomRepository.findAll()).willReturn(List.of(sampleRoom, occupied));

        List<Room> available = roomService.getAvailableRooms();

        assertThat(available).containsExactly(sampleRoom);
    }

    /* ---------- setRate ---------- */

    @Test
    @DisplayName("setRate() updates price and returns updated room")
    void setRate_success() {
        given(roomRepository.findById(1L)).willReturn(Optional.of(sampleRoom));
        given(roomRepository.save(sampleRoom)).willReturn(sampleRoom);

        Room updated = roomService.setRate(1L, 150.0);

        assertThat(updated.getPrice()).isEqualTo(150.0);
    }

    @Test
    @DisplayName("setRate() throws when room does not exist")
    void setRate_notFound() {
        given(roomRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.setRate(99L, 99.0))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("Room with ID 99 not found");
    }
}