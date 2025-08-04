import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { reviewComplete } from 'src/app/@store/models/reviewComplete';
import { AuthService } from 'src/app/core/auth/auth.service';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VirtualDesktopService {

  public vdsData:any = null; // not using behaviour subject, because the user roles are retrieved after the vds data is successful.
  addedReviewer : BehaviorSubject<string> = new BehaviorSubject<string>('not assigned');
  showReviewerSub : Subject<string> = new Subject<string>();
  assignedAnalyst: BehaviorSubject<string> = new BehaviorSubject<string>('');
  isNoContent: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
 
  constructor(public http: HttpClient, public authService: AuthService) { }

  getShowReviewerSub(){
   // console.log('we here');
    
    return this.showReviewerSub.asObservable();
    }

  showReviewer(){
    return this.showReviewerSub.next('show');
  }

  getReviewer(){
    return this.addedReviewer.asObservable();
  }

  assignReviewer(){    
    this.addedReviewer.next('assigned');
  }

 
  // getNotesList() { 
  //   let user = localStorage.getItem('loggedUserName');
  //   let projectId = localStorage.getItem('projectId');
  //   const options = {
  //     headers: new HttpHeaders({
  //       // @ts-ignore
  //       userId: user.replace('SVC', '').substring(1),
  //       // @ts-ignore
  //       projectId: projectId,
  //       // Authorisation: `Bearer ${token}`
  //     }),
  //   };
  //   return this.http
  //     .get<any>(`${environment.apiBaseurl}/etrack-permit/notes`, options)
  //     .toPromise()
  //     .then((data) => {
  //       return data;
  //     });
  // }

  addNote(payload:any, queryProjectId:any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId;
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-permit/notes`,
      payload,
      options
    );
  }
  updateNote(payload:any, queryProjectId?:any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId ;
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-permit/notes`,
      payload,
      options
    );
  }

 

updateGiId(projectId: any, inquiryId: any) {
  console.log("GID in serv",projectId, inquiryId)
    let user = localStorage.getItem('loggedUserName');
    let project = projectId;
    let inquiry = inquiryId;
    console.log("GID in serv",project, inquiry)
    const options = {
      headers: new HttpHeaders({       
        userId: user!.replace('SVC', '').substring(1),
        projectId: project!.toString(),
        inquiryId: inquiry!.toString(),
      }),
    };
    console.log(options);
    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-permit/associate-inquiry`, {},
      options
    );
  }

  updateGiNote(payload: any, queryInquiryId: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        inquiryId: queryInquiryId,
      }),
    };
    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-permit/spatial-inquiry/notes`,
      payload,
      options
    );
  }

  permitKeyWordData(queryProjectId?:any){
    let user = localStorage.getItem('loggedUserName');    
    let projectId = queryProjectId;
    console.log("test", projectId)
    const options = {
      headers: new HttpHeaders({
        userId: user!.replace('SVC', '').substring(1),      
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/keyword/project`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  } 
  deleteNote(notesId:any, queryProjectId?:any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId ;
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http.delete<any>(
      `${environment.apiBaseurl}/etrack-permit/notes/${notesId}`,
      options
    );
  }
  deleteGiNote(notesId: any, queryInquiryId: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
      inquiryId: queryInquiryId,
      }),
    };
    return this.http.delete<any>(
      `${environment.apiBaseurl}/etrack-permit/spatial-inquiry/notes/${notesId}`,
      options
    );
  }
  getNoteDetailsById(noteId:number, queryProjectId?:any) { 
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId ;
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
      .get<any>(`${environment.apiBaseurl}/etrack-permit/notes/${noteId}`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getGiNoteDetailsById(noteId: number, queryInquiryId: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        inquiryId: queryInquiryId,
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/spatial-inq/notes/${noteId}`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getVirtualDesktopData(queryProjectId?:any) { 
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId ;
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
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/virtual-workspace`, {headers: options.headers, observe: 'response'});
  }

  getGiVirtualDesktopData(queryInquiryId: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        inquiryId: queryInquiryId,
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/spatial-inq/virtual-workspace`, options);
  }

  getFeesAndInvoiceOptions(queryProjectId?:any) { 
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId ;
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
      .get<any>(`${environment.apiBaseurl}/etrack-config/invoice-fees`, options);
  }

  getSuppportDocumentById(id:any, queryProjectId:any){
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
      .get<any>(`${environment.apiBaseurl}/etrack-dcs/support-document/document/${id}`, options);
  }

  getSupportDocumentById(id:any){
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: '',
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dcs/support-document/document/${id}`, options);
  }



  getReviewersList(regionId:any, queryProjectId: string){
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: queryProjectId?.toString() || '0',
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/staff/${regionId}`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  getEmailList(){
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/email/users/list`, options)
      .toPromise()
      .then((data) => {
        return data;
      });

  }

  updateReviewerData(payload:any, queryProjectId:any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId ;
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
       userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-permit/assign-doc-reviewer`,
      payload,
      options
    );
  }

  updateGiReviewerData(payload:any, queryInquiryId:any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
       userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        inquiryId: queryInquiryId,
      }),
    };
    return this.http.post<any>(
      `${environment.apiBaseurl}/etrack-permit/spatial-inquiry/assign-doc-reviewer`,
      payload,
      options
    );
  }
  

  getReviewDocs(queryProjectId: any) { 
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId ;
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
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/review-documents`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getUserRegionId(){
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
      //  projectId: projectId,
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/regionId`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

    
// @PostMapping("/review-complete")  public void updateDocReviewCompletion(@RequestHeader final String userId,      @RequestHeader Long projectId, @RequestBody List<Long> documentIds) {

//   [12:41 PM] Mahalingam, Moorthi (DEC)
//   /etrack-permit/review-complete
  
  
//   }

  markReviewAsComplete(payload : reviewComplete, queryProjectId: any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId;
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
      .post<any>(`${environment.apiBaseurl}/etrack-permit/review-complete`,payload, options).toPromise();
  }

  markGiReviewAsComplete(payload: any, queryInquiryId: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        inquiryId: queryInquiryId,
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .post<any>(`${environment.apiBaseurl}/etrack-permit/spatial-inquiry/review-complete`,payload, options).toPromise();
  }

  saveLitigation(payload:any, queryProjectId?:any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId;
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
      .post<any>(`${environment.apiBaseurl}/etrack-permit/add-litigation`, payload,options)
  }

  saveFoil(payload:any, queryProjectId?:any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId ;
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
      .post<any>(`${environment.apiBaseurl}/etrack-permit/add-foil`, payload,options)
  }
  saveFoilAndLitigation(payload:any, queryProjectId?:any) { 
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId;
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
      .post<any>(`${environment.apiBaseurl}/etrack-permit/virtual-workspace`, payload,options)
     
  }
  getProgramReviewerCorrespondence(payload:any , queryProjectId: any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId || localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: projectId,
        reviewerId :payload.reviewerId
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/user/correspondence/${payload.documentId}`, options);
  }

  getVWTransTypes(queryProjectId?:any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId ;
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
      .get<any>(`${environment.apiBaseurl}/etrack-config/transTypes`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  getAvailablePermits(queryProjectId: any){
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId ;
    const options = {
      headers: new HttpHeaders({        
        userId: user!.replace('SVC', '').substring(1),        
        projectId: projectId!
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/available-permits-to-add`, options);
  }

  submitPermitTypesValues(payload:any, queryProjectId: any ){
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId;
    const options: any = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        
        // @ts-ignore
        ppid: localStorage.getItem('ppid'), 
        // @ts-ignore
        projectId: projectId,
        // Authorisation: `Bearer ${token}`
      }),
      observe:'response',
    };
    return this.http
      .post<any>(`${environment.apiBaseurl}/etrack-permit/addl-application`, payload, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  saveApplicantCommunications(payload: any, files: File[], projectId: any) {
    let user = localStorage.getItem('loggedUserName');
    let formData = new FormData();
    if (files?.length > 0) {
      Array.from(files).forEach((f) => 
        formData.append('attachments', new Blob([f], {}), f.name)
      );
    }

    formData.append(
      'applicantCorrespondence',
      new Blob([JSON.stringify(payload)], {
        type: 'application/json',
      }),
      'applicantCorrespondence.json'
    );
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        
        // @ts-ignore
        projectId: projectId
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .post<any>(`${environment.apiBaseurl}/etrack-permit/applicant-correspondence`, formData, options);
  }

  markCommunicationsAsClosed(payload: any, queryProjectId: any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryProjectId;
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
      .put<any>(`${environment.apiBaseurl}/etrack-permit/applicant-correspondence/${payload.correspondenceId}`,
      payload, options).toPromise();
  }

  submitInquiryResponse(payload: any, queryInquiryId: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        inquiryId: queryInquiryId,
        // Authorisation: `Bearer ${token}`
      }),
    };

    return this.http
      .post<any>(`${environment.apiBaseurl}/etrack-permit/spatial-inquiry/response`,
      payload, options);
  }

  getInquiryNoteConfig() {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-dart-db/spatial-inq/note-config`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }
}