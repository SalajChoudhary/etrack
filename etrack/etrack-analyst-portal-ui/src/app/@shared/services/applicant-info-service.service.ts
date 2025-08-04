import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { zip } from 'lodash';
import { environment } from '../../../environments/environment';

import { CommonService } from './commonService';

@Injectable({
  providedIn: 'root',
})
export class ApplicantInfoServiceService {

  constructor(public http: HttpClient,
    private commonService: CommonService) {}

  submitApplicantInfo(formPayload:any, category: string) {
    let user = localStorage.getItem('loggedUserName');
    let projectId=localStorage.getItem('projectId');
    let mode = localStorage.getItem('mode');
    let modeIndicator = "";
    if(mode === 'validate') modeIndicator = '1';
    else modeIndicator = '0';

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId: projectId || '',
        //userId: "loadrunner"
        // Authorisation: `Bearer ${token}`
      }),
    };
    let url= `${environment.apiBaseurl}/etrack-permit/applicant/${category}`;
    return this.http.post<any>(
     url,
      formPayload,
      options
    );
  }

  updateApplicantInfo(formPayload:any, category: string){
    let user = localStorage.getItem('loggedUserName');
    let projectId=localStorage.getItem('projectId');

    let mode = localStorage.getItem('mode');
    let modeIndicator = "";
    if(mode === 'validate') modeIndicator = '1';
    else modeIndicator = '0';


    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),

        // projectId: '901'

         // @ts-ignore
         projectId:projectId,

        //userId: "loadrunner"
        // Authorisation: `Bearer ${token}`
      }),
    };
    let url= `${environment.apiBaseurl}/etrack-permit/applicant/${category}`;
    return this.http.put<any>(
      url,
      formPayload,
      options
    );
  }
  getAllExistingApplicants(associatedInd?: string, category?: string) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId: localStorage?.getItem('projectId')?.toString() || '',
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/applicants/${category}/${associatedInd}`,
      options
    );
  }
  
  getExistingContacts(projectId: string) {
    let user = localStorage.getItem('loggedUserName');
    //let projectId=localStorage.getItem('projectId');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId:projectId,
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/contacts`,
      options
    );
  }
  getApplicantDetailsById(applicantId: string, applicationType:string) {
    let user = localStorage.getItem('loggedUserName');
    let projectId=localStorage.getItem('projectId');

    // let mode = localStorage.getItem('mode');
    // let modeIndicator = "";
    // if(mode === 'validate') modeIndicator = '1';
    // else modeIndicator = '0';
    

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
         // @ts-ignore
        // projectId: '901',
        projectId:localStorage.getItem('projectId'),
      }),
    };
    console.log(applicationType);
    if(this.commonService.getFromScreen()=='search'){
      console.log(this.commonService.getselectedApplicantype());
    }
    if(applicationType){
      return this.http.get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/applicant/${applicantId}/${applicationType}`,
        //`${environment.apiBaseurl}/etrack-dart-db/applicant/view/${applicantId}`,
        options
      );
    }
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/applicant/${applicantId}`,
      //`${environment.apiBaseurl}/etrack-dart-db/applicant/view/${applicantId}`,
      options
    );
  }

  viewApplicantInfo(applicantId: any){    
    let user = localStorage.getItem('loggedUserName');
   // let projectId=localStorage.getItem('projectId');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
         // @ts-ignore
        // projectId: '901',
       projectId:localStorage.getItem('projectId'),
      }),
    };

    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/applicant/view/${applicantId}`,
      options
    );

  }

  validatePublicId(publicId: any,appId:any){   
    console.log('heyyy') 
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
         // @ts-ignore
        // projectId: '901',
       projectId:localStorage.getItem('projectId'),
       publicId: appId
      }),
    };
    console.log('hhh3')

    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/validate/dart-public/${publicId}`,
      options
    );

  }


  getContactDetailsById(applicantId: string, applicationType:string) {
    let user = localStorage.getItem('loggedUserName');
    let projectId=localStorage.getItem('projectId');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
         // @ts-ignore
        // projectId: '901',
        projectId:localStorage.getItem('projectId'),
      }),
    };
    if(applicationType){
      return this.http.get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/applicant/${applicantId}/${applicationType}`,
        //`${environment.apiBaseurl}/etrack-dart-db/applicant/${applicantId}`,
        options
      );
    }
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/contact/${applicantId}`,
      options
    );
  }
  searchByName( publicType: string,firstName:any,lastName:any,fType:any,lType:any) {
    let user = localStorage.getItem('loggedUserName');
    
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        //userId: "loadrunner"
        // Authorisation: `Bearer ${token}`
        projectId:'0',
        firstName: firstName,
        lastName: lastName,
        fType: fType,
        lType: lType,
      }),
    };
    // let params='';
    // if(firstName){
    //   params = `${publicType}?firstName=${firstName}&fType=${fType}`;
    // }else{
    //   params = `${publicType}?firstName=&fType=`;
    // }
    // if(lastName){
    //   const ParamsLastName = `&lastName=${lastName}&lType=${lType}`;
    //   params = params.concat(ParamsLastName);
    // }

    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/search/${publicType}`,
      options
    );
    // return this.http.get<any>(
    //   `${environment.apiBaseurl}/etrack-dart-db/search/`+params,
    //   options
    // );
  }
  deleteAgentById(agentId: any, edbPublicId: string, category:string) {
    let user = localStorage.getItem('loggedUserName');
    
    console.log(typeof(edbPublicId), edbPublicId);
  
    let options:any={};
    if(edbPublicId==null || edbPublicId==''){
      options = {
        headers: new HttpHeaders({
          // @ts-ignore
          userId: user.replace('SVC', '').substring(1),
         //userId: "loadrunner",
   // @ts-ignore
         projectId:localStorage.getItem('projectId'),
        })
      };
    }else{
      options = {
        headers: new HttpHeaders({
          edbPublicId: edbPublicId.toString(),
          // @ts-ignore
          userId: user.replace('SVC', '').substring(1),
         //userId: "loadrunner",
   // @ts-ignore
         projectId:localStorage.getItem('projectId'),
        })
      };
    }
    return this.http
      .delete<any>(
        `${environment.apiBaseurl}/etrack-permit/applicant/${agentId}/${category}`,
        options
      )

  }
  verifyBusinessName(legalName : string){
    let user = localStorage.getItem('loggedUserName');
    let projectId= localStorage.getItem('projectId');
        const options = {
      observe: 'response' as const,
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
         // @ts-ignore
        projectId,
        legalName,
      }),
    };

    return this.http
    .get<any>(
      `${environment.apiBaseurl}/etrack-permit/verify/business`,
      options
    );

  }

  getCityAndState(address: string, address2: string, zipCode : string){
    console.log('making call in serv');

    let user = localStorage.getItem('loggedUserName');
    const body = {"streetAddress1" : address,
    "streetAddress2" : address2,
                  "zipCode" : zipCode
                };

        const options = {
      observe: 'response' as const,
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      }) }
      return this.http
    .post<any>(
      `${environment.apiBaseurl}/etrack-gis/address`, body,
      options
    );
}


 

 

}


