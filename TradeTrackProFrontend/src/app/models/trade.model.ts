export interface Trade {
  id: number;
  symbol: string;
  entryPrice: number;
  exitPrice: number;
  profitLoss: number;
  notes: string;
  userId: number;
  tradeDate: string;
  quantity: number;
  positionType: 'BUY' | 'SELL';
  outcome: 'WIN' | 'LOSS' | 'BREAKEVEN';
}
