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

  getTrades(userId: number): Observable<any> {
    return this.http.get(`http://localhost:8080/api/trades?userId=${userId}`).pipe(
      tap(
        data => console.log(`[TradeService] HTTP request executed for userId: ${userId}`, data),
        error => console.error(`[TradeService] HTTP request failed for userId: ${userId}`, error)
      )
    );
  }

}
