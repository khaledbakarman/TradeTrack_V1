import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AnalyticsService } from '../../services/analytics.service';
import { CurrencyService } from '../../services/currency.service';

@Component({
  selector: 'app-quick-stats',
  templateUrl: './quick-stats.component.html',
  styleUrls: ['./quick-stats.component.scss']
})
export class QuickStatsComponent implements OnInit {

  loading = true;
  currency = '$';

  // Stats
  netPnl = 0;
  totalTrades = 0;
  winRate = 0;
  avgPnlPerTrade = 0;

  constructor(
    private analyticsService: AnalyticsService,
    private currencyService: CurrencyService,
    private router: Router
  ) {
    this.currencyService.currency$.subscribe(c => this.currency = c);
  }

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats() {
    this.analyticsService.getCurrentMonthStats().subscribe({
      next: (data) => {
        console.log('Quick Stats API response:', data);
        this.totalTrades = data.totalTrades || 0;
        this.winRate = data.winRate || 0;
        this.netPnl = data.netProfit || 0;

        // Calculate average P/L per trade
        if (this.totalTrades > 0) {
          this.avgPnlPerTrade = this.netPnl / this.totalTrades;
        } else {
          this.avgPnlPerTrade = 0;
        }

        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading quick stats:', err);
        this.loading = false;
      }
    });
  }

  navigateToAnalytics() {
    this.router.navigate(['/analytics']);
  }

  getNetPnlColor(): string {
    if (this.netPnl > 0) return 'text-green-400';
    if (this.netPnl < 0) return 'text-red-400';
    return 'text-gray-400';
  }

  getAvgPnlColor(): string {
    if (this.avgPnlPerTrade > 0) return 'text-green-400';
    if (this.avgPnlPerTrade < 0) return 'text-red-400';
    return 'text-gray-400';
  }
}
