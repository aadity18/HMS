import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-guest-edit',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './guest-edit.component.html',
  styleUrls: ['./guest-edit.component.css']
})
export class GuestEditComponent implements OnInit {
  guestForm!: FormGroup;
  id!: string;
  submitting = false;
  error: string | null = null;
  private readonly apiUrl = 'http://localhost:8080/api/guests';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id') || '';
    this.guestForm = this.fb.group({
      memberCode: ['', [Validators.required, Validators.maxLength(20)]],
      name: ['', [Validators.required, Validators.minLength(3)]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^\+?[0-9]{7,15}$/)]],
      company: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      gender: ['', Validators.required],
      address: ['', [Validators.required, Validators.maxLength(200)]]
    });

    this.loadGuest();
  }

loadGuest(): void {
  this.http.get<any>(`${this.apiUrl}/get/${this.id}`).subscribe({
    next: data => this.guestForm.patchValue(data),
    error: err => {
      this.error = 'Failed to load guest details.';
      console.error('Error loading guest:', err);
    }
  });
}


  onSubmit(): void {
    if (this.guestForm.invalid) {
      this.guestForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.http.put(`${this.apiUrl}/edit/${this.id}`, this.guestForm.value).subscribe({
      next: () => {
        this.submitting = false;
        alert('Guest details updated successfully');
        this.router.navigate(['/guests']);
      },
      error: err => {
        this.submitting = false;
        this.error = 'Failed to update guest. Please try again.';
        alert(this.error);
        console.error('Update error:', err);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/guests']);
  }
}
