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

  constructor(private tradeService: TradeService, private router: Router) { }

  submit() {
    const userId = Number(localStorage.getItem('userId')) || 3; // temporary fallback
    if (!this.symbol || this.entryPrice == null || this.exitPrice == null) {
      alert('Please fill symbol, entry and exit prices.');
      return;
    }

    const payload = {
      userId,
      symbol: this.symbol.trim().toUpperCase(),
      entryPrice: Number(this.entryPrice),
      exitPrice: Number(this.exitPrice),
      profitLoss: Number((Number(this.exitPrice) - Number(this.entryPrice)).toFixed(2)),
      notes: this.notes
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
