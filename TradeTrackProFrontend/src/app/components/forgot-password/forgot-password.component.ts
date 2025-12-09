import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {
  // Step 1: Enter username
  // Step 2: Answer security question
  // Step 3: Enter new password
  currentStep = 1;

  username = '';
  securityQuestion = '';
  securityAnswer = '';
  newPassword = '';
  confirmNewPassword = '';

  loading = false;
  error = '';
  success = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  // Step 1: Fetch security question
  fetchSecurityQuestion() {
    if (!this.username.trim()) {
      this.error = 'Please enter your username';
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.getSecurityQuestion(this.username).subscribe({
      next: (response) => {
        this.securityQuestion = response.securityQuestion;
        this.currentStep = 2;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'User not found';
        this.loading = false;
        console.error(err);
      }
    });
  }

  // Step 2: Verify security answer
  verifyAnswer() {
    if (!this.securityAnswer.trim()) {
      this.error = 'Please enter your answer';
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.verifySecurityAnswer(this.username, this.securityAnswer).subscribe({
      next: (response) => {
        if (response.success) {
          this.currentStep = 3;
          this.loading = false;
        } else {
          this.error = 'Incorrect answer. Please try again.';
          this.loading = false;
        }
      },
      error: (err) => {
        this.error = 'Verification failed. Please try again.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  // Step 3: Reset password
  resetPassword() {
    if (!this.newPassword.trim()) {
      this.error = 'Please enter a new password';
      return;
    }

    if (this.newPassword !== this.confirmNewPassword) {
      this.error = 'Passwords do not match';
      return;
    }

    if (this.newPassword.length < 4) {
      this.error = 'Password must be at least 4 characters';
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.resetPassword(this.username, this.newPassword).subscribe({
      next: (response) => {
        if (response.success) {
          this.success = 'Password reset successful! Redirecting to login...';
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        } else {
          this.error = 'Failed to reset password';
          this.loading = false;
        }
      },
      error: (err) => {
        this.error = 'Failed to reset password. Please try again.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  goBack() {
    if (this.currentStep > 1) {
      this.currentStep--;
      this.error = '';
    }
  }
}
