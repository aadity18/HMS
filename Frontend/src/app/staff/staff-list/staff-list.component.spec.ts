// import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
// import { StaffListComponent } from './staff-list.component';
// import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
// import { Router } from '@angular/router';

// describe('StaffListComponent', () => {
//   let component: StaffListComponent;
//   let fixture: ComponentFixture<StaffListComponent>;
//   let httpMock: HttpTestingController;
//   let router: Router;

//   beforeEach(async () => {
//     await TestBed.configureTestingModule({
//       imports: [HttpClientTestingModule, StaffListComponent],
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
//     fixture = TestBed.createComponent(StaffListComponent);
//     component = fixture.componentInstance;
//     httpMock = TestBed.inject(HttpTestingController);
//     router = TestBed.inject(Router);
//     fixture.detectChanges();
//   });

//   afterEach(() => {
//     httpMock.verify();
//   });

//   it('should create', () => {
//     // Flush the GET request for staff
//     httpMock.expectOne('http://localhost:8080/api/staff').flush([]);
//     expect(component).toBeTruthy();
//   });

//   it('should load staff list on init', fakeAsync(() => {
//     const mockStaff = [
//       { id: 1, staffCode: 'S001', employeeName: 'John Doe', employeeAddress: '123 Main St', nic: '123456789V', salary: 50000, age: 30, occupation: 'MANAGER', email: 'john@example.com' }
//     ];
//     httpMock.expectOne('http://localhost:8080/api/staff').flush(mockStaff);
//     tick();
//     expect(component.staffList).toEqual(mockStaff);
//     expect(component.loading).toBeFalse();
//     expect(component.error).toBe('');
//   }));

//   it('should set error if loading staff fails', fakeAsync(() => {
//     httpMock.expectOne('http://localhost:8080/api/staff').error(new ErrorEvent('Network error'));
//     tick();
//     expect(component.error).toBe('Failed to load staff data.');
//     expect(component.loading).toBeFalse();
//   }));

//   it('should navigate to edit page', () => {
//     // Flush the GET request for staff
//     httpMock.expectOne('http://localhost:8080/api/staff').flush([]);
//     component.editStaff(123);
//     expect(router.navigate).toHaveBeenCalledWith(['/staff/edit', 123]);
//   });

//   it('should delete staff if confirmed', fakeAsync(() => {
//     // Flush the GET request for staff
//     httpMock.expectOne('http://localhost:8080/api/staff').flush([
//       { id: 1, staffCode: 'S001', employeeName: 'John Doe', employeeAddress: '123 Main St', nic: '123456789V', salary: 50000, age: 30, occupation: 'MANAGER', email: 'john@example.com' },
//       { id: 2, staffCode: 'S002', employeeName: 'Jane Doe', employeeAddress: '456 Main St', nic: '987654321V', salary: 40000, age: 28, occupation: 'RECEPTIONIST', email: 'jane@example.com' }
//     ]);
//     spyOn(window, 'confirm').and.returnValue(true);
//     component.staffList = [
//       { id: 1, staffCode: 'S001', employeeName: 'John Doe', employeeAddress: '123 Main St', nic: '123456789V', salary: 50000, age: 30, occupation: 'MANAGER', email: 'john@example.com' },
//       { id: 2, staffCode: 'S002', employeeName: 'Jane Doe', employeeAddress: '456 Main St', nic: '987654321V', salary: 40000, age: 28, occupation: 'RECEPTIONIST', email: 'jane@example.com' }
//     ];
//     component.deleteStaff(1);
//     const req = httpMock.expectOne('http://localhost:8080/api/staff/1');
//     expect(req.request.method).toBe('DELETE');
//     req.flush({});
//     tick();
//     expect(component.staffList.length).toBe(1);
//     expect(component.staffList[0].id).toBe(2);
//   }));

//   it('should not delete staff if not confirmed', () => {
//     // Flush the GET request for staff
//     httpMock.expectOne('http://localhost:8080/api/staff').flush([
//       { id: 1, staffCode: 'S001', employeeName: 'John Doe', employeeAddress: '123 Main St', nic: '123456789V', salary: 50000, age: 30, occupation: 'MANAGER', email: 'john@example.com' }
//     ]);
//     spyOn(window, 'confirm').and.returnValue(false);
//     component.staffList = [
//       { id: 1, staffCode: 'S001', employeeName: 'John Doe', employeeAddress: '123 Main St', nic: '123456789V', salary: 50000, age: 30, occupation: 'MANAGER', email: 'john@example.com' }
//     ];
//     component.deleteStaff(1);
//     httpMock.expectNone('http://localhost:8080/api/staff/1');
//     expect(component.staffList.length).toBe(1);
//   });

//   it('should alert if delete fails', fakeAsync(() => {
//     // Flush the GET request for staff
//     httpMock.expectOne('http://localhost:8080/api/staff').flush([
//       { id: 1, staffCode: 'S001', employeeName: 'John Doe', employeeAddress: '123 Main St', nic: '123456789V', salary: 50000, age: 30, occupation: 'MANAGER', email: 'john@example.com' }
//     ]);
//     spyOn(window, 'confirm').and.returnValue(true);
//     spyOn(window, 'alert');
//     component.staffList = [
//       { id: 1, staffCode: 'S001', employeeName: 'John Doe', employeeAddress: '123 Main St', nic: '123456789V', salary: 50000, age: 30, occupation: 'MANAGER', email: 'john@example.com' }
//     ];
//     component.deleteStaff(1);
//     const req = httpMock.expectOne('http://localhost:8080/api/staff/1');
//     req.error(new ErrorEvent('Network error'));
//     tick();
//     expect(window.alert).toHaveBeenCalledWith('Failed to delete staff member.');
//   }));
// });
