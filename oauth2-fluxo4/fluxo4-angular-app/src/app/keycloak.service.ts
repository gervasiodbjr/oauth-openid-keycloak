import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({ providedIn: 'root' })
export class KeycloakService {
  private keycloak: any;
  constructor() {
    this.keycloak = new Keycloak({
      url: 'http://localhost:8080/',
      realm: 'project-realm',
      clientId: 'project-client04',
    });
  }

  async init() {
    try {
      await this.keycloak.init({ onLoad: 'login-required', checkLoginIframe: false });
    } catch (e) {
      console.error('Erro ao inicializar Keycloak', e);
      throw e;
    }
  }
  login() {
    return this.keycloak.login();
  }
  logout() {
    return this.keycloak.logout();
  }
  get token() {
    return this.keycloak.token as string | undefined;
  }
  hasRole(role: string) {
    return this.keycloak.hasRealmRole(role);
  }

}
