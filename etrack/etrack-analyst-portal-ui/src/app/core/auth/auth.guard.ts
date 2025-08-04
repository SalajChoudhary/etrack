import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, CanActivateChild, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { map } from 'rxjs/operators';

@Injectable()
export class AuthGuard implements CanActivate, CanActivateChild {

  constructor(private authService: AuthService, private router: Router) {}

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
   // console.log("In guard checking child authorization...");
    return this.authService.getIsAuthorized().pipe(
      map((isAuthorized: boolean) => {
        if (isAuthorized) {
       //   console.log(state.url);
          return true;
        }
       // console.log(state.url);
        this.authService.setRedirectUrl(state.url);
        
        this.router.navigate(['/login']);
        return false;
      })
    );
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
  //  console.log("In guard checking authorization...");
    return this.authService.getIsAuthorized().pipe(
      map((isAuthorized: boolean) => {
        if (isAuthorized) {
          let userInfo = this.authService.getUserInfo();
       //   console.log(userInfo);
          if(userInfo && !userInfo.group.includes("DEC_eTrack_users"))
                 this.router.navigate(['/unauthorized']);
            return true;
         // return ? true:false;
        }
       // console.log(state.url);
        this.authService.setRedirectUrl(state.url);
        
        this.router.navigate(['/login']);
        return false;
      })
    );
  }

}
