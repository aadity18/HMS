import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import * as jwtDecodeModule from 'jwt-decode';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['login']);
    await TestBed.configureTestingModule({
      imports: [LoginComponent, FormsModule],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should login and navigate to dashboard on success', fakeAsync(() => {
    const fakeToken = 'fake.jwt.token';
    const fakeDecoded = { role: 'OWNER' };

    // Properly mock jwtDecode even if it's a read-only export
    Object.defineProperty(jwtDecodeModule, 'jwtDecode', {
      value: jasmine.createSpy().and.returnValue(fakeDecoded),
      configurable: true,
    });

    authService.login.and.returnValue(of(fakeToken));
    component.authRequest = { username: 'test', password: 'pass' };
    component.onSubmit();
    tick();

    expect(localStorage.getItem('token')).toBe(fakeToken);
    expect(localStorage.getItem('userRole')).toBe('OWNER');
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
    expect(component.errorMessage).toBe('');
  }));

  it('should set errorMessage on login error', fakeAsync(() => {
    spyOn(console, 'error'); // Suppress error logging
    authService.login.and.returnValue(throwError(() => new Error('Invalid')));
    component.authRequest = { username: 'test', password: 'wrong' };
    component.onSubmit();
    tick();
    expect(component.errorMessage).toBe('Invalid username or password.');
  }));
});
