import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <section>
      <div style="font-size: 44px; color: blue;">Área de Admin</div>
      <a routerLink="/">Voltar para Home</a>
    </section>
  `
})
export class AdminComponent {}
