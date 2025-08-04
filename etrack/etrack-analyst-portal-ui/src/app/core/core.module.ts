import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthModule, OidcSecurityService } from 'angular-auth-oidc-client';
import { AuthService } from './auth/auth.service';
import { AuthGuard } from './auth/auth.guard';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    HttpClientModule,

    AuthModule.forRoot()
  ],
  providers: [
    OidcSecurityService,
    AuthService,
    AuthGuard
  ]
})
export class CoreModule { }
