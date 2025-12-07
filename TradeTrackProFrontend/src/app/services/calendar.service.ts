import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class CalendarService {

    private apiUrl = 'http://localhost:8080/api/calendar/month';

    constructor(private http: HttpClient) { }

    getCalendarMonth(year: number, month: number, userId: number): Observable<any[]> {
        return this.http.get<any[]>(this.apiUrl, {
            params: {
                year: year.toString(),
                month: month.toString(),
                userId: userId.toString()
            }
        });
    }
}
