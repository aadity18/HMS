import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-room-edit',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './room-edit.component.html',
  styleUrls: ['./room-edit.component.css']
})
export class RoomEditComponent implements OnInit {
  roomForm: FormGroup;
  roomId: string = '';
  roomTypes: string[] = ['SINGLE', 'DOUBLE', 'SUITE'];
  statusOptions: string[] = ['AVAILABLE', 'OCCUPIED', 'MAINTENANCE'];

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.roomForm = this.fb.group({
      id: [{ value: '', disabled: true }],
      code: ['', Validators.required],
      roomType: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(0)]],
      capacity: ['', [Validators.required, Validators.min(1)]],
      status: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.roomId = this.route.snapshot.paramMap.get('id') || '';
    this.loadRoomDetails();
  }

  loadRoomDetails(): void {
    this.http.get<any>(`http://localhost:8080/api/rooms/${this.roomId}`).subscribe({
      next: (data) => {
        this.roomForm.patchValue({
          id: data.id,
          code: data.code,
          roomType: data.roomType,
          price: data.price,
          capacity: data.capacity,
          status: data.status
        });
      },
      error: () => {
        alert('Failed to load room details.');
      }
    });
  }

  onSubmit(): void {
    if (this.roomForm.valid) {
      const updatedRoom = {
        ...this.roomForm.getRawValue() // includes disabled ID field
      };
      this.http.put(`http://localhost:8080/api/rooms/${this.roomId}`, updatedRoom).subscribe({
        next: () => {
          alert('Room updated successfully!');
          this.router.navigate(['/rooms']);
        },
        error: (err) => {
          console.error('Error updating room:', err);
          alert('Update failed. Please try again.');
        }
      });
    }
  }
}
