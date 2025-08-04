import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth/auth.service';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class KeywordMaintainService {

  destroyAssociatedSub : BehaviorSubject<string> = new BehaviorSubject<string> ('');
  constructor(public http: HttpClient, public authService: AuthService) {}


  setDestroyAssociatedSub(txt: string){
    this.destroyAssociatedSub.next(txt);
  }

  getDestroyAssociatedSub(){
    return this.destroyAssociatedSub.asObservable();
  }

  loadKeywordDropDownList(){
    let user = localStorage.getItem('loggedUserName');    
    const options = {
      headers: new HttpHeaders({
        userId: user!.replace('SVC', '').substring(1)
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/keyword/category`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  loadKeywordData(id?: any) {
    let user = localStorage.getItem('loggedUserName');    
    const options = {
      headers: new HttpHeaders({
        userId: user!.replace('SVC', '').substring(1)
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/keyword${id ? '/' + id : ''}`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  permitKeyWordData(){
    let user = localStorage.getItem('loggedUserName');    
    let projectId = localStorage.getItem('projectId');
    console.log("test", projectId)
    const options = {
      headers: new HttpHeaders({
        userId: user!.replace('SVC', '').substring(1),      
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/keyword/project`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  updateKeyword(payload:any, associatedInd: any, queryProjectId?: any) {
    let user = localStorage.getItem('loggedUserName'); 
    let projectId = queryProjectId || localStorage.getItem('projectId');   
    const options = {
      headers: new HttpHeaders({
        userId: user!.replace('SVC', '').substring(1),
        projectId: projectId!
      }),
    };
    return this.http
      .post<any>(`${environment.apiBaseurl}/etrack-permit/keyword/project/keyword/${associatedInd}`, payload , options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  addOtherKeyword(payload: any, queryProjectId?:any) {
    let user = localStorage.getItem('loggedUserName'); 
    let projectId = localStorage.getItem('projectId');   
    const options = {
      headers: new HttpHeaders({
        userId: user!.replace('SVC', '').substring(1),
        projectId: queryProjectId || projectId!
      }),
    };
    return this.http
      .post<any>(`${environment.apiBaseurl}/etrack-permit/keyword/project/keyword`, payload , options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  addKeyword(payload: any) {
    let user = localStorage.getItem('loggedUserName');    
    const options = {
      headers: new HttpHeaders({
        userId: user!.replace('SVC', '').substring(1)
      }),
    };
    return this.http
      .post<any>(`${environment.apiBaseurl}/etrack-permit/keyword`, payload , options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  addPermitType(payload: any) {
    let user = localStorage.getItem('loggedUserName');    
    const options = {
      headers: new HttpHeaders({
        userId: user!.replace('SVC', '').substring(1)
      }),
    };
    return this.http
      .post<any>(`${environment.apiBaseurl}/etrack-permit/keyword/permit`, payload , options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  loadKeywordPermitType(id?: any) {
    let user = localStorage.getItem('loggedUserName');    
    const options = {
      headers: new HttpHeaders({
        userId: user!.replace('SVC', '').substring(1)
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/keyword/permit${id ? '/' + id : ''}`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  createKeywordCategory(formPayload: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        userId: user!.replace('SVC', '').substring(1)
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-permit/keyword/category`,
      formPayload,
      options
    );
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

  getProjectData() {
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/projectInfo`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

 
  
}