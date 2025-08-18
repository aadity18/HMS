// import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
// import { ReservationCreateComponent } from './reservation-create.component';
// import { ReactiveFormsModule } from '@angular/forms';
// import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
// import { Router } from '@angular/router';
// import { RoomSelectionService } from '../../rooms/room-selection.service';
// import { of } from 'rxjs';

// class MockRoomSelectionService {
//   getRoomId = () => 1;
//   getGuestId = () => 2;
//   getCheckInDate = () => '2024-06-20';
//   getCheckOutDate = () => '2024-06-22';
//   clearAll = jasmine.createSpy('clearAll');
// }

// describe('ReservationCreateComponent', () => {
//   let component: ReservationCreateComponent;
//   let fixture: ComponentFixture<ReservationCreateComponent>;
//   let httpMock: HttpTestingController;
//   let router: Router;
//   let roomSelection: RoomSelectionService;

//   beforeEach(async () => {
//     await TestBed.configureTestingModule({
//       imports: [ReactiveFormsModule, HttpClientTestingModule, ReservationCreateComponent],
//       providers: [
//         { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } },
//         { provide: RoomSelectionService, useClass: MockRoomSelectionService }
//       ]
//     }).compileComponents();

//     fixture = TestBed.createComponent(ReservationCreateComponent);
//     component = fixture.componentInstance;
//     httpMock = TestBed.inject(HttpTestingController);
//     router = TestBed.inject(Router);
//     roomSelection = TestBed.inject(RoomSelectionService);
//     fixture.detectChanges();
//   });

//   afterEach(() => {
//     httpMock.verify();
//   });

//   it('should create', () => {
//     expect(component).toBeTruthy();
//   });

//   it('should patch form values from RoomSelectionService on init', () => {
//     expect(component.reservationForm.value.roomId).toBe(1);
//     expect(component.reservationForm.value.guestId).toBe(2);
//     expect(component.reservationForm.value.checkInDate).toBe('2024-06-20');
//     expect(component.reservationForm.value.checkOutDate).toBe('2024-06-22');
//   });

//   it('should submit reservation and set reservationCreated and reservationId', fakeAsync(() => {
//     component.reservationForm.patchValue({
//       roomId: 1,
//       guestId: 2,
//       numberOfAdults: 2,
//       numberOfChildren: 1,
//       checkInDate: '2024-06-20',
//       checkOutDate: '2024-06-22'
//     });
//     component.onSubmit();
//     const req = httpMock.expectOne('http://localhost:8080/api/reservations/create');
//     expect(req.request.method).toBe('POST');
//     req.flush({ id: 123 });
//     tick();
//     expect(component.reservationCreated).toBeTrue();
//     expect(component.reservationId).toBe(123);
//   }));

//   it('should alert and not proceed if reservationForm is invalid on submit', () => {
//     spyOn(window, 'alert');
//     component.reservationForm.patchValue({ roomId: '', guestId: '' });
//     component.onSubmit();
//     expect(window.alert).not.toHaveBeenCalledWith('Reservation created successfully!');
//   });

//   it('should handle payment success and navigate to /reservations', fakeAsync(() => {
//     component.reservationCreated = true;
//     component.reservationId = 123;
//     spyOn(window, 'alert');
//     (window as any).Razorpay = function (opts: any) {
//       setTimeout(() => opts.handler({}), 0);
//       return { open: () => {} };
//     };
//     component.onCompletePayment();
//     const req = httpMock.expectOne('http://localhost:8080/payment/create/123');
//     req.flush({ paymentId: 'pay_abc', amount: 1000, currency: 'INR', orderId: 'order_xyz' });
//     tick();
//     // Simulate handler
//     const req2 = httpMock.expectOne('http://localhost:8080/payment/payment-success/pay_abc');
//     req2.flush({});
//     expect(window.alert).toHaveBeenCalledWith('Payment successful!');
//     expect(router.navigate).toHaveBeenCalledWith(['/reservations']);
//   }));

//   it('should handle payment failure and navigate to /reservations', fakeAsync(() => {
//     component.reservationCreated = true;
//     component.reservationId = 123;
//     spyOn(window, 'alert');
//     (window as any).Razorpay = function (opts: any) {
//       setTimeout(() => opts.modal.ondismiss(), 0);
//       return { open: () => {} };
//     };
//     component.onCompletePayment();
//     const req = httpMock.expectOne('http://localhost:8080/payment/create/123');
//     req.flush({ paymentId: 'pay_abc', amount: 1000, currency: 'INR', orderId: 'order_xyz' });
//     tick();
//     // Simulate ondismiss
//     const req2 = httpMock.expectOne('http://localhost:8080/payment/payment-failure/pay_abc');
//     req2.flush({});
//     expect(window.alert).toHaveBeenCalledWith('Payment failed or cancelled.');
//     expect(router.navigate).toHaveBeenCalledWith(['/reservations']);
//   }));

//   it('should alert if trying to pay before reservation is created', () => {
//     spyOn(window, 'alert');
//     component.reservationCreated = false;
//     component.reservationId = null;
//     component.onCompletePayment();
//     expect(window.alert).toHaveBeenCalledWith('Create a reservation first!');
//   });

//   it('should clear selection and reset form', () => {
//     component.reservationForm.patchValue({
//       roomId: 1,
//       guestId: 2,
//       numberOfAdults: 2,
//       numberOfChildren: 1,
//       checkInDate: '2024-06-20',
//       checkOutDate: '2024-06-22'
//     });
//     component.clearSelection();
//     expect(roomSelection.clearAll).toHaveBeenCalled();
//     expect(component.reservationForm.value.numberOfAdults).toBe(1);
//     expect(component.reservationForm.value.numberOfChildren).toBe(0);
//   });
// });
