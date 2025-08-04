import { Injectable, OnDestroy } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Subscription, Observable, throwError, BehaviorSubject, Subject, fromEvent, merge } from 'rxjs';
import {
  OidcSecurityService,
  OpenIdConfiguration,
  AuthWellKnownEndpoints,
  AuthorizationResult,
  AuthorizationState,
} from 'angular-auth-oidc-client';
import { Router } from '@angular/router';
// import { UserInfo } from 'app/interfaces/user-info';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { UserInfo } from '../../@shared/interfaces/user-info';
import { Utils } from 'src/app/@shared/services/utils';

@Injectable()
export class AuthService implements OnDestroy {
  clientId: string;
  userName: string="";
  authUrl: string;
  //afterLoginUrl: string;
  redirectUri: string;
  post_login_route: string;
  private originUrl: string;
  private isAuthorizedSubscription: Subscription = new Subscription();
  openIdConfiguration!: OpenIdConfiguration;
  authWellKnownEndpoints!: AuthWellKnownEndpoints;
  readonly revokeTokenOnLogout = true;
  isAuthorized = false;
  private hasStorage: boolean;
  userInfo!: UserInfo;
  emitAuthInfo=new BehaviorSubject<any>(null);
  userActivitySubscription = new Subscription();
  private inactiveSessionTimer : any;
  private sessionTimeout : number;
  private lastTimeActive!: number;
  private setIdleInterval = 10000;
  emitInactiveSessionInfo = new BehaviorSubject<boolean>(false);


  constructor(
    private oidcSecurityService: OidcSecurityService,
    private router: Router,
    public utils: Utils,
    private http: HttpClient
  ) {
    this.hasStorage = typeof Storage !== 'undefined';
    this.clientId = environment.clientId;
    this.authUrl = environment.authUrl;
    //this.afterLoginUrl = environment.afterLoginUrl;
    this.redirectUri = environment.redirectUri;
    this.originUrl = document.getElementsByTagName('base')[0].href;
    this.post_login_route = '/dashboard';
    this.sessionTimeout = environment.sessionTimeout;
  }

  ngOnDestroy(): void {
    if (this.isAuthorizedSubscription) {
      this.isAuthorizedSubscription.unsubscribe();
    }
    if (this.userActivitySubscription) {
      this.userActivitySubscription.unsubscribe();
    }
  }

  public initAuth() {
    this.openIdConfiguration = {
      stsServer: this.authUrl,
      redirect_url: this.redirectUri,
      client_id: this.clientId,
      response_type: 'code',
      scope: 'openid profile email',
      post_logout_redirect_uri: environment.logoutRedirectUrl, //this.originUrl, // + '/login',
      trigger_authorization_result_event: true,
      post_login_route: this.post_login_route,
      forbidden_route: '/forbidden',
      unauthorized_route: '/unauthorized',
      //silent_renew: true,
      //silent_renew_url: this.originUrl + 'silent-renew.html',
      history_cleanup_off: true,
      auto_userinfo: false,
      log_console_warning_active: false,
      log_console_debug_active: false,
      max_id_token_iat_offset_allowed_in_seconds: 10,
    };

    this.authWellKnownEndpoints = {
      issuer: this.authUrl,
      jwks_uri: this.authUrl + "/discovery/keys?client_id=" + this.clientId,
      authorization_endpoint: this.authUrl + "/oauth2/authorize",
      token_endpoint: this.authUrl + "/oauth2/token",
      userinfo_endpoint: this.authUrl + "/userinfo",
      end_session_endpoint: this.authUrl + "/oauth2/logout",
      check_session_iframe: this.authUrl + "/v1/checksession",
      revocation_endpoint: this.authUrl + "/v1/revoke",
      introspection_endpoint: this.authUrl + "/v1/introspect",
    };

    this.oidcSecurityService.setupModule(
      this.openIdConfiguration,
      this.authWellKnownEndpoints
    );

    this.isAuthorizedSubscription = this.oidcSecurityService
      .getIsAuthorized()
      .subscribe((isAuthorized) => {
        // debugger;
        this.isAuthorized = isAuthorized;
      });
    this.oidcSecurityService.onAuthorizationResult.subscribe(
      (authorizationResult: AuthorizationResult) => {
        this.onAuthorizationResultComplete(authorizationResult);
      }
    );
    this.initEvents();
    this.initSessionTimeout();
  }

  private onAuthorizationResultComplete(
    authorizationResult: AuthorizationResult
  ) {
    // console.log('Auth result received AuthorizationState:'
    //   + authorizationResult.authorizationState
    //   + ' validationResult:' + authorizationResult.validationResult);

    if (
      authorizationResult.authorizationState === AuthorizationState.authorized
    ) {
      //console.log("Before get ID token");
      let accessToken = this.getToken();
      this.utils.emitLoadingEmitter(false);
      //this.userInfoService.postUserInfo(accessToken);
      //console.log("After get ID token");
      this.userInfo = this.getIdentityClaim();
      if (this.userInfo.unique_name.length > 0) {
       this.setRedirectUrl('/dashboard');
       console.log(this.userInfo);
       window.location.href="/dashboard";
      }
      const redirectUrl: string = this.getRedirectUrl()
        ? this.getRedirectUrl()
        : this.post_login_route;

      this.router.navigate([redirectUrl]);
    } else {
      this.router.navigate(['/unauthorized']);
    }
  }

  onOidcModuleSetup() {
    //console.log("AuthService:onOidcModuleSetup");
    if (this.oidcSecurityService.moduleSetup) {
      this.doCallbackLogicIfRequired();
    } else {
      this.oidcSecurityService.onModuleSetup.subscribe(() => {
        this.doCallbackLogicIfRequired();
      });
    }
  }

//   refreshToken(){
    //const token2 = this.oidcSecurityService.getRefreshToken();
//     const state = this.oidcSecurityService.getState();
//     //console.log("In getToken method - "+token);
//     console.log("refresh token", this.oidcSecurityService.getToken());
//    // console.log(token2);
//   //  this.oidcSecurityService.refreshTokensWithCodeProcedure(token2,state)
//   //  .subscribe(response=>{
//   //    console.log("refresh data");
//   //    console.log(response);
//   //  })
//     this.getNewAccessToken(token2)
//     .then(res=>{
//       console.log("response");
//       console.log(res);
//     })
//   }

//   getNewAccessToken(refresh_token: string){
//     let formData = new FormData();
// formData.append( "grant_type", "refresh_token");
// formData.append("client_id", "cf379bae-e302-4587-972f-eba42af6d8fd");
// formData.append("refresh_token", refresh_token);

//     return this.http
//     .toPromise()
//     .then((data) => {
//       return data;
//     });
//   }

  doCallbackLogicIfRequired() {
    if (window.location.hash || window.location.search) {
      this.oidcSecurityService.authorizedCallbackWithCode(
        window.location.toString()
      );
      //console.log("in Callback");
    } else {
      //console.log('AuthService:redirect to auto-login');
      this.getIsAuthorized().subscribe((authorized: boolean) => {
        if (!authorized) {
          this.router.navigate(['/login']);
        }
      });
    }
  }

  getIsAuthorized(): Observable<boolean> {
    return this.oidcSecurityService.getIsAuthorized();
  }

  login() {
    console.log('start login');
    this.oidcSecurityService.authorize();
  }

  logout() {
    console.log('start logoff');
    if(this.inactiveSessionTimer) {
      clearTimeout(this.inactiveSessionTimer);
    }
    // if (this.revokeTokenOnLogout) {
    //   const token = this.oidcSecurityService.getToken();
    //   this.oidcSecurityService.logoff(url => this.revokeToken(token));
    // }
    // else {
    this.oidcSecurityService.logoff();
  }

  // private revokeToken(token: string) {
  //   console.log('Revoking token = ' + token);
  //   const headers = new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')
  //   let urlSearchParams = new URLSearchParams();
  //   urlSearchParams.append('token', token);
  //   urlSearchParams.append('token_type_hint', 'access_token');
  //   urlSearchParams.append('client_id', this.openIdConfiguration.client_id);
  //   this.http.post(this.authWellKnownEndpoints.revocation_endpoint, urlSearchParams.toString(), { headers })
  //     .subscribe(result => {
  //       console.log('Access token and related refresh token (if any) have been successfully revoked');
  //     }, (error) => {
  //       console.error('Something went wrong on token revocation');
  //       this.oidcSecurityService.handleError(error);
  //       return throwError(error);
  //     });
  // }

  public getToken() {
    const token = this.oidcSecurityService.getToken();
    return token;
  }

  public getRefreshToken() {
    const token = this.oidcSecurityService.getRefreshToken();
    return token;
  }

  public getIdentityClaim() {
    return this.oidcSecurityService.getPayloadFromIdToken();
  }

  public getUserInfo(): UserInfo {
    let userInfo = {} as UserInfo;
   // console.log(this.getIdentityClaim(),'user payload')
    userInfo.first_name=this.getIdentityClaim().given_name;
    userInfo.last_name=this.getIdentityClaim().family_name;
    userInfo.unique_name = this.getIdentityClaim().unique_name;
    userInfo.upn = this.getIdentityClaim().upn;
    userInfo.group = this.getIdentityClaim().group;
    userInfo.ppid = this.getIdentityClaim().ppid;
    return userInfo;
  }

  public getRedirectUrl(): any {
    if (this.hasStorage) {
      return sessionStorage.getItem('redirectUrl');
    }
    return null;
  }

  public setRedirectUrl(url: string): void {
    if (this.hasStorage) {
      sessionStorage.setItem('redirectUrl', url);
    }
  }

  public removeRedirectUrl(): void {
    sessionStorage.removeItem('redirectUrl');
  }

  private initEvents(): void {
    const mouseMove = fromEvent(document, 'mousemove');
    const keyDown = fromEvent(document, 'keydown');
    const click = fromEvent(document, 'click');
    const wheel = fromEvent(document, 'wheel');
    const userActivity = merge(mouseMove, keyDown, click, wheel);
    this.lastTimeActive = Date.now();
    this.userActivitySubscription = userActivity.subscribe(() => {
        //set idle to 'n' some time after last detected activity
        if(this.hasStorage && Date.now() - this.lastTimeActive > this.setIdleInterval) {
          localStorage.setItem('idle', 'n');
          this.lastTimeActive = Date.now();
          this.resetTimer();
        }
    });
    if (this.hasStorage) {
      localStorage.setItem('sessionTimedout', 'n');
      localStorage.setItem('idle', 'y');
    }
    window.addEventListener('storage', (event: StorageEvent) => {
      if(event.storageArea == localStorage) {
        if (this.hasStorage) {
          if(localStorage.getItem('sessionTimedout') === 'y') {
            this.onSessionTimeout();
          }
        }
      }
    });
    window.addEventListener('storage', (event: StorageEvent) => {
      if(event.storageArea == localStorage) {
        //Detect user activity from other tabs
        if(localStorage.getItem('idle') === 'n') {
          this.resetTimer();
          setTimeout(() => {
            localStorage.setItem('idle', 'y');
          }, 500);
        }
      }
    });
  }

  private resetTimer(): void {
    if(this.inactiveSessionTimer) {
      clearTimeout(this.inactiveSessionTimer);
    }
    this.initSessionTimeout();
  }

  private initSessionTimeout(): void {
    this.inactiveSessionTimer = setTimeout(() => {
      if (this.hasStorage) {
        localStorage.setItem('sessionTimedout', 'y');
      }
      this.onSessionTimeout();
    }, this.sessionTimeout);
  }

  private onSessionTimeout() {
    this.userActivitySubscription.unsubscribe();
    this.emitInactiveSessionInfo.next(true);
  }
}
