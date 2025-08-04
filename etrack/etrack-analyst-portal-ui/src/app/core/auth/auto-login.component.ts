import { Component, OnInit} from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-auto-login',
  template: `<div class="container-fluid"></div>`,
})
export class AutoLoginComponent implements OnInit {

  constructor(public oidcSecurityService: OidcSecurityService) {
    this.oidcSecurityService.onModuleSetup.subscribe(() => { this.login(); });
  }

  ngOnInit() {
    if (this.oidcSecurityService.moduleSetup) {
      this.login();
    }
  }

  private login() {
    this.oidcSecurityService.authorize();
  }

}
