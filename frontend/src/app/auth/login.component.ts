// src/app/auth/login.component.ts
import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from './auth.service'; 

@Component({
selector: 'app-login',
standalone: true,
imports: [CommonModule, ReactiveFormsModule, RouterLink],
templateUrl: './login.component.html',
styleUrls: ['./login.component.scss']
})
export class LoginComponent {
private fb = inject(FormBuilder);
private auth = inject(AuthService);
private router = inject(Router);

hidePassword = signal(true);
loading = signal(false);
errorMsg = signal('');

form: FormGroup = this.fb.group({
email: ['', [Validators.required, Validators.email]],
password: ['', [Validators.required, Validators.minLength(6)]],
    remember: [true],
  });

  submit() {
  this.errorMsg.set('');
  if (this.form.invalid) { this.form.markAllAsTouched(); return; }

  this.loading.set(true);

  const email = this.form.get('email')?.value ?? '';
  const senha = this.form.get('password')?.value ?? this.form.get('senha')?.value ?? '';

  this.auth.login(email, senha).subscribe({
    next: (res: any) => {
      const token = res?.token ?? res?.accessToken ?? '';
      if (token) localStorage.setItem('token', token);
      this.router.navigateByUrl('/painel');
    },
    error: (err: HttpErrorResponse) => {
      this.errorMsg.set(err.error?.message || 'Falha ao autenticar. Verifique suas credenciais.');
      this.loading.set(false);
    },
    complete: () => this.loading.set(false)
  });
}
}

