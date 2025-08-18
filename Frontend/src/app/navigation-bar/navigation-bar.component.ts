// import { CommonModule } from '@angular/common';
// import { Component, OnInit } from '@angular/core';
// import { Router, NavigationEnd } from '@angular/router';
// import { filter } from 'rxjs/operators';

// @Component({
//   selector: 'app-navigation-bar',
//   imports:[CommonModule],
//   templateUrl: './navigation-bar.component.html',
//   styleUrls: ['./navigation-bar.component.css']
// })
// export class NavigationBarComponent implements OnInit {
//   role: string = '';
//   currentUrl: string = '';

//   constructor(private router: Router) {}

//   ngOnInit(): void {
//     this.role = localStorage.getItem('userRole') || '';

//     // Subscribe to router events to update currentUrl on navigation
//     this.router.events.pipe(
//       filter(event => event instanceof NavigationEnd)
//     ).subscribe((event: NavigationEnd) => {
//       this.currentUrl = event.urlAfterRedirects;
//     });

//     // Initialize currentUrl on component load
//     this.currentUrl = this.router.url;
//   }

//   navigateTo(path: string) {
//     this.router.navigate([path]);
//   }

//   isActive(path: string): boolean {
//     // Normalize paths for matching (optional)
//     if (!path.startsWith('/')) path = '/' + path;
//     return this.currentUrl === path;
//   }
// }

import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd, RouterModule } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../auth/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navigation-bar',
  imports:[CommonModule,RouterModule],
  templateUrl: './navigation-bar.component.html',
  styleUrls: ['./navigation-bar.component.css'],
  standalone:true
})
export class NavigationBarComponent implements OnInit {
  role: string = '';
  currentUrl: string = '';

  constructor(public authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.role = localStorage.getItem('userRole') || '';

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.currentUrl = event.urlAfterRedirects;
    });

    this.currentUrl = this.router.url;
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
  }

  isActive(path: string): boolean {
    if (!path.startsWith('/')) path = '/' + path;
    return this.currentUrl === path;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  navClass(route: string): string {
  return this.router.url === route
    ? 'text-white fw-semibold border-bottom border-2 border-light'
    : 'text-light';
  }


  scrollTo(id: string) {
    const el = document.getElementById(id);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
  }
}
}
