// import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
// import { RoomEditComponent } from './room-edit.component';
// import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
// import { ActivatedRoute, Router } from '@angular/router';
// import { FormBuilder } from '@angular/forms';

// describe('RoomEditComponent', () => {
//   let component: RoomEditComponent;
//   let fixture: ComponentFixture<RoomEditComponent>;
//   let httpMock: HttpTestingController;
//   let router: Router;

//   beforeEach(async () => {
//     await TestBed.configureTestingModule({
//       imports: [HttpClientTestingModule, RoomEditComponent],
//       providers: [
//         { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '123' } } } },
//         { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } },
//         FormBuilder
//       ]
//     }).compileComponents();
//   });

//   beforeEach(() => {
//     fixture = TestBed.createComponent(RoomEditComponent);
//     component = fixture.componentInstance;
//     httpMock = TestBed.inject(HttpTestingController);
//     router = TestBed.inject(Router);
//     fixture.detectChanges();
//   });

//   afterEach(() => {
//     httpMock.verify();
//   });

//   it('should create', () => {
//     // Flush the GET request triggered by ngOnInit
//     httpMock.expectOne('http://localhost:8080/api/rooms/123').flush({ roomType: '', price: '', capacity: '' });
//     expect(component).toBeTruthy();
//   });

//   it('should load room details on init', fakeAsync(() => {
//     const mockRoom = { roomType: 'DOUBLE', price: 200, capacity: 3 };
//     httpMock.expectOne('http://localhost:8080/api/rooms/123').flush(mockRoom);
//     tick();
//     expect(component.roomForm.value).toEqual(mockRoom);
//   }));

//   it('should show alert if loading room details fails', fakeAsync(() => {
//     spyOn(window, 'alert');
//     httpMock.expectOne('http://localhost:8080/api/rooms/123').error(new ErrorEvent('Network error'));
//     tick();
//     expect(window.alert).toHaveBeenCalledWith('Failed to load room details.');
//   }));

//   it('should submit updated room and navigate on success', fakeAsync(() => {
//     spyOn(window, 'alert');
//     // Flush GET for ngOnInit
//     httpMock.expectOne('http://localhost:8080/api/rooms/123').flush({ roomType: 'SUITE', price: 500, capacity: 4 });

//     component.roomForm.setValue({ roomType: 'SUITE', price: 500, capacity: 4 });
//     component.onSubmit();

//     // PUT request for update
//     const req = httpMock.expectOne('http://localhost:8080/api/rooms/123');
//     expect(req.request.method).toBe('PUT');
//     expect(req.request.body).toEqual({ roomType: 'SUITE', price: 500, capacity: 4 });
//     req.flush({});
//     tick();

//     expect(window.alert).toHaveBeenCalledWith('Room updated successfully!');
//     expect(router.navigate).toHaveBeenCalledWith(['/rooms']);
//   }));

//   it('should alert and log error if update fails', fakeAsync(() => {
//     spyOn(window, 'alert');
//     spyOn(console, 'error');
//     // Flush GET for ngOnInit
//     httpMock.expectOne('http://localhost:8080/api/rooms/123').flush({ roomType: 'SINGLE', price: 100, capacity: 1 });

//     component.roomForm.setValue({ roomType: 'SINGLE', price: 100, capacity: 1 });
//     component.onSubmit();

//     // PUT request for update
//     const req = httpMock.expectOne('http://localhost:8080/api/rooms/123');
//     req.error(new ErrorEvent('Network error'));
//     tick();

//     expect(console.error).toHaveBeenCalled();
//     expect(window.alert).toHaveBeenCalledWith('Update failed. Please try again.');
//   }));
// });
