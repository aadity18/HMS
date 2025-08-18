import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { AuthRequest } from '../models/auth-request.model';
import { AuthService } from '../auth.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-login',
  imports: [FormsModule,CommonModule,RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  authRequest: AuthRequest = new AuthRequest();
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.authService.login(this.authRequest).subscribe({
      next: (token) => {
        console.log('Login success, token:', token);
        localStorage.setItem('token', token);
        const decodedToken: any = jwtDecode(token);
        const role = decodedToken.role; // assuming the role claim key is 'role'
        localStorage.setItem('userRole', role);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Login error:', err);
        this.errorMessage = 'Invalid username or password.';
      },
    });
  }
}