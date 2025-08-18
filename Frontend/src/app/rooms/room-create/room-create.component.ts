import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-room-create',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './room-create.component.html',
  styleUrls: ['./room-create.component.css']
})
export class RoomCreateComponent {
  roomForm: FormGroup;
  roomTypes: string[] = ['SINGLE', 'DOUBLE', 'SUITE', 'DELUXE'];
  statuses: string[] = ['Available', 'Booked', 'Maintenance'];

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.roomForm = this.fb.group({
      code: ['', Validators.required],
      roomType: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(0)]],
      capacity: ['', [Validators.required, Validators.min(1)]],
      status: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.roomForm.valid) {
      this.http.post('http://localhost:8080/api/rooms', this.roomForm.value).subscribe({
        next: () => {
          alert('Room created successfully!');
          this.router.navigate(['/rooms']); // Navigate to rooms list
        },
        error: (err) => {
          console.error('Failed to create room:', err);
          alert('Something went wrong. Please try again.');
        }
      });
    }
  }
}
