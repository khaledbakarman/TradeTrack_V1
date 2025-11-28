import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(private router: Router) { }

    canActivate(): boolean {
        const userId = localStorage.getItem('userId');

        if (userId) {
            return true;   // user is logged in → allow
        }

        // user NOT logged in → block and redirect
        this.router.navigate(['/login']);
        return false;
    }
}
