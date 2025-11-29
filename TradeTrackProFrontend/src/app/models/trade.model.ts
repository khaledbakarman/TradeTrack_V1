export interface Trade {
  id: number;
  symbol: string;
  entryPrice: number;
  exitPrice: number;
  profitLoss: number;
  notes: string;
  userId: number;
}
