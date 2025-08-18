import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-staff-create',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './staff-create.component.html',
  styleUrls: ['./staff-create.component.css'],
})
export class StaffCreateComponent implements OnInit {
  staffForm!: FormGroup;
  submitted = false;

  staffRoles: string[] = [
    'MANAGER',
    'RECEPTIONIST',
    'HOUSEKEEPING',
    'CHEF',
    'WAITER',
    'SECURITY',
    'MAINTENANCE',
    'ACCOUNTANT',
    'BELLBOY',
    'OWNER'
  ];

  currentUserRole: string = '';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUserRole = localStorage.getItem('userRole') || '';
    this.staffForm = this.fb.group({
      staffCode: [''], // optional, backend auto-generates if empty
      name: ['', [Validators.required, Validators.minLength(3)]],
      address: ['', [Validators.required]],
      nic: ['', [Validators.required]],
      salary: [0, [Validators.required, Validators.min(0)]],
      age: [18, [Validators.required, Validators.min(18)]],
      occupation: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
    });
  }

  get f() {
    return this.staffForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    if (this.staffForm.invalid) return;

    this.http
      .post('http://localhost:8080/api/staff/add', this.staffForm.value, {
        headers: {
          'Content-Type': 'application/json',
        },
      })
      .subscribe({
        next: () => {
          alert('Staff created successfully!');
          this.router.navigate(['/staff']);
        },
        error: (error) => {
          console.error('POST error:', error);
          alert('Error creating staff. Please check console for details.');
        },
      });
  }
}
