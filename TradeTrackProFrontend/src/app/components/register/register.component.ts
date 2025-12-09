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
  securityQuestion = '';
  securityAnswer = '';

  securityQuestions = [
    'What is the name of your first pet?',
    'What city were you born in?',
    'What is your mother\'s maiden name?',
    'What was the name of your first school?',
    'What is your favorite movie?',
    'What is your favorite book?'
  ];

  constructor(private authService: AuthService, private router: Router) { }

  register() {
    if (!this.securityQuestion) {
      alert('Please select a security question!');
      return;
    }

    if (!this.securityAnswer.trim()) {
      alert('Please enter your security answer!');
      return;
    }

    if (this.password !== this.confirmPassword) {
      alert('Passwords do not match!');
      return;
    }

    this.authService.register(this.username, this.password, this.securityQuestion, this.securityAnswer).subscribe({
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

