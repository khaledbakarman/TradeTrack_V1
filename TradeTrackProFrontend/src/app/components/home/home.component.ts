import { Component, OnInit } from '@angular/core';
import { TradeService } from '../../services/trade.service';
import { Trade } from '../../models/trade.model';

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

    recentTrades: Trade[] = [];
    loading = true;

    constructor(private tradeService: TradeService) { }

    ngOnInit(): void {
        this.loadRecentTrades();
    }

    loadRecentTrades() {
        console.log('Loading recent trades...');
        this.tradeService.getTrades().subscribe({
            next: (data: Trade[]) => {
                console.log('All trades loaded:', data);
                // Sort by date DESC and take first 4
                this.recentTrades = data
                    .sort((a, b) => new Date(b.tradeDate).getTime() - new Date(a.tradeDate).getTime())
                    .slice(0, 4);
                console.log('Recent 4 trades:', this.recentTrades);
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading recent trades:', err);
                this.loading = false;
            }
        });
    }

    onTradeDeleted(tradeId: number) {
        this.recentTrades = this.recentTrades.filter(t => t.id !== tradeId);
    }

    formatDate(date: string): string {
        const d = new Date(date);
        return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    }

    getPnlColor(pnl: number): string {
        if (pnl > 0) return 'text-green-400';
        if (pnl < 0) return 'text-red-500';
        return 'text-gray-400';
    }
}
