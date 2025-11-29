import { Component, OnInit } from '@angular/core';
import { TradeService } from '../../services/trade.service';
import { Trade } from '../../models/trade.model';

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss']
})
export class AnalyticsComponent implements OnInit {

  trades: Trade[] = [];

  totalTrades = 0;
  wins = 0;
  losses = 0;
  winRate = 0;
  totalProfit = 0;

  constructor(private tradeService: TradeService) { }

  ngOnInit(): void {
    const userId = Number(localStorage.getItem('userId'));

    this.tradeService.getTrades(userId).subscribe({
      next: (data) => {
        this.trades = data;
        this.calculateStats();
      },
      error: (err) => console.error('Error loading analytics:', err)
    });
  }

  calculateStats() {
    this.totalTrades = this.trades.length;

    this.wins = this.trades.filter(t => t.profitLoss > 0).length;
    this.losses = this.trades.filter(t => t.profitLoss < 0).length;

    this.totalProfit = Number(
      this.trades.reduce((sum, t) => sum + t.profitLoss, 0).toFixed(2)
    );

    this.winRate = this.totalTrades > 0
      ? Number(((this.wins / this.totalTrades) * 100).toFixed(2))
      : 0;
  }
}
