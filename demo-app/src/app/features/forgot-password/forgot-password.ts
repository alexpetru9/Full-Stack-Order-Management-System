import { Component, inject, signal } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './forgot-password.html',
  styleUrls: ['./forgot-password.scss'],
})
export class ForgotPasswordComponent {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private router = inject(Router);

  codeSent = signal(false);
  isSubmitting = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  requestForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  resetForm = this.fb.group({
    token: ['', Validators.required],
    newPassword: ['', Validators.required],
  });

  get emailCtrl() {
    return this.requestForm.controls.email;
  }
  get tokenCtrl() {
    return this.resetForm.controls.token;
  }
  get newPasswordCtrl() {
    return this.resetForm.controls.newPassword;
  }

  sendCode() {
    if (this.requestForm.invalid) return;

    this.isSubmitting.set(true);
    this.errorMessage.set('');

    this.http
      .post('http://localhost:8080/auth/forgot-password', {
        email: this.requestForm.value.email,
      })
      .subscribe({
        next: () => {
          this.codeSent.set(true);
          this.isSubmitting.set(false);
          this.successMessage.set('Reset code sent to your email.');
        },
        error: (err: HttpErrorResponse) => {
          const errorData = err.error as { message?: string };
          this.errorMessage.set(errorData?.message ?? 'Failed to send code.');
          this.isSubmitting.set(false);
        },
      });
  }

  submitReset() {
    if (this.resetForm.invalid) return;

    this.isSubmitting.set(true);
    this.errorMessage.set('');

    const payload = {
      email: this.requestForm.value.email,
      token: this.resetForm.value.token,
      newPassword: this.resetForm.value.newPassword,
    };

    this.http.post('http://localhost:8080/auth/reset-password', payload).subscribe({
      next: () => {
        alert('Password updated successfully! You can now log in.');
        void this.router.navigate(['/login']);
      },
      error: (err: HttpErrorResponse) => {
        const errorData = err.error as { message?: string };
        this.errorMessage.set(errorData?.message ?? 'Failed to reset password.');
        this.isSubmitting.set(false);
      },
    });
  }
}
