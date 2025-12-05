import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Trade } from '../../models/trade.model';
import { CurrencyService } from '../../services/currency.service';

@Component({
  selector: 'app-trade-card',
  templateUrl: './trade-card.component.html',
  styleUrls: ['./trade-card.component.scss']
})
export class TradeCardComponent {
  @Input() trade!: Trade;
  @Output() edit = new EventEmitter<number>();
  @Output() delete = new EventEmitter<number>();

  currency = '$';

  constructor(private currencyService: CurrencyService) {
    this.currencyService.currency$.subscribe(c => this.currency = c);
  }

  onEdit() {
    this.edit.emit(this.trade.id);
  }

  onDelete() {
    this.delete.emit(this.trade.id);
  }
}
