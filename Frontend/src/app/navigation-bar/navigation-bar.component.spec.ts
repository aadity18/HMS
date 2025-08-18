import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { NavigationBarComponent } from './navigation-bar.component';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { Subject } from 'rxjs';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

class MockAuthService {
  logout = jasmine.createSpy('logout');
  isLoggedIn = jasmine.createSpy('isLoggedIn').and.returnValue(true);
}

describe('NavigationBarComponent', () => {
  let component: NavigationBarComponent;
  let fixture: ComponentFixture<NavigationBarComponent>;
  let router: Router;
  let authService: AuthService;
  let routerEvents$: Subject<any>;

  beforeEach(async () => {
    routerEvents$ = new Subject();
    localStorage.setItem('userRole', 'MANAGER');
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        RouterModule,
        NavigationBarComponent
      ],
      providers: [
        { provide: AuthService, useClass: MockAuthService },
        {
          provide: Router,
          useValue: {
            url: '/dashboard',
            events: routerEvents$.asObservable(),
            navigate: jasmine.createSpy('navigate'),
            createUrlTree: jasmine.createSpy('createUrlTree').and.returnValue({}),
            serializeUrl: jasmine.createSpy('serializeUrl').and.returnValue('')
          }
        },
        { provide: ActivatedRoute, useValue: {} }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NavigationBarComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    authService = TestBed.inject(AuthService);
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set role from localStorage on init', () => {
    expect(component.role).toBe('MANAGER');
  });

  it('should update currentUrl on NavigationEnd event', fakeAsync(() => {
    routerEvents$.next(new NavigationEnd(1, '/old', '/new-url'));
    tick();
    expect(component.currentUrl).toBe('/new-url');
  }));

  it('should navigate to given path', () => {
    component.navigateTo('/rooms');
    expect(router.navigate).toHaveBeenCalledWith(['/rooms']);
  });

  it('should return true for isActive if path matches currentUrl', () => {
    component.currentUrl = '/dashboard';
    expect(component.isActive('/dashboard')).toBeTrue();
    expect(component.isActive('dashboard')).toBeTrue();
    expect(component.isActive('/rooms')).toBeFalse();
  });

  it('should call authService.logout and navigate to /login on logout', () => {
    component.logout();
    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should return correct navClass', () => {
    component.currentUrl = '/dashboard';
    expect(component.navClass('/dashboard')).toContain('fw-semibold');
    expect(component.navClass('/rooms')).toContain('text-light');
  });

  it('should scroll to element by id', () => {
    const el = document.createElement('div');
    el.id = 'test-section';
    document.body.appendChild(el);
    spyOn(el, 'scrollIntoView');
    component.scrollTo('test-section');
    expect(el.scrollIntoView).toHaveBeenCalledWith({ behavior: 'smooth' });
    document.body.removeChild(el);
  });
});
