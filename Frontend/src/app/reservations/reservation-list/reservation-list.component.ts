import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

declare var Razorpay: any;

@Component({
  selector: 'app-reservation-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation-list.component.html',
  styleUrls: ['./reservation-list.component.css'],
})
export class ReservationListComponent implements OnInit, OnDestroy {
  reservations: any[] = [];
  guests: any[] = [];
  error: string = '';
  loading: boolean = false;
  currentUserRole: string = '';
  searchCode: string = '';

  // One countdown window (2 minutes) per reservation, starting on first "Pay Now".
  private paymentDeadlines: { [reservationId: number]: number } = {};
  private globalTick: any = null;

  private readonly BASE_URL = 'http://localhost:8080/api/reservations';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const role = localStorage.getItem('userRole');
    this.currentUserRole = role ? role.toUpperCase() : '';
    this.loadGuestsAndReservations();

    // Global tick to update all countdowns and flip to EXPIRED when needed
    this.globalTick = setInterval(() => {
      const now = Date.now();
      Object.keys(this.paymentDeadlines).forEach((idStr) => {
        const id = +idStr;
        const deadline = this.paymentDeadlines[id];
        if (deadline && now >= deadline) {
          const res = this.reservations.find(r => r.id === id);
          if (res && res.status !== 'CONFIRMED') {
            res.status = 'EXPIRED';
          }
          delete this.paymentDeadlines[id];
        }
      });
    }, 1000);
  }

  ngOnDestroy(): void {
    if (this.globalTick) {
      clearInterval(this.globalTick);
      this.globalTick = null;
    }
  }

  loadGuestsAndReservations() {
    this.loading = true;
    this.http.get<any[]>('http://localhost:8080/api/guests/get').subscribe({
      next: (guestsData) => {
        this.guests = guestsData;
        this.loadReservations();
      },
      error: () => {
        this.error = 'Failed to load guests';
        this.loading = false;
      },
    });
  }

  loadReservations() {
    this.http.get<any[]>(this.BASE_URL).subscribe({
      next: (data) => {
        this.reservations = data.map(res => ({
          ...res,
          guestName: this.getGuestNameById(res.guestId),
        }));
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load reservations';
        this.loading = false;
      },
    });
  }

  getGuestNameById(guestId: number): string {
    const guest = this.guests.find(g => g.id === guestId);
    return guest ? guest.name : 'Unknown';
  }

  editReservation(id: number) {
    this.router.navigate(['/reservations/edit', id]);
  }

  deleteReservation(id: string) {
    if (!confirm('Are you sure you want to delete this reservation?')) return;

    this.http.delete(`${this.BASE_URL}/${id}`).subscribe({
      next: () => {
        this.reservations = this.reservations.filter(
          reservation => reservation.id !== +id
        );
        const numId = +id;
        if (this.paymentDeadlines[numId]) {
          delete this.paymentDeadlines[numId];
        }
      },
      error: () => {
        alert('Failed to delete the reservation.');
      },
    });
  }

  /* ---------- Search by Reservation Code ---------- */
  getReservationByCode() {
    if (!this.searchCode.trim()) {
      this.loadGuestsAndReservations();
      return;
    }
    this.loading = true;
    this.error = '';
    this.http.get<any>(`${this.BASE_URL}/code/${this.searchCode}`).subscribe({
      next: (data) => {
        this.reservations = [data];
        this.loading = false;
      },
      error: () => {
        this.error = `Reservation with code "${this.searchCode}" not found.`;
        this.reservations = [];
        this.loading = false;
      },
    });
  }

  /* ---------- Countdown helpers ---------- */
  private ensureDeadline(reservationId: number) {
    if (!this.paymentDeadlines[reservationId]) {
      this.paymentDeadlines[reservationId] = Date.now() + 2 * 60 * 1000;
    }
  }

  private remainingMs(reservationId: number): number {
    const deadline = this.paymentDeadlines[reservationId];
    return deadline ? deadline - Date.now() : -1;
  }

  isDeadlineActive(reservationId: number): boolean {
    return this.remainingMs(reservationId) > 0;
  }

  getTimerText(reservationId: number): string {
    const ms = this.remainingMs(reservationId);
    if (ms <= 0) return '00:00';
    const totalSec = Math.floor(ms / 1000);
    const m = Math.floor(totalSec / 60);
    const s = totalSec % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  /* ---------- Payment flow ---------- */
  completePayment(reservationId: number) {
    this.startPayment(reservationId, 'PENDING');
  }

  retryPayment(reservationId: number) {
    this.startPayment(reservationId, 'CANCELLED');
  }

  private startPayment(reservationId: number, fromStatus: 'PENDING' | 'CANCELLED') {
    const res = this.reservations.find(r => r.id === reservationId);
    if (!res) return;

    this.ensureDeadline(reservationId);

    if (!this.isDeadlineActive(reservationId)) {
      res.status = 'EXPIRED';
      delete this.paymentDeadlines[reservationId];
      alert('Payment window closed.');
      return;
    }

    this.http
      .post<any>(`http://localhost:8080/payment/create/${reservationId}`, {})
      .subscribe({
        next: (paymentRes) => {
          const paymentId = paymentRes.paymentId;
          const options = {
            key: 'rzp_test_d6uw7zNAhY0zd5',
            amount: paymentRes.amount,
            currency: paymentRes.currency,
            name: 'Hotel Management',
            description: 'Reservation Payment',
            order_id: paymentRes.orderId,
            handler: (response: any) => {
              this.http
                .post(
                  `http://localhost:8080/payment/payment-success/${paymentId}`,
                  response
                )
                .subscribe({
                  next: () => {
                    if (this.paymentDeadlines[reservationId]) {
                      delete this.paymentDeadlines[reservationId];
                    }
                    alert('Payment successful!');
                    this.loadGuestsAndReservations();
                  },
                  error: () => {
                    alert('Payment success but server update failed.');
                  },
                });
            },
            modal: {
              ondismiss: () => {
                this.http
                  .post(
                    `http://localhost:8080/payment/payment-failure/${paymentId}`,
                    { reservationId }
                  )
                  .subscribe({
                    next: () => {
                      const r = this.reservations.find(x => x.id === reservationId);
                      if (r && r.status !== 'CONFIRMED') {
                        r.status = 'CANCELLED';
                      }
                      alert('Payment failed or cancelled.');
                    },
                    error: () => {
                      if (res.status !== 'CONFIRMED') {
                        res.status = 'CANCELLED';
                      }
                      alert('Payment cancelled (server update failed).');
                    },
                  });
              },
            },
          };
          const rzp = new Razorpay(options);
          rzp.open();
        },
        error: () => {
          alert('Failed to initiate payment. Please try again.');
        },
      });
  }
}