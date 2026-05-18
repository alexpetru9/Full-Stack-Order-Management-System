import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterModule,
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class RegisterComponent {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  errorMessage = signal<string | null>(null);

  private readonly passwordRegex =
    '^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$';

  registerForm = this.fb.group({
    name: [
      '',
      [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100),
        Validators.pattern('^[^0-9]+$'),
      ],
    ],
    age: [18, [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.pattern(this.passwordRegex)]],
  });

  submit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const formData = this.registerForm.getRawValue();

    this.http
      .post<{ success: boolean; message: string }>('http://localhost:8080/auth/register', formData)
      .subscribe({
        next: () => {
          alert('Account created successfully! You can now log in.');
          void this.router.navigate(['/login']);
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 400 && err.error) {
            const errorData = err.error as Record<string, string>;

            if (errorData['message']) {
              this.errorMessage.set(errorData['message']);
            } else {
              const keys = Object.keys(errorData);
              if (keys.length > 0) {
                const firstKey = keys[0];
                this.errorMessage.set(
                  errorData[firstKey] ?? 'An error occurred during registration.',
                );
              } else {
                this.errorMessage.set('An error occurred during registration.');
              }
            }
          } else {
            this.errorMessage.set('An error occurred during registration.');
          }
        },
      });
  }
}
