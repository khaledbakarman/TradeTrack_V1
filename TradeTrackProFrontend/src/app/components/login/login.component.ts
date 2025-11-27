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

  constructor(private authService: AuthService, private router: Router) { }

  submit() {
    this.authService.login(this.username, this.password).subscribe({
      next: (res) => {

        // Store userId so the entire app knows who is logged in
        localStorage.setItem('userId', res.userId);

        // Navigate to /trades
        this.router.navigate(['/trades']);
      },
      error: (err) => {
        console.error('Login failed', err);
        alert('Invalid username or password');
      }
    });
  }
}
