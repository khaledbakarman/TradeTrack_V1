import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
// Re-trigger compile
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AnalyticsService {
    private apiUrl = 'http://localhost:8080/api/analytics';

    private calendarUrl = 'http://localhost:8080/api/calendar';

    constructor(private http: HttpClient) { }

    getCurrentMonthStats(): Observable<any> {
        return this.http.get(`${this.apiUrl}/current-month`);
    }

    getCalendarData(year: number, month: number): Observable<any[]> {
        return this.http.get<any[]>(`${this.calendarUrl}/month?year=${year}&month=${month}`);
    }
}
