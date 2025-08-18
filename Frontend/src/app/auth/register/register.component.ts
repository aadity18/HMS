import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { FormsModule, NgForm } from '@angular/forms';
import { User } from '../models/user.model';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  user: User = new User();
  message = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    // Only allow OWNER to access this page
    const role = localStorage.getItem('userRole');
    if (role !== 'OWNER') {
      alert("Only Owner Can Add Users");
      this.router.navigate(['/']);
    }
  }

  onSubmit() {
    this.authService.register(this.user).subscribe({
      next: (res) => {
        this.message = res;  
        this.errorMessage = '';
        this.user = new User();
      },
      error: (err) => {
        this.errorMessage = 'Registration failed.';
        this.message = '';
      },
    });
  }
}