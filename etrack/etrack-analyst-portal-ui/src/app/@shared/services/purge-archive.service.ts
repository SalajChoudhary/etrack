import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class PurgeArchiveService {
  constructor(private http: HttpClient) {}

  getQueryResults() {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/purge-archive/query-result`,options
    );
  }

  getResultDocuments(resultId: any) {
    let user = localStorage.getItem('loggedUserName');
    const resultIdStr = resultId.toString();
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1)
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/purge-archive/result-document/${resultIdStr}`,options
    );
  }

  removeDocument(resultId: any, docId: any) {
    let user = localStorage.getItem('loggedUserName');
    const resId = resultId.toString();
    const docIdStr = docId.toString();
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        
      }),
    };
    return this.http.delete<any>(
      `${environment.apiBaseurl}/etrack-permit/purge-archive/remove-document/${docIdStr}`, options
    );
  }

  markReviewComplete(resultId: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1)
      }),
    };
    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-permit/purge-archive/review-complete`, {
        resultId: resultId
      }, options
    );
  }

  updateResult(payload: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1)
      }),
    };
    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-permit/purge-archive/result`, payload, options,
    );
  }

  runQuery(payload: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-permit/purge-archive/run-query`, payload, options,
    );
  }

  archiveDocument(resultId: any, documentId: any) {
    let user = localStorage.getItem('loggedUserName');
    const docId = documentId.toString();
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1)
      }),
    };
    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-permit/purge-archive/archive-document/${docId}`, {
        resultId: resultId,
        archiveDocInd: "Y"
      },  options
    );
  }

  archiveAllDocuments(payload: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1)
      }),
    };
    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-permit/purge-archive/archive-documents`, 
      payload, options
    );
  }

  deleteResult(resultId: any) {
    let user = localStorage.getItem('loggedUserName');
    const resId = resultId.toString();
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1)
      }),
    };
    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-permit/purge-archive/result/${resId}`, {}, options
    );
  }

  purgeDocuments(resultId: any, payload: any) {
    let user = localStorage.getItem('loggedUserName');
    const resId = resultId.toString();
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1)
      }),
    };
    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-permit/purge-archive/purge/${resId}`, payload, options
    );
  }

  removeDocuments(resultId: any, payload: any) {
    let user = localStorage.getItem('loggedUserName');
    const resId = resultId.toString();
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1)
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-permit/purge-archive/make-ineligible/${resId}`, payload, options
    );
  }
}