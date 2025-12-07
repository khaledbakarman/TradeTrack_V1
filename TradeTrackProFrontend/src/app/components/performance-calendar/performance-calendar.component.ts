import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AnalyticsService } from '../../services/analytics.service';

interface CalendarDay {
    date: Date;
    dayNum: number;
    isCurrentMonth: boolean;
    pnl?: number;
    wins?: number;
    losses?: number;
    hasTrade?: boolean;
}

@Component({
    selector: 'app-performance-calendar',
    templateUrl: './performance-calendar.component.html',
    styleUrls: ['./performance-calendar.component.scss']
})
export class PerformanceCalendarComponent implements OnInit {
    currentDate = new Date();
    calendarDays: CalendarDay[] = [];
    weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    loading = false;

    constructor(
        private analyticsService: AnalyticsService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadCalendarData();
    }

    get currentMonthYear(): string {
        return this.currentDate.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
    }

    prevMonth() {
        this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() - 1, 1);
        this.loadCalendarData();
    }

    nextMonth() {
        this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, 1);
        this.loadCalendarData();
    }

    loadCalendarData() {
        this.loading = true;
        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth() + 1; // API expects 1-12

        this.analyticsService.getCalendarData(year, month).subscribe({
            next: (data: any[]) => {
                this.generateCalendar(data);
                this.loading = false;
            },
            error: (err: any) => {
                console.error('Failed to load calendar data', err);
                this.generateCalendar([]); // Generate empty calendar on error
                this.loading = false;
            }
        });
    }

    generateCalendar(apiData: any[]) {
        this.calendarDays = [];
        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth(); // 0-11

        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);

        // Days from prev month to fill first week
        const startPadding = firstDay.getDay();

        // Total days to generate (padding + days in month)
        const totalDays = startPadding + lastDay.getDate();
        // Round up to nearest week (row)
        const totalSlots = Math.ceil(totalDays / 7) * 7;

        for (let i = 0; i < totalSlots; i++) {
            const dayOffset = i - startPadding + 1;
            const date = new Date(year, month, dayOffset);
            const isCurrentMonth = date.getMonth() === month;

            let dayData: CalendarDay = {
                date: date,
                dayNum: date.getDate(),
                isCurrentMonth: isCurrentMonth,
                hasTrade: false
            };

            if (isCurrentMonth) {
                // Find data match (backend returns string YYYY-MM-DD)
                const dateStr = date.toISOString().split('T')[0];
                const stats = apiData.find(d => d.date === dateStr);
                if (stats) {
                    dayData = { ...dayData, ...stats, date: dayData.date, hasTrade: true };
                }
            }

            this.calendarDays.push(dayData);
        }
    }

    onDateClick(day: CalendarDay) {
        if (!day.isCurrentMonth) return;
        const dateStr = day.date.toISOString().split('T')[0];
        this.router.navigate(['/trades'], { queryParams: { date: dateStr } });
    }

    getDayClass(day: CalendarDay): string {
        if (!day.isCurrentMonth) return 'opacity-20 pointer-events-none';
        if (!day.hasTrade) return 'bg-[#1e293b] text-gray-500';

        const pnl = day.pnl || 0;
        if (pnl > 0) {
            if (pnl > 100) return 'bg-emerald-500/80 text-white shadow-emerald-500/20'; // High profit
            return 'bg-emerald-500/40 text-emerald-100'; // Small profit
        } else if (pnl < 0) {
            if (pnl < -100) return 'bg-red-500/80 text-white shadow-red-500/20'; // High loss
            return 'bg-red-500/40 text-red-100'; // Small loss
        }
        return 'bg-gray-700 text-gray-300'; // Breakeven
    }
}
