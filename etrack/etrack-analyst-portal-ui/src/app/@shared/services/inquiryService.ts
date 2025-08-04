import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth/auth.service';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class InquiryService {
  docSub = new BehaviorSubject<any>({});
  uploadIsSuccessfulSub = new BehaviorSubject<{}>('');
  otherUploadIsSuccessfulSub = new BehaviorSubject<{}>('');
  listInd = new BehaviorSubject<string>('');
  existingDocumentNames = new BehaviorSubject<string[]>([]);
  existingFiles = new BehaviorSubject<any[]>([]);
  deleteListInd = new BehaviorSubject<string>('');
  docName = '';
  constructor(public http: HttpClient, public authService: AuthService) {}

  setDocName(docName: string){
    this.docName = docName;
  }

  getDocName(){
    return this.docName;
  }

  setDeleteListInd(ind: string) {
    this.deleteListInd.next(ind);
  }

  getDeleteListInd() {
    return this.deleteListInd.asObservable();
  }

  setExistingFilesArray(files: any[]) {
    this.existingFiles.next(files);
  }

  getExistingFilesArray() {
    return this.existingFiles.asObservable();
  }

  setExistingDocumentNames(names: any[]) {
    this.existingDocumentNames.next(names);
  }

  setDocument(doc: {}) {
    this.docSub.next(doc);
  }

  getDocument() {
    return this.docSub.asObservable();
  }

  uploadWasSuccessful(doc: {}) {
    this.uploadIsSuccessfulSub.next(doc);
  }

  getUploadWasSuccessfulSub() {
    return this.uploadIsSuccessfulSub.asObservable();
  }

  otherUpLoadWasSuccessful(doc: {}) {
    this.otherUploadIsSuccessfulSub.next(doc);
  }

  getOtherUploadWasSuccessful() {
    return this.otherUploadIsSuccessfulSub.asObservable();
  }

  setListInd(ind: string) {
    this.listInd.next(ind);
  }

  getListInd() {
    return this.listInd.asObservable();
  }

  getAnalystsByRegion(regionId: string) {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analysts/${regionId}`,
      options
    );
  }

/**
   * Gets the current status inquiry
   * @constructor
   * @param {any} inquiryId - The inquiryId.
   * @returns {Promise} Promise
   */
 getInquiryStatus(inquiryId: any) {
  let user = localStorage.getItem('loggedUserName');
  const options = {
    headers: new HttpHeaders({
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1),
      inquiryId: inquiryId,
    }),
  };
  return this.http
    .get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/status`,
      options
    )
    .toPromise()
    .then((data) => {
      return data;
    });
}

submitFinalInquiry(){
  let options = {
    headers: this.buildHeaders(),
  };
  return this.http.post<any>(
    `${environment.apiBaseurl}/etrack-permit/spatial-inquiry/submit`,
    null,
    options
  );
}

  uploadMapToGI(spatialInquiryMapUrl:string){
    let user = localStorage.getItem('loggedUserName');
    let inquiryId = localStorage.getItem('inquiryId');
    let options = {
      headers: new HttpHeaders({
        // @ts-ignore
        inquiryId: inquiryId,
        spatialInquiryMapUrl:spatialInquiryMapUrl,
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      })
    };
    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-permit/spatial-inquiry/upload-gi-map/`+inquiryId,
        options
      )
      .toPromise()
      .then((data) => {
        console.log(data);
        return data;
      });
  }

  getRequiredAndRelatedDocs() {
    let options = {
      headers: this.buildHeadersWithCategoryCode('1'),
    };
      return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/doc-summary`,
        options
      )
      .toPromise()
      .then((data) => {
        console.log(data);
        return data;
      });

  }

  formatInquiryId(inquiryId: any) {
    let formattedInqId = "GID-";
    return formattedInqId + "0".repeat(6 - inquiryId.toString().length) + inquiryId.toString();
  }

  decodeInquiryId(formattedInquiryId:any) {
    let inqId = formattedInquiryId.substring(4);
    for(let i = 0; i < inqId.length; i++) {
      if(inqId[i] !== '0') {
        return inqId.substring(i);
      }
    }
    return inqId;
  }

  getUploadDocDetails() {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1)
      }),
    };
    return this.http

      .get<any>(
        `${environment.apiBaseurl}/etrack-config/support-doc-config`,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  /**
   * Gets the lists of doc types.
   * @constructor
   * @returns {Promise} Promise
   */

  getDocumentTypes() {
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-config/docTypes`)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getAlreadyUploadedData() {
    let options = {
      headers: this.buildHeaders(),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dcs/spatial-inquiry/documents`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getReviewDocs(queryInquiryId: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        inquiryId: queryInquiryId
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/spatial-inq/review-documents`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  

  getInquiryDocumentFiles(documentId: string,  queryInquiryId?:any) {
    console.log("get Inquiry Document Files")
    let headersFromLocal = this.buildHeaders();
    let options = {
      headers: queryInquiryId ? this.buildHeaders(queryInquiryId) : headersFromLocal,
    };
    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-dcs/spatial-inquiry/document/${documentId}`,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  

  deleteInquiryDocumentFile(documentId: string[]) {
    console.log("delete Inquiry Document File")
    let options = {
      headers: this.buildHeaders(),
    };
    return this.http
      .delete<any>(
        `${environment.apiBaseurl}/etrack-dcs/spatial-inquiry/document/${documentId}`,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  retrieveFileContent(fileNames: string, documentId: string, queryInquiryId?:any) {
    console.log("retrieveFileContent");
    let user = localStorage.getItem('loggedUserName');
    let inquiryId = queryInquiryId||localStorage.getItem('inquiryId') ;
    let payload ={
      fileName: fileNames
    }
  //   const options={ headers : new HttpHeaders({
  //     'Content-Type': 'application/json',
  //     // @ts-ignore
  //     userId: user.replace('SVC', '').substring(1),
  //     // @ts-ignore
  //     inquiryId: inquiryId,
  //   })
  // }

  //   return this.http
  //   .post<any>(
  //       `${environment.apiBaseurl}/etrack-dcs/spatial-inquiry/retrieveFileContent/${documentId}`,payload, 
  //       { ...options,responseType:"arraybuffer"}
  //     )
  //     .toPromise()
  //     .then((data: any) => {
  //       if (data) {
  //         return new Blob([data], { type: data.type });
  //       } else {
  //         return data;
  //       }
  //     });
      const options={
        headers : new HttpHeaders({
          'Content-Type': 'application/json',
          // @ts-ignore
          userId: user.replace('SVC', '').substring(1),
          // @ts-ignore
          inquiryId: inquiryId,
        }),
        
      };
    
      return this.http
        .post(
          `${environment.apiBaseurl}/etrack-dcs/spatial-inquiry/retrieveFileContent/${documentId}`,payload,
         { ...options,responseType: 'blob'}
          )
        .toPromise()
        .then((data: any) => {
          if (data) {
            return new Blob([data], { type: data.type });
          } else {
            return data;
          }
         
        });
  }

  replaceExistingDocument(req: any, docClassName: string) {
    let options = {
      headers: this.buildHeaders(),
    };
    return this.http
      .post<any>(
        `${environment.apiBaseurl}/etrack-dcs/spatial-inquiry/replace-support-document`,
        req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  /**
   * Gets the content of a file to render it on a browser tab
   * @constructor
   * @param {number} documentId - The documentId.
   * @param {string} fileName - The filename.
   * @param {string} docClassName - The documentClass.
   * @returns {Promise} Promise
   */
  getFileContent(
    documentId: number,
    fileName: string,
    docClassName: string,
    projectId: string = ''
  ) {
    console.log("get File Content");
    let user = localStorage.getItem('loggedUserName');
    let headers = new HttpHeaders({
      'Content-Type': 'application/json',
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1),
      fileName,
      projectId: projectId ? projectId.toString() : '',
      documentClass: docClassName,
    });

    return this.http
      .get(
        `${environment.apiBaseurl}/etrack-dcs/spatial-inquiry/retrieveFileContent/${documentId}`,
        { headers, responseType: 'blob' }
      )
      .toPromise()
      .then((data: any) => {
        if (data) {
          return new Blob([data], { type: data.type });
        } else {
          return data;
        }
      });
  }

  alreadyUploadedSubmit(payload: any) {
    let options = {
      headers: this.buildHeaders(),
    };
    return this.http
      .post<any>(
        `${environment.apiBaseurl}/etrack-dcs/spatial-inquiry/reference-doc`,
        payload,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  addAdditionalDocument(request: any,docClassName:any,queryInquiryId?:any) {
    let options = {
      headers: this.buildHeadersWithClassification(docClassName,queryInquiryId),
    };
    return this.http
      .post<any>(
        `${environment.apiBaseurl}/etrack-dcs/spatial-inquiry/additional-doc`,
        request,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  uploadButtonDocument(req: any, docClassName: string,queryInquiryId?:any) {
    let options = {
      headers: this.buildHeadersWithClassification(docClassName,queryInquiryId),
    };
    req.documentTitleId=200;
    return this.http
      .post<any>(
        `${environment.apiBaseurl}/etrack-dcs/spatial-inquiry/upload`,
        req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  private buildHeaders(queryInquiryId? : any): HttpHeaders {
    let user = localStorage.getItem('loggedUserName');
    let inquiryId = queryInquiryId || localStorage.getItem('inquiryId');
    return new HttpHeaders({
      // @ts-ignore
      inquiryId: inquiryId,
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1),
    });
  }

  private buildHeadersWithCategoryCode(categoryCode:string): HttpHeaders {
    let user = localStorage.getItem('loggedUserName');
    let inquiryId = localStorage.getItem('inquiryId');
    return new HttpHeaders({
      // @ts-ignore
      inquiryId: inquiryId,
      categoryCode:categoryCode,
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1),
    });
  }

  private buildHeadersWithClassification(docClassName: string,queryInquiryId?:any): HttpHeaders {
    let user = localStorage.getItem('loggedUserName');
    let inquiryId =  queryInquiryId || localStorage.getItem('inquiryId');
    return new HttpHeaders({
      // @ts-ignore
      inquiryId: inquiryId,
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1),
      documentClass: docClassName,
    });
  }

  inquiryDocSkipCall(){
    let options = {
      headers: this.buildHeaders()
    };
    const payload ={
    }

    return this.http
      .post<any>(
        `${environment.apiBaseurl}/etrack-permit/spatial-inquiry/skip-documents`,
        payload,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });

  }

}
