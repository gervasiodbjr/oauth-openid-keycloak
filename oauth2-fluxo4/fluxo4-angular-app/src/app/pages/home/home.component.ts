import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <section>
      <div style="font-size: 44px; color: blue;">Página Inicial</div>
      <p>Bem-vindo ao Sistema Angular com OAuth2</p>
      <nav style="margin-top:.5rem; display:flex; gap:.5rem;">
        <a routerLink="/user">Ir para área do Usuário</a>
        <a routerLink="/admin">Ir para área de Admin</a>
      </nav>
    </section>
  `
})
export class HomeComponent {}
