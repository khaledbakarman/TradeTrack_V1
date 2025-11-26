import { Component, OnInit } from '@angular/core';
import { TradeService } from '../../services/trade.service';
import { Trade } from '../../models/trade.model';

@Component({
  selector: 'app-trade-list',
  templateUrl: './trade-list.component.html',
  styleUrls: ['./trade-list.component.scss']
})
export class TradeListComponent implements OnInit {

  trades: Trade[] = [];

  constructor(private tradeService: TradeService) { }

  ngOnInit(): void {
    console.log("TradeListComponent loaded");
    this.loadTrades();
  }


  private loadTrades(): void {
    const userId = this.resolveUserId();
    console.log(`[TradeListComponent] Requesting trades for userId: ${userId}`);

    this.tradeService.getTrades(userId).subscribe({
      next: (data: Trade[]) => {
        console.log('[TradeListComponent] Trades received:', data);
        this.trades = data;
      },
      error: (err) => {
        console.error('Error fetching trades:', err);
      }
    });
  }

  private resolveUserId(): number {
    const storedUserId = localStorage.getItem('userId');
    const parsedUserId = storedUserId ? Number(storedUserId) : NaN;
    const normalizedUserId = Number.isFinite(parsedUserId) && parsedUserId > 0 ? parsedUserId : 3;

    localStorage.setItem('userId', normalizedUserId.toString());
    return normalizedUserId;
  }
}
