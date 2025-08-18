import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-room-list',
  templateUrl: './room-list.component.html',
  styleUrls: ['./room-list.component.css'],
  imports: [CommonModule, FormsModule]
})
export class RoomListComponent implements OnInit {
  rooms: any[] = [];
  loading = true;
  error = '';
  role: string = '';
  searchCode: string = '';

  private BASE_URL = 'http://localhost:8080/api/rooms';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.role = localStorage.getItem('userRole') || 'RECEPTIONIST';
    this.loadRooms();
  }

  loadRooms() {
    this.loading = true;
    this.error = '';
    this.http.get<any[]>(this.BASE_URL).subscribe({
      next: (data) => {
        this.rooms = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load rooms';
        this.loading = false;
      }
    });
  }

  getRoomByCode() {
    if (!this.searchCode.trim()) {
      this.loadRooms();
      return;
    }
    this.loading = true;
    this.error = '';
    this.http.get<any>(`${this.BASE_URL}/code/${this.searchCode}`).subscribe({
      next: (data) => {
        this.rooms = [data];
        this.loading = false;
      },
      error: () => {
        this.error = `Room with code "${this.searchCode}" not found.`;
        this.rooms = [];
        this.loading = false;
      }
    });
  }

  goToEdit(id: string) {
    this.router.navigate(['/rooms/edit', id]);
  }

  deleteRoom(id: string) {
    if (this.role !== 'OWNER') {
      alert('Only the owner can delete rooms.');
      return;
    }

    if (!confirm('Are you sure you want to delete this room?')) {
      return;
    }

    this.http.delete(`${this.BASE_URL}/${id}`).subscribe({
      next: () => {
        this.rooms = this.rooms.filter(room => room.id !== id);
      },
      error: () => {
        alert('Failed to delete the room.');
      }
    });
  }
}
