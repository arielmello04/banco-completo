// src/app/auth/login.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent implements OnInit {
  form!: FormGroup;
  error = '';

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log('LoginComponent carregado');
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      senha: ['', Validators.required],
    });
  }

  onSubmit() {
  if (this.form.invalid) return;

  const { email, senha } = this.form.value;

  this.auth.login(email!, senha!).subscribe({
    next: (res) => {
      console.log('Login bem-sucedido:', res);

      // Role recebido da resposta da API
      const role = res.role;

      if (role === 'ADMIN') {
      this.router.navigateByUrl('/painel');
      } else if (role === 'GERENTE') {
        this.router.navigateByUrl('/painel');
      } else if (role === 'CLIENTE') {
        this.router.navigateByUrl('/cliente');
      }
    },
    error: () => (this.error = 'Credenciais inválidas'),
  });
}
}
