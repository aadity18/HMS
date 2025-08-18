import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class RoomSelectionService {
  private roomCode: string | null = null;
  private guestCode: string | null = null;
  private checkInDate: string | null = null;
  private checkOutDate: string | null = null;

  setRoomCode(code: string) {
    this.roomCode = code;
  }
  getRoomCode() {
    return this.roomCode;
  }
  clearRoomCode() {
    this.roomCode = null;
  }

  setGuestCode(code: string) {
    this.guestCode = code;
  }
  getGuestCode() {
    return this.guestCode;
  }
  clearGuestCode() {
    this.guestCode = null;
  }

  setCheckInDate(date: string) {
    this.checkInDate = date;
  }
  getCheckInDate() {
    return this.checkInDate;
  }
  setCheckOutDate(date: string) {
    this.checkOutDate = date;
  }
  getCheckOutDate() {
    return this.checkOutDate;
  }

  clearAll() {
    this.roomCode = null;
    this.guestCode = null;
    this.checkInDate = null;
    this.checkOutDate = null;
  }
}
