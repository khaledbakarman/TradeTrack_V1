import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TradeService } from '../../services/trade.service';
import { Trade } from '../../models/trade.model';

@Component({
  selector: 'app-trade-list',
  templateUrl: './trade-list.component.html',
  styleUrls: ['./trade-list.component.scss']
})
export class TradeListComponent implements OnInit {

  trades: Trade[] = [];

  constructor(private tradeService: TradeService, private router: Router) { }

  ngOnInit(): void {
    this.loadTrades();
  }

  loadTrades() {
    this.tradeService.getTrades().subscribe({
      next: (data) => {
        this.trades = data;
        console.log('Loaded trades:', this.trades.length);
      },
      error: (err) => console.error(err)
    });
  }

  editTrade(id: number): void {
    this.router.navigate(['/edit-trade', id]);
  }

  deleteTrade(id: number): void {
    if (!confirm('Are you sure you want to delete this trade?')) return;

    this.tradeService.deleteTrade(id).subscribe({
      next: () => {
        this.loadTrades(); // Reload to refresh list and pagination
      },
      error: (err) => console.error('Delete failed', err)
    });
  }
}
