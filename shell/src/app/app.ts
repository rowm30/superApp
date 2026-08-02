import { JsonPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from './auth.config';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, JsonPipe],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private oauth = inject(OAuthService);

  private http = inject(HttpClient);

  claims = signal<any>(null);

  apiResult = signal<any>(null);

  apiError = signal<string | null>(null);

  ngOnInit(){
    this.oauth.configure(authConfig);

    this.oauth.loadDiscoveryDocumentAndTryLogin().then(()=>{

      if(this.oauth.hasValidAccessToken()){
        this.claims.set(this.oauth.getIdentityClaims());
      }
    });
  }

  callApi(){

    this.apiError.set(null);
    this.apiResult.set(null);

    const token = this.oauth.getAccessToken();

    this.http.get('http://localhost:8081/me',{
      headers:{Authorization: 'Bearer '+ token}
    })
    .subscribe({
      next: (data)=> this.apiResult.set(data),

      error: (err) => this.apiError.set(err.status + ' - '+ err.message)
  })
  }

  login(){
    this.oauth.initLoginFlow();
  }

  logout(){
    this.oauth.logOut();
  }
}
