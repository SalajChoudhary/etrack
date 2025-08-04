import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth/auth.service';

@Injectable()
export class DocumentService {
  constructor(public http: HttpClient, public authService: AuthService) { }

  /**
   * Gets the list of reasons.
   * @constructor
   * @returns {Promise} Promise
   */
  getReason() {
    return this.http
      .get<any>('assets/data/reasons.json')
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  /**
   * Gets the list of documents.
   * @constructor
   * @param {any} districtId - The districtcId.
   * @returns {Promise} Promise 
   */
  getGridFromFacility(districtId: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1)
       //userId: "loadrunner"
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http

      .get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/retrieveDistrictDetails/${districtId}`,
        options
      ) 
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  /**
   * Gets the list of facility dropdown options.
   * @constructor
   * @param {any} search - The search text.
   * @param {any} type - The search type , defaults to DECID.
   * @param {string} searchType - The searchType, defaults to 'E'.
   * @returns {Promise} Promise
   */
  getFacilityOptions(
    search: any,
    type: any = 'DECID',
    searchType: string = 'E'
  ) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
       //userId: "loadrunner"
      }),
    };
    let url =
      type == 'DECID'
        ? environment.apiBaseurl +
        '/etrack-dart-db/retrieveDistrictByDecId/' +
        search
        : `${environment.apiBaseurl}/etrack-dart-db/retrieveDistrictByFacilityName/${search}/${searchType}`;

    return this.http
      .get<any>(url, options)
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
    //console.log(this.authService.getToken());    
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-config/docTypes`)
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
  getFileContent(documentId: number, fileName: string, docClassName: string, projectId:string='') {
    let user = localStorage.getItem('loggedUserName');
    let extension = fileName.split('.');
    let payload={
      fileName:fileName,
    }
    const options ={ headers : new HttpHeaders({
      'Content-Type': 'application/json',
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1),
      //userId: "loadrunner",
      documentClass: docClassName,
      projectId:projectId?projectId.toString():'',
    })}

    return this.http
      .post(
        `${environment.apiBaseurl}/etrack-dcs/dcs/retrieveFileContent/${documentId}`,payload,
        { ...options, responseType: 'blob' }
      )
      .toPromise()
      .then((data: any) => {
        if(data){
          return new Blob([data], { type: data.type });
        }else{
          return data;
        }
       
      });
  }

  /**
   * Uploads documents details.
   * @constructor
   * @param {any} req - The request object.
   * @param {any} districtId - The districtcId.
   * @param {string} docClassName - The documentClass.
   * @returns {Promise} Promise
   */

  uploadDocument(req: any, districtId: string, docClassName: string) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        //'Content-Type': 'multipart/form-data',
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      // userId: "loadrunner",
        districtId: districtId.toString(),
        docclassification: docClassName,
      }),
    };

    return this.http
      .post<any>(
        `${environment.apiBaseurl}/etrack-dcs/dcs/uploadDocument`,
        req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }


  addAdditionalDocument(request: any, docClassName:any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
   
    const options = {
      headers: new HttpHeaders({
        //'Content-Type': 'multipart/form-data',
        // @ts-ignore
        projectId: projectId,
  // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      // userId: "loadrunner",
      docclassification: docClassName,
      }),
    };

    return this.http
    .post<any>(
      // `${environment.apiBaseurl}/etrack-dcs/support-document/additional-doc`,
      `${environment.apiBaseurl}/etrack-dcs/support-document/upload`,
      request,
      options
    )
    .toPromise()
    .then((data) => {
      return data;
    });

  }

  uploadButtonDocument(req: any, docClassName: string, queryProjectId?:any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId || localStorage.getItem('projectId');
   
    const options = {
      headers: new HttpHeaders({
        //'Content-Type': 'multipart/form-data',
        // @ts-ignore
        projectId: projectId,
  // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      // userId: "loadrunner",
      // @ts-ignore
      docClassification: docClassName,
      }),
    };

    return this.http
      .post<any>(
        `${environment.apiBaseurl}/etrack-dcs/support-document/upload`,
        req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  /**
   * Replaces documents details for an document with exisitng document name.
   * @constructor
   * @param {any} req - The request object.
   * @param {any} districtId - The districtcId.
   * @param {string} docClassName - The documentClass.
   * @returns {Promise} Promise
   */
  replaceDocument(req: any, districtId: string, docClassName: string) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // 'Content-Type': 'multipart/form-data',
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
       //userId: "loadrunner",
        districtId: districtId.toString(),
        docClassification: docClassName,
      }),
    };
    return this.http
      .post<any>(
        `${environment.apiBaseurl}/etrack-dcs/dcs/replaceDocument`,
        req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  /**
   * Gets the list of error messages from the backend.
   * @constructor
   * @returns {Promise} Promise
   */
  getValidationErros() {
    return (
      this.http
        .get<any>(`${environment.apiBaseurl}/etrack-config/messages`)
        .toPromise()
        .then((data) => {
          return data;
        })
    );
  }

  /**
   * Updates documents details.
   * @constructor
   * @param {any} req - The request object.
   * @param {any} districtId - The districtcId.
   * @param {string} docClassName - The documentClass.
   * @returns {Promise} Promise
   */
  updateDocument(req: any, districtId: any, docClassName: string, projectId?: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: projectId ? new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      //userId: "loadrunner",
        documentClass: docClassName,
        projectId: projectId.toString()
      }) :
      new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      //userId: "loadrunner",
        documentClass: docClassName
      }),
    };
    return this.http
      .put<any>(
        `${environment.apiBaseurl}/etrack-dcs/dcs/updateMetadata/${req.metadataProperties.eTrackDocumentID}`,
        req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  /**
   * Logically deletes a document.
   * @constructor
   * @param {any} documentId - The documentId.
   * @param {any} documentClass - The documentClass.
   * @returns {Promise} Promise
   */

  deleteDocument(documentId: any, documentClass: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
       //userId: "loadrunner",
        documentClass: documentClass
      })
    };

    return this.http
      .delete<any>(
        `${environment.apiBaseurl}/etrack-dcs/dcs/deleteDocument/${documentId}`,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  deleteDMSDocumentFile(documentId : string [], projectId: any){
    let user = localStorage.getItem('loggedUserName');
    let queryProjectId = projectId;
     const options = {
        headers: new HttpHeaders({
          // @ts-ignore
          userId: user.replace('SVC', '').substring(1),
          // @ts-ignore
          projectId: queryProjectId.toString() ,
        }),
      };
      return this.http
        .delete<any>(
          `${environment.apiBaseurl}/etrack-dcs/support-document/document/${documentId}`,
          options
        )
        .toPromise()
        .then((data) => {
          return data;
        });
  
   }
  
  getUploadDocDetails() {
    let user = localStorage.getItem('loggedUserName');
    
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1)
       //userId: "loadrunner"
        // Authorisation: `Bearer ${token}`
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

  alreadyUploadedSubmit(payload:any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
   
    const options = {
      headers: new HttpHeaders({
        //'Content-Type': 'multipart/form-data',
        // @ts-ignore
        projectId: projectId,
  // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      // userId: "loadrunner",
      //  docClass: docClassName,
      }),
    };

    return this.http
      .post<any>(
        `${environment.apiBaseurl}/etrack-dcs/support-document/reference-doc`,
        payload,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  getDimsrDetails(decId:any) {
    let user = localStorage.getItem('loggedUserName');
    
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1)
       //userId: "loadrunner"
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http

      .get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/dmisr-detail/${decId}`,
        options
      ) 
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  
  submitDimsrDetails(req: any, ) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        //'Content-Type': 'multipart/form-data',
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        ppid: localStorage.getItem('ppid')
      }),
    };

    return this.http
      .post<any>(
        `${environment.apiBaseurl}/etrack-permit/dimsr-application`,
        req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }
}
