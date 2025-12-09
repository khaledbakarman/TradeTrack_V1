import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface UserProfile {
    id: number;
    username: string;
    displayName: string;
    profilePictureUrl: string | null;
    securityQuestion: string;
}

@Injectable({
    providedIn: 'root'
})
export class ProfileService {

    private baseUrl = 'http://localhost:8080/api/user';

    // BehaviorSubject to share profile data across components
    private profileSubject = new BehaviorSubject<UserProfile | null>(null);
    public profile$ = this.profileSubject.asObservable();

    constructor(private http: HttpClient) { }

    getProfile(): Observable<UserProfile> {
        return this.http.get<UserProfile>(`${this.baseUrl}/profile`).pipe(
            tap(profile => this.profileSubject.next(profile))
        );
    }

    updateUsername(newUsername: string): Observable<UserProfile> {
        return this.http.put<UserProfile>(`${this.baseUrl}/update-username`, { newUsername }).pipe(
            tap(profile => this.profileSubject.next(profile))
        );
    }

    updateDisplayName(displayName: string): Observable<UserProfile> {
        return this.http.put<UserProfile>(`${this.baseUrl}/update-display-name`, { displayName }).pipe(
            tap(profile => this.profileSubject.next(profile))
        );
    }

    updateProfilePicture(file: File): Observable<UserProfile> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post<UserProfile>(`${this.baseUrl}/update-profile-picture`, formData).pipe(
            tap(profile => this.profileSubject.next(profile))
        );
    }

    removeProfilePicture(): Observable<UserProfile> {
        return this.http.delete<UserProfile>(`${this.baseUrl}/remove-profile-picture`).pipe(
            tap(profile => this.profileSubject.next(profile))
        );
    }

    changePassword(oldPassword: string, newPassword: string): Observable<{ success: boolean; message?: string; error?: string }> {
        return this.http.put<{ success: boolean; message?: string; error?: string }>(`${this.baseUrl}/change-password`, { oldPassword, newPassword });
    }

    changeSecurityQuestion(securityQuestion: string, securityAnswer: string): Observable<UserProfile> {
        return this.http.put<UserProfile>(`${this.baseUrl}/change-security-question`, { securityQuestion, securityAnswer }).pipe(
            tap(profile => this.profileSubject.next(profile))
        );
    }

    // Helper to get current profile value
    getCurrentProfile(): UserProfile | null {
        return this.profileSubject.getValue();
    }
}
