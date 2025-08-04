import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
  HttpParams,
} from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth/auth.service';
import { RolePermissions } from './permissions';
import { BehaviorSubject, Observable, Subject, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { emailSend } from 'src/app/@store/models/emailSend';

@Injectable({
  providedIn: 'root',
})
export class CommonService {
  isFixedFooter = new BehaviorSubject(false);
  selectedApplicants: any = [];
  searchApplicantType: string = '';
  fromScreen: string = '';
  closeApplicantModal = new BehaviorSubject<boolean>(false);
  closeApplicantSelectionModal = new BehaviorSubject<boolean>(false);
  closeAppSearchModal = new BehaviorSubject<boolean>(false);
  emitSubmitStatus = new BehaviorSubject<boolean>(false);
  emitLitCheckChanged = new BehaviorSubject<boolean>(false);
  projectIdChanged = new BehaviorSubject(false);
  activeMode = new BehaviorSubject('');
  errorMsgObj: any = {};
  restartStepperSub: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(
    false
  );
  stepThreePageFrom: BehaviorSubject<string> = new BehaviorSubject<string>(
    'service'
  );
  showAlertNotification: BehaviorSubject<boolean> =
    new BehaviorSubject<boolean>(false);
  emitErrorMessages = new BehaviorSubject<boolean>(false);
  emitClearTableFilters = new BehaviorSubject<boolean>(false);
  

  constructor(
    public http: HttpClient,
    public authService: AuthService,
    private router: Router
  ) {
    this.activeMode.next(localStorage.getItem('mode') || '');
    //sessionStorage.getItem('selectedApplicants')
  }

  public getData(): Observable<Response> {
    const error = new HttpErrorResponse({
      status: 500,
      statusText: 'This is a test',
    });
    return throwError(error) as any;
  }

  // permissions:any[]=[
  //     {role:"System Administrator", permissions: ["Create_Submittal","Submit_Submittal", "Resume_Submittal"]},
  //     {role:"Analsyst", permissions: ["Create_Submittal","Submit_Submittal", "Resume_Submittal"]}

  // ];

  // Array of permissions the current logged in user has
  permissions: any[] = [];
  roles: any[] = [];
  navigateToMainPage() {
    this.activeMode.next('');
    // localStorage.setItem('mode', '');
    this.router.navigate(['/apply-for-permit-details']);
  }
  setApplicants(array: any) {
    // array.sort((a: any, b: any) => parseInt(b) - parseInt(a));

    let index = array.findIndex((num: string) => num == '0');
    if (index >= 0) {
      array.splice(index, 1);
      array.push('0');
    }

    this.selectedApplicants = [...array];
    console.log('this.seectedApplicants', this.selectedApplicants);
  }
  addGreenBackground() {
    let body = document.getElementById('main-body');
    body?.classList.add('body-bg');
  }
  removeGreenBackground() {
    let body = document.getElementById('main-body');
    body?.classList.remove('body-bg');
  }
  setSelectedApplicantype(searchApplicantType: string) {
    this.searchApplicantType = searchApplicantType;
  }

  setFromScreen(fromScreen: string) {
    this.fromScreen = fromScreen;
  }
  getApplicants() {
    return this.selectedApplicants;
  }

  getselectedApplicantype() {
    return this.searchApplicantType;
  }
  getFromScreen() {
    return this.fromScreen;
  }
  /**
   * Gets logged in users list of permissions.
   * @constructor
   * @returns {void} Void
   */
  loadPermissions() {
    let userInfo = this.authService.getUserInfo();
    localStorage.setItem('loggedUserName', userInfo.unique_name);
    this.getUsersRoleAndPermissions(userInfo.ppid)
      .then((response) => {
        this.permissions = response.permissions;
        this.roles = response.roles;
        //  this.roles = ['DEC Program Staff']; // only for testing purpose. To be commented on production
        this.authService.emitAuthInfo.next(response);
      })
      .catch((err) =>
        this.authService.emitAuthInfo.next({ isError: true, error: err })
      );
    // this.roles=["Online Submitter"];
  }

  /**
   * Gets user's role and permissions.
   * @constructor
   * @param {any} ppid - The user's ppid.
   * @returns {Promise} Promise
   */
  getUsersRoleAndPermissions(ppid: any) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user ? user.replace('SVC', '').substring(1) : '',
        guid: ppid,
        // Authorisation: `Bearer ${token}`
      }),
    };
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-asms/user/authInfo`, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }

  /**
   * Activates a specific feature based on users role
   * @constructor
   * @param {string} feature - A string value of the feature.
   * @returns {Boolean || null} null
   */

  activateFeature(feature: string[]) {
    // let roles = ['Analsyst'];
    // for (var i = 0; i < this.permissions.length; i++) {
    //   if (
    //     roles.includes(this.permissions[i].role) &&
    //     this.permissions[i].permissions.includes(feature)
    //   ) {
    //     return true;
    //   }
    // }
    // return false;
    for (let i = 0; i < feature.length; i++) {
      if (!this.permissions.includes(RolePermissions[feature[i]])) return false;
    }
    return true;
  }
  getAllErrorMessages() {
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-config/messages`)
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  getAllConfigurations() {
    return this.http
      .get<any>(`${environment.apiBaseurl}/etrack-config/configTypes`)
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  getErrorMsgsObj() {
    // console.log(this.errorMsgObj, 'msgObj');
    return this.errorMsgObj;
  }
  setErrorMsgs(msgObj: any) {
    this.errorMsgObj = msgObj;
    // console.log(this.errorMsgObj, 'msgObj');
  }

  restartStepper() {
    this.restartStepperSub.next(true);
  }

  

  SendEmail(emailContent: emailSend, files: File[], projectId: number) {
    let user = localStorage.getItem('loggedUserName');
    let formData = new FormData();
    if (files?.length > 0) {
      Array.from(files).forEach((f) =>
        formData.append('attachments', new Blob([f], {}), f.name)
      );
    }
    formData.append(
      'emailContent',
      new Blob([JSON.stringify(emailContent)], {
        type: 'application/json',
      }),
      'emailContent.json'
    );

    let params = new HttpParams();
    params.append('emailContent', JSON.stringify({ emailContent }));

    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId: projectId?.toString() || '',
      }),
    };
    let url = `${environment.apiBaseurl}/etrack-permit/send-email`;
    return this.http.post(url, formData, options);
  }

  getEnvelops() {
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore,
        userId: user.replace('SVC', '').substring(1),
      }),
    };
    let url = `${environment.apiBaseurl}/etrack-dart-db/email/dashboard`;
    return this.http
      .get<any>(url, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  getVdEnvelops(queryParamsProjectId: any) {
    let user = localStorage.getItem('loggedUserName');
    let projectId = queryParamsProjectId;
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId: projectId || '0',
      }),
    };
    let url = `${environment.apiBaseurl}/etrack-dart-db/email/virtual-workspace`;
    return this.http
      .get<any>(url, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  getEnvelopDetails(correspondenceId: number, Id: string) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId: Id.toString() || '',
      }),
    };
    let url = `${environment.apiBaseurl}/etrack-dart-db/user/envelops/${correspondenceId}`;
    return this.http.get<any>(url, options);
  }

  getEmailSubjectsVD(
    correspondenceType: string,
    emailSenderId: string,
    emailReceiverId: string,
    projectId:string,
  ) {
    let user = localStorage.getItem('loggedUserName');   
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId: projectId || '0',
        emailSenderId,
        emailReceiverId,
      }),
    };
    let url = `${environment.apiBaseurl}/etrack-dart-db/email/virtual-workspace/${correspondenceType}`;
    return this.http
      .get<any>(url, options)
      .toPromise()
      .then((data) => {
        return data;
      });
  }
  emailStatus(correspondenceId: number, Id: string) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId: Id.toString() || '',
      }),
    };
    let url = `${environment.apiBaseurl}/etrack-permit/email-read/${correspondenceId}`;
    return this.http.post(url, null, options);
  }

  deleteNotification(correspondenceId: number, Id: string) {
    let user = localStorage.getItem('loggedUserName');
    const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        projectId: Id.toString() || '',
      }),
    };
    let url = `${environment.apiBaseurl}/etrack-permit/delete-email/${correspondenceId}`;
    return this.http.delete(url, options);
  }
  getSystemParameters() {
    let url = `${environment.apiBaseurl}/etrack-config/system-parameters`;
    return this.http.get<any>(url);
  }





}
