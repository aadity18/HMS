import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

interface Staff {
  id: number;
  code: string;
  name: string;
  address: string;
  nic: string;
  salary: number;
  age: number;
  occupation: string;
  email: string;
}

@Component({
  selector: 'app-staff-list',
  imports: [CommonModule,FormsModule],
  templateUrl: './staff-list.component.html',
  styleUrls: ['./staff-list.component.css']
})
export class StaffListComponent implements OnInit {
  staffList: Staff[] = [];
  loading = true;
  error = '';
  role: string = '';
  searchCode: string = '';

  private BASE_URL = 'http://localhost:8080/api/staff';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.role = localStorage.getItem('userRole') || '';
    this.fetchStaff();
  }

  fetchStaff(): void {
    this.loading = true;
    this.error = '';
    this.http.get<Staff[]>(`${this.BASE_URL}/all`).subscribe({
      next: (data) => {
        this.staffList = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load staff data.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  searchByCode(): void {
    if (!this.searchCode.trim()) {
      this.fetchStaff();
      return;
    }
    this.loading = true;
    this.error = '';
    this.http.get<Staff>(`${this.BASE_URL}/code/${this.searchCode}`).subscribe({
      next: (data) => {
        this.staffList = [data];
        this.loading = false;
      },
      error: (err) => {
        this.staffList = [];
        this.error = `Staff with code "${this.searchCode}" not found.`;
        this.loading = false;
        console.error(err);
      }
    });
  }

  editStaff(id: number): void {
    this.router.navigate(['/staff/edit', id]);
  }

  deleteStaff(id: number): void {
    if (this.role !== 'OWNER') {
      alert('Only the owner can delete staff members.');
      return;
    }

    if (!confirm('Are you sure you want to delete this staff member?')) return;

    this.http.delete(`${this.BASE_URL}/${id}`, { responseType: 'text' }).subscribe({
      next: () => {
        this.staffList = this.staffList.filter(staff => staff.id !== id);
      },
      error: (err) => {
        alert('Failed to delete staff member.');
        console.error(err);
      }
    });
  }
}
