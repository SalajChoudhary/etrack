import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth/auth.service';
import { BehaviorSubject } from 'rxjs';
import { isEmpty } from 'lodash';

@Injectable({
  providedIn: 'root',
})
export class ProjectService {
  JAFPermits:string[]=['DA','DO','FW','TW','CE','WR','EF','WWP','WWN','LI2','WQ','SD','WSR','ETS'];
  destroyAssociatedSub : BehaviorSubject<string> = new BehaviorSubject<string> ('');
  pId:any;
  iId: any;
  constructor(public http: HttpClient, public authService: AuthService) {}


  setDestroyAssociatedSub(txt: string){
    this.destroyAssociatedSub.next(txt);
  }

  getDestroyAssociatedSub(){
    return this.destroyAssociatedSub.asObservable();
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

  getInvoicePermits(queryProjectId?:any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId || localStorage.getItem('projectId');
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
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/invoice-fee`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  /**
   * Gets the current status of a specific project
   * @constructor
   * @param {any} projectId - The projectId.
   * @returns {Promise} Promise
   */
  getProjectStatus(projectId: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        //userId: "loadrunner"
        // Authorisation: `Bearer ${token}`
      }),
    };
    let mode = localStorage.getItem('mode');
    let url = `etrack-permit/project/status/${projectId}`;
    if(mode == 'validate'){
      url += `/1`;
    }
    return this.http
      .get<any>(
        `${environment.apiBaseurl}/${url}`,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  /**
   * Gets all project configurations
   * @constructor
   * @returns {Promise} Promise
   */
  getProjectConfigs() {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        //userId: "loadrunner"
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-config/configTypes`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

   /**
   * Gets all project configurations
   * @constructor
   * @returns {Promise} Promise
   */
    getPermitConfigs() {
      let user = localStorage.getItem('loggedUserName');
      const options = {
        headers: new HttpHeaders({
          // @ts-ignore
          userId: user.replace('SVC', '').substring(1),
          //userId: "loadrunner"
          // Authorisation: `Bearer ${token}`
        }),
      };
      return this.http
        .get<any>(`${environment.apiBaseurl}/etrack-config/permits-by-sub-catg`, options)
        .toPromise()
        .then((data) => {
          return data;
        });
    }

  

  /**
   * Create a new project.
   * @constructor
   * @param {any} req - The request object.
   * @returns {Promise} Promise
   */

  getFacilityDetails(pId?:any) {
    let user = localStorage.getItem('loggedUserName');
let projectId= pId || localStorage.getItem('projectId'); // TODO:pId is added but not used. Need to check the impact
let options:any={};
if(projectId){
  options = {
    headers: new HttpHeaders({
      //'Content-Type': 'multipart/form-data',
      // @ts-ignore
      userId: user?.replace('SVC', '').substring(1),
      projectId: projectId,
    }),
  };
}else{
  options = {
    headers: new HttpHeaders({
      //'Content-Type': 'multipart/form-data',
      // @ts-ignore
      userId: user?.replace('SVC', '').substring(1),
    }),
  };
}


    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/facility`,
        //req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

removeContacts(contacts:string){
  let user = localStorage.getItem('loggedUserName');
  let projectId= localStorage.getItem('projectId');
  const options = {
    headers: new HttpHeaders({
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1),
     //userId: "loadrunner",
      projectId: projectId?projectId:''
    })
  };

  return this.http
    .delete<any>(
      `${environment.apiBaseurl}/etrack-permit/contacts/${contacts}`,
      options
    )
    .toPromise()
    .then((data) => {
      return data;
    });
}

  getAppContactDetails() {
    let user = localStorage.getItem('loggedUserName');
let projectId= localStorage.getItem('projectId');
let options:any={};
if(projectId){
  options = {
    headers: new HttpHeaders({
      //'Content-Type': 'multipart/form-data',
      // @ts-ignore
      userId: user?.replace('SVC', '').substring(1),
      projectId: projectId,
    }),
  };
}else{
  options = {
    headers: new HttpHeaders({
      //'Content-Type': 'multipart/form-data',
      // @ts-ignore
      userId: user?.replace('SVC', '').substring(1),
    }),
  };
}
    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/permit-assignment`,
        //req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  getAssociateDetails(associatedInd?: string, category?: string) {
  
    let user = localStorage.getItem('loggedUserName');
    // let mode = localStorage.getItem('mode');
    // let modeIndicator = "";
    // if(mode === 'validate') modeIndicator = '1';
    // else modeIndicator = '0';

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: localStorage.getItem('projectId'),
      }),
    };

    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/applicants/${category}/${associatedInd}`,
        options
      )
      .toPromise()
      .then((data) => {

        return data;
      });
  }

  // getProjectAddress(applicationId: any) {
  //   let user = localStorage.getItem('loggedUserName');

  //   const options = {
  //     headers: new HttpHeaders({
  //       //'Content-Type': 'multipart/form-data',
  //       // @ts-ignore
  //       userId: user.replace('SVC', '').substring(1),
  //       // userId: "dxdevada",
  //       // @ts-ignore
  //       projectId: localStorage.getItem('projectId'),
  //     }),
  //   };

  //   return this.http

  //     .get<any>(
  //       `${environment.apiBaseurl}/etrack-dart-db/applicants/summary`,
  //       options
  //     )
  //     .toPromise()
  //     .then((data) => {
  //       return data;
  //     });
  // }

  getSicCodes() {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        //'Content-Type': 'multipart/form-data',
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // projectId: '901',
        // projectId: localStorage.getItem('projectId'),
        // userId: "loadrunner",
      }),
    };

    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-config/sic-naics`,
        //req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getXtraIds() {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        //'Content-Type': 'multipart/form-data',
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      }),
    };

    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-config/xtra-prog-id-spl-attn`,
        //req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getPermitType() {
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // projectId: projectId,
        // projectId: localStorage.getItem('projectId'),
        // userId: "loadrunner",
      }),
    };
    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-config/permitTypes/${projectId}`,
        //req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getRejectedProjectDetail() {
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId')!;
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),        
        projectId: projectId,
        // userId: "loadrunner",
      }),
    };
    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/reject-detail`,
        //req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getNaicsCodes(sicCode: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        //'Content-Type': 'multipart/form-data',
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      }),
    };
    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-config/naics/${sicCode}`,
        //req,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  submitAppContactsForm(formPayload: any) {
    console.log('Form data',formPayload)
    
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-permit/permit-contacts`,
      formPayload,
      options
    );
  }
  submitProjectInfo(formPayload: any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-permit/projectInfo`,
      formPayload,
      options
    );
  }
  submitFinalProject(){
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-permit/submit-project`,
      null,
      options
    );
  }
  submitPermitTypesValues(payload:any, isModify:boolean){
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    if(isModify){
      return this.http.put<any>(
        `${environment.apiBaseurl}/etrack-permit/application-permit`,
        payload,
        options
      );  
    }else{
      return this.http.post<any>(
        `${environment.apiBaseurl}/etrack-permit/application-permit`,
        payload,
        options
      );  
    }
  }
  submitPermitTypes(payload:any){
   return this.submitPermitTypesValues(payload,false);
  }
  getExistingPermitTypes() {
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
      .get<any>(`${environment.apiBaseurl}/etrack-permit/permitTypes`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }


  getPermitSummary() {
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
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/permit-selection`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getPermitForSummaryScreen() {
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
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/permit-summary`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getTransTypes() {
    let user = localStorage.getItem('loggedUserName');
    //let projectId = localStorage.getItem('projectId') || '9803';
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
       // projectId: projectId,
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-config/transTypes`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

 


  reviewedPermits(payload:any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        
        // @ts-ignore
        ppid: localStorage.getItem('ppid'), 
        // @ts-ignore
        projectId: projectId,
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .post<any>(`${environment.apiBaseurl}/etrack-permit/upload-to-dart`, payload, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }


  getBridgeIds(){
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');;

    //let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
  }
  return this.http
  .get<any>(`${environment.apiBaseurl}/etrack-dart-db/bridgeIds`, options)
  .toPromise()
  .then((data) => {
    return data;
  });
}



  getRequiredAndRelatedDocs(){
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');;

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
  }
  return this.http
  .get<any>(`${environment.apiBaseurl}/etrack-dart-db/support-doc-summary`, options)
  .toPromise()
  .then((data) => {
    return data;
  });
  }

  deleteProjectSelectionSummary(applicationId:any, permitType:any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
  }
  return this.http
  .delete<any>(`${environment.apiBaseurl}/etrack-permit/application-permit/${applicationId}/${permitType}`, options)
  .toPromise()
  .then((data) => {
    return data;
  });
  }

  deleteBatch(payload: any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-permit/del-appl-permit`,
      payload,
      options
    );
  }

  getSignsData() {
    let user = localStorage.getItem('loggedUserName');
    let projectId=localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/reqd-signed-applicants`,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  saveApplicantDocs(payload: any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-permit/acknowledge-applicants`,
      payload,
      options
    );
  }

  submitMissingNote(payload: any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-permit/reqd-doc-missing-note`,
      payload,
      options
    );
  }
  
  supportingDocNextCall(){
    let user = localStorage.getItem('loggedUserName');
    let projectId=localStorage.getItem('projectId');    
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user ? user.replace('SVC', '').substring(1) : '',
        // @ts-ignore
        projectId : localStorage.getItem('projectId')
      }),
    };

    const payload ={
    }
    console.log(options);
    return this.http
      .post<any>(
        `${environment.apiBaseurl}/etrack-permit/support-document`,
        payload,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });

  }

  getAlreadyUploadedData() {
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
      .get<any>(`${environment.apiBaseurl}/etrack-dcs/support-document/documents`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  postAlreadyUploaded(payload: any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/support-document/reference-doc`,
      payload,
      options
    );
  }

  getAllRegions() {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        //userId: "loadrunner"
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-config/region`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }


  getProjectAlerts() {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        //userId: "loadrunner"
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/analysts/view-alerts`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getProjectAlertsScheduler() {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        //userId: "loadrunner"
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/analyst/alerts`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

//   API to delete the alert:


  deleteProjectAlert(alertId : string, projectId? : string, inquiryId?: string){  
    let user = localStorage.getItem('loggedUserName');
    let options: any;
    if (projectId) {
       options = {
        headers: new HttpHeaders({
          // @ts-ignore
          userId: user.replace('SVC', '').substring(1),  
          // @ts-ignore       
           projectId : projectId ? ''+projectId : undefined,
            // @ts-ignore      
          //  inquiryId: inquiryId ? ''+inquiryId : undefined,
        }),
      };
  
    } else if (inquiryId) {
       options = {
        headers: new HttpHeaders({
          // @ts-ignore
          userId: user.replace('SVC', '').substring(1),  
          // @ts-ignore       
          //  projectId : projectId ? ''+projectId : undefined,
            // @ts-ignore      
           inquiryId: inquiryId ? ''+inquiryId : undefined,
        }),
      };
  
    }
   

   return this.http
      .delete<any>(`${environment.apiBaseurl}/etrack-permit/delete-alert/${alertId}`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  

//   API to notify that alert is read
// Method : PUT
// Header:
// userId : <userId> 
// projectId : <projectId>

  markAlertAsRead(alertId : string){
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
         // @ts-ignore
         projectId : localStorage.getItem('projectId')
      }),
    };
    console.log('options', options);
    
    return this.http
      .put<any>(`${environment.apiBaseurl}/etrack-permit/alert-read/${alertId}`, {}, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }


  getSolidWasteArray() {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        //userId: "loadrunner"
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-config/sw-faclity-type`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }


  
  updateExistingPermitAmendRequest(payload : any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId') as string;
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        //userId: "loadrunner"
        projectId : projectId
      }),
    };
    return this.http
      .put<any>(`${environment.apiBaseurl}/etrack-permit/submit-permit-form`, payload, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getExtModRequestData(batchId : string) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId') as string;
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        //userId: "loadrunner"
        projectId : projectId
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-permit/permit-form/${batchId}`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  generateInvoiceDocument(queryParamProjectId: any, invoiceId : string) {
    let user = localStorage.getItem('loggedUserName');    
    let projectId = queryParamProjectId as string;
    let headers = new HttpHeaders({
      'Content-Type': 'application/json',
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1),
      projectId : projectId,
    });

    return this.http
      .get(
        `${environment.apiBaseurl}/etrack-dart-db/report/invoice-report/${invoiceId}`,
        { headers, responseType: 'blob' }
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

  uploadProjectDetailsToDart(payload:any){
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),  
        // @ts-ignore
        ppid: localStorage.getItem('ppid'), 
         // @ts-ignore
         projectId : localStorage.getItem('projectId')
      }),
    };

  //  let payload ={};
    return this.http
      .post<any>(`${environment.apiBaseurl}/etrack-permit/upload-to-dart`, payload, options)
      .toPromise()
      .then((data) => {
        return data;
      });
 
  }

}