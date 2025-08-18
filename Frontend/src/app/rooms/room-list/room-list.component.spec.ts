import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RoomListComponent } from './room-list.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';

describe('RoomListComponent', () => {
  let component: RoomListComponent;
  let fixture: ComponentFixture<RoomListComponent>;
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RoomListComponent],
      providers: [
        { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RoomListComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    spyOn(localStorage, 'getItem').and.callFake((key: string) => {
      if (key === 'userRole') return 'OWNER';
      return null;
    });
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    // Flush the GET request for rooms
    httpMock.expectOne('http://localhost:8080/api/rooms').flush([]);
    expect(component).toBeTruthy();
  });

  it('should load rooms on init', fakeAsync(() => {
    const mockRooms = [{ id: '1', roomType: 'SINGLE' }];
    httpMock.expectOne('http://localhost:8080/api/rooms').flush(mockRooms);
    tick();
    expect(component.rooms).toEqual(mockRooms);
    expect(component.loading).toBeFalse();
    expect(component.error).toBe('');
  }));

  it('should set error if loading rooms fails', fakeAsync(() => {
    httpMock.expectOne('http://localhost:8080/api/rooms').error(new ErrorEvent('Network error'));
    tick();
    expect(component.error).toBe('Failed to load rooms');
    expect(component.loading).toBeFalse();
  }));

  it('should navigate to edit page', () => {
    // Flush the GET request for rooms
    httpMock.expectOne('http://localhost:8080/api/rooms').flush([]);
    component.goToEdit('123');
    expect(router.navigate).toHaveBeenCalledWith(['/rooms/edit', '123']);
  });

  it('should not delete room if not OWNER', () => {
    // Flush the GET request for rooms
    httpMock.expectOne('http://localhost:8080/api/rooms').flush([]);
    component.role = 'RECEPTIONIST';
    spyOn(window, 'alert');
    component.deleteRoom('1');
    expect(window.alert).toHaveBeenCalledWith('Only the owner can delete rooms.');
  });

  it('should not delete room if not confirmed', () => {
    // Flush the GET request for rooms
    httpMock.expectOne('http://localhost:8080/api/rooms').flush([]);
    component.role = 'OWNER';
    spyOn(window, 'confirm').and.returnValue(false);
    component.rooms = [{ id: '1' }, { id: '2' }];
    component.deleteRoom('1');
    expect(component.rooms.length).toBe(2);
  });

  it('should delete room if confirmed and OWNER', fakeAsync(() => {
    // Flush the GET request for rooms
    httpMock.expectOne('http://localhost:8080/api/rooms').flush([{ id: '1' }, { id: '2' }]);
    component.role = 'OWNER';
    spyOn(window, 'confirm').and.returnValue(true);
    component.rooms = [{ id: '1' }, { id: '2' }];
    component.deleteRoom('1');
    const req = httpMock.expectOne('http://localhost:8080/api/rooms/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
    tick();
    expect(component.rooms).toEqual([{ id: '2' }]);
  }));

  it('should alert if delete fails', fakeAsync(() => {
    // Flush the GET request for rooms
    httpMock.expectOne('http://localhost:8080/api/rooms').flush([{ id: '1' }]);
    component.role = 'OWNER';
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(window, 'alert');
    component.rooms = [{ id: '1' }];
    component.deleteRoom('1');
    const req = httpMock.expectOne('http://localhost:8080/api/rooms/1');
    req.error(new ErrorEvent('Network error'));
    tick();
    expect(window.alert).toHaveBeenCalledWith('Failed to delete the room.');
  }));
});
