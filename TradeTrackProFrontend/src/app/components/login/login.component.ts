import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  username = '';
  password = '';

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        alert('Login successful!');
        const incomingUserId = response?.userId;
        if (incomingUserId) {
          localStorage.setItem('userId', incomingUserId.toString());
        } else {
          localStorage.setItem('userId', '3');
        }
        this.router.navigate(['/trades']);
      },
      error: () => {
        alert('Login failed! Check username/password');
      }
    });
  }
}
