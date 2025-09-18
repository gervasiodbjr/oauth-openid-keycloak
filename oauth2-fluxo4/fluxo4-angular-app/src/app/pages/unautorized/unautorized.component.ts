import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-unautorized',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <section>
      <div style="font-size: 44px; color: red;">Acesso negado</div>
      <p>Usuário não autorizado para acessar o recurso.</p>
      <a routerLink="/">Voltar para Home</a>
    </section>
  `
})
export class UnautorizedComponent {}
