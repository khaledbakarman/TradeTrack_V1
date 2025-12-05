import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { TradeService } from '../../services/trade.service';
import { Trade } from '../../models/trade.model';
import flatpickr from "flatpickr";

@Component({
  selector: 'app-trade-list',
  templateUrl: './trade-list.component.html',
  styleUrls: ['./trade-list.component.scss']
})
export class TradeListComponent implements OnInit, AfterViewInit {

  trades: Trade[] = [];
  filteredTrades: Trade[] = [];

  startDate: string | null = null;
  endDate: string | null = null;
  selectedOutcome: string = 'All';

  currentPage: number = 1;
  pageSize: number = 12;
  totalPages: number = 0;
  totalPagesArray: number[] = [];

  constructor(private tradeService: TradeService, private router: Router) { }

  ngOnInit(): void {
    this.loadTrades();
  }

  ngAfterViewInit() {
    flatpickr("#startDate", {
      dateFormat: "Y-m-d",
      allowInput: true,
      altInput: true,
      altFormat: "M j, Y",

      onChange: (selectedDates, dateStr) => {
        this.startDate = dateStr;
        this.applyFilters();
      }
    });

    flatpickr("#endDate", {
      dateFormat: "Y-m-d",
      allowInput: true,
      altInput: true,
      altFormat: "M j, Y",

      onChange: (selectedDates, dateStr) => {
        this.endDate = dateStr;
        this.applyFilters();
      }
    });
  }

  loadTrades() {
    this.tradeService.getTrades().subscribe({
      next: (data: Trade[]) => {
        this.trades = data.sort((a, b) => b.id - a.id);
        this.filteredTrades = [...this.trades];
        this.calculatePagination();
        console.log('Loaded trades:', this.trades.length);
      },
      error: (err) => console.error(err)
    });
  }

  applyFilters() {
    this.filteredTrades = this.trades.filter(trade => {
      const matchesStart = !this.startDate || trade.tradeDate >= this.startDate;
      const matchesEnd = !this.endDate || trade.tradeDate <= this.endDate;
      const matchesOutcome = this.selectedOutcome === 'All' || trade.outcome === this.selectedOutcome;

      return matchesStart && matchesEnd && matchesOutcome;
    });
    this.currentPage = 1;
    this.calculatePagination();
  }

  clearFilters() {
    this.startDate = null;
    this.endDate = null;
    this.selectedOutcome = 'All';
    this.filteredTrades = [...this.trades];
    this.currentPage = 1;
    this.calculatePagination();

    // Clear flatpickr inputs
    const startPicker = document.querySelector("#startDate") as any;
    const endPicker = document.querySelector("#endDate") as any;
    if (startPicker && startPicker._flatpickr) startPicker._flatpickr.clear();
    if (endPicker && endPicker._flatpickr) endPicker._flatpickr.clear();
  }

  calculatePagination() {
    this.totalPages = Math.ceil(this.filteredTrades.length / this.pageSize);
    this.totalPagesArray = Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  get paginatedTrades() {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.filteredTrades.slice(start, start + this.pageSize);
  }

  goToPage(page: number) {
    this.currentPage = page;
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  editTrade(id: number): void {
    this.router.navigate(['/edit-trade', id]);
  }

  // Export Logic
  showExportModal = false;
  exportStartDate: string = '';
  exportEndDate: string = '';
  exportFormat: 'EXCEL' | 'PDF' = 'EXCEL';

  openExportModal() {
    this.showExportModal = true;
    setTimeout(() => {
      flatpickr("#exportStartDate", {
        dateFormat: "Y-m-d",
        allowInput: true,
        altInput: true,
        altFormat: "M j, Y",

        onChange: (selectedDates, dateStr) => {
          this.exportStartDate = dateStr;
        }
      });

      flatpickr("#exportEndDate", {
        dateFormat: "Y-m-d",
        allowInput: true,
        altInput: true,
        altFormat: "M j, Y",

        onChange: (selectedDates, dateStr) => {
          this.exportEndDate = dateStr;
        }
      });
    }, 0);
  }

  closeExportModal() {
    this.showExportModal = false;
  }

  exportTrades() {
    if (!this.exportStartDate || !this.exportEndDate) {
      alert('Please select both start and end dates.');
      return;
    }
    if (this.exportStartDate > this.exportEndDate) {
      alert('Start date cannot be after end date.');
      return;
    }

    if (this.exportFormat === 'EXCEL') {
      this.tradeService.exportExcel(this.exportStartDate, this.exportEndDate).subscribe(blob => {
        this.downloadFile(blob, 'trades.xlsx');
        this.closeExportModal();
      });
    } else {
      this.tradeService.exportPdf(this.exportStartDate, this.exportEndDate).subscribe(blob => {
        this.downloadFile(blob, 'trades.pdf');
        this.closeExportModal();
      });
    }
  }

  private downloadFile(blob: Blob, fileName: string) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  deleteTrade(id: number): void {
    console.log('Attempting to delete trade:', id);
    if (!confirm('Are you sure you want to delete this trade?')) return;

    console.log('DELETE request sent for ID:', id);
    this.tradeService.deleteTrade(id).subscribe({
      next: () => {
        console.log('Delete successful, reloading trades...');
        this.loadTrades(); // Reload to refresh list and pagination
      },
      error: (err) => console.error('Delete failed', err)
    });
  }
}
