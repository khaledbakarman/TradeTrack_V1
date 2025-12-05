import { Component, EventEmitter, Output } from '@angular/core';
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

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  toggleCurrency() {
    this.currencyService.toggleCurrency();
  }
}
