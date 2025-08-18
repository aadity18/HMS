import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RoomSelectionService } from '../../rooms/room-selection.service'; // <-- Add this import

interface Guest {
  id: number;
  code: string;
  name: string;
  phoneNumber: string;
  email: string;
  gender: string;
  address: string;
}

@Component({
  selector: 'app-guest-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './guest-list.component.html',
  styleUrls: ['./guest-list.component.css'],
})
export class GuestListComponent implements OnInit {
  guests: Guest[] = [];
  loading = true;
  error: string | null = null;
  role: string = '';
  searchPhone: string = '';

  constructor(
    private http: HttpClient,
    private router: Router,
    public roomSelection: RoomSelectionService // <-- Inject the service
  ) {}

  ngOnInit(): void {
    this.fetchGuests();
    this.role = localStorage.getItem('userRole') || '';
  }

  fetchGuests(): void {
    this.loading = true;
    this.error = null;
    this.http.get<Guest[]>('http://localhost:8080/api/guests/get').subscribe({
      next: (data) => {
        this.guests = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load guests.';
        this.loading = false;
      },
    });
  }

  // ✅ Search by phone number
  getGuestByPhone(): void {
    const phone = (this.searchPhone || '').trim();
    if (!phone) {
      this.fetchGuests();
      return;
    }
    this.loading = true;
    this.error = null;
    this.http.get<Guest>(`http://localhost:8080/api/guests/get/phone/${phone}`).subscribe({
      next: (guest) => {
        this.guests = [guest];
        this.loading = false;
      },
      error: () => {
        this.error = `No guest found with phone number ${phone}.`;
        this.guests = [];
        this.loading = false;
      },
    });
  }

  editGuest(id: number): void {
    this.router.navigate(['/guests/edit', id]); // assumes you have a route like /guests/edit/:id
  }

  deleteGuest(id: number): void {
    if (confirm('Are you sure you want to delete this guest?')) {
      this.http
        .delete(`http://localhost:8080/api/guests/${id}`, {
          responseType: 'text',
        })
        .subscribe({
          next: () => {
            this.guests = this.guests.filter((g) => g.id !== id);
          },
          error: () => {
            alert('Failed to delete guest.');
          },
        });
    }
  }

  onSelectGuest(guest: any) {
    this.roomSelection.setGuestCode(guest.code);
    this.router.navigate(['/reservations/create']);
  }
}
