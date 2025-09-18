import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink } from '@angular/router';
import { KeycloakService } from './keycloak.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  template: `
    <header style="padding: 1rem; background:#f5f5f5; text-align: center; font-size: 24px;">
      <h1 style="margin:0; font-size: 24px;">Sistema Angular Com OAuth2</h1>
      <nav style="
        margin-top: 1rem;
        display: flex;
        justify-content: center;
        gap: .75rem;
        flex-wrap: wrap;
      ">
        <a routerLink="/">Home</a>
        <a routerLink="/user">User</a>
        <a routerLink="/admin">Admin</a>
        <button type="button" (click)="logout()" style="font-size: 20px;">Logout</button>
      </nav>
    </header>
    <main style="
      padding: 1rem;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 50vh;
      text-align: center;
      font-size: 24px;
    ">
      <router-outlet />
    </main>
  `
})
export class AppComponent {
  constructor(private keycloakService: KeycloakService) {}
  logout() {
    this.keycloakService.logout();
  }
}
