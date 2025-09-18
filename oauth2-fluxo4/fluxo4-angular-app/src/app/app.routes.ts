import { Routes, CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { HomeComponent } from './pages/home/home.component';
import { UserComponent } from './pages/user/user.component';
import { AdminComponent } from './pages/admin/admin.component';
import { KeycloakService } from './keycloak.service';
import { UnautorizedComponent } from './pages/unautorized/unautorized.component';

// Guard simples com suporte a roles; redireciona para login se não autenticado
function authGuard(...requiredRoles: string[]): CanActivateFn {
  return () => {
    const keycloakService = inject(KeycloakService);
    const router = inject(Router);

    // Evitar perder o contexto de "this": nunca desestruture métodos/getters do service
    const token = keycloakService.token;

    if (!token) {
      // Não autenticado: inicia fluxo de login no Keycloak
      keycloakService.login();
      return false;
    }
    if (requiredRoles.length > 0 && !requiredRoles.some((role) => keycloakService.hasRole(role))) {
      // Sem permissão: redireciona para página de não autorizado
      router.navigate(['/unautorized']);
      return false;
    }
    return true;
  };
}

export const appRoutes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'unautorized', component: UnautorizedComponent },
  { path: 'user', component: UserComponent, canActivate: [authGuard('USER', 'ADMIN')] },
  { path: 'admin', component: AdminComponent, canActivate: [authGuard('ADMIN')] }
];
