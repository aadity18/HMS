// import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
// import { RoomCreateComponent } from './room-create.component';
// import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
// import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
// import { Router } from '@angular/router';
// import { CommonModule } from '@angular/common';

// describe('RoomCreateComponent', () => {
//   let component: RoomCreateComponent;
//   let fixture: ComponentFixture<RoomCreateComponent>;
//   let httpMock: HttpTestingController;
//   let router: Router;

//   beforeEach(async () => {
//     await TestBed.configureTestingModule({
//       imports: [HttpClientTestingModule, RoomCreateComponent],
//       providers: [
//         { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } }
//       ]
//     }).compileComponents();
//   });

//   beforeEach(() => {
//     fixture = TestBed.createComponent(RoomCreateComponent);
//     component = fixture.componentInstance;
//     httpMock = TestBed.inject(HttpTestingController);
//     router = TestBed.inject(Router);
//     fixture.detectChanges();
//   });

//   afterEach(() => {
//     httpMock.verify();
//   });

//   it('should create', () => {
//     expect(component).toBeTruthy();
//   });

//   it('should have a form with roomType, price, and capacity controls', () => {
//     expect(component.roomForm.contains('roomType')).toBeTrue();
//     expect(component.roomForm.contains('price')).toBeTrue();
//     expect(component.roomForm.contains('capacity')).toBeTrue();
//   });

//   it('should not submit if form is invalid', () => {
//     spyOn(window, 'alert');
//     component.roomForm.setValue({ roomType: '', price: '', capacity: '' });
//     component.onSubmit();
//     expect(window.alert).not.toHaveBeenCalled();
//   });

//   it('should submit form and navigate on success', fakeAsync(() => {
//     spyOn(window, 'alert');
//     component.roomForm.setValue({ roomType: 'SINGLE', price: 100, capacity: 2 });
//     component.onSubmit();

//     const req = httpMock.expectOne('http://localhost:8080/api/rooms/create');
//     expect(req.request.method).toBe('POST');
//     expect(req.request.body).toEqual({ roomType: 'SINGLE', price: 100, capacity: 2 });
//     req.flush({});

//     tick();
//     expect(window.alert).toHaveBeenCalledWith('Room created successfully!');
//     expect(router.navigate).toHaveBeenCalledWith(['/rooms']);
//   }));

//   it('should alert on error', fakeAsync(() => {
//     spyOn(window, 'alert');
//     spyOn(console, 'error');
//     component.roomForm.setValue({ roomType: 'DOUBLE', price: 200, capacity: 3 });
//     component.onSubmit();

//     const req = httpMock.expectOne('http://localhost:8080/api/rooms/create');
//     req.error(new ErrorEvent('Network error'));

//     tick();
//     expect(console.error).toHaveBeenCalled();
//     expect(window.alert).toHaveBeenCalledWith('Something went wrong. Please try again.');
//   }));
// });
