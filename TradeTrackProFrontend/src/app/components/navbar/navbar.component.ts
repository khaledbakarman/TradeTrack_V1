import { Component, EventEmitter, Output, HostListener, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CurrencyService } from '../../services/currency.service';
import { ProfileService, UserProfile } from '../../services/profile.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, OnDestroy {
  @Output() currencyChange = new EventEmitter<string>();
  currency = '$';

  profile: UserProfile | null = null;
  private profileSubscription: Subscription | null = null;

  constructor(
    private router: Router,
    private authService: AuthService,
    private currencyService: CurrencyService,
    private profileService: ProfileService
  ) {
    this.currencyService.currency$.subscribe(c => {
      this.currency = c;
      this.currencyChange.emit(c);
    });
  }

  ngOnInit(): void {
    // Subscribe to profile changes
    this.profileSubscription = this.profileService.profile$.subscribe(profile => {
      this.profile = profile;
    });

    // Load profile if not already loaded
    if (!this.profileService.getCurrentProfile()) {
      this.profileService.getProfile().subscribe();
    }
  }

  ngOnDestroy(): void {
    if (this.profileSubscription) {
      this.profileSubscription.unsubscribe();
    }
  }

  showNavbar(): boolean {
    const hiddenRoutes = ['/login', '/register', '/forgot-password'];
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

  goToProfile() {
    this.isProfileMenuOpen = false;
    this.router.navigate(['/profile']);
  }

  logout() {
    localStorage.clear();
    this.isProfileMenuOpen = false;
    this.router.navigate(['/login']);
  }

  getDisplayName(): string {
    return this.profile?.displayName || this.profile?.username || 'User';
  }

  getProfilePictureUrl(): string {
    if (this.profile?.profilePictureUrl) {
      return 'http://localhost:8080' + this.profile.profilePictureUrl;
    }
    return '';
  }

  getInitial(): string {
    const name = this.profile?.displayName || this.profile?.username || 'U';
    return name.charAt(0).toUpperCase();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.profile-wrapper')) {
      this.isProfileMenuOpen = false;
    }
  }
}

