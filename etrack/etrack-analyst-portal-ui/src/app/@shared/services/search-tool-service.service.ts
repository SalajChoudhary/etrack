import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth/auth.service';

@Injectable({
    providedIn: 'root'
})
export class SearchToolService {
constructor(public http: HttpClient, public authService: AuthService) {}

getSearchByAttributes() {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // Authorisation: `Bearer ${token}`
      }),
    };
 return this.http.get<any>(`${environment.apiBaseurl}/etrack-dart-db/search/search-by-attributes`, options);
}

getAvailableSearches() {
  let user = localStorage.getItem('loggedUserName');
  const options = {
    headers: new HttpHeaders({
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1),
      // Authorisation: `Bearer ${token}`
    }),
  };
return this.http.get<any>(`${environment.apiBaseurl}/etrack-dart-db/search/available-searches`, options);
}

getParticularSearch(id: any) {
  let user = localStorage.getItem('loggedUserName');
  const options = {
    headers: new HttpHeaders({
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1),
    }),
  };
return this.http.get<any>(`${environment.apiBaseurl}/etrack-dart-db/search/available-searches/${id}`, options);
}

saveQuery(data: any) {
  let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      }),
    };
 return this.http.post<any>(`${environment.apiBaseurl}/etrack-permit/search/save-query`,data, options);
}

getRunQuery(id: any) {
  let user = localStorage.getItem('loggedUserName');
  const options = {
    headers: new HttpHeaders({
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1),
    }),
  };
return this.http.get<any>(`${environment.apiBaseurl}/etrack-dart-db/search/run-query/${id}`, options);
}

/**
   * Logically deletes a document.
   * @constructor
   * @param {any} documentId - The documentId.
   * @param {any} documentClass - The documentClass.
   * @returns {Promise} Promise
   */

 deleteQuery(searchQueryId: any) {
  let user = localStorage.getItem('loggedUserName');
  const options = {
    headers: new HttpHeaders({
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1)
      
    })
  };
  return this.http
    .delete<any>(
      `${environment.apiBaseurl}/etrack-permit/search/delete-search-query?searchQueryId=${searchQueryId}`,
      options
    )
    .toPromise()
    .then((data) => {
      return data;
    });
}

getSupportDocumentFiles(documentId: string, queryProjectId:string){
  let user = localStorage.getItem('loggedUserName');
   const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId:''+queryProjectId,
      }),
    };
    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-dcs/support-document/document/${documentId}`,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
 }

}