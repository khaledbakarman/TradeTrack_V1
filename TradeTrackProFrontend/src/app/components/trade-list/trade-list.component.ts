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
    console.log("TradeListComponent loaded");
    this.loadTrades();
  }

  loadTrades(): void {
    this.tradeService.getTrades().subscribe({
      next: (data: Trade[]) => {
        this.trades = data;
      },
      error: (err) => console.error('Error loading trades', err)
    });
  }

  editTrade(id: number): void {
    this.router.navigate(['/edit-trade', id]);
  }

  deleteTrade(id: number): void {
    if (!confirm('Are you sure you want to delete this trade?')) return;

    this.tradeService.deleteTrade(id).subscribe({
      next: () => {
        this.trades = this.trades.filter(t => t.id !== id);
      },
      error: (err) => console.error('Delete failed', err)
    });
  }
}
