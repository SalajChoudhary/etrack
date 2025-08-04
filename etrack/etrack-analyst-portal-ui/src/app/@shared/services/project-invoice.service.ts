import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProjectInvoiceService {

  constructor(private http : HttpClient) {}

  getInvoiceDetails(queryParamProjectId: any , invoiceId: any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryParamProjectId || localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId || '1302',
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/invoice/${invoiceId}`,options
    );
  }


  submitProjectInfo(queryParamProjectId: any, invoiceRequest: any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryParamProjectId || localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId || '1302',
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-fmis/invoice`,
      invoiceRequest,options
    );
  }

  getTransactionId(queryParamProjectId: any, invoiceNumber:any){
    let user = localStorage.getItem('loggedUserName');
    let projectId =queryParamProjectId;
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId || '1302',
        invoiceNumber: invoiceNumber,
        'Content-Type': 'text/plain; charset=utf-8', // remove after getting json response

      }),
      responseType: 'text' as 'json'// remove after getting json response
    };
    return this.http.post<string>(
      `${environment.apiBaseurl}/etrack-fmis/vps/transaction`,
      {}, options
    );
  }
  onSaveNotes(queryParamProjectId: any, payload: any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryParamProjectId;
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
        projectId: projectId || '',
      }),
    };

    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-fmis/invoice/${payload.invoiceNum}`,
      payload,
      options
    );
  }
  
  onCancelInvoice(queryProjectId: any, payload:any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId;
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
        projectId: projectId || '',
      }),
    };

    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-fmis/invoice/cancel`,
      payload,
      options
    );

  }



}
