// import { ComponentFixture, TestBed, fakeAsync, tick, inject } from '@angular/core/testing';
// import { AvailableRoomsComponent } from './available-rooms.component';
// import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
// import { Router } from '@angular/router';
// import { RoomSelectionService } from '../room-selection.service';

// class MockRoomSelectionService {
//   setRoomId = jasmine.createSpy('setRoomId');
//   clearGuestId = jasmine.createSpy('clearGuestId');
//   setCheckInDate = jasmine.createSpy('setCheckInDate');
//   setCheckOutDate = jasmine.createSpy('setCheckOutDate');
// }

// describe('AvailableRoomsComponent', () => {
//   let component: AvailableRoomsComponent;
//   let fixture: ComponentFixture<AvailableRoomsComponent>;
//   let router: Router;
//   let roomSelection: RoomSelectionService;
//   let httpMock: HttpTestingController;

//   beforeEach(async () => {
//     await TestBed.configureTestingModule({
//       imports: [HttpClientTestingModule, AvailableRoomsComponent],
//       providers: [
//         { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } },
//         { provide: RoomSelectionService, useClass: MockRoomSelectionService }
//       ]
//     }).compileComponents();
//   });

//   beforeEach(() => {
//     fixture = TestBed.createComponent(AvailableRoomsComponent);
//     component = fixture.componentInstance;
//     router = TestBed.inject(Router);
//     roomSelection = TestBed.inject(RoomSelectionService);
//     httpMock = TestBed.inject(HttpTestingController);
//     fixture.detectChanges();
//   });

//   afterEach(() => {
//     httpMock.verify();
//   });

//   it('should create', () => {
//     expect(component).toBeTruthy();
//   });

//   it('should show error if dates are not selected', () => {
//     component.checkInDate = '';
//     component.checkOutDate = '';
//     component.fetchAvailableRooms();
//     expect(component.error).toBe('Please select both check-in and check-out dates.');
//   });

//   it('should show error if dates are in the past', () => {
//     const yesterday = new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString().split('T')[0];
//     component.checkInDate = yesterday;
//     component.checkOutDate = yesterday;
//     component.fetchAvailableRooms();
//     expect(component.error).toBe('Check-in and check-out dates cannot be in the past.');
//   });

//   it('should fetch available rooms and set roomsSearched', fakeAsync(() => {
//     const today = new Date();
//     const tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);
//     component.checkInDate = today.toISOString().split('T')[0];
//     component.checkOutDate = tomorrow.toISOString().split('T')[0];

//     component.fetchAvailableRooms();

//     const req = httpMock.expectOne(
//       r => r.url === 'http://localhost:8080/api/rooms/available'
//     );
//     expect(req.request.method).toBe('GET');
//     req.flush([{ id: 1, number: '101' }]);
//     tick();

//     expect(component.rooms.length).toBe(1);
//     expect(component.roomsSearched).toBeTrue();
//     expect(component.loading).toBeFalse();
//     expect(component.error).toBe('');
//   }));

//   it('should handle error when fetching rooms', fakeAsync(() => {
//     const today = new Date();
//     const tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);
//     component.checkInDate = today.toISOString().split('T')[0];
//     component.checkOutDate = tomorrow.toISOString().split('T')[0];

//     component.fetchAvailableRooms();

//     const req = httpMock.expectOne(
//       r => r.url === 'http://localhost:8080/api/rooms/available'
//     );
//     req.error(new ErrorEvent('Network error'));
//     tick();

//     expect(component.error).toBe('Error fetching available rooms.');
//     expect(component.loading).toBeFalse();
//     expect(component.roomsSearched).toBeTrue();
//   }));

//   it('should call setRoomId and navigate on onRoomSelect', () => {
//     const room = { id: 5 };
//     component.onRoomSelect(room);
//     expect(roomSelection.setRoomId).toHaveBeenCalledWith(5);
//     expect(router.navigate).toHaveBeenCalledWith(['/guests/create']);
//   });

//   it('should call setRoomId, clearGuestId, set dates, and navigate on selectGuest', () => {
//     const room = { id: 7 };
//     component.checkInDate = '2024-06-20';
//     component.checkOutDate = '2024-06-22';
//     const event = { stopPropagation: jasmine.createSpy('stopPropagation') } as any;
//     component.selectGuest(room, event);
//     expect(event.stopPropagation).toHaveBeenCalled();
//     expect(roomSelection.setRoomId).toHaveBeenCalledWith(7);
//     expect(roomSelection.clearGuestId).toHaveBeenCalled();
//     expect(roomSelection.setCheckInDate).toHaveBeenCalledWith('2024-06-20');
//     expect(roomSelection.setCheckOutDate).toHaveBeenCalledWith('2024-06-22');
//     expect(router.navigate).toHaveBeenCalledWith(['/guests']);
//     expect(component.selectedRoomId).toBeNull();
//   });

//   it('should call setRoomId, clearGuestId, set dates, and navigate on addGuest', () => {
//     const room = { id: 8 };
//     component.checkInDate = '2024-06-20';
//     component.checkOutDate = '2024-06-22';
//     const event = { stopPropagation: jasmine.createSpy('stopPropagation') } as any;
//     component.addGuest(room, event);
//     expect(event.stopPropagation).toHaveBeenCalled();
//     expect(roomSelection.setRoomId).toHaveBeenCalledWith(8);
//     expect(roomSelection.clearGuestId).toHaveBeenCalled();
//     expect(roomSelection.setCheckInDate).toHaveBeenCalledWith('2024-06-20');
//     expect(roomSelection.setCheckOutDate).toHaveBeenCalledWith('2024-06-22');
//     expect(router.navigate).toHaveBeenCalledWith(['/guests/create']);
//     expect(component.selectedRoomId).toBeNull();
//   });

//   it('should set selectedRoomId on showGuestOptions', () => {
//     const room = { id: 9 };
//     const event = { stopPropagation: jasmine.createSpy('stopPropagation') } as any;
//     component.showGuestOptions(room, event);
//     expect(event.stopPropagation).toHaveBeenCalled();
//     expect(component.selectedRoomId).toBe(9);
//   });

//   it('should clear selectedRoomId on cancelGuestOptions', () => {
//     component.selectedRoomId = 10;
//     const event = { stopPropagation: jasmine.createSpy('stopPropagation') } as any;
//     component.cancelGuestOptions(event);
//     expect(event.stopPropagation).toHaveBeenCalled();
//     expect(component.selectedRoomId).toBeNull();
//   });

//   it('should call onRoomSelect and stopPropagation on bookRoom', () => {
//     const room = { id: 11 };
//     spyOn(component, 'onRoomSelect');
//     const event = { stopPropagation: jasmine.createSpy('stopPropagation') } as any;
//     component.bookRoom(room, event);
//     expect(event.stopPropagation).toHaveBeenCalled();
//     expect(component.onRoomSelect).toHaveBeenCalledWith(room);
//   });
// });
