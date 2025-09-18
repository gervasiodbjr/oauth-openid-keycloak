import { bootstrapApplication } from '@angular/platform-browser';
import { APP_INITIALIZER } from '@angular/core';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { appRoutes } from './app/app.routes';
import { KeycloakService } from './app/keycloak.service';

function initializeKeycloak(keycloakService: KeycloakService) {
  return () => keycloakService.init();
}

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(appRoutes),
    { provide: APP_INITIALIZER, useFactory: initializeKeycloak, deps: [KeycloakService], multi: true }
  ]
});
