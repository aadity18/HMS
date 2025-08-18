// import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
// import { ReservationListComponent } from './reservation-list.component';
// import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
// import { Router } from '@angular/router';

// describe('ReservationListComponent', () => {
//   let component: ReservationListComponent;
//   let fixture: ComponentFixture<ReservationListComponent>;
//   let httpMock: HttpTestingController;
//   let router: Router;

//   beforeEach(async () => {
//     await TestBed.configureTestingModule({
//       imports: [HttpClientTestingModule, ReservationListComponent],
//       providers: [
//         { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } }
//       ]
//     }).compileComponents();
//   });

//   beforeEach(() => {
//     fixture = TestBed.createComponent(ReservationListComponent);
//     component = fixture.componentInstance;
//     httpMock = TestBed.inject(HttpTestingController);
//     router = TestBed.inject(Router);
//     fixture.detectChanges();

//     // Always flush initial HTTP requests if ngOnInit is called
//     const reqGuests = httpMock.expectOne('http://localhost:8080/api/guests');
//     reqGuests.flush([]);
//     const reqReservations = httpMock.expectOne('http://localhost:8080/api/reservations');
//     reqReservations.flush([]);
//   });

//   afterEach(() => {
//     httpMock.verify();
//   });

//   it('should create', () => {
//     expect(component).toBeTruthy();
//   });

//   it('should load guests and reservations on init', fakeAsync(() => {
//     const guests = [{ id: 1, name: 'John Doe' }];
//     const reservations = [{ id: 10, guestId: 1, roomId: 2, checkInDate: '2024-06-20', checkOutDate: '2024-06-22' }];
//     component.ngOnInit();

//     const reqGuests = httpMock.expectOne('http://localhost:8080/api/guests');
//     expect(reqGuests.request.method).toBe('GET');
//     reqGuests.flush(guests);

//     const reqReservations = httpMock.expectOne('http://localhost:8080/api/reservations');
//     expect(reqReservations.request.method).toBe('GET');
//     reqReservations.flush(reservations);

//     tick();
//     expect(component.guests.length).toBe(1);
//     expect(component.reservations.length).toBe(1);
//     expect(component.reservations[0].guestName).toBe('John Doe');
//   }));

//   it('should set error if guests load fails', fakeAsync(() => {
//     component.loadGuestsAndReservations();
//     const reqGuests = httpMock.expectOne('http://localhost:8080/api/guests');
//     reqGuests.error(new ErrorEvent('Network error'));
//     tick();
//     expect(component.error).toBe('Failed to load guests');
//   }));

//   it('should set error if reservations load fails', fakeAsync(() => {
//     const guests = [{ id: 1, name: 'John Doe' }];
//     component.loadGuestsAndReservations();
//     const reqGuests = httpMock.expectOne('http://localhost:8080/api/guests');
//     reqGuests.flush(guests);

//     const reqReservations = httpMock.expectOne('http://localhost:8080/api/reservations');
//     reqReservations.error(new ErrorEvent('Network error'));
//     tick();
//     expect(component.error).toBe('Failed to load rooms');
//   }));

//   it('should return guest name by id', () => {
//     component.guests = [{ id: 1, name: 'John Doe' }];
//     expect(component.getGuestNameById(1)).toBe('John Doe');
//     expect(component.getGuestNameById(2)).toBe('Unknown');
//   });

//   it('should navigate to edit reservation', () => {
//     component.editReservation(5);
//     expect(router.navigate).toHaveBeenCalledWith(['/reservations/edit', 5]);
//   });

//   it('should delete reservation after confirmation', fakeAsync(() => {
//     spyOn(window, 'confirm').and.returnValue(true);
//     component.reservations = [{ id: 1 }, { id: 2 }];
//     component.deleteReservation(1 as any);

//     const req = httpMock.expectOne('http://localhost:8080/api/reservations/1');
//     expect(req.request.method).toBe('DELETE');
//     req.flush({});
//     tick();
//     expect(component.reservations.length).toBe(1);
//     expect(component.reservations[0].id).toBe(2);
//   }));

//   it('should not delete reservation if not confirmed', () => {
//     spyOn(window, 'confirm').and.returnValue(false);
//     component.reservations = [{ id: 1 }, { id: 2 }];
//     component.deleteReservation(1 as any);
//     expect(component.reservations.length).toBe(2);
//   });

//   it('should alert if delete fails', fakeAsync(() => {
//     spyOn(window, 'confirm').and.returnValue(true);
//     spyOn(window, 'alert');
//     component.reservations = [{ id: 1 }];
//     component.deleteReservation(1 as any);

//     const req = httpMock.expectOne('http://localhost:8080/api/reservations/1');
//     req.error(new ErrorEvent('Network error'));
//     tick();
//     expect(window.alert).toHaveBeenCalledWith('Failed to delete the room.');
//   }));

//   it('should call startPayment on completePayment and retryPayment', () => {
//     spyOn(component, 'startPayment');
//     component.completePayment(10);
//     expect(component.startPayment).toHaveBeenCalledWith(10);
//     component.retryPayment(20);
//     expect(component.startPayment).toHaveBeenCalledWith(20);
//   });

//   it('should handle payment success and reload reservations', fakeAsync(() => {
//     spyOn(window, 'alert');
//     spyOn(component, 'loadReservations');
//     (window as any).Razorpay = function (opts: any) {
//       setTimeout(() => opts.handler({}), 0);
//       return { open: () => {} };
//     };
//     component.startPayment(123);
//     const req1 = httpMock.expectOne('http://localhost:8080/payment/create/123');
//     req1.flush({ paymentId: 'pay_abc', amount: 1000, currency: 'INR', orderId: 'order_xyz' });
//     tick();

//     const req2 = httpMock.expectOne('http://localhost:8080/payment/payment-success/pay_abc');
//     req2.flush({});
//     expect(window.alert).toHaveBeenCalledWith('Payment successful!');
//     expect(component.loadReservations).toHaveBeenCalled();
//   }));

//   it('should handle payment failure', fakeAsync(() => {
//     spyOn(window, 'alert');
//     (window as any).Razorpay = function (opts: any) {
//       setTimeout(() => opts.modal.ondismiss(), 0);
//       return { open: () => {} };
//     };
//     component.startPayment(123);
//     const req1 = httpMock.expectOne('http://localhost:8080/payment/create/123');
//     req1.flush({ paymentId: 'pay_abc', amount: 1000, currency: 'INR', orderId: 'order_xyz' });
//     tick();

//     const req2 = httpMock.expectOne('http://localhost:8080/payment/payment-failure/pay_abc');
//     req2.flush({});
//     expect(window.alert).toHaveBeenCalledWith('Payment failed or cancelled.');
//   }));

//   it('should alert if payment initiation fails', fakeAsync(() => {
//     spyOn(window, 'alert');
//     component.startPayment(123);
//     const req1 = httpMock.expectOne('http://localhost:8080/payment/create/123');
//     req1.error(new ErrorEvent('Network error'));
//     tick();
//     expect(window.alert).toHaveBeenCalledWith('Failed to initiate payment. Please try again.');
//   }));

//   it('should return true for canMakePayment if today <= checkInDate', () => {
//     const today = new Date();
//     const futureDate = new Date(today.getTime() + 24 * 60 * 60 * 1000);
//     expect(component.canMakePayment(futureDate.toISOString().split('T')[0])).toBeTrue();
//   });

//   it('should return false for canMakePayment if today > checkInDate', () => {
//     const pastDate = new Date(Date.now() - 24 * 60 * 60 * 1000);
//     expect(component.canMakePayment(pastDate.toISOString().split('T')[0])).toBeFalse();
//   });

//   it('should checkout reservation and reload reservations', fakeAsync(() => {
//     spyOn(window, 'alert');
//     spyOn(component, 'loadReservations');
//     component.checkoutReservation(123);
//     const req = httpMock.expectOne('http://localhost:8080/api/reservations/123/checkout');
//     expect(req.request.method).toBe('PATCH');
//     req.flush({});
//     tick();
//     expect(window.alert).toHaveBeenCalledWith('Checkout successful!');
//     expect(component.loadReservations).toHaveBeenCalled();
//   }));

//   it('should alert if checkout fails', fakeAsync(() => {
//     spyOn(window, 'alert');
//     component.checkoutReservation(123);
//     const req = httpMock.expectOne('http://localhost:8080/api/reservations/123/checkout');
//     req.error(new ErrorEvent('Network error'));
//     tick();
//     expect(window.alert).toHaveBeenCalledWith('Failed to checkout. Please try again.');
//   }));
// });
