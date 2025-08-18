import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RoomSelectionService } from '../../rooms/room-selection.service';

@Component({
  selector: 'app-guest-create',
  imports:[CommonModule,ReactiveFormsModule],
  templateUrl: './guest-create.component.html',
  styleUrls: ['./guest-create.component.css']
})
export class GuestCreateComponent implements OnInit {
  guestForm!: FormGroup;
  submitting = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private roomSelection: RoomSelectionService // Injecting the service here
  ) {}

  ngOnInit(): void {
    this.guestForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^\+?[0-9]{7,15}$/)]],
      email: ['', [Validators.required, Validators.email]],
      gender: ['', Validators.required],
      address: ['', [Validators.required, Validators.maxLength(200)]]  // fixed validators here
    });
  }

  onSubmit(): void {
    this.error = null;

    if (this.guestForm.invalid) {
      this.guestForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.http.post('http://localhost:8080/api/guests/add', this.guestForm.value).subscribe({
      next: (newGuest) => {
        this.submitting = false;
        this.onGuestCreated(newGuest); // Call the new method on successful guest creation
      },
      error: (err) => {
        this.submitting = false;
        this.error = 'Failed to create guest. Please try again.';
        console.error('Guest creation error:', err);
      }
    });
  }

  onGuestCreated(newGuest: any) {
    this.roomSelection.setGuestCode(newGuest.code);
    this.router.navigate(['/reservations/create']);
  }

  goBack(): void {
    this.router.navigate(['/guests']);
  }
}
