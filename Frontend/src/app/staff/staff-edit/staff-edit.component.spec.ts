// import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
// import { StaffEditComponent } from './staff-edit.component';
// import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
// import { ActivatedRoute, Router } from '@angular/router';
// import { FormBuilder } from '@angular/forms';

// describe('StaffEditComponent', () => {
//   let component: StaffEditComponent;
//   let fixture: ComponentFixture<StaffEditComponent>;
//   let httpMock: HttpTestingController;
//   let router: Router;

//   beforeEach(async () => {
//     await TestBed.configureTestingModule({
//       imports: [HttpClientTestingModule, StaffEditComponent],
//       providers: [
//         { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '123' } } } },
//         { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } },
//         FormBuilder
//       ]
//     }).compileComponents();
//   });

//   beforeEach(() => {
//     spyOn(localStorage, 'getItem').and.callFake((key: string) => {
//       if (key === 'userRole') return 'OWNER';
//       return null;
//     });
//     fixture = TestBed.createComponent(StaffEditComponent);
//     component = fixture.componentInstance;
//     httpMock = TestBed.inject(HttpTestingController);
//     router = TestBed.inject(Router);
//     fixture.detectChanges();
//   });

//   afterEach(() => {
//     httpMock.verify();
//   });

//   it('should create', () => {
//     // Flush the GET request for staff details
//     httpMock.expectOne('http://localhost:8080/api/staff/123').flush({
//       staffCode: 'S001',
//       employeeName: 'John Doe',
//       employeeAddress: '123 Main St',
//       nic: '123456789V',
//       salary: 50000,
//       age: 30,
//       occupation: 'MANAGER',
//       email: 'john@example.com'
//     });
//     expect(component).toBeTruthy();
//   });

//   it('should load staff details on init', fakeAsync(() => {
//     const mockStaff = {
//       staffCode: 'S001',
//       employeeName: 'John Doe',
//       employeeAddress: '123 Main St',
//       nic: '123456789V',
//       salary: 50000,
//       age: 30,
//       occupation: 'MANAGER',
//       email: 'john@example.com'
//     };
//     httpMock.expectOne('http://localhost:8080/api/staff/123').flush(mockStaff);
//     tick();
//     expect(component.staffForm.value).toEqual(mockStaff);
//   }));

//   it('should alert if loading staff details fails', fakeAsync(() => {
//     spyOn(window, 'alert');
//     httpMock.expectOne('http://localhost:8080/api/staff/123').error(new ErrorEvent('Network error'));
//     tick();
//     expect(window.alert).toHaveBeenCalledWith('Error loading staff data');
//   }));

//   it('should submit updated staff and navigate on success', fakeAsync(() => {
//     spyOn(window, 'alert');
//     // Flush GET for staff details
//     httpMock.expectOne('http://localhost:8080/api/staff/123').flush({
//       staffCode: 'S001',
//       employeeName: 'John Doe',
//       employeeAddress: '123 Main St',
//       nic: '123456789V',
//       salary: 50000,
//       age: 30,
//       occupation: 'MANAGER',
//       email: 'john@example.com'
//     });

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

//     const req = httpMock.expectOne('http://localhost:8080/api/staff/123');
//     expect(req.request.method).toBe('PUT');
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

//     expect(window.alert).toHaveBeenCalledWith('Staff updated successfully!');
//     expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
//   }));

//   it('should alert if update fails', fakeAsync(() => {
//     spyOn(window, 'alert');
//     // Flush GET for staff details
//     httpMock.expectOne('http://localhost:8080/api/staff/123').flush({
//       staffCode: 'S001',
//       employeeName: 'John Doe',
//       employeeAddress: '123 Main St',
//       nic: '123456789V',
//       salary: 50000,
//       age: 30,
//       occupation: 'MANAGER',
//       email: 'john@example.com'
//     });

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

//     const req = httpMock.expectOne('http://localhost:8080/api/staff/123');
//     req.error(new ErrorEvent('Network error'));
//     tick();

//     expect(window.alert).toHaveBeenCalledWith('Error updating staff.');
//   }));
// });
