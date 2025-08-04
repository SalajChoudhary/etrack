import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth/auth.service';
import { BehaviorSubject } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class SearchReportService {
  destroyAssociatedSub : BehaviorSubject<string> = new BehaviorSubject<string> ('');
  constructor(public http: HttpClient, public authService: AuthService) {}


  setDestroyAssociatedSub(txt: string){
    this.destroyAssociatedSub.next(txt);
  }

  getDestroyAssociatedSub(){
    return this.destroyAssociatedSub.asObservable();
  }

  getPermiTypes() {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId:'2132',
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/keyword/permitTypes`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  permitKeyWordData(){
    let user = localStorage.getItem('loggedUserName');    
    const options = {
      headers: new HttpHeaders({
        userId: user!.replace('SVC', '').substring(1),      
        // @ts-ignore
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/keyword/candidate`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  } 

  replaceCandidateKeyword(payload:any, replaceKey: any){
    let user = localStorage.getItem('loggedUserName');
    const body = replaceKey;
        const options = {
      observe: 'response' as const,
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        replacementkeyword: payload,

      }) }
      return this.http
    .post<any>(
      `${environment.apiBaseurl}/etrack-permit/keyword/replace-candidate`, body,
      options
    );
    
  }

  loadCandidateKeywordReport() {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
       
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/query/candidate-keywords`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  loadProjectReport(payload:any){

    let user = localStorage.getItem('loggedUserName');
    const body = payload;
        const options = {
      observe: 'response' as const,
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      }) }
      return this.http
    .post<any>(
      `${environment.apiBaseurl}/etrack-dart-db/query/project-submittal`, body,
      options
    );

    // let user = localStorage.getItem('loggedUserName');  
    // const body= payload;  
    // const options = {
    //   headers: new HttpHeaders({
    //     userId: user!.replace('SVC', '').substring(1)
    //   }),
    // };
    // return this.http
    //   .post<any>(`${environment.apiBaseurl}/etrack-dart-db/query/project-submittal`, options)
    //   .toPromise()
    //   .then((data) => {
    //     return data;
    //   });
  }

  
}
