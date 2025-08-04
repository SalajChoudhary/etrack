import { Injectable } from '@angular/core';
import {
  HttpClient, HttpHeaders
} from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CodeTablesService {

  constructor(private http: HttpClient) { }


  getCategories() {
    let url = `${environment.apiBaseurl}/etrack-config/code-table/categories`;
    return this.http.get<any>(url);
  }


  getCategoryTablesData() {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-config/code-table/categories`,
      options
    );
  }

  getSelectedTableData(tableName: any) {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-config/code-table/view-table/${tableName.toLowerCase()}`,
      options
    );
  }

  updateTable(body: any) {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-config/code-table/addUpdate/${body.tableName.toLowerCase()}`, body.keyValues,
      options
    ).toPromise()
    .then((data) => {
      return data;
    });
  }

  getReqdDocumentsTableData(body: any) {
    let user = localStorage.getItem('loggedUserName');
    let reqdDocsIds: any = {};
    if(body.keyValues.permitTypeCode) {
      reqdDocsIds['permitTypeCode'] = body.keyValues.permitTypeCode.toString();
    }
    if(body.keyValues.swFacSubTypeId) {
      reqdDocsIds['swFacSubTypeId'] = body.keyValues.swFacSubTypeId.toString();
    }
    if(body.keyValues.swFacTypeId) {
      reqdDocsIds['swFacTypeId'] = body.keyValues.swFacTypeId.toString();
    }

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
        ...reqdDocsIds
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-config/code-table/document-config/${body.tableName.toLowerCase()}`, options
    ).toPromise()
    .then((data) => {
      return data;
    });
  }

  getSwFacTypeAssociate() {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-config/code-table/sw-fac-type-associate`,
      options
    ).toPromise()
    .then((data) => {
      return data;
    });
  }

  updateReqdDocumentsTable(body: any) {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-config/code-table/document-config/${body.tableName.toLowerCase()}`, body.keyValues,
      options
    ).toPromise()
    .then((data) => {
      return data;
    });
  }

  getTransactionTableData(body: any) {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
        permitSubCategory: body.keyValues.permitSubCategory
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-config/code-table/transaction-rule/${body.tableName.toLowerCase()}`, options
    ).toPromise()
    .then((data) => {
      return data;
    });
  }

  updateTransactionTable(body: any) {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-config/code-table/transaction-rule/${body.tableName.toLowerCase()}`, body.keyValues,
      options
    ).toPromise()
    .then((data) => {
      return data;
    });
  }

}

/**
 * return this.http
      .post<any>(
        `${environment.apiBaseurl}/etrack-dcs/dcs/uploadDocument`,
        req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
 */