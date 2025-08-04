import { Component, OnInit } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { AuthService } from './auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Utils } from 'src/app/@shared/services/utils';

@Component({
  selector: 'app-auth-callback',
  template: `<div></div>`
})
export class AuthCallbackComponent implements OnInit {

  constructor(public utils : Utils,private authService: AuthService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    this.authService.onOidcModuleSetup();
    this.utils.emitLoadingEmitter(true);

    this.route.queryParams.subscribe(params => {
      if(params['error']) {
        this.router.navigate(['/unauthorized']);
      }
    })
    
  }

}
