import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule
} from '@angular/forms';

@Component({
  selector: 'app-staff-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './staff-edit.component.html',
  styleUrl: './staff-edit.component.css'
})
export class StaffEditComponent implements OnInit {
  staffForm!: FormGroup;
  submitted = false;
  staffId!: string;

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
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUserRole = localStorage.getItem('userRole') || '';
    this.staffId = this.route.snapshot.paramMap.get('id') || '';
    this.staffForm = this.fb.group({
      staffCode: ['', Validators.required],
      name: ['', [Validators.required, Validators.minLength(3)]],
      address: ['', Validators.required],
      nic: ['', Validators.required],
      salary: [0, [Validators.required, Validators.min(0)]],
      age: [0, [Validators.required, Validators.min(18)]],
      occupation: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]]
    });

    this.loadStaffDetails();
  }

  get f() {
    return this.staffForm.controls;
  }

  loadStaffDetails() {
    this.http.get<any>(`http://localhost:8080/api/staff/${this.staffId}`).subscribe({
      next: (data) => this.staffForm.patchValue(data),
      error: (err) => alert('Error loading staff data')
    });
  }

  onSubmit() {
    this.submitted = true;
    if (this.staffForm.invalid) return;

    this.http.put(`http://localhost:8080/api/staff/${this.staffId}`, this.staffForm.value).subscribe({
      next: () => {
        alert('Staff updated successfully!');
        this.router.navigate(['/dashboard']);
      },
      error: () => alert('Error updating staff.')
    });
  }
}
