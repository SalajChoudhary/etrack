import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  constructor(private http: HttpClient) {}

  getUserDashboard() {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analyst/user/resume-entry`,
      options
    );
  }
  getReviewerDashboard() {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/reviewer-dashboard`,
      //Commented code ->      `${environment.apiBaseurl}/etrack-dart-db/reviewer-dashboard/${regionId}`,
      options
    );
  }
  getReviewerRegionalDashboard(regionId=0) {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/reviewer-dashboard/${regionId}`,
      //Commented code ->      `${environment.apiBaseurl}/etrack-dart-db/reviewer-dashboard/${regionId}`,
      options
    );
  }

  getPendingApplications() {
    // TODO: Delete function
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/pending-applications`,
      options
    );
  }

  deleteApplications(projectId: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
        projectId: projectId.toString(),
      }),
    };
    return this.http.delete<any>(
      `${environment.apiBaseurl}/etrack-permit/project`,
      options
    );
  }

  rejectApplications(projectId: any, payload: any) {
    let user = localStorage.getItem('loggedUserName');
    console.log("Service ID", projectId)
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
        projectId: projectId.toString(),
      }),
    };
    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-permit/reject-project`, payload,
      options
    );
  }

  getRegionalDashboard(regionId: number) {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analyst/regional/active-apps/${regionId}`,
      options
    );
  }

  getAnalystRegion() {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/regionId`,
      options
    );
  }

  getRegionalUnvalidatedRecords(regionId: number){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analyst/regional/unvalidated-apps/${regionId}`,
      options
    );
  }
  getRegionalProgramreviewRecords(regionId: number){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analyst/regional/program-review-apps/${regionId}`,
      options
    );
  }
  getRegionalDisposedRecords(regionId: number){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analyst/regional/disposed-apps/${regionId}`,
      options
    );
  }

  getAnalystsByRegion(regionId: number) {
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

  assignProject(projectId: any, payload: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
        projectId: projectId.toString(),
      }),
    };

    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-permit/assign-project`,
      payload,
      options
    );
  }

  assignInquiry(inquiryId: any, payload: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
        inquiryId: inquiryId.toString()
      }),
    };

    return this.http.put<any>(
      `${environment.apiBaseurl}/etrack-permit/spatial-inquiry/assign-inquiry`,
      payload,
      options
    );
  }

  getAssignNotesDetails(projectId: any) {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
        projectId:projectId.toString()
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/assignment`,
      options
    );
  }

  getValidateRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analyst/user/validate`,
      options
    );
  }
  getAllActiveRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analyst/user/all-active`,
      options
    );

  }
  getTaskDueRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analyst/user/tasks-due`,
      options
    );

  }
  getApplicantResponseDueRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analyst/user/aplct-response-due`,
      options
    );
  }
  getOutForReviewRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analyst/user/out-for-review`,
      options
    );
  }
  getEmergencyAuthorizationRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analyst/user/emergency-apps`,
      options
    );
  }
  getSuspendedRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/analyst/user/suspended-apps`,
      options
    );
  }
  getPermitScreeningRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/JURISDICTION_DETERMINATION`,
      options
    );
  }
  getEnergyProjectRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/ENERGY_PROJ`,
      options
    );
  }
  getSanitorySewageRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/SANITARY_SEWER_EXT`,
      options
    );
  }
  getMgmtCompRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/MGMT_COMPRE_PLAN`,
      options
    );
  }
  getBblDeterminationRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/BOROUGH_DETERMINATION`,
      options
    );
  }
  getPreAppMtgRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/PRE_APPLN_REQ`,
      options
    );
  }
  getLeadAgencyRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/SEQR_LA_REQ`,
      options
    );
  }
  getSerpcertificationRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/SERP_CERT`,
      options
    );
  }
  getGeographicalRecords(){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/reviewer-dashboard`,
      options
    );
  }

  getRegionalPermitScreeningRecords(regionId?:number){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    if(regionId || regionId == 0) {

      return this.http.get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/JURISDICTION_DETERMINATION/${regionId}`,
        options
      );
    }
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/JURISDICTION_DETERMINATION`,
      options
    );
  }
  getRegionalEnergyProjectRecords(regionId?:number){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    if(regionId || regionId == 0) {

      return this.http.get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/ENERGY_PROJ/${regionId}`,
        options
      );
    }
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/ENERGY_PROJ`,
      options
    );
  }
  getRegionalSanitorySewageRecords(regionId?:number){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    if(regionId || regionId == 0) {

      return this.http.get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/SANITARY_SEWER_EXT/${regionId}`,
        options
      );
    }
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/SANITARY_SEWER_EXT`,
      options
    );
  }
  getRegionalMgmtCompRecords(regionId?:number){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    if(regionId || regionId == 0) {

      return this.http.get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/MGMT_COMPRE_PLAN/${regionId}`,
        options
      );
    }
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/MGMT_COMPRE_PLAN`,
      options
    );
  }
  getRegionalBblDeterminationRecords(regionId?:number){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    if(regionId || regionId == 0) { 
      return this.http.get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/BOROUGH_DETERMINATION/${regionId}`,
        options
      );
    }
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/BOROUGH_DETERMINATION`,
      options
    );
  }
  getRegionalPreAppMtgRecords(regionId?:number){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    if(regionId || regionId == 0) {

      return this.http.get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/PRE_APPLN_REQ/${regionId}`,
        options
      );
    }
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/PRE_APPLN_REQ`,
      options
    );
  }
  getRegionalLeadAgencyRecords(regionId?:number){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    if(regionId || regionId == 0) {

      return this.http.get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/SEQR_LA_REQ/${regionId}`,
        options
      );
    }
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/SEQR_LA_REQ`,
      options
    );
  }
  getRegionalSerpcertificationRecords(regionId?:number){
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    if(regionId || regionId == 0) {

      return this.http.get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/SERP_CERT/${regionId}`,
        options
      );
    }
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/regional/SERP_CERT`,
      options
    );
  }

  getRegionalAllActiveInquiriesRecords(regionId?: number) {
    let user = localStorage.getItem('loggedUserName');

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user?.replace('SVC', '').substring(1),
      }),
    };
    if(regionId || regionId == 0) {

      return this.http.get<any>(
        `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/all-inquiries/${regionId}`,
        options
      );
    }
    return this.http.get<any>(
      `${environment.apiBaseurl}/etrack-dart-db/spatial-inq/all-inquiries`,
      options
    );
  }


  // Method : DELETE
  // Header:
  // userId : <userId>
  // projectId : <projectId>

  // This end point helps to delete the non submittal projects.
}
