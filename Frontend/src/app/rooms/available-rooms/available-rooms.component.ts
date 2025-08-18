import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RoomSelectionService } from '../room-selection.service';

@Component({
  selector: 'app-available-rooms',
  imports: [CommonModule, FormsModule],
  templateUrl: './available-rooms.component.html',
  styleUrls: ['./available-rooms.component.css']
})
export class AvailableRoomsComponent {
  checkInDate: string = '';
  checkOutDate: string = '';
  rooms: any[] = [];
  error: string = '';
  loading = false;
  roomsSearched = false;
  selectedRoomCode: string | null = null;
  today: string = '';

  // ✅ Fixed endpoint
  private AVAILABLE_ROOMS_URL = 'http://localhost:8080/api/reservations/available-rooms';

  constructor(
    private http: HttpClient,
    private router: Router,
    private roomSelection: RoomSelectionService
  ) {
    const now = new Date();
    const yyyy = now.getFullYear();
    const mm = String(now.getMonth() + 1).padStart(2, '0');
    const dd = String(now.getDate()).padStart(2, '0');
    this.today = `${yyyy}-${mm}-${dd}`;
  }

  fetchAvailableRooms() {
    this.error = '';
    this.roomsSearched = false;

    const todayDate = new Date(this.today);
    const checkIn = this.checkInDate ? new Date(this.checkInDate) : null;
    const checkOut = this.checkOutDate ? new Date(this.checkOutDate) : null;

    if (!this.checkInDate || !this.checkOutDate) {
      this.error = 'Please select both check-in and check-out dates.';
      return;
    }
    if ((checkIn && checkIn < todayDate) || (checkOut && checkOut < todayDate)) {
      this.error = 'Check-in and check-out dates cannot be in the past.';
      return;
    }

    this.loading = true;

    // ✅ Call correct backend endpoint
    this.http.get<any[]>(`${this.AVAILABLE_ROOMS_URL}?checkInDate=${this.checkInDate}&checkOutDate=${this.checkOutDate}`)
      .subscribe({
        next: (availableRooms) => {
          this.rooms = availableRooms;
          this.loading = false;
          this.roomsSearched = true;
        },
        error: (err) => {
          console.error(err);
          this.error = 'Error fetching available rooms.';
          this.loading = false;
          this.roomsSearched = true;
        }
      });
  }

  selectGuest(room: any, event: MouseEvent) {
    event.stopPropagation();
    this.roomSelection.setRoomCode(room.code);
    this.roomSelection.clearGuestCode();
    this.roomSelection.setCheckInDate(this.checkInDate);
    this.roomSelection.setCheckOutDate(this.checkOutDate);
    this.router.navigate(['/guests']);
    this.selectedRoomCode = null;
  }

  addGuest(room: any, event: MouseEvent) {
    event.stopPropagation();
    this.roomSelection.setRoomCode(room.code);
    this.roomSelection.clearGuestCode();
    this.roomSelection.setCheckInDate(this.checkInDate);
    this.roomSelection.setCheckOutDate(this.checkOutDate);
    this.router.navigate(['/guests/create']);
    this.selectedRoomCode = null;
  }

  showGuestOptions(room: any, event: MouseEvent) {
    event.stopPropagation();
    this.selectedRoomCode = room.code;
  }

  cancelGuestOptions(event: MouseEvent) {
    event.stopPropagation();
    this.selectedRoomCode = null;
  }
}
