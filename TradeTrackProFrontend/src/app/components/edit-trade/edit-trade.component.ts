import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TradeService } from '../../services/trade.service';
import { Trade } from '../../models/trade.model';

@Component({
  selector: 'app-edit-trade',
  templateUrl: './edit-trade.component.html',
  styleUrls: ['./edit-trade.component.scss']
})
export class EditTradeComponent implements OnInit {

  tradeId!: number;
  trade!: Trade;

  symbol = '';
  entryPrice!: number;
  exitPrice!: number;
  notes = '';
  tradeDate!: string;
  quantity!: number;
  positionType: 'BUY' | 'SELL' = 'BUY';
  outcome: 'WIN' | 'LOSS' | 'BREAKEVEN' = 'WIN';

  constructor(
    private route: ActivatedRoute,
    private tradeService: TradeService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.tradeId = Number(this.route.snapshot.paramMap.get('id'));

    this.tradeService.getTradeById(this.tradeId).subscribe(t => {
      this.trade = t;

      this.symbol = t.symbol;
      this.entryPrice = t.entryPrice;
      this.exitPrice = t.exitPrice;
      this.notes = t.notes;
      this.tradeDate = t.tradeDate;
      this.quantity = t.quantity;
      this.positionType = t.positionType;
      this.outcome = t.outcome;
    });
  }

  save() {
    const payload = {
      symbol: this.symbol,
      entryPrice: this.entryPrice,
      exitPrice: this.exitPrice,
      profitLoss: Number((this.exitPrice - this.entryPrice).toFixed(2)),
      notes: this.notes,
      tradeDate: this.tradeDate,
      quantity: this.quantity,
      positionType: this.positionType,
      outcome: this.outcome
    };

    this.tradeService.updateTrade(this.tradeId, payload).subscribe({
      next: () => this.router.navigate(['/trades']),
      error: (err) => console.error('Update failed', err)
    });
  }
}
