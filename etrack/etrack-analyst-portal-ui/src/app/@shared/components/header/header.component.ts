import {
  Component,
  ChangeDetectorRef,
  Input,
  ViewChild,
  ElementRef,
} from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { interval, Observable, Subject, Subscription } from 'rxjs';
import { filter, map, shareReplay } from 'rxjs/operators';
import { AuthService } from 'src/app/core/auth/auth.service';
import { NavigationEnd, ResolveEnd, Route, Router, ActivatedRoute, RoutesRecognized, ActivationEnd } from '@angular/router';
import { CommonService } from '../../services/commonService';
import { Utils } from '../../services/utils';
import { UserRole } from '../../constants/UserRole';
import { ProjectService } from '../../services/projectService';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';
import { MailNotificationsComponent } from '../mail-notifications/mail-notifications.component';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';

import { environment } from '../../../../environments/environment';
import { EmailValidator } from '@angular/forms';
import { get, isEmpty } from 'lodash';
import { InquiryService } from '../../services/inquiryService';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  isHandset$: Observable<boolean> = this.breakpointObserver
    .observe(Breakpoints.Handset)
    .pipe(
      map((result) => result.matches),
      shareReplay()
    );
  screenerName: string = '';
  userRoles: any[] = [];
  closeResult!: string;
  appVersion: string = '';
  hideRegularHeader: boolean = false;
  fullName: string = '';
  userRole = UserRole;
  mailNotificationsData: any[] = [];
  @ViewChild('alertModal', { static: true })
  alertModal!: CustomModalPopupComponent;
  @ViewChild('mailNotifications', { static: true })
  notificationalert!: MailNotificationsComponent;
  alertData: any[] = [];
  pageFrom: string | undefined = '';
  @Input() showAlertNotification: boolean = true;
  mailnotificationcount!: number;
  existingnotificationCount!: number;
  showAlertIcon: boolean = false;
  openAlertsSub: Subject<boolean> = new Subject();
  notificationInterval = new Subscription();
  isPage!: string;
  pageIsVW = false;
  emailCount: any;
  subs=new Subscription();
      projectId:any = '';
  inquiryId: any;
  isNoContent: boolean = false;

  constructor(
    private breakpointObserver: BreakpointObserver,
    public authService: AuthService,
    public router: Router,
    private inquiryService: InquiryService,
    private activatedRoute:ActivatedRoute,
    public cdr: ChangeDetectorRef,
    public commonService: CommonService,
    public projectService: ProjectService,
    private util: Utils,
    private vds:VirtualDesktopService
  ) {    
    this.appVersion = util.version;
    this.screenerName = `${this.authService.getUserInfo().unique_name}`;
    localStorage.setItem('loggedUserName', this.screenerName);
    const ppid = this.authService.getUserInfo()?.ppid;
    localStorage.setItem('ppid', ppid);
    

    this.router.events.subscribe((routerData) => {
      if (routerData instanceof ResolveEnd) {  
        if (routerData.url.includes('/virtual-workspace') || 
        routerData.url.includes('/gi-virtual-workspace')) {
          this.pageIsVW = true;
          this.hideRegularHeader = true;

        }else if(routerData.url ==='/dashboard'){
          this.pageIsVW = false;
          this.getProjectAlertsScheduler(false);
          this.hideRegularHeader = false;
          this.inquiryId = null;
        } else {
          this.inquiryId = null;
          this.hideRegularHeader = false;
          this.pageIsVW = false;
        }
      }
    });

  }
  ngAfterViewChecked() {
    this.cdr.detectChanges();
  }
  navigateTo(url: string) {
    this.router.navigate([url]);
  }
  ngOnDestroy() {
    this.notificationInterval.unsubscribe();
    this.subs.unsubscribe();
  }
  async getAllErrorMsgs() {
    try {
      let response = await this.commonService.getAllErrorMessages();
     // console.log(response);
      if (!!response) {
        setTimeout(()=>{
          this.commonService.setErrorMsgs(response['en-US']);
     //   console.log(response,'app comp');
        this.commonService.emitErrorMessages.next(true);
        },1000)
       
      }
    } catch (e) {
      this.commonService.setErrorMsgs(null);
      console.log(e);
    }
  }
  ngOnInit() {
    this.commonService.emitErrorMessages.subscribe((val)=>{
      //console.log(val,'head error');
    })
    
    this.getAllErrorMsgs();
    setTimeout(()=>{
      this.commonService.loadPermissions();

    },1000)
    this.getCurrentUserRole();
    this.getEmailData('initial');
    interval(environment.ajaxCallTime).subscribe(() => {
      this.getEmailData('ajax');
    });
    interval(environment.ajaxCallTime).subscribe(() => {
      this.getProjectAlertsScheduler();
    });
  

    // this.router.events.subscribe((routerData) => {
    //   if (routerData instanceof ResolveEnd) {
    //    if(routerData.url ==='/dashboard'){
    //     console.log('setting true');
    //       this.isDashboard = true;
    //   console.log('making that call');
    //   this.getProjectAlerts(false);
    //     }
    //   }
    // });

    this.router.events
      .pipe(filter((e) => e instanceof NavigationEnd))
      //@ts-ignore
      .subscribe((navEnd: NavigationEnd) => {
      //  console.log(navEnd.urlAfterRedirects);

        this.isPage = navEnd.urlAfterRedirects;
        
      });
      
      this.router.events
      .pipe(
        filter(e => (e instanceof ActivationEnd) && (Object.keys(e.snapshot.params).length > 0)),
        map(e => e instanceof ActivationEnd ? e.snapshot.params : {})
      )
      .subscribe(params => {
        this.projectId = params?.projectId;
        this.inquiryId = params?.inquiryId;
      });

      this.subs.add(this.vds.isNoContent.subscribe((flag: any) => {
        this.isNoContent = flag;
      }));
     
  }
  getEmailData(input: string) {
    this.commonService.getEnvelops().then((response) => {
      if (response != null || response != undefined) {
        this.emailCount = response.length;
        const unreadArray: any[] = [];
        response.map((item: any) => {
          if (item.unreadCount > 0) {
            unreadArray.push(item.unreadCount);
          }
        });
        if (unreadArray.length > 0) {
          this.showAlertIcon = true;
        } else {
          this.showAlertIcon = false;
        }
      }
    });
  }

  getCurrentUserRole() {
    // let userInfo = this.authService.getUserInfo();
    // this.commonService
    //   .getUsersRoleAndPermissions(userInfo.ppid)
    //   .then((response) => {
    //     this.userRoles = response.roles;
    //     this.authService.emitAuthInfo.next(response);
    //     //this.userRoles = ['DEC Program Staff'];
    //   }).catch(err=>this.authService.emitAuthInfo.next({isError:true,error:err}));
    this.subs.add(this.authService.emitAuthInfo.subscribe((authInfo: any) => {
      if (authInfo && !authInfo.isError){
        this.userRoles = authInfo.roles; // ['DEC Program Staff'];;
        this.updateFullname();
      } 
    }));
  }

  updateFullname(){
    if(this.pageIsVW){
        this.vds.assignedAnalyst.subscribe((analystName) => {
          this.fullName = analystName;
        })
        // const interval$ = setInterval(()=>{
        //   const vdsData = this.vds.assignedAnalyst;
        //   if(!isEmpty(vdsData)){
        //     clearInterval(interval$);
        //     let fullName = get(vdsData, 'loggedInUserName', null);
        //     if(this.userRoles.includes(this.userRole.DEC_Program_Staff)){
        //       fullName = get(vdsData, 'assignedAnalystName', null);
        //     }
        //     if(fullName){
        //       this.fullName = fullName;
        //     }
        //   }
        // }, 100);
      
    }else{
      this.fullName = `${
        this.authService.getUserInfo().first_name +
        ' ' +
        this.authService.getUserInfo().last_name
      }`;
      localStorage.setItem('fullName', this.fullName);
    }
        
  }
  openHelpPdf() {
    window.open(
      '../../assets/data/eTrack Analyst Portal (PI 3) User Guide.pdf',
      '_blank'
    );
  }
  logout() {
    this.authService.logout();
  }

  nameSplitter(name: string) {
    if (!name.length) return '';
    let nameArr = name.split('');
    return nameArr.length > 1
      ? `${nameArr[0][0].toUpperCase()} ${nameArr[1][0].toUpperCase()}`
      : nameArr[0][0].toUpperCase();
  }
  ngAfterViewInit() {
    this.screenerName = `${this.authService.getUserInfo().unique_name}`;
    this.pollNewNotifications();
  }
  getProjectAlerts(isOpenPopUp = false) {
    
    if (!this.pageIsVW) {
      this.projectService.getProjectAlerts().then((data) => {
        this.alertData = data;
        console.log("Alert", this.alertData = data);
        this.alertData = this.alertData.map((e: any) => {
          return {
            ...e,
            inquiryId: e.inquiryId ? this.inquiryService.formatInquiryId(''+e.inquiryId) : null
          }
        })
        if (isOpenPopUp) {
          this.openAlertsSub.next(true);
          this.alertModal.open('info');
        }
      });
    }
  }

  getProjectAlertsScheduler(isOpenPopUp = false) {
    
    if (!this.pageIsVW) {
      this.projectService.getProjectAlertsScheduler().then((data) => {
        this.alertData = data;
        this.alertData = this.alertData.map((e: any) => {
          return {
            ...e,
            inquiryId: e.inquiryId ? this.inquiryService.formatInquiryId(''+e.inquiryId) : null
          }
        })
        if (isOpenPopUp) {
          this.openAlertsSub.next(true);
          this.alertModal.open('info');
        }
      });
    }
  }


  pollNewNotifications() {
    if (!this.pageIsVW && this.isPage) {
      this.notificationInterval.add(
        interval(300000).subscribe(() => {
          this.getProjectAlertsScheduler();
        })
      );
      this.getProjectAlerts();
    }
  }

  openAlerts() {
    let urlArray: string[] = this.router.url.split('/');
    // if (urlArray.includes('regional-project')) {
    //   this.pageFrom = 'regional-project';
    // } else if (urlArray.includes('dashboard')) {
    //   this.pageFrom = 'dashboard';
    // } else if (urlArray.includes('virtual-workspace')) {
    //   // this.pageFrom = 'virtual-workspace';
    // } else {
    //   this.pageFrom = undefined;
    // }
    // if (this.pageFrom) {
      console.log("test-url", urlArray);
    
      if (
        !urlArray.includes('virtual-workspace')
      ) {
        this.getProjectAlerts(true);
      }
      // if (this.pageFrom === 'virtual-workspace') {
      //   this.virtualService.getVirtualDesktopData().subscribe(data => {
      //     this.alertData = data.alerts;
      //     this.alertModal.open();
      //   });
      // }
    // }
  }
  alertClosed() {
    if (!this.pageIsVW) {
      this.getProjectAlertsScheduler();
    }
  }
  showNotification() {
    let show = this.commonService.showAlertNotification.value;    
    if (show) return 'red';
    else return 'white';
  }

  openNotifications() {
    this.notificationalert.open(this.hideRegularHeader);
  }
}
