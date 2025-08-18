import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { RoomListComponent } from './rooms/room-list/room-list.component';
import { RoomCreateComponent } from './rooms/room-create/room-create.component';
import { RoomEditComponent } from './rooms/room-edit/room-edit.component';
import { AvailableRoomsComponent } from './rooms/available-rooms/available-rooms.component';
import { ReservationListComponent } from './reservations/reservation-list/reservation-list.component';
import { ReservationCreateComponent } from './reservations/reservation-create/reservation-create.component';
import { GuestListComponent } from './guests/guest-list/guest-list.component';
import { GuestCreateComponent } from './guests/guest-create/guest-create.component';
import { GuestEditComponent } from './guests/guest-edit/guest-edit.component';
import { BillingComponent } from './billing/billing-list/billing.component';
import { StaffListComponent } from './staff/staff-list/staff-list.component';
import { StaffCreateComponent } from './staff/staff-create/staff-create.component';
import { StaffEditComponent } from './staff/staff-edit/staff-edit.component';
import { AccessDeniedComponent } from './errors/access-denied/access-denied.component';
import { NotFoundComponent } from './errors/not-found/not-found.component';
import { AuthGuard } from './auth/auth.guard';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { AboutComponent } from './pages/about/about.component';
import { FeaturesComponent } from './pages/features/features.component';
import { ContactComponent } from './pages/contact/contact.component';

export const routes: Routes = [
  { path: '', component:LandingPageComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {path:'about',component:AboutComponent},
  {path:'features', component:FeaturesComponent},
  {path:'contact', component:ContactComponent},

  // Protected Routes
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'rooms', component: RoomListComponent, canActivate: [AuthGuard] },
  { path: 'rooms/create', component: RoomCreateComponent, canActivate: [AuthGuard] },
  { path: 'rooms/edit/:id', component: RoomEditComponent, canActivate: [AuthGuard] },
  { path: 'rooms/available', component: AvailableRoomsComponent, canActivate: [AuthGuard] },

  { path: 'reservations', component: ReservationListComponent, canActivate: [AuthGuard] },
  { path: 'reservations/create', component: ReservationCreateComponent, canActivate: [AuthGuard] },

  { path: 'guests', component: GuestListComponent, canActivate: [AuthGuard] },
  { path: 'guests/create', component: GuestCreateComponent, canActivate: [AuthGuard] },
  { path: 'guests/edit/:id', component: GuestEditComponent, canActivate: [AuthGuard] },

  { path: 'billing', component: BillingComponent, canActivate: [AuthGuard] },

  { path: 'staff', component: StaffListComponent, canActivate: [AuthGuard] },
  { path: 'staff/create', component: StaffCreateComponent, canActivate: [AuthGuard] },
  { path: 'staff/edit/:id', component: StaffEditComponent, canActivate: [AuthGuard] },

  { path: '403', component: AccessDeniedComponent },
  { path: '**', component: NotFoundComponent }
];
