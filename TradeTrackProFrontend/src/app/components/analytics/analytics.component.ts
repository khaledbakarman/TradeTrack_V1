import { Component, OnInit } from '@angular/core';
import { TradeService } from '../../services/trade.service';
import { Trade } from '../../models/trade.model';
import { CurrencyService } from '../../services/currency.service';

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss']
})
export class AnalyticsComponent implements OnInit {

  trades: Trade[] = [];
  currency = '$';

  totalTrades = 0;
  wins = 0;
  losses = 0;
  winRate = 0;
  totalProfit = 0;

  // Win / Loss Chart
  winLossLabels = ['Wins', 'Losses'];
  winLossData: any = {
    labels: this.winLossLabels,
    datasets: [
      {
        label: 'Count',
        data: [0, 0],
        backgroundColor: ['green', 'red']
      }
    ]
  };

  // Equity Curve Chart
  equityLabels: string[] = [];
  equityData: any = {
    labels: this.equityLabels,
    datasets: [
      {
        label: 'Equity Curve',
        data: [],
        borderColor: 'blue',
        fill: false,
        tension: 0.1
      }
    ]
  };

  constructor(private tradeService: TradeService, private currencyService: CurrencyService) {
    this.currencyService.currency$.subscribe(c => this.currency = c);
  }

  ngOnInit(): void {
    // Fetch all trades for analytics
    this.tradeService.getTrades().subscribe({
      next: (data) => {
        this.trades = data;
        this.calculateStats();
      },
      error: (err) => console.error('Error loading analytics:', err)
    });
  }

  calculateStats() {
    if (!this.trades || this.trades.length === 0) return;

    // 1️⃣ Sort trades by ID so charts display correctly
    this.trades.sort((a, b) => a.id - b.id);

    // 2️⃣ Basic Numbers
    this.totalTrades = this.trades.length;
    this.wins = this.trades.filter(t => t.profitLoss > 0).length;
    this.losses = this.trades.filter(t => t.profitLoss < 0).length;

    this.totalProfit = Number(
      this.trades.reduce((s, t) => s + t.profitLoss, 0).toFixed(2)
    );

    this.winRate = this.totalTrades > 0
      ? Number(((this.wins / this.totalTrades) * 100).toFixed(2))
      : 0;

    // 3️⃣ Win / Loss Chart
    this.winLossData = {
      labels: ['Wins', 'Losses'],
      datasets: [
        {
          label: 'Count',
          data: [this.wins, this.losses],
          backgroundColor: ['#2ecc71', '#e74c3c']
        }
      ]
    };

    // 4️⃣ Equity Curve
    let equity = 0;
    const curve: number[] = [];
    const labels: string[] = [];

    for (let t of this.trades) {
      equity += t.profitLoss;
      curve.push(Number(equity.toFixed(2)));
      labels.push(t.symbol);
    }

    this.equityLabels = labels;
    this.equityData = {
      labels: this.equityLabels,
      datasets: [
        {
          label: 'Equity Curve',
          data: curve,
          borderColor: 'blue',
          fill: false,
          tension: 0.2
        }
      ]
    };
  }
}
