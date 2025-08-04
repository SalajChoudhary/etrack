import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpEvent, HttpHandler, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { OidcSecurityService }  from 'angular-auth-oidc-client';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    error!: {errorCode: number, errorMessage: string, traceId: string, uiMessage: string, timestamp: string}
    constructor(private authService: AuthService, 
                     private router: Router) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const jwt = this.authService.getToken();
        if (!!jwt) {
            req = req.clone({
                setHeaders: {
                    Authorization: `Bearer ${jwt}`
                }
            });
        }
        return next.handle(req).pipe(
            tap((event: HttpEvent<any>) => {
                let successMessage = '';
                if(event instanceof HttpResponse && event.status === 200) {
                    successMessage = 'Success!';
                }
                return successMessage;
            }),
            catchError((error: HttpErrorResponse) => {
                    // if(error && (error.status==401 || error.status==403)) {
                    //   // Unauthorized                   
                    //   alert("Your session has expired");
                    //  this.router.navigate(["/login"]);
                    // }
                
                // if (error.error instanceof ErrorEvent) {
                //     // client-side error
                //     this.error = {errorCode: 500, errorMessage: "null", traceId: "null", uiMessage: `Error: ${error.error.message}`, timestamp: "null"};
                // } else {
                //     // server-side error
                //     switch (error.status) {
                //         case 0:      //No Response
                //         // errorMessage = "Invalid entry.";
                //         this.error = {errorCode: error.status, errorMessage: error.error.message, traceId: error.error.traceId,
                //              uiMessage: "Internal Server Error. Please try again later.", timestamp: error.error.timestamp};
                //              this.router.navigate(['/oops']);
                //         break;
                //         case 400:      //Bad Request
                //             // errorMessage = "Invalid entry.";
                //             this.error = {errorCode: error.status, errorMessage: error.error.message, traceId: error.error.traceId,
                //                  uiMessage: "Invalid entry.", timestamp: error.error.timestamp};
                //                  this.router.navigate(['/oops']);
                //             break;
                //         case 401:      //Unauthorized
                //             this.router.navigate(['/login']);
                //             break;
                //         case 403:      //Forbidden
                //             this.router.navigate(['/unauthorized']);
                //             break;
                //         case 404:     //Not Found
                //             // errorMessage = "This search returned 0 results.";
                //             this.error = {errorCode: error.status, errorMessage: error.error.message, traceId: error.error.traceId,
                //                 uiMessage: "This search returned 0 results.", timestamp: error.error.timestamp};
                //                 this.router.navigate(['/notFound']);
                //             break;
                //             case 412:     //Not Found
                //             // errorMessage = "This search returned 0 results.";
                //             console.log(error);
                //             this.error = {errorCode: error.error.resultCode, errorMessage: error.error.resultMessage, traceId: error.error.traceId,
                //               uiMessage: error.error.resultCode, timestamp: error.error.timestamp};
                //             break;
                //             case 424:     //Not Found
                //             // errorMessage = "This search returned 0 results.";
                //             console.log(error);
                //             this.error = {errorCode: error.error.resultCode, errorMessage: error.error.resultMessage, traceId: error.error.traceId,
                //               uiMessage: error.error.resultCode, timestamp: error.error.timestamp};
                //             break;
                //             // case 412:     //Not Found
                //             // console.log(error);
                //             // this.error = {errorCode: error.error.resultCode, errorMessage: error.error.resultMessage, traceId: error.error.traceId,
                //             //   uiMessage: error.error.resultCode, timestamp: error.error.timestamp};
                //             // break;
                //         // case 408:     //Retry error
                //         //     // errorMessage = "This search returned 0 results.";
                //         //     this.error = {errorCode: error.status, errorMessage: error.error.message, traceId: error.error.traceId,
                //         //         uiMessage: "Exceeded maximum number of retries. Please try again.", timestamp: error.error.timestamp};
                //         //     break;
                //         case 500:     //Internal Server Error
                //             // errorMessage = "Internal Server Error. Please try again later.";
                //             this.error = {errorCode: error.status, errorMessage: error.error.message, traceId: error.error.traceId,
                //                 uiMessage: "Internal Server Error. Please try again later.", timestamp: error.error.timestamp};
                //                 //this.router.navigate(['/oops']);
                //             break;
                //         case 503:     //Service Unavailable
                //             // errorMessage= "Service currently unavailable. Please enter bill information below to be added.";
                //             this.error = {errorCode: error.status, errorMessage: error.error.message, traceId: error.error.traceId,
                //                 uiMessage: "Service currently unavailable. Please enter bill information below to be added.", timestamp: error.error.timestamp};
                //                 this.router.navigate(['/oops']);
                //             break;
                //     }
                // }
                if(error.status == 401){
                    console.log("status 401 :::::::::::")
                    const jwt = this.authService.getRefreshToken();
                    if (!!jwt) {
                        req = req.clone({
                            setHeaders: {
                                Authorization: `Bearer ${jwt}`
                            }
                        });
                    }
                }
                return throwError(error);
         
            })
        )
    }

}





 