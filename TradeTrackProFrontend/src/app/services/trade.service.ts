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

  getTrades(): Observable<any> {
    // Backend gets userId from the JWT token automatically
    return this.http.get<any>(this.apiUrl).pipe(
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

  deleteTrade(tradeId: number): Observable<any> {
    const url = `${this.apiUrl}/${tradeId}`;
    console.log('TradeService: Deleting trade at URL:', url);
    return this.http.delete(url);
  }

  updateTrade(tradeId: number, payload: any) {
    return this.http.put(`${this.apiUrl}/${tradeId}`, payload);
  }

  getTradeById(id: number): Observable<Trade> {
    return this.http.get<Trade>(`${this.apiUrl}/${id}`);
  }

  exportData(startDate: string, endDate: string, includeTrades: boolean, includeAnalytics: boolean, format: string): Observable<Blob> {
    const params = `?startDate=${startDate}&endDate=${endDate}&includeTrades=${includeTrades}&includeAnalytics=${includeAnalytics}&format=${format}`;
    return this.http.post(`${this.apiUrl}/export${params}`, {}, {
      responseType: 'blob'
    });
  }

}
