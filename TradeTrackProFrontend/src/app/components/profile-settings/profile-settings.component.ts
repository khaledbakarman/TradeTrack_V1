import { Component, OnInit, OnDestroy } from '@angular/core';
import { ProfileService, UserProfile } from '../../services/profile.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-profile-settings',
  templateUrl: './profile-settings.component.html',
  styleUrls: ['./profile-settings.component.scss']
})
export class ProfileSettingsComponent implements OnInit, OnDestroy {

  profile: UserProfile | null = null;
  loading = true;

  // Form fields
  newUsername = '';
  newDisplayName = '';

  // Password change
  oldPassword = '';
  newPassword = '';
  confirmNewPassword = '';

  // Security question
  newSecurityQuestion = '';
  newSecurityAnswer = '';

  // Profile picture
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  showPreviewModal = false;

  // UI state
  savingUsername = false;
  savingDisplayName = false;
  savingPassword = false;
  savingSecurityQuestion = false;
  uploadingPicture = false;

  // Messages
  successMessage = '';
  errorMessage = '';

  securityQuestions = [
    'What is the name of your first pet?',
    'What city were you born in?',
    'What is your mother\'s maiden name?',
    'What was the name of your first school?',
    'What is your favorite movie?',
    'What is your favorite book?'
  ];

  private subscription: Subscription | null = null;

  constructor(private profileService: ProfileService) { }

  ngOnInit(): void {
    this.loadProfile();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  loadProfile(): void {
    this.loading = true;
    this.profileService.getProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.newUsername = profile.username || '';
        this.newDisplayName = profile.displayName || '';
        this.newSecurityQuestion = profile.securityQuestion || '';
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load profile';
        this.loading = false;
        console.error(err);
      }
    });
  }

  showSuccess(message: string): void {
    this.successMessage = message;
    this.errorMessage = '';
    setTimeout(() => this.successMessage = '', 3000);
  }

  showError(message: string): void {
    this.errorMessage = message;
    this.successMessage = '';
    setTimeout(() => this.errorMessage = '', 5000);
  }

  // Update Username
  updateUsername(): void {
    if (!this.newUsername.trim()) {
      this.showError('Username cannot be empty');
      return;
    }

    this.savingUsername = true;
    this.profileService.updateUsername(this.newUsername).subscribe({
      next: (profile) => {
        this.profile = profile;
        this.savingUsername = false;
        this.showSuccess('Username updated successfully!');
      },
      error: (err) => {
        this.savingUsername = false;
        this.showError(err.error?.error || 'Failed to update username');
      }
    });
  }

  // Update Display Name
  updateDisplayName(): void {
    if (!this.newDisplayName.trim()) {
      this.showError('Display name cannot be empty');
      return;
    }

    this.savingDisplayName = true;
    this.profileService.updateDisplayName(this.newDisplayName).subscribe({
      next: (profile) => {
        this.profile = profile;
        this.savingDisplayName = false;
        this.showSuccess('Display name updated successfully!');
      },
      error: (err) => {
        this.savingDisplayName = false;
        this.showError(err.error?.error || 'Failed to update display name');
      }
    });
  }

  // Profile Picture
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedFile = input.files[0];

      // Create preview
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewUrl = e.target?.result as string;
        this.showPreviewModal = true;
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  confirmUpload(): void {
    if (!this.selectedFile) return;

    this.uploadingPicture = true;
    this.showPreviewModal = false;

    this.profileService.updateProfilePicture(this.selectedFile).subscribe({
      next: (profile) => {
        this.profile = profile;
        this.uploadingPicture = false;
        this.selectedFile = null;
        this.previewUrl = null;
        this.showSuccess('Profile picture updated successfully!');
      },
      error: (err) => {
        this.uploadingPicture = false;
        this.showError(err.error?.error || 'Failed to upload picture');
      }
    });
  }

  cancelUpload(): void {
    this.showPreviewModal = false;
    this.selectedFile = null;
    this.previewUrl = null;
  }

  removeProfilePicture(): void {
    if (!confirm('Are you sure you want to remove your profile picture?')) return;

    this.uploadingPicture = true;
    this.profileService.removeProfilePicture().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.uploadingPicture = false;
        this.showSuccess('Profile picture removed!');
      },
      error: (err) => {
        this.uploadingPicture = false;
        this.showError(err.error?.error || 'Failed to remove picture');
      }
    });
  }

  // Change Password
  changePassword(): void {
    if (!this.oldPassword || !this.newPassword) {
      this.showError('Please fill in all password fields');
      return;
    }

    if (this.newPassword !== this.confirmNewPassword) {
      this.showError('New passwords do not match');
      return;
    }

    if (this.newPassword.length < 4) {
      this.showError('Password must be at least 4 characters');
      return;
    }

    this.savingPassword = true;
    this.profileService.changePassword(this.oldPassword, this.newPassword).subscribe({
      next: (response) => {
        this.savingPassword = false;
        if (response.success) {
          this.showSuccess('Password changed successfully!');
          this.oldPassword = '';
          this.newPassword = '';
          this.confirmNewPassword = '';
        } else {
          this.showError(response.error || 'Failed to change password');
        }
      },
      error: (err) => {
        this.savingPassword = false;
        this.showError(err.error?.error || 'Failed to change password');
      }
    });
  }

  // Change Security Question
  changeSecurityQuestion(): void {
    if (!this.newSecurityQuestion) {
      this.showError('Please select a security question');
      return;
    }

    if (!this.newSecurityAnswer.trim()) {
      this.showError('Please enter your security answer');
      return;
    }

    this.savingSecurityQuestion = true;
    this.profileService.changeSecurityQuestion(this.newSecurityQuestion, this.newSecurityAnswer).subscribe({
      next: (profile) => {
        this.profile = profile;
        this.savingSecurityQuestion = false;
        this.newSecurityAnswer = '';
        this.showSuccess('Security question updated successfully!');
      },
      error: (err) => {
        this.savingSecurityQuestion = false;
        this.showError(err.error?.error || 'Failed to update security question');
      }
    });
  }

  getProfilePictureUrl(): string {
    if (this.profile?.profilePictureUrl) {
      return 'http://localhost:8080' + this.profile.profilePictureUrl;
    }
    return '';
  }
}
