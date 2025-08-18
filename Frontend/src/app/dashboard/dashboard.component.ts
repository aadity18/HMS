import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NavigationBarComponent } from '../navigation-bar/navigation-bar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  role: string = ''; // OWNER, MANAGER, RECEPTIONIST

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.role = localStorage.getItem('userRole') || 'RECEPTIONIST';
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
  }
}
