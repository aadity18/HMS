import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { User } from './models/user.model';
import { AuthRequest } from './models/auth-request.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/auth'; // backend URL

  constructor(private http: HttpClient) {}

  logout() {
    localStorage.removeItem('token');
  }
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token'); // adjust as needed
  }

  register(user: User): Observable<string> {
    // Tell Angular to expect plain text response
    return this.http.post(this.baseUrl + '/register', user, {
      responseType: 'text',
    });
  }

  login(authRequest: AuthRequest): Observable<string> {
    return this.http.post(`${this.baseUrl}/login`, authRequest, {
      responseType: 'text',
    });
  }
}
