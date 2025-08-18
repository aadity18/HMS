import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { RoomSelectionService } from '../../rooms/room-selection.service';

declare var Razorpay: any;

@Component({
  selector: 'app-reservation-create',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './reservation-create.component.html',
  styleUrls: ['./reservation-create.component.css']
})
export class ReservationCreateComponent implements OnInit {
  reservationForm: FormGroup;
  private BASE_URL = 'http://localhost:8080/api/reservations';

  reservationCreated = false;
  reservationId: number | null = null;
  reservationCode: string | null = null; // ✅ new property to store auto-generated code

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private roomSelection: RoomSelectionService
  ) {
    this.reservationForm = this.fb.group({
      roomCode: ['', Validators.required],
      guestCode: ['', Validators.required],
      numberOfAdults: [1, [Validators.required, Validators.min(1)]],
      numberOfChildren: [0, [Validators.required, Validators.min(0)]],
      checkInDate: ['', Validators.required],
      checkOutDate: ['', Validators.required],
    });
  }

  ngOnInit() {
    const roomCode = this.roomSelection.getRoomCode();
    const guestCode = this.roomSelection.getGuestCode();
    const checkInDate = this.roomSelection.getCheckInDate();
    const checkOutDate = this.roomSelection.getCheckOutDate();

    this.reservationForm.patchValue({
      roomCode: roomCode ?? '',
      guestCode: guestCode ?? '',
      checkInDate: checkInDate ?? '',
      checkOutDate: checkOutDate ?? ''
    });
  }

  onSubmit(): void {
    if (this.reservationForm.valid) {
      const reservation = this.reservationForm.value;
      this.http.post<{ id: number, code: string }>(`${this.BASE_URL}/create`, reservation).subscribe({
        next: (res) => {
          alert('Reservation created successfully!');
          this.reservationCreated = true;
          this.reservationId = res.id;
          this.reservationCode = res.code; // ✅ save auto-generated code from backend
        },
        error: (err) => {
          console.error('Error creating reservation:', err);
          alert('Failed to create reservation. Please try again.');
        }
      });
    }
  }

  onCompletePayment(): void {
    if (!this.reservationCreated || !this.reservationId) {
      alert('Create a reservation first!');
      return;
    }

    this.http.post<any>(`http://localhost:8080/payment/create/${this.reservationId}`, {}).subscribe({
      next: (paymentRes) => {
        const paymentId = paymentRes.paymentId;
        const options = {
          key: 'rzp_test_d6uw7zNAhY0zd5',
          amount: paymentRes.amount,
          currency: paymentRes.currency,
          name: 'Hotel Management',
          description: `Reservation Payment (${this.reservationCode})`, // ✅ show code during payment
          order_id: paymentRes.orderId,
          handler: (response: any) => {
            this.http.post(`http://localhost:8080/payment/payment-success/${paymentId}`, response).subscribe(() => {
              alert('Payment successful!');
              this.router.navigate(['/reservations']);
            });
          },
          modal: {
            ondismiss: () => {
              this.http.post(`http://localhost:8080/payment/payment-failure/${paymentId}`, {
                reservationId: this.reservationId
              }).subscribe(() => {
                alert('Payment failed or cancelled.');
                this.router.navigate(['/reservations']);
              });
            }
          }
        };
        const rzp = new Razorpay(options);
        rzp.open();
      },
      error: (err) => {
        console.error('Error creating payment order:', err);
        alert('Failed to initiate payment. Please try again.');
      }
    });
  }

  clearSelection(): void {
    this.roomSelection.clearAll();
    this.reservationForm.reset({
      numberOfAdults: 1,
      numberOfChildren: 0
    });
  }
}
