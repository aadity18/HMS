// import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
// import { StaffCreateComponent } from './staff-create.component';
// import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
// import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
// import { Router } from '@angular/router';
// import { CommonModule } from '@angular/common';

// describe('StaffCreateComponent', () => {
//   let component: StaffCreateComponent;
//   let fixture: ComponentFixture<StaffCreateComponent>;
//   let httpMock: HttpTestingController;
//   let router: Router;

//   beforeEach(async () => {
//     await TestBed.configureTestingModule({
//       imports: [HttpClientTestingModule, StaffCreateComponent],
//       providers: [
//         { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } }
//       ]
//     }).compileComponents();
//   });

//   beforeEach(() => {
//     spyOn(localStorage, 'getItem').and.callFake((key: string) => {
//       if (key === 'userRole') return 'OWNER';
//       return null;
//     });
//     fixture = TestBed.createComponent(StaffCreateComponent);
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

//   it('should initialize form with controls', () => {
//     expect(component.staffForm.contains('staffCode')).toBeTrue();
//     expect(component.staffForm.contains('employeeName')).toBeTrue();
//     expect(component.staffForm.contains('employeeAddress')).toBeTrue();
//     expect(component.staffForm.contains('nic')).toBeTrue();
//     expect(component.staffForm.contains('salary')).toBeTrue();
//     expect(component.staffForm.contains('age')).toBeTrue();
//     expect(component.staffForm.contains('occupation')).toBeTrue();
//     expect(component.staffForm.contains('email')).toBeTrue();
//   });

//   it('should not submit if form is invalid', () => {
//     component.staffForm.setValue({
//       staffCode: '',
//       employeeName: '',
//       employeeAddress: '',
//       nic: '',
//       salary: 0,
//       age: 0,
//       occupation: '',
//       email: ''
//     });
//     component.onSubmit();
//     // No HTTP request should be made
//     httpMock.expectNone('http://localhost:8080/api/staff');
//   });

//   it('should submit form and navigate on success', fakeAsync(() => {
//     spyOn(window, 'alert');
//     component.staffForm.setValue({
//       staffCode: 'S001',
//       employeeName: 'John Doe',
//       employeeAddress: '123 Main St',
//       nic: '123456789V',
//       salary: 50000,
//       age: 30,
//       occupation: 'MANAGER',
//       email: 'john@example.com'
//     });
//     component.onSubmit();

//     const req = httpMock.expectOne('http://localhost:8080/api/staff');
//     expect(req.request.method).toBe('POST');
//     expect(req.request.body).toEqual({
//       staffCode: 'S001',
//       employeeName: 'John Doe',
//       employeeAddress: '123 Main St',
//       nic: '123456789V',
//       salary: 50000,
//       age: 30,
//       occupation: 'MANAGER',
//       email: 'john@example.com'
//     });
//     req.flush({});
//     tick();

//     expect(window.alert).toHaveBeenCalledWith('Staff created successfully!');
//     expect(router.navigate).toHaveBeenCalledWith(['/staff']);
//   }));

//   it('should alert and log error on HTTP error', fakeAsync(() => {
//     spyOn(window, 'alert');
//     spyOn(console, 'error');
//     component.staffForm.setValue({
//       staffCode: 'S002',
//       employeeName: 'Jane Doe',
//       employeeAddress: '456 Main St',
//       nic: '987654321V',
//       salary: 40000,
//       age: 28,
//       occupation: 'RECEPTIONIST',
//       email: 'jane@example.com'
//     });
//     component.onSubmit();

//     const req = httpMock.expectOne('http://localhost:8080/api/staff');
//     req.error(new ErrorEvent('Network error'));
//     tick();

//     expect(console.error).toHaveBeenCalled();
//     expect(window.alert).toHaveBeenCalledWith('Error creating staff.');
//   }));
// });
