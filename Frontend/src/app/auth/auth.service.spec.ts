import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { User } from './models/user.model';
import { AuthRequest } from './models/auth-request.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should remove token on logout', () => {
    localStorage.setItem('token', 'abc');
    service.logout();
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('should return true if token exists for isLoggedIn', () => {
    localStorage.setItem('token', 'abc');
    expect(service.isLoggedIn()).toBeTrue();
  });

  it('should return false if token does not exist for isLoggedIn', () => {
    localStorage.removeItem('token');
    expect(service.isLoggedIn()).toBeFalse();
  });

  it('should call POST /register and return text', () => {
    const user: User = { name: 'Test User', password: 'pass', role: 'OWNER' };
    const responseText = 'Registered!';
    service.register(user).subscribe(res => {
      expect(res).toBe(responseText);
    });
    const req = httpMock.expectOne('http://localhost:8080/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(user);
    expect(req.request.responseType).toBe('text');
    req.flush(responseText);
  });

  it('should call POST /login and return text', () => {
    const authRequest: AuthRequest = { username: 'test', password: 'pass' } as AuthRequest;
    const responseText = 'token123';
    service.login(authRequest).subscribe(res => {
      expect(res).toBe(responseText);
    });
    const req = httpMock.expectOne('http://localhost:8080/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(authRequest);
    expect(req.request.responseType).toBe('text');
    req.flush(responseText);
  });
});
