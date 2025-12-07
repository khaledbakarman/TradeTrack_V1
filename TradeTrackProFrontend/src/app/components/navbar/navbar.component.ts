import { Component, EventEmitter, Output, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CurrencyService } from '../../services/currency.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  @Output() currencyChange = new EventEmitter<string>();
  currency = '$';

  constructor(
    private router: Router,
    private authService: AuthService,
    private currencyService: CurrencyService
  ) {
    this.currencyService.currency$.subscribe(c => {
      this.currency = c;
      this.currencyChange.emit(c);
    });
  }

  showNavbar(): boolean {
    const hiddenRoutes = ['/login', '/register'];
    return !hiddenRoutes.includes(this.router.url);
  }

  toggleCurrency() {
    this.currencyService.toggleCurrency();
  }

  isProfileMenuOpen = false;

  toggleProfileMenu() {
    this.isProfileMenuOpen = !this.isProfileMenuOpen;
  }

  goToTrades() {
    this.isProfileMenuOpen = false;
    this.router.navigate(['/trades']);
  }

  goToAnalytics() {
    this.isProfileMenuOpen = false;
    this.router.navigate(['/analytics']);
  }

  logout() {
    localStorage.clear();
    this.isProfileMenuOpen = false;
    this.router.navigate(['/login']);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.profile-wrapper')) {
      this.isProfileMenuOpen = false;
    }
  }
}
