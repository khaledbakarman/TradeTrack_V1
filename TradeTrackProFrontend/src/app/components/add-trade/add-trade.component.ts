import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TradeService } from '../../services/trade.service';

@Component({
  selector: 'app-add-trade',
  templateUrl: './add-trade.component.html',
  styleUrls: ['./add-trade.component.scss']
})
export class AddTradeComponent {
  symbol = '';
  entryPrice: number | null = null;
  exitPrice: number | null = null;
  notes = '';
  tradeDate = new Date().toISOString().split('T')[0];
  quantity: number | null = null;
  positionType: 'BUY' | 'SELL' = 'BUY';
  outcome: 'WIN' | 'LOSS' | 'BREAKEVEN' = 'WIN';

  constructor(private tradeService: TradeService, private router: Router) { }

  submit() {
    if (!this.symbol || this.entryPrice == null || this.exitPrice == null || this.quantity == null) {
      alert('Please fill symbol, prices, and quantity.');
      return;
    }

    const payload = {
      symbol: this.symbol.trim().toUpperCase(),
      entryPrice: Number(this.entryPrice),
      exitPrice: Number(this.exitPrice),
      profitLoss: Number((Number(this.exitPrice) - Number(this.entryPrice)).toFixed(2)),
      notes: this.notes,
      tradeDate: this.tradeDate,
      quantity: Number(this.quantity),
      positionType: this.positionType,
      outcome: this.outcome
    };

    this.tradeService.addTrade(payload).subscribe({
      next: (res) => {
        // success -> navigate to trades
        this.router.navigate(['/trades']);
      },
      error: (err) => {
        console.error('Add trade failed', err);
        alert('Failed to add trade. Check console for details.');
      }
    });
  }
}
