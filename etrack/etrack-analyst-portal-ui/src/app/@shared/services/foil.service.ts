import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FoilService {

  constructor(private http:HttpClient) { }

  save(payload:any) { 
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
      .post<any>(`${environment.apiBaseurl}/etrack-permit/foil-request`, payload,options)
     
  }
}
