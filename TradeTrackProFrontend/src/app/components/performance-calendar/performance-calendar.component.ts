import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CalendarService } from '../../services/calendar.service';

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

    // Tooltip state
    tooltipVisible = false;
    tooltipX = 0;
    tooltipY = 0;
    tooltipDay: CalendarDay | null = null;

    constructor(
        private calendarService: CalendarService,
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
        const userId = Number(localStorage.getItem('userId'));

        console.log('Calendar loading - userId from localStorage:', userId);

        if (!userId) {
            console.error("User ID missing. Rendering empty calendar.");
            this.generateCalendar([]);
            this.loading = false;
            return;
        }

        this.calendarService.getCalendarMonth(year, month, userId).subscribe({
            next: (data: any[]) => {
                this.generateCalendar(data);
                this.loading = false;
            },
            error: (err: any) => {
                console.error('Failed to load calendar data', err);
                this.generateCalendar([]);
                this.loading = false;
            }
        });
    }

    generateCalendar(apiData: any[]) {
        this.calendarDays = [];
        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth(); // 0-11

        // Build a lookup map from API data using normalized keys
        const calendarDataMap: { [key: string]: any } = {};
        apiData.forEach(day => {
            // Backend returns date as YYYY-MM-DD string
            const key = String(day.date);
            calendarDataMap[key] = day;
        });
        console.log('API Data Map:', calendarDataMap);

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
            const cellDate = new Date(year, month, dayOffset);
            const isCurrentMonth = cellDate.getMonth() === month;

            // Format cell date as YYYY-MM-DD using local date (no timezone shift)
            const cellYear = cellDate.getFullYear();
            const cellMonth = String(cellDate.getMonth() + 1).padStart(2, '0');
            const cellDay = String(cellDate.getDate()).padStart(2, '0');
            const cellKey = `${cellYear}-${cellMonth}-${cellDay}`;

            let dayData: CalendarDay = {
                date: cellDate,
                dayNum: cellDate.getDate(),
                isCurrentMonth: isCurrentMonth,
                hasTrade: false
            };

            if (isCurrentMonth && calendarDataMap[cellKey]) {
                const stats = calendarDataMap[cellKey];
                console.log('Match found:', cellKey, stats);
                dayData = {
                    ...dayData,
                    pnl: stats.pnl,
                    wins: stats.wins,
                    losses: stats.losses,
                    hasTrade: true
                };
            }

            this.calendarDays.push(dayData);
        }
    }

    onDateClick(day: CalendarDay) {
        if (!day.isCurrentMonth) return;
        const dateStr = day.date.toISOString().split('T')[0];
        this.router.navigate(['/trades'], { queryParams: { date: dateStr } });
    }

    getHeatColor(day: CalendarDay): string {
        if (!day.isCurrentMonth) return 'bg-slate-800/30';
        if (!day.hasTrade) return 'bg-slate-700/50';

        const pnl = day.pnl || 0;
        if (pnl > 0) {
            if (pnl > 100) return 'bg-emerald-500/80';
            return 'bg-emerald-500/40';
        } else if (pnl < 0) {
            if (pnl < -100) return 'bg-red-500/80';
            return 'bg-red-500/40';
        }
        return 'bg-slate-600'; // Breakeven
    }

    showTooltip(event: MouseEvent, day: CalendarDay) {
        if (!day.isCurrentMonth || !day.hasTrade) return;
        this.tooltipDay = day;
        this.tooltipX = event.clientX + 10;
        this.tooltipY = event.clientY + 10;
        this.tooltipVisible = true;
    }

    hideTooltip() {
        this.tooltipVisible = false;
        this.tooltipDay = null;
    }

    formatPnl(pnl: number | undefined): string {
        if (pnl === undefined) return '$0.00';
        const sign = pnl >= 0 ? '+' : '';
        return `${sign}$${pnl.toFixed(2)}`;
    }
}
