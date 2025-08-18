import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-billing',
  imports: [CommonModule],
  templateUrl: './billing.component.html',
  styleUrl: './billing.component.css'
})
export class BillingComponent implements OnInit {
  bills: any[] = [];
  filteredBill: any | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchBills();
  }

  fetchBills() {
    this.http.get<any[]>('http://localhost:8080/api/bills').subscribe({
      next: (data) => {
        this.bills = data;
      },
      error: (err) => {
        console.error('Error fetching bills:', err);
      }
    });
  }

  generatePrintableBill(bill: any) {
    this.filteredBill = bill;

    // Allow Angular to render the bill in the DOM first, then print
    setTimeout(() => {
      window.print();
      this.filteredBill = null;
    }, 100);
  }
}