import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-painel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './painel.component.html',
  styleUrls: ['./painel.component.scss']
})
export class PainelComponent implements OnInit {
  role: string | null = null;

  constructor(private auth: AuthService, private router: Router) {}
  sair() {
  this.auth.logout?.();
  localStorage.removeItem('token');
  this.router.navigateByUrl('/login');
  }

  ngOnInit(): void {
    this.role = this.auth.getUserRole();
  }

  irParaDashboard() {
    this.router.navigateByUrl('/dashboard');
  }
}
