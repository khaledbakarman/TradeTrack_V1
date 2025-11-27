import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Trade } from '../models/trade.model';

@Injectable({
  providedIn: 'root'
})
export class TradeService {

  private apiUrl = 'http://localhost:8080/api/trades';

  constructor(private http: HttpClient) { }

  getTrades(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}?userId=${userId}`).pipe(
      tap({
        next: (data) => console.log('Trades loaded:', data.length),
        error: (err) => console.error('Failed to load trades:', err)
      })
    );
  }

  addTrade(trade: any): Observable<any> {
    // backend expects a trade payload including userId
    return this.http.post<any>(this.apiUrl, trade);
  }

}
