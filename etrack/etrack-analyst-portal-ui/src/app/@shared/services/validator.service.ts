import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ValidatorService {
  constructor(private http: HttpClient) {}

  replaceNullWithEmptyString(array: any[]): any[] {
    if(!array || array?.length===0)return [];
    array.forEach((obj: any) => {
      for (let key in obj) {
        if (!obj[key]) obj[key] = '';
      }
    });
    return array;
  }
  update(params: any) {
    const url = `${environment.apiBaseurl}/etrack-permit/validator/${params?.category}/${params?.activityId}/${params?.indicator}`;
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId: projectId || '',
      }),
    };
    return this.http.post<any>(url, {}, options);
  }
}
