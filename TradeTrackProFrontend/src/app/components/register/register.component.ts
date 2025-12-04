import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  username = '';
  password = '';
  confirmPassword = '';

  constructor(private authService: AuthService, private router: Router) { }

  register() {
    if (this.password !== this.confirmPassword) {
      alert("Passwords do not match!");
      return;
    }

    const body = {
      username: this.username,
      password: this.password
    };

    this.authService.register(body.username, body.password).subscribe({
      next: (response) => {
        alert('Registration successful!');
        console.log('Backend:', response);
        this.router.navigate(['/login']);
      },
      error: (error: any) => {
        if (error.status === 400) {
          alert('Username already exists!');
        } else {
          alert('Registration failed!');
        }
        console.error(error);
      }
    });
  }
}
