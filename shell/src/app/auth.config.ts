import { AuthConfig } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {
  issuer: 'http://localhost:8080/realms/superapp',
  redirectUri: window.location.origin,
  clientId: 'web-shell',
  responseType: 'code',
  scope: 'openid profile email',
  showDebugInformation: true,
};