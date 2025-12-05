import { Component, OnInit, AfterViewInit } from '@angular/core';
import { TradeService } from '../../services/trade.service';
import { Trade } from '../../models/trade.model';
import { CurrencyService } from '../../services/currency.service';
import flatpickr from "flatpickr";

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss']
})
export class AnalyticsComponent implements OnInit, AfterViewInit {

  allTrades: Trade[] = [];
  filteredTrades: Trade[] = [];
  currency = '$';

  startDate: string | null = null;
  endDate: string | null = null;

  totalTrades = 0;
  wins = 0;
  losses = 0;
  winRate = 0;
  totalProfit = 0;

  // Profit by Symbol Chart
  symbolLabels: string[] = [];
  symbolData: any = {
    labels: this.symbolLabels,
    datasets: [
      {
        label: 'Net Profit',
        data: [],
        backgroundColor: [],
        borderColor: "#1E293B",
        borderWidth: 2
      }
    ]
  };

  symbolOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false }
    },
    scales: {
      x: {
        ticks: { color: "#CBD5E1" }
      },
      y: {
        ticks: { color: "#CBD5E1" }
      }
    }
  };

  // Profit by Day of Week Chart
  dayLabels: string[] = [];
  dayData: any = {
    labels: this.dayLabels,
    datasets: [
      {
        label: 'Net P/L',
        data: [],
        backgroundColor: [],
        borderColor: "#1E293B",
        borderWidth: 2
      }
    ]
  };

  dayOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false }
    },
    scales: {
      x: {
        ticks: { color: "#CBD5E1" }
      },
      y: {
        ticks: { color: "#CBD5E1" }
      }
    }
  };

  // Pie Chart (Win / Loss Distribution)
  equityLabels: string[] = ['Wins', 'Losses'];
  equityData: any = {
    labels: this.equityLabels,
    datasets: [
      {
        data: [0, 0],
        backgroundColor: ["#10B981", "#EF4444"],
        borderColor: "#1E293B",
        borderWidth: 2,
        hoverOffset: 12
      }
    ]
  };

  equityOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: "bottom",
        labels: {
          color: "#CBD5E1",
          font: { size: 14 }
        }
      }
    }
  };

  constructor(private tradeService: TradeService, private currencyService: CurrencyService) {
    this.currencyService.currency$.subscribe(c => this.currency = c);
  }

  ngOnInit(): void {
    // Fetch all trades for analytics
    this.tradeService.getTrades().subscribe({
      next: (data: Trade[]) => {
        this.allTrades = data.sort((a, b) => a.id - b.id);
        this.filteredTrades = [...this.allTrades];
        this.updateAnalytics(this.filteredTrades);
      },
      error: (err) => console.error('Error loading analytics:', err)
    });
  }

  ngAfterViewInit() {
    flatpickr("#analyticsStartDate", {
      dateFormat: "Y-m-d",
      allowInput: true,
      altInput: true,
      altFormat: "M j, Y",
      onChange: (selectedDates, dateStr) => {
        this.startDate = dateStr;
      }
    });

    flatpickr("#analyticsEndDate", {
      dateFormat: "Y-m-d",
      allowInput: true,
      altInput: true,
      altFormat: "M j, Y",
      onChange: (selectedDates, dateStr) => {
        this.endDate = dateStr;
      }
    });
  }

  applyDateFilter() {
    const start = this.startDate ? new Date(this.startDate) : null;
    const end = this.endDate ? new Date(this.endDate) : null;

    this.filteredTrades = this.allTrades.filter(t => {
      const tradeDate = new Date(t.tradeDate);
      // Reset time for accurate date comparison
      tradeDate.setHours(0, 0, 0, 0);
      if (start) start.setHours(0, 0, 0, 0);
      if (end) end.setHours(0, 0, 0, 0);

      return (!start || tradeDate >= start) &&
        (!end || tradeDate <= end);
    });

    this.updateAnalytics(this.filteredTrades);
  }

  clearFilter() {
    this.startDate = null;
    this.endDate = null;
    this.filteredTrades = [...this.allTrades];
    this.updateAnalytics(this.filteredTrades);

    // Clear flatpickr inputs
    const startPicker = document.querySelector("#analyticsStartDate") as any;
    const endPicker = document.querySelector("#analyticsEndDate") as any;
    if (startPicker && startPicker._flatpickr) startPicker._flatpickr.clear();
    if (endPicker && endPicker._flatpickr) endPicker._flatpickr.clear();
  }

  updateAnalytics(trades: Trade[]) {
    // 1️⃣ Basic Numbers
    this.totalTrades = trades.length;
    this.wins = trades.filter(t => t.outcome === 'WIN').length;
    this.losses = trades.filter(t => t.outcome === 'LOSS').length;

    this.totalProfit = Number(
      trades.reduce((s, t) => s + t.profitLoss, 0).toFixed(2)
    );

    this.winRate = this.totalTrades > 0
      ? Number(((this.wins / this.totalTrades) * 100).toFixed(2))
      : 0;

    // 2️⃣ Profit by Symbol Chart
    const profitMap: { [symbol: string]: number } = {};
    trades.forEach(t => {
      if (!profitMap[t.symbol]) {
        profitMap[t.symbol] = 0;
      }
      profitMap[t.symbol] += t.profitLoss;
    });

    const symbols = Object.keys(profitMap);
    const profits = Object.values(profitMap);

    this.symbolData = {
      labels: symbols,
      datasets: [
        {
          label: 'Net Profit',
          data: profits,
          backgroundColor: profits.map(v => v >= 0 ? "#10B981" : "#EF4444"),
          borderColor: "#1E293B",
          borderWidth: 2
        }
      ]
    };

    // 3️⃣ Pie Chart (Win / Loss Distribution)
    this.equityLabels = ['Wins', 'Losses'];
    this.equityData = {
      labels: this.equityLabels,
      datasets: [
        {
          data: [this.wins, this.losses],
          backgroundColor: ["#10B981", "#EF4444"],
          borderColor: "#1E293B",
          borderWidth: 2,
          hoverOffset: 12
        }
      ]
    };

    // 4️⃣ Profit by Day of Week Chart
    const dayIndexToName = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
    const dayProfitMap: { [day: string]: number } = {
      Sun: 0, Mon: 0, Tue: 0, Wed: 0, Thu: 0, Fri: 0, Sat: 0
    };

    trades.forEach(t => {
      const d = new Date(t.tradeDate);
      const day = dayIndexToName[d.getDay()];
      dayProfitMap[day] += t.profitLoss;
    });

    const days = Object.keys(dayProfitMap);
    const dayProfits = Object.values(dayProfitMap);

    this.dayData = {
      labels: days,
      datasets: [
        {
          label: 'Net P/L',
          data: dayProfits,
          backgroundColor: dayProfits.map(v => v >= 0 ? "#10B981" : "#EF4444"),
          borderColor: "#1E293B",
          borderWidth: 2
        }
      ]
    };

    // 5️⃣ Monthly Performance Heatmap
    this.calculateMonthlyPerformance(trades);
  }

  // Heatmap Logic
  monthlyData: { [year: string]: { [month: string]: number } } = {};
  months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

  calculateMonthlyPerformance(trades: Trade[]) {
    const result: { [year: string]: { [month: string]: number } } = {};

    trades.forEach(t => {
      const date = new Date(t.tradeDate);
      const year = date.getFullYear().toString();
      const month = date.toLocaleString('en-US', { month: 'short' }); // Jan, Feb, Mar...

      if (!result[year]) result[year] = {};
      if (!result[year][month]) result[year][month] = 0;

      result[year][month] += t.profitLoss;
    });

    this.monthlyData = result;
  }

  getYears() {
    return Object.keys(this.monthlyData || {}).sort((a, b) => Number(b) - Number(a)); // Sort years desc
  }

  getHeatmapColor(value: number) {
    if (value > 0) {
      if (value > 5000) return 'bg-green-600 text-black';
      if (value > 1000) return 'bg-green-500 text-black';
      return 'bg-green-400 text-black';
    }

    if (value < 0) {
      if (value < -5000) return 'bg-red-700 text-white';
      if (value < -1000) return 'bg-red-600 text-white';
      return 'bg-red-500 text-white';
    }

    return 'bg-gray-700 text-gray-300';
  }
}
