import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class CurrencyService {
    private currencySubject = new BehaviorSubject<string>(localStorage.getItem('currency') || '$');
    currency$ = this.currencySubject.asObservable();

    constructor() { }

    getCurrency(): string {
        return this.currencySubject.value;
    }

    toggleCurrency() {
        const newCurrency = this.currencySubject.value === '$' ? '₹' : '$';
        this.currencySubject.next(newCurrency);
        localStorage.setItem('currency', newCurrency);
    }
}
