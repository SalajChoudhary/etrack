import { Component, OnInit, ViewChild } from '@angular/core';
import { Utils } from '../../../../@shared/services/utils';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/auth/auth.service';
import { CommonService } from 'src/app/@shared/services/commonService';
import { ActivatedRoute, DefaultUrlSerializer, Router } from '@angular/router';
import { DashboardService } from 'src/app/@shared/services/dashboard.service';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { get, cloneDeep, clone, concat } from 'lodash';
import moment from 'moment';
import { environment } from 'src/environments/environment';
// import { Server } from 'http';
import { fromEvent, Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import {
  AllActiveHeaders,
  ApplicationResponseHeaders,
  OutForReviewHeaders,
  ResumeEntryHeaders,
  ReviewEntryHeaders,
  TaskDueHeaders,
  ValidateHeaders,
  EmergencyAuthorizationHeaders,
  SuspendedHeaders,
  PermitScreeningHeaders,
  EnergyProjectHeaders,
  SanitorySewageHeaders,
  MGMTCompHeaders,
  BBLDeterminationHeaders,
  PreAppMeetingRequestHeaders,
  LeadAgencyRequestHeaders,
  SerpCertificationHeaders,
  GeographicalHeaders,
} from './dashboard-headers';
import { PTableHeader } from 'src/app/@shared/components/dashboard-table/table.model';
import { ValidatorService } from 'src/app/@shared/services/validator.service';
import { HeaderComponent } from 'src/app/@shared/components/header/header.component';
import { DashboardTableComponent } from '../../../../@shared/components/dashboard-table/dashboard-table.component';
import { DatePipe } from '@angular/common';
import { InquiryService } from 'src/app/@shared/services/inquiryService';
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {
  activeTab: any = 'resume-entry';
  activeSubTab: number = 1;
  closeResult = '';
  products: any[] = [];
  items: any = [];
  reviewItems: any = [];
  UserRole = UserRole;
  userRoles: any = [];
  applicantResponseData: any = [];
  validateData: any = [];
  allActiveData: any = [];
  taskDueData: any = [];
  private unsubscriber: Subject<void> = new Subject<void>();
  reviewEntryHeaders: PTableHeader[] = ReviewEntryHeaders;
  resumeEntryHeaders: PTableHeader[] = ResumeEntryHeaders;
  permitScreeningHeaders: PTableHeader[] = PermitScreeningHeaders;
  energyProjectHeaders: PTableHeader[] = EnergyProjectHeaders;
  sanitorySewageHeaders: PTableHeader[] = SanitorySewageHeaders;
  mgmtCompHeaders: PTableHeader[] = MGMTCompHeaders;
  bblDeterminationHeaders: PTableHeader[] = BBLDeterminationHeaders;
  preAppMeetingRequestHeaders: PTableHeader[] = PreAppMeetingRequestHeaders;
  leadAgencyRequestHeaders: PTableHeader[] = LeadAgencyRequestHeaders;
  serpCertificationHeaders: PTableHeader[] = SerpCertificationHeaders;
  applicationResponseHeaders: PTableHeader[] = ApplicationResponseHeaders;
  validateHeaders: PTableHeader[] = ValidateHeaders;
  taskDueHeaders: PTableHeader[] = TaskDueHeaders;
  allActiveHeaders: PTableHeader[] = AllActiveHeaders;
  outForReviewHeaders: PTableHeader[] = OutForReviewHeaders;
  emergencyAuthorizationHeaders: PTableHeader[] = EmergencyAuthorizationHeaders;
  suspendedHeaders: PTableHeader[] = SuspendedHeaders;
  geographicalHeaders: PTableHeader[] = GeographicalHeaders;
  outForReviewData: any[] = [];
  emergencyAuthorizationData: any[] = [];
  suspendedData: any[] = [];
  permitScreeningData: any[] = [];
  energyProjectData: any[] = [];
  sanitorySewageData: any[] = [];
  mgmtCompData: any[] = [];
  bblDeterminationData: any[] = [];
  preAppMeetingRequestData: any[] = [];
  leadAgencyRequestData: any[] = [];
  serpCertificationData: any[] = [];
  geographicalData: any[] = [];
  isProgramReviewer: boolean = false;
  isAnalyst: boolean = false;

  subs = new Subscription();
  isDisplayGrids: boolean = true;
  isDisplayGiGrid: boolean = true;

  searchItem:any='';
  searchTitle:string='';
  @ViewChild('dashboardTableComponent',{ static: false }) dashboardTableComponent!:DashboardTableComponent;
  // get totalRecords() {
  //   return (
  //     this.validateData?.length +
  //     this.allActiveData?.length +
  //     this.taskDueData?.length +
  //     this.applicantResponseData?.length +
  //     this.outForReviewData?.length +
  //     this.emergencyAuthorizationData?.length +
  //     this.suspendedData?.length
  //   );
  // }

  constructor(
    public commonService: CommonService,
    public authService: AuthService,
    public utils: Utils,
    public translate: TranslateService,
    private router: Router,
    private dashService: DashboardService,
    private route: ActivatedRoute,
    private datePipe: DatePipe,
    private inquiryService: InquiryService,
    private util: ValidatorService
  ) {
    translate.addLangs(['en', 'hi']);
    translate.setDefaultLang('en');
    const browserLang = translate.getBrowserLang();
    // translate code
    // translate.use(browserLang.match(/en|hi/) ? browserLang : 'en');
    translate.use('en');
    // translate.setTranslation('en', {
    //   DYNAMIC:{
    //     INPROGRESS:"In Progress"
    //   }
    //  });
    //  translate.setTranslation('hi', {
    //   DYNAMIC:{
    //     INPROGRESS:"चालू"
    //   }
    //  });
  }
  // goToVirtual() { //TODO: remove unused function
  //   this.router.navigate(['/virtual-workspace']);
  // }
  onDelete(event: any) { }

  onValidateDelete(event: any){}

  ngOnInit(): void {
    this.getCurrentUserRole();
    this.commonService.removeGreenBackground();
    setTimeout(() => {
      let screenerName = `${this.authService.getUserInfo().unique_name}`;
      localStorage.setItem('loggedUserName', screenerName);
      const ppid = this.authService.getUserInfo()?.ppid;
      localStorage.setItem('ppid', ppid);
      const fullName = `${this.authService.getUserInfo().first_name +
        ' ' +
        this.authService.getUserInfo().last_name
        }`;
      localStorage.setItem('fullName', fullName);
    }, 500);

    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
  }
  getCurrentUserRole() {
    this.subs.add(
      this.authService.emitAuthInfo.subscribe((authInfo: any) => {
        if (authInfo === null) return;
        if (authInfo && !authInfo.isError) this.userRoles = authInfo.roles;
        if (this.userRoles.includes(UserRole.DEC_Program_Staff)) {
          this.isProgramReviewer = true;
          this.activeTab = "permitApplications";
          this.searchTitle = "Search in My Reviews";
          this.getReviewerDashboardData();
        } else {
          this.isAnalyst = true;
          this.activeTab = "resume-entry";
          this.searchTitle = "Search in Resume Entry";
          this.getUserDashboardData();
        }
      })
    );
  }

  getUserDashboardData() {
    this.utils.emitLoadingEmitter(true);
    this.dashService.getUserDashboard().subscribe(
      (response) => {
        console.log("response", response)
        this.renderResumeEntry(
          this.util.replaceNullWithEmptyString(response)
        );
        // this.renderApplicantResponseDue(
        //   this.util.replaceNullWithEmptyString(
        //     get(response, 'applct-response-due', [])
        //   )
        // );
        // this.renderValidate(
        //   this.util.replaceNullWithEmptyString(get(response, 'validate', []))
        // );
        // console.log("Val Data", get(response, 'validate', []))
        // this.renderAllActive(
        //   this.util.replaceNullWithEmptyString(get(response, 'all-active', []))
        // );
        // this.renderTaskDue(
        //   this.util.replaceNullWithEmptyString(get(response, 'tasks-due', []))
        // );
        // this.renderOutForReview(
        //   this.util.replaceNullWithEmptyString(
        //     get(response, 'out-for-review', [])
        //   )
        // );

        // this.renderEmergencyAuthorization(
        //   this.util.replaceNullWithEmptyString(
        //     this.util.replaceNullWithEmptyString(get(response, 'emergency-authorization', []))
        //   )
        // ); 
        // console.log("EA Data", get(response, 'emergency-authorization', []))
        // this.renderSuspended(
        //   this.util.replaceNullWithEmptyString(
        //     get(response, 'suspended-apps', [])
        //   )
        // );





        // let array = Object.values(response);
        // // const resultArray = array.filter(
        // //   (gridData: any) => gridData && gridData.length > 0
        // // );
        // if (array.length) {
        //   this.isDisplayGrids = true;
        //   this.commonService.isFixedFooter.next(false);
        // } else {
        //   this.isDisplayGrids = false;
        //   this.commonService.isFixedFooter.next(true);
        // }
        this.utils.emitLoadingEmitter(false);
      },
      (err) => {
        this.items = [];

        this.utils.emitLoadingEmitter(false);
      }
    );
  }
  innerTabChange(input: any) {
    this.searchItem = '';
    this.activeTab = input;
    switch (input) {
      case 'resume-entry':
        this.items = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getUserDashboard().subscribe(response => {
          this.renderResumeEntry(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false)
          this.searchTitle = "Search in Resume Entry"
        })
        break;
      case 'validate':
        this.validateData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getValidateRecords().subscribe(response => {
          this.renderValidate(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
          this.searchTitle = "Search in Validate"
        })
        break;
      case 'allActive':
        this.allActiveData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getAllActiveRecords().subscribe(response => {
          this.renderAllActive(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
          this.searchTitle = "Search in All Active"
        })
        break;
      case 'taskDue':
        this.taskDueData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getTaskDueRecords().subscribe(response => {
          this.renderTaskDue(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
          this.searchTitle = "Search in Tasks Due"
        })
        break;
      case 'applicantResponseDue':
        this.applicantResponseData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getApplicantResponseDueRecords().subscribe(response => {
          this.renderApplicantResponseDue(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
          this.searchTitle = "Search in Applicant Response Due"
        })
        break;
      case 'outForReview':
        this.outForReviewData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getOutForReviewRecords().subscribe(response => {
          this.renderOutForReview(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
          this.searchTitle = "Search in Out For Review"
        })
        break;
      case 'emergencyAuthorization':
        this.emergencyAuthorizationData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getEmergencyAuthorizationRecords().subscribe(response => {
          this.renderEmergencyAuthorization(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
          this.searchTitle = "Search in Emergency Authorization"
        })
        break;
      case 'suspended':
        this.suspendedData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getSuspendedRecords().subscribe(response => {
          this.renderSuspended(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
          this.searchTitle = "Search in Suspended"
        })
        break;
      case 'permitScreenings':
        this.permitScreeningData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getPermitScreeningRecords().subscribe(response => {
          this.renderPermitScreening(this.util.replaceNullWithEmptyString(response));
          this.searchTitle = "Search in Permit Screenings"
          this.utils.emitLoadingEmitter(false);
        })

        break;
      case 'energyProject':
        this.energyProjectData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getEnergyProjectRecords().subscribe(response => {
          this.renderEnergyProject(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
        })
        this.searchTitle = "Search in Energy Projects"
        break;
      case 'sanitorySewage':
        this.sanitorySewageData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getSanitorySewageRecords().subscribe(response => {
          this.renderSanitorySewage(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
        });
        this.searchTitle = "Search in Sanitary Sewage Extension"
        break;
      case 'mgmtComp':
        this.mgmtCompData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getMgmtCompRecords().subscribe(response => {
          this.renderMgmtComp(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
        })
        this.searchTitle = "Search in Mgmt/Comp Plan"
        break;
      case 'bblDetermination':
        this.bblDeterminationData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getBblDeterminationRecords().subscribe(response => {
          this.renderBblDetermination(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
        })
        this.searchTitle = "Search in Borough/Block/Lot"
        break;
      case 'preAppMtgRequest':
        this.preAppMeetingRequestData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getPreAppMtgRecords().subscribe(response => {
          this.renderPreAppMtg(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
        })
        this.searchTitle = "Search in Pre-App Mtg Requests"
        break;
      case 'leadAgencyRequest':
        this.leadAgencyRequestData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getLeadAgencyRecords().subscribe(response => {
          this.renderLeadAgency(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
        })
        this.searchTitle = "Search in SEQR Lead Agency Req"
        break;
      case 'serpCertification':
        this.serpCertificationData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getSerpcertificationRecords().subscribe(response => {
          this.renderSerpCertification(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
        })
        this.searchTitle = "Search in SERP Certification"
        break;
      case 'permitApplications':
        this.reviewItems = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getReviewerDashboard().subscribe(response => {
          this.renderPermitApplications(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
        });
        this.searchTitle = "Search in Permit Applications"
        break;
      case 'geographical':
        this.geographicalData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashService.getGeographicalRecords().subscribe(response => {
          this.renderGeographical(this.util.replaceNullWithEmptyString(response));
          this.utils.emitLoadingEmitter(false);
        })
       
        this.searchTitle = "Search in Geographical Inquiries"
        break;
      default:
        break;
    }

  }
  getTime(date?: Date) {
    return date != null ? new Date(date).getTime() : 0;
  }

  getReviewerDashboardData() {
    this.dashService.getReviewerDashboard().subscribe(
      (response) => {


        if (response.length) {
          this.isDisplayGrids = true;
          this.commonService.isFixedFooter.next(false);
        } else {
          this.isDisplayGrids = false;
          this.commonService.isFixedFooter.next(true);
        }
        this.renderPermitApplications(response);
      },
      (err) => {
        this.items = [];
      }
    );

        this.dashService.getGeographicalRecords().subscribe(response => {
          if(response.length) {
            this.isDisplayGiGrid = true;
          }
          else {
            this.isDisplayGiGrid = false;
          }
        })
  }
  renderPermitApplications(data: any) {
    let projectId = new Set<any>();
    let applicant = new Set<any>();
    let decId = new Set<any>();
    let facilityName = new Set<any>();
    let county = new Set<any>();
    let municipality = new Set<any>();
    let permitType = new Set<any>();
    let analystName = new Set<any>();
    let dueDate = new Set<any>();
    let dateAssigned = new Set<any>();
    let edbPublicId = new Set<any>();
    let edbDistrictId = new Set<any>();
    this.reviewItems = this.util.replaceNullWithEmptyString(
      data.map((item: any) => {
        return {
          projectId: get(item, 'projectId', ''),
          applicant: get(item, 'applicant', ''),
          decId: get(item, 'facility.decId', ''),
          facilityName: get(item, 'facility.facilityName', ''),
          county: get(item, 'facility.county', ''),
          municipality: get(item, 'facility.municipality', ''),
          permitType: get(item, 'permitType', ''),
          analystName: get(item, 'analystName', ''),
          dueDate: get(item, 'dueDate', ''),
          permitTypeDesc: get(item, 'permitTypeDesc', ''),
          // ? new Date(get(item, 'dueDate', null))
          // : '',
          dateAssigned: get(item, 'dateAssigned', ''),
          edbPublicId: get(item, 'edbPublicId', ''),
          edbDistrictId: get(item, 'edbDistrictId', ''),
        };
      })
    );
    // this.reviewItems = this.reviewItems.sort(
    //   (a: any, b: any) => this.getTime(a.dueDate) - this.getTime(b.dueDate)
    // );
    this.reviewItems.sort((a: any, b: any) => {
      if (b.projectId < a.projectId) return -1;
      return 1;
    });
    this.reviewItems.forEach((data: any) => {
      if (data.projectId) projectId.add(data.projectId);
      if (data.applicant) applicant.add(data.applicant);
      if (data.decId) decId.add(data.decId);
      if (data.facilityName) facilityName.add(data.facilityName);
      if (data.county) county.add(data.county);
      if (data.municipality) municipality.add(data.municipality);
      if (data.permitType) permitType.add(data.permitType);
      if (data.analystName) analystName.add(data.analystName);
      if (data.dueDate) dueDate.add(data.dueDate);
      if (data.dateAssigned) dateAssigned.add(data.dateAssigned);
      if (data.edbPublicId) edbPublicId.add(data.edbPublicId);
      if (data.edbDistrictId) edbDistrictId.add(data.edbDistrictId);
      this.reviewEntryHeaders.forEach((header: PTableHeader) => {
        if (header.columnTitle === 'applicant') {
          data[header.columnTitle + 'linkToNavigate'] =
            environment.lrpUrl + data.edbPublicId;
        }
        if (header.columnTitle === 'facilityName') {
          data[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl + data?.edbDistrictId;
        }
        if (header.columnTitle === 'projectId') {
          data[header.columnTitle + 'linkToNavigate'] =
            '/virtual-workspace/' + data.projectId;


            }

            switch (header.columnTitle) {
              case 'projectId':
                header.filtersList = Array.from(projectId)
                  .sort((a: any, b: any) => {
                    if (a < b) return -1;
                    return 1;
                  })
                  .map((str: string) => {
                    return { label: str, value: str };
                  });
                break;

              case 'facilityName':
                header.filtersList = Array.from(facilityName)
                  .sort((a: any, b: any) => {
                    if (a < b) return -1;
                    return 1;
                  })
                  .map((str: string) => {
                    return { label: str, value: str };
                  });
                break;
              case 'decId':
                header.filtersList = Array.from(decId)
                  .sort((a: any, b: any) => {
                    if (a < b) return -1;
                    return 1;
                  })
                  .map((str: string) => {
                    return { label: str, value: str };
                  });
                break;
              case 'applicant':
                header.filtersList = Array.from(applicant)
                  .sort((a: any, b: any) => {
                    if (a < b) return -1;
                    return 1;
                  })
                  .map((str: string) => {
                    return { label: str, value: str };
                  });
                break;
              case 'county':
                header.filtersList = Array.from(county)
                  .sort((a: any, b: any) => {
                    if (a < b) return -1;
                    return 1;
                  })
                  .map((str: string) => {
                    return { label: str, value: str };
                  });
                break;
              case 'permitType':
                header.filtersList = Array.from(permitType)
                  .sort((a: any, b: any) => {
                    if (a < b) return -1;
                    return 1;
                  })
                  .map((str: string) => {
                    return { label: str, value: str };
                  });
                break;

              case 'municipality':
                header.filtersList = Array.from(municipality)
                  .sort((a: any, b: any) => {
                    if (a < b) return -1;
                    return 1;
                  })
                  .map((str: string) => {
                    return { label: str, value: str };
                  });
                break;

              case 'analystName':
                header.filtersList = Array.from(analystName)
                  .sort((a: any, b: any) => {
                    if (a < b) return -1;
                    return 1;
                  })
                  .map((str: string) => {
                    return { label: str, value: str };
                  });
                break;
              case 'dueDate':
                header.filtersList = Array.from(dueDate)
                  .sort((a: any, b: any) => {
                    if (new Date(b) < new Date(a)) return -1;
                    return 1;
                  })
                  .map((str: string) => {
                    return { label: str, value: str };
                  });
                break;
              case 'dateAssigned':
                header.filtersList = Array.from(dateAssigned)
                  .sort((a: any, b: any) => {
                    if (new Date(b) < new Date(a)) return -1;
                    return 1;
                  })
                  .map((str: string) => {
                    return { label: str, value: str };
                  });
                break;
              case 'edbPublicId':
                header.filtersList = Array.from(edbPublicId)
                  .sort((a: any, b: any) => {
                    if (a < b) return -1;
                    return 1;
                  })
                  .map((str: string) => {
                    return { label: str, value: str };
                  });
                break;
              case 'edbDistrictId':
                header.filtersList = Array.from(edbDistrictId)
                  .sort((a: any, b: any) => {
                    if (a < b) return -1;
                    return 1;
                  })
                  .map((str: string) => {
                    return { label: str, value: str };
                  });
                break;
              default:
                header.filtersList = [];
                break;
            }
          });
        });
      }
  

  renderResumeEntry(data: any) {
    data.forEach((obj: any) => {
      obj.createDateValueOf = moment(obj.createDate, 'MM/DD/YYYY').valueOf();
      obj.facilityName = obj?.facility?.facilityName;
      obj.locationDirections = this.getFullAddress(obj);
      obj.municipality = obj?.facility?.municipality;
      obj.eaInd = obj.eaInd === 'E' ? 'Yes' : 'No';
      obj.gpInd = obj.gpInd === 1 ? 'Yes' : 'No';
      this.resumeEntryHeaders.forEach((header: any) => {
        if (header.columnTitle === '' && header.isButton) {
          if(obj.rejectedProject === "Y"){
            obj.isButton = 'Rejected';
          }else{
            obj.isButton = 'Resume';
          }
          
        }
        if (header.columnTitle === 'eaInd') {
          if (obj.eaInd === 'Yes')
            obj[header.columnTitle + 'isCheckbox'] = true;
        }
        if (header.columnTitle === 'gpInd') {
          if (obj.gpInd === 'Yes')
            obj[header.columnTitle + 'isCheckbox'] = true;
        }
        
         //header.isRejected = obj['rejectedProject'] == 'Y' ? true : false;        
      });
    });
    data?.sort((a: any, b: any) => {
      if (a.createDateValueOf > b.createDateValueOf) return -1;
      return 1;
    });
    this.items = [...data];
    // this.getActiveTab();
  }

  renderPermitScreening(data: any) {
    let giArray = new Set<any>();
    let projectName = new Set<any>();
    let requestor = new Set<any>();
    let address = new Set<any>();
    let municipality = new Set<any>();
    let recvdDate = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      item.address = this.getGIAddress(item);
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
      }

      giArray.add(item.inquiryId ? item.inquiryId : '');
      projectName.add(item.projectName ? item.projectName: '');
      requestor.add(item.requestorName ? item.requestorName : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');

      this.permitScreeningHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId') {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
            + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
      });

      this.permitScreeningHeaders.forEach((header: any) => {
        switch(header.columnTitle) {
          case 'inquiryId':
            header.filtersList = Array.from(giArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'projectName':
            header.filtersList = Array.from(projectName)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'requestorName':
            header.filtersList = Array.from(requestor)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'address':
            header.filtersList = Array.from(address)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'municipality':
            header.filtersList = Array.from(municipality)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'rcvdDate':
            header.filtersList = Array.from(recvdDate)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          default:
            header.filtersList = [];
            break;
        }
      });
    });
    this.permitScreeningData = [...data];
  }
  renderGeographical(data:any){
    let giArray = new Set<any>();
    let inquiryType = new Set<any>();
    let requestor = new Set<any>();
    let nameAddress = new Set<any>();
    let municipalityCounty = new Set<any>();
    let projManager = new Set<any>();
    let reviewDue = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item:any)=>{
      item.address = this.getReviewerAddress(item);
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
      }
      item.municicounty = item.municipality +'/'+item.county;
      let projAddress = '';
      if(item.projectName) {
        projAddress += item.projectName;
        if(item.address) {
          projAddress += '/';
        }
      }
      item.projAddress = projAddress + item.address;
      if(item.dueDate) {

        item.revDue = this.datePipe.transform(item.dueDate, 'MM/dd/yyyy');
      }

      this.geographicalHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId') {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
            + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
      });

      giArray.add(item.inquiryId ? item.inquiryId : '');
      inquiryType.add(item.inquiryTypeDesc ? item.inquiryTypeDesc : '');
      requestor.add(item.requestorName ? item.requestorName : '');
      nameAddress.add(item.projAddress ? item.projAddress : '');
      municipalityCounty.add(item.municicounty ? item.municicounty : '');
      projManager.add(item.analystName ? item.analystName : '');
      reviewDue.add(item.revDue ? item.revDue : '');

      this.geographicalHeaders.forEach((header: any) => {
        switch(header.columnTitle) {
          case 'inquiryId':
            header.filtersList = Array.from(giArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'inquiryTypeDesc':
            header.filtersList = Array.from(inquiryType)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'requestorName':
            header.filtersList = Array.from(requestor)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'projAddress':
            header.filtersList = Array.from(nameAddress)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'municicounty':
            header.filtersList = Array.from(municipalityCounty)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'analystName':
            header.filtersList = Array.from(projManager)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'revDue':
            header.filtersList = Array.from(reviewDue)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          default:
            header.filtersList = [];
            break;
        }
      });
    });

    this.geographicalData = [...data];
  }
  renderEnergyProject(data: any) {
    let giArray = new Set<any>();
    let projectName = new Set<any>();
    let developer = new Set<any>();
    let owner = new Set<any>();
    let pscDocket = new Set<any>();
    let address = new Set<any>();
    let municipality = new Set<any>();
    let recvdDate = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      // map the data
      item.address = this.getGIAddress(item);
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
      }

      giArray.add(item.inquiryId ? item.inquiryId : '');
      projectName.add(item.projectName ? item.projectName : '');
      developer.add(item.developer ? item.developer : '');
      owner.add(item.owner ? item.owner : '');
      pscDocket.add(item.pscDocketNum ? item.pscDocketNum : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');

      this.energyProjectHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId') {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
      })

      this.energyProjectHeaders.forEach((header: any) => {
        switch(header.columnTitle) {
          case 'inquiryId':
            header.filtersList = Array.from(giArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'projectName':
            header.filtersList = Array.from(projectName)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'developer':
            header.filtersList = Array.from(developer)
                .sort((a: any, b: any) => {
                  if (a < b) return -1;
                  return 1;
                })
                .map((str: string) => {
                  return { label: str, value: str };
                });
            break;
          case 'owner':
            header.filtersList = Array.from(owner)
                .sort((a: any, b: any) => {
                  if (a < b) return -1;
                  return 1;
                })
                .map((str: string) => {
                  return { label: str, value: str };
                });
            break;
          case 'pscDocketNum':
            header.filtersList = Array.from(pscDocket)
                .sort((a: any, b: any) => {
                  if (a < b) return -1;
                  return 1;
                })
                .map((str: string) => {
                  return { label: str, value: str };
                });
            break;
          case 'address':
            header.filtersList = Array.from(address)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'municipality':
            header.filtersList = Array.from(municipality)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'rcvdDate':
            header.filtersList = Array.from(recvdDate)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          default:
            header.filtersList = [];
            break;
        }
      });
    });
    this.energyProjectData = [...data];
  }

  renderSanitorySewage(data: any) {
    let giArray = new Set<any>();
    let extender = new Set<any>();
    let dowContact = new Set<any>();
    let address = new Set<any>();
    let municipality = new Set<any>();
    let recvdDate = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      item.address = this.getGIAddress(item);
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
      }

      giArray.add(item.inquiryId ? item.inquiryId : '');
      extender.add(item.extenderName ? item.extenderName : '');
      dowContact.add(item.dowContact ? item.dowContact : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');

      this.sanitorySewageHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId') {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
      });

      this.sanitorySewageHeaders.forEach((header: any) => {
        switch(header.columnTitle) {
          case 'inquiryId':
            header.filtersList = Array.from(giArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'extName':
            header.filtersList = Array.from(extender)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'dowContact':
            header.filtersList = Array.from(dowContact)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'address':
            header.filtersList = Array.from(address)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'municipality':
            header.filtersList = Array.from(municipality)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'rcvdDate':
            header.filtersList = Array.from(recvdDate)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          default:
            header.filtersList = [];
            break;
        }
      });
    });
    this.sanitorySewageData = [...data];
  }

  renderMgmtComp(data: any) {
    let giArray = new Set<any>();
    let planName = new Set<any>();
    let requestor = new Set<any>();
    let address = new Set<any>();
    let municipality = new Set<any>();
    let recvdDate = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      // map the data
      item.address = this.getGIAddress(item);
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
      }

      giArray.add(item.inquiryId ? item.inquiryId : '');
      planName.add(item.planName ? item.planName : '');
      requestor.add(item.requestorName ? item.requestorName : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');

      this.mgmtCompHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId') {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
      });

      this.mgmtCompHeaders.forEach((header: any) => {
        switch(header.columnTitle) {
          case 'inquiryId':
            header.filtersList = Array.from(giArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'planName':
            header.filtersList = Array.from(planName)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'requestorName':
            header.filtersList = Array.from(requestor)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'address':
            header.filtersList = Array.from(address)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'municipality':
            header.filtersList = Array.from(municipality)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'rcvdDate':
            header.filtersList = Array.from(recvdDate)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          default:
            header.filtersList = [];
            break;
        }
      });

    });
    this.mgmtCompData = [...data];
  }

  renderBblDetermination(data: any) {
    let giArray = new Set<any>();
    let requestor = new Set<any>();
    let address = new Set<any>();
    let bblArray = new Set<any>();
    let recvdDate = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      // map the datalet 
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
      }
      
      this.bblDeterminationHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId') {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
      });
      let bbl = '';
      bbl += item.borough ? item.borough + '/' : '';
      bbl += item.block ? item.block + (item.lot ? '/' : '') : '';
      bbl += item.lot ? item.lot : '';
      item.bbl = bbl;
      item.address = this.getGIAddress(item);
      

      giArray.add(item.inquiryId ? item.inquiryId : '');
      requestor.add(item.requestorName ? item.requestorName : '');
      address.add(item.address ? item.address : '');
      bblArray.add(item.bbl);
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');

      this.bblDeterminationHeaders.forEach((header: any) => {
        switch(header.columnTitle) {
          case 'inquiryId':
            header.filtersList = Array.from(giArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'requestorName':
            header.filtersList = Array.from(requestor)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'address':
            header.filtersList = Array.from(address)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'bbl':
            header.filtersList = Array.from(bblArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'rcvdDate':
            header.filtersList = Array.from(recvdDate)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          default:
            header.filtersList = [];
            break;
        }
      });

    });
    this.bblDeterminationData = [...data];
  }

  renderPreAppMtg(data: any) {
    let giArray = new Set<any>();
    let projectName = new Set<any>();
    let projectSponsor = new Set<any>();
    let address = new Set<any>();
    let municipality = new Set<any>();
    let recvdDate = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      // map the data   
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
      }
      this.preAppMeetingRequestHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId') {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
      })

      item.address = this.getGIAddress(item);
      giArray.add(item.inquiryId ? item.inquiryId : '');
      projectName.add(item.projectName ? item.projectName : '');
      projectSponsor.add(item.projectSponsor ? item.projectSponsor : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');

      this.preAppMeetingRequestHeaders.forEach((header: any) => {
        switch(header.columnTitle) {
          case 'inquiryId':
            header.filtersList = Array.from(giArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'projectName':
            header.filtersList = Array.from(projectName)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'projectSponsor':
            header.filtersList = Array.from(projectSponsor)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'address':
            header.filtersList = Array.from(address)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'municipality':
            header.filtersList = Array.from(municipality)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'rcvdDate':
            header.filtersList = Array.from(recvdDate)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          default:
            header.filtersList = [];
            break;
        }
      })
    });
    this.preAppMeetingRequestData = [...data];
  }

  renderLeadAgency(data: any) {
    let giArray = new Set<any>();
    let projectName = new Set<any>();
    let projectSponsor = new Set<any>();
    let address = new Set<any>();
    let municipality = new Set<any>();
    let leadAgencyName = new Set<any>();
    let leadAgencyContact = new Set<any>();
    let recvdDate = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });
    
    data.forEach((item: any) => {
      // map the data
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
      }
      this.leadAgencyRequestHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId') {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
      });
      item.address = this.getGIAddress(item);

      giArray.add(item.inquiryId ? item.inquiryId : '');
      projectName.add(item.projectName ? item.projectName : '');
      projectSponsor.add(item.projectSponsor ? item.projectSponsor : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      leadAgencyName.add(item.leadAgencyName ? item.leadAgencyName : '');
      leadAgencyContact.add(item.leadAgencyContact ? item.leadAgencyContact : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');

      this.leadAgencyRequestHeaders.forEach((header: any) => {
        switch(header.columnTitle) {
          case 'inquiryId':
            header.filtersList = Array.from(giArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'projectName':
            header.filtersList = Array.from(projectName)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'projectSponsor':
            header.filtersList = Array.from(projectSponsor)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'address':
            header.filtersList = Array.from(address)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'municipality':
            header.filtersList = Array.from(municipality)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'leadAgencyName':
            header.filtersList = Array.from(leadAgencyName)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'leadAgencyContact':
            header.filtersList = Array.from(leadAgencyContact)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'rcvdDate':
            header.filtersList = Array.from(recvdDate)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          default:
            header.filtersList = [];
            break;
        }
      })
    });
    this.leadAgencyRequestData = [...data];
  }

  renderSerpCertification(data: any) {
    let giArray = new Set<any>();
    let projectName = new Set<any>();
    let efcContact = new Set<any>();
    let address = new Set<any>();
    let municipality = new Set<any>();
    let recvdDate = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      // map the data
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
      }
      this.serpCertificationHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId') {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
      })
      item.address = this.getGIAddress(item);

      giArray.add(item.inquiryId ? item.inquiryId : '');
      projectName.add(item.projectName ? item.projectName : '');
      efcContact.add(item.efcContact ? item.efcContact : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');

      this.serpCertificationHeaders.forEach((header: any) => {
        switch(header.columnTitle) {
          case 'inquiryId':
            header.filtersList = Array.from(giArray)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'projectName':
            header.filtersList = Array.from(projectName)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'efcContact':
            header.filtersList = Array.from(efcContact)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'address':
            header.filtersList = Array.from(address)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'municipality':
            header.filtersList = Array.from(municipality)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'rcvdDate':
            header.filtersList = Array.from(recvdDate)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          default:
            header.filtersList = [];
            break;
        }
      })
    });
    this.serpCertificationData = [...data];
  }



  // getActiveTab() {
  //   let activeTab = this.route.snapshot.queryParamMap.get('activeTab');
  //   if (activeTab) this.activeTab = activeTab;
  //   else {
  //     this.items?.length === 0 ? (this.activeTab = 'validate') : 'resume-entry';
  //   }
  // }
  renderApplicantResponseDue(data: any) {
    data?.sort((a: any, b: any) => {
      if (a.createDate > b.createDate) return -1;
      return 1;
    });
    let permitTypeArray = new Set<any>();
    let dartStatus = new Set<any>();
    let projectId = new Set<any>();
    let facilityName = new Set<any>();
    let locationDirections = new Set<any>();
    let trackedIdFormatted = new Set<any>();
    let dueDate = new Set<any>();
    let outForReview = new Set<any>();
    let eaArray = new Set<any>();
    let gpArray = new Set<any>();
    data.forEach((item: any) => {
      const dueDateMomented = moment(item.dueDate, 'MM/DD/YYYY');
      const createDateMomented = moment(item.createDate, 'MM/DD/YYYY');
      item.isReponseDateLessThanCurrentDate =
        dueDateMomented.isBefore(createDateMomented);
      item.locationDirections = this.getFullAddress(item);
    });

    const dataAlteredForSorting = data.map((item: any) => {
      item.facilityName = get(item, 'facility.facilityName', '');
      // item.locationDirections = get(item, 'facility.locationDirections', '');
      item.municipality = get(item, 'facility.municipality', '');

      if (item.permitType) {
        let permits = item.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      item.eaInd = item.eaInd === 'E' ? 'Yes' : 'No';
      item.gpInd = item.gpInd === 1 ? 'Yes' : 'No';
      item.outForReview = item.outForReview === 'Y' ? 'Yes' : 'No';
      if (item.permitType) {
        let permits = item.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      eaArray.add(item.eaInd ? item.eaInd : '');
      gpArray.add(item.gpInd ? item.gpInd : '');
      if (item.projectId) projectId.add(item.projectId);
      dartStatus.add(item.dartStatus ? item.dartStatus : '');
      trackedIdFormatted.add(
        item.trackedIdFormatted ? item.trackedIdFormatted : ''
      );
      // dueDate.add(item.responseDate ? item.dueDate : '');
      dueDate.add(item.dueDate);
      if (item.outForReview) outForReview.add(item.outForReview);
      facilityName.add(item.facilityName ? item.facilityName : '');
      locationDirections.add(
        item.locationDirections ? item.locationDirections : ''
      );
      this.applicationResponseHeaders.forEach((header: any) => {
        if (header.columnTitle === 'applicant') {
          item[header.columnTitle + 'linkToNavigate'] =
            environment.lrpUrl + item.edbPublicId;
        }
        if (header.columnTitle === 'facilityName') {
          item[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl + item.edbDistrictId;
        }
        if (header.columnTitle === 'projectId') {
          item[header.columnTitle + 'linkToNavigate'] = '/virtual-workspace/' + item.projectId;
        }
        if (header.columnTitle === 'eaInd') {
          if (item.eaInd === 'Yes')
            item[header.columnTitle + 'isCheckbox'] = true;
        }
        if (header.columnTitle === 'gpInd') {
          if (item.gpInd === 'Yes')
            item[header.columnTitle + 'isCheckbox'] = true;
        }
      });

      return item;
    });
    this.applicationResponseHeaders.forEach((header: any) => {
      switch (header.columnTitle) {
        case 'projectId':
          header.filtersList = Array.from(projectId)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;

        case 'facilityName':
          header.filtersList = Array.from(facilityName)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'locationDirections':
          header.filtersList = Array.from(locationDirections)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'trackedIdFormatted':
          header.filtersList = Array.from(trackedIdFormatted)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'outForReview':
          header.filtersList = Array.from(outForReview)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'dueDate':
          header.filtersList = Array.from(dueDate)
            .sort((a: any, b: any) => {
              if (new Date(b) < new Date(a)) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;

        case 'permitType':
          header.filtersList = Array.from(permitTypeArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;

        case 'dartStatus':
          header.filtersList = Array.from(dartStatus)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'eaInd':
          header.filtersList = Array.from(eaArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'gpInd':
          header.filtersList = Array.from(gpArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        default:
          header.filtersList = [];
          break;
      }
    });
    this.applicantResponseData = [...dataAlteredForSorting];
  }

  renderValidate(data: any) {
    let municipalityArray = new Set<any>();
    let permitTypeArray = new Set<any>();
    let appTypeArray = new Set<any>();
    let EAArray = new Set<any>();
    let GPArray = new Set<any>();
    let projectId = new Set<any>();
    let applicant = new Set<any>();
    let facilityName = new Set<any>();
    let locationDirections = new Set<any>();
    let rcvdDate = new Set<any>();

    const dataAlteredForSorting = data.map((item: any, i: number) => {
      item.facilityName = get(item, 'facility.facilityName', '');
      // item.locationDirections = get(item, 'facility.locationDirections', '');
      item.locationDirections = this.getFullAddress(item);
      //  obj.locationDirections=this.getFullAddress(obj);
      item.municipality = get(item, 'facility.municipality', '');
      // item.projectId = item.projectId ? item.projectId : '';
      municipalityArray.add(item.municipality ? item.municipality : '');
      if (item.permitType) {
        let permits = item.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      item.eaInd = item.eaInd === 'E' ? 'Yes' : 'No';
      item.gpInd = item.gpInd === 1 ? 'Yes' : 'No';
      appTypeArray.add(item.appType ? item.appType : '');

      EAArray.add(item.eaInd ? item.eaInd : '');
      GPArray.add(item.gpInd ? item.gpInd : '');
      if (item.projectId) projectId.add(item.projectId);
      applicant.add(item.applicant ? item.applicant : '');
      facilityName.add(item.facilityName ? item.facilityName : '');
      if (item.rcvdDate) rcvdDate.add(item.rcvdDate);

      locationDirections.add(
        item.locationDirections ? item.locationDirections : ''
      );

      this.validateHeaders.forEach((header: any) => {
        if (header.columnTitle === 'projectId') {
          item[header.columnTitle + 'linkToNavigate'] = '/virtual-workspace/' + item.projectId;
        }
        if (header.columnTitle === 'applicant') {
          item[header.columnTitle + 'linkToNavigate'] =
            environment.lrpUrl + item.edbPublicId;
        }
        if (header.columnTitle === 'facilityName') {
          item[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl + item.edbDistrictId;
        }
        if (header.columnTitle === '' && header.isButton) {
          item.isButton = 'VALIDATE';
        }

        if (header.columnTitle === 'eaInd') {
          if (item.eaInd === 'Yes')
            item[header.columnTitle + 'isCheckbox'] = true;
          // let permitTypeArr=item.permitType.split(',');
          // if(permitTypeArr.includes('EA'))item[header.columnTitle+'isCheckbox']=true;
        }
        if (header.columnTitle === 'gpInd') {
          if (item.gpInd === 'Yes')
            item[header.columnTitle + 'isCheckbox'] = true;
          // let permitTypeArr=item.permitType.split(',');
          // if(permitTypeArr.includes('GP'))item[header.columnTitle+'isCheckbox']=true;
        }
      });

      return item;
    });

    this.validateHeaders.forEach((header: any) => {
      switch (header.columnTitle) {
        case 'projectId':
          header.filtersList = Array.from(projectId)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'applicant':
          header.filtersList = Array.from(applicant)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'facilityName':
          header.filtersList = Array.from(facilityName)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'locationDirections':
          header.filtersList = Array.from(locationDirections)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'rcvdDate':
          header.filtersList = Array.from(rcvdDate)
            .sort((a: any, b: any) => {
              if (new Date(b) < new Date(a)) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'municipality':
          header.filtersList = Array.from(municipalityArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'permitType':
          header.filtersList = Array.from(permitTypeArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'appType':
          header.filtersList = Array.from(appTypeArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'eaInd':
          header.filtersList = Array.from(EAArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'gpInd':
          header.filtersList = Array.from(GPArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        default:
          header.filtersList = [];
          break;
      }
    });
    this.validateData = [...dataAlteredForSorting];
    this.validateData.forEach((item: any) => {
      item.rcvdDateValueOf = item?.rcvdDate
        ? moment(item?.rcvdDate, 'MM/DD/YYYY').valueOf()
        : '';
    });
    this.validateData?.sort((a: any, b: any) => {
      if (a.rcvdDateValueOf > b.rcvdDateValueOf) return -1;
      return 1;
    });
  }

  renderAllActive(data: any) {
    data?.sort((a: any, b: any) => {
      if (a.createDate > b.createDate) return -1;
      return 1;
    });
    let municipalityArray = new Set<any>();
    let permitTypeArray = new Set<any>();
    let appTypeArray = new Set<any>();
    let EAArray = new Set<any>();
    let GPArray = new Set<any>();

    let projectId = new Set<any>();
    let applicant = new Set<any>();
    let facilityName = new Set<any>();
    let locationDirections = new Set<any>();

    let trackedIdFormatted = new Set<any>();
    let batchId = new Set<any>();
    let dueDate = new Set<any>();
    let rcvdDate = new Set<any>();
    let dartStatus = new Set<any>();

    const dataAlteredForSorting = data.map((item: any, i: number) => {
      item.facilityName = get(item, 'facility.facilityName', '');
      // item.locationDirections = get(item, 'facility.locationDirections', '');
      item.locationDirections = this.getFullAddress(item);
      //  obj.locationDirections=this.getFullAddress(obj);
      item.municipality = get(item, 'facility.municipality', '');
      // item.projectId = item.projectId ? item.projectId : '';
      municipalityArray.add(item.municipality ? item.municipality : '');

      if (item.permitType) {
        let permits = item.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      item.eaInd = item.eaInd === 'E' ? 'Yes' : 'No';
      item.gpInd = item.gpInd === 1 ? 'Yes' : 'No';
      EAArray.add(item.eaInd ? item.eaInd : '');
      GPArray.add(item.gpInd ? item.gpInd : '');
      appTypeArray.add(item.appType ? item.appType : '');
      dartStatus.add(item.dartStatus ? item.dartStatus : '');
      if (item.projectId) projectId.add(item.projectId);
      applicant.add(item.applicant ? item.applicant : '');
      facilityName.add(item.facilityName ? item.facilityName : '');
      locationDirections.add(
        item.locationDirections ? item.locationDirections : ''
      );

      trackedIdFormatted.add(
        item.trackedIdFormatted ? item.trackedIdFormatted : ''
      );
      batchId.add(item.batchId ? item.batchId : '');
      rcvdDate.add(item.rcvdDate ? item.rcvdDate : '');
      dueDate.add(item.dueDate ? item.dueDate : '');

      this.allActiveHeaders.forEach((header: any) => {
        if (header.columnTitle === 'applicant') {
          item[header.columnTitle + 'linkToNavigate'] =
            environment.lrpUrl + item.edbPublicId;
        }
        if (header.columnTitle === 'facilityName') {
          item[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl + item.edbDistrictId;
        }
        if (header.columnTitle === 'projectId') {
          item[header.columnTitle + 'linkToNavigate'] = '/virtual-workspace/' + item.projectId;
        }
        if (header.columnTitle === 'eaInd') {
          if (item.eaInd === 'Yes')
            item[header.columnTitle + 'isCheckbox'] = true;
        }
        if (header.columnTitle === 'gpInd') {
          if (item.gpInd === 'Yes')
            item[header.columnTitle + 'isCheckbox'] = true;
        }
      });

      return item;
    });

    this.allActiveHeaders.forEach((header: any) => {
      switch (header.columnTitle) {
        case 'projectId':
          header.filtersList = Array.from(projectId)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'applicant':
          header.filtersList = Array.from(applicant)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'facilityName':
          header.filtersList = Array.from(facilityName)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'locationDirections':
          header.filtersList = Array.from(locationDirections)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'trackedIdFormatted':
          header.filtersList = Array.from(trackedIdFormatted)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'batchId':
          header.filtersList = Array.from(batchId)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'dueDate':
          header.filtersList = Array.from(dueDate)
            .sort((a: any, b: any) => {
              if (new Date(b) < new Date(a)) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'rcvdDate':
          header.filtersList = Array.from(rcvdDate)
            .sort((a: any, b: any) => {
              if (new Date(b) < new Date(a)) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'municipality':
          header.filtersList = Array.from(municipalityArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'permitType':
          header.filtersList = Array.from(permitTypeArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'appType':
          header.filtersList = Array.from(appTypeArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'dartStatus':
          header.filtersList = Array.from(dartStatus)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'eaInd':
          header.filtersList = Array.from(EAArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'gpInd':
          header.filtersList = Array.from(GPArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        default:
          header.filtersList = [];
          break;
      }
    });
    this.allActiveData = [...dataAlteredForSorting];
  }
  getFullAddress(obj: any): string {
    let address = '';
    let facility = obj.facility;
    address += facility.locationDirections
      ? facility.locationDirections + ', '
      : '';
    address += facility.city ? facility.city + ', ' : '';
    address += facility.state
      ? facility.state + (facility.zip ? ', ' : '')
      : '';
    address += facility.zip ? facility.zip : '';
    return address;
  }
 
  renderTaskDue(data: any) {
    let municipalityArray = new Set<any>();
    let permitTypeArray = new Set<any>();
    let dartStatus = new Set<any>();
    let projectId = new Set<any>();
    let applicant = new Set<any>();
    let facilityName = new Set<any>();
    let locationDirections = new Set<any>();
    let trackedIdFormatted = new Set<any>();
    let batchId = new Set<any>();
    let responseDate = new Set<any>();
    let outForReview = new Set<any>();
    let eaArray = new Set<any>();
    let gpArray = new Set<any>();
    data?.sort((a: any, b: any) => {
      if (a.createDate > b.createDate) return -1;
      return 1;
    });
    data.forEach((obj: any) => {
      obj.facilityName = obj?.facility?.facilityName;
      obj.locationDirections = this.getFullAddress(obj);
      obj.eaInd = obj.eaInd === 'E' ? 'Yes' : 'No';
      obj.gpInd = obj.gpInd === 1 ? 'Yes' : 'No';
      obj.outForReview = obj.outForReview ==='Y'? 'Yes' : 'No';
      obj.municipality = obj?.facility?.municipality;
      municipalityArray.add(obj.municipality ? obj.municipality : '');
      if (obj.permitType) {
        let permits = obj.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      dartStatus.add(obj.dartStatus ? obj.dartStatus : '');
      if (obj.projectId) projectId.add(obj.projectId);
      applicant.add(obj.applicant ? obj.applicant : '');
      facilityName.add(obj.facilityName ? obj.facilityName : '');
      eaArray.add(obj.eaInd ? obj.eaInd : '');
      gpArray.add(obj.gpInd ? obj.gpInd : '');
      outForReview.add(obj.outForReview ? obj.outForReview : '');
      locationDirections.add(
        obj.locationDirections ? obj.locationDirections : ''
      );

      trackedIdFormatted.add(
        obj.trackedIdFormatted ? obj.trackedIdFormatted : ''
      );
      batchId.add(obj.batchId ? obj.batchId : '');
      if (obj.dueDate) {
        responseDate.add(obj.dueDate ? obj.dueDate : '');
      }
      // if (obj.outForReview) {
      //   outForReview.add(obj.outForReview ? obj.outForReview : '');
      // }
      obj.outForReview = (obj.outForReview === 'Y' || obj.outForReview === 'Yes')? 'Yes' : 'No';
      obj.gpInd = obj.gpInd === 1 ? 'Yes' : 'No';
      obj.eaInd = obj.eaInd === 'E' ? 'Yes' : 'No';
      this.taskDueHeaders.forEach((header: any) => {
        if (header.columnTitle === 'applicant') {
          obj[header.columnTitle + 'linkToNavigate'] =
            environment.lrpUrl + obj.edbPublicId;
        }
        if (header.columnTitle === 'facilityName') {
          obj[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl +
            (obj.edbDistrictId ? obj.edbDistrictId : null);
        }
        if (header.columnTitle === 'projectId') {
          obj[header.columnTitle + 'linkToNavigate'] = '/virtual-workspace/' + obj.projectId;
        }
        if (header.columnTitle === 'eaInd') {
          if (obj.eaInd === 'Yes')
            obj[header.columnTitle + 'isCheckbox'] = true;
        }
        if (header.columnTitle === 'gpInd') {
          if (obj.gpInd === 'Yes')
            obj[header.columnTitle + 'isCheckbox'] = true;
        }
        if(header.columnTitle === 'outForReview'){
          if(obj.outForReview ==='Yes')
          obj[header.columnTitle + 'isCheckbox'] = true;
        }
      });
    });

    this.taskDueHeaders.forEach((header: any) => {
      switch (header.columnTitle) {
        case 'projectId':
          header.filtersList = Array.from(projectId)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'applicant':
          header.filtersList = Array.from(applicant)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'facilityName':
          header.filtersList = Array.from(facilityName)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'locationDirections':
          header.filtersList = Array.from(locationDirections)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'trackedIdFormatted':
          header.filtersList = Array.from(trackedIdFormatted)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'batchId':
          header.filtersList = Array.from(batchId)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'outForReview':
          header.filtersList = Array.from(outForReview)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'dueDate':
          header.filtersList = Array.from(responseDate)
            .sort((a: any, b: any) => {
              if (new Date(b) < new Date(a)) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'municipality':
          header.filtersList = Array.from(municipalityArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'permitType':
          header.filtersList = Array.from(permitTypeArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'dartStatus':
          header.filtersList = Array.from(dartStatus)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'eaInd':
          header.filtersList = Array.from(eaArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'gpInd':
          header.filtersList = Array.from(gpArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        default:
          header.filtersList = [];
          break;
      }
    });
    this.taskDueData = [...data];
  }

  renderOutForReview(data: any) {
    let eaArray = new Set<any>();
    let permitTypeArray = new Set<any>();
    let dartStatus = new Set<any>();
    let projectId = new Set<any>();
    let gpArray = new Set<any>();
    let facilityName = new Set<any>();
    let locationDirections = new Set<any>();
    let trackedIdFormatted = new Set<any>();
    let programStaff = new Set<any>();
    let dueDate = new Set<any>();
    data?.sort((a: any, b: any) => {
      if (a.createDate > b.createDate) return -1;
      return 1;
    });
    data?.forEach((obj: any) => {
      obj.eaInd = obj.eaInd === 'E' ? 'Yes' : 'No';
      obj.gpInd = obj.gpInd === 1 ? 'Yes' : 'No';
      obj.outForReview = obj.outForReview === 'Y' ? 'Yes' : 'No';
      obj.formattedAddress = obj?.facility?.formattedAddress
        ? obj.facility.formattedAddress
        : '';
      obj.facilityName = obj?.facility?.facilityName
        ? obj.facility.facilityName
        : '';
      eaArray.add(obj.eaInd ? obj.eaInd : '');
      gpArray.add(obj.gpInd ? obj.gpInd : '');

      if (obj.permitType) {
        let permits = obj.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      dartStatus.add(obj.dartStatus ? obj.dartStatus : '');
      projectId.add(obj.projectId ? obj.projectId : '');
      dueDate.add(obj.dueDate ? obj.dueDate : '');
      facilityName.add(
        obj?.facility?.facilityName ? obj.facility.facilityName : ''
      );
      locationDirections.add(
        obj?.facility?.formattedAddress ? obj.facility.formattedAddress : ''
      );

      trackedIdFormatted.add(
        obj.trackedIdFormatted ? obj.trackedIdFormatted : ''
      );
      programStaff.add(obj.programStaff ? obj.programStaff : '');

      this.outForReviewHeaders.forEach((header: any) => {
        if (header.columnTitle === 'facilityName') {
          obj[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl +
            (obj?.edbDistrictId ? obj?.edbDistrictId : null);
        }
        if (header.columnTitle === 'projectId') {
          obj[header.columnTitle + 'linkToNavigate'] = '/virtual-workspace/' + obj.projectId;
        }
        if (header.columnTitle === 'eaInd') {
          if (obj.eaInd === 'Yes')
            obj[header.columnTitle + 'isCheckbox'] = true;
        }
        if (header.columnTitle === 'gpInd') {
          if (obj.gpInd === 'Yes')
            obj[header.columnTitle + 'isCheckbox'] = true;
        }
      });
    });
    this.outForReviewHeaders.forEach((header: any) => {
      switch (header.columnTitle) {
        case 'projectId':
          header.filtersList = Array.from(projectId)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'programStaff':
          header.filtersList = Array.from(programStaff)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'dueDate':
          header.filtersList = Array.from(dueDate)
            .sort((a: any, b: any) => {
              if (new Date(b) < new Date(a)) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'facilityName':
          header.filtersList = Array.from(facilityName)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'formattedAddress':
          header.filtersList = Array.from(locationDirections)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'trackedIdFormatted':
          header.filtersList = Array.from(trackedIdFormatted)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'permitType':
          header.filtersList = Array.from(permitTypeArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'dartStatus':
          header.filtersList = Array.from(dartStatus)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'eaInd':
          header.filtersList = Array.from(eaArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'gpInd':
          header.filtersList = Array.from(gpArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        default:
          header.filtersList = [];
          break;
      }
    });
    this.outForReviewData = data ? [...cloneDeep(data)] : [...[]];
  }

  renderSuspended(data: any) {
    //TOOD: refractor the function to fit to suspended data
    let eaArray = new Set<any>();
    let permitTypeArray = new Set<any>();
    let dartStatus = new Set<any>();
    let projectId = new Set<any>();
    let gpArray = new Set<any>();
    let facilityName = new Set<any>();
    let locationDirections = new Set<any>();
    let trackedIdFormatted = new Set<any>();
    let programStaff = new Set<any>();
    let dueDate = new Set<any>();
    data?.sort((a: any, b: any) => {
      if (a.createDate > b.createDate) return -1;
      return 1;
    });
    data?.forEach((obj: any) => {
      obj.eaInd = obj.eaInd === 'E' ? 'Yes' : 'No';
      obj.gpInd = obj.gpInd === 1 ? 'Yes' : 'No';
      obj.formattedAddress = this.getFullAddress(obj);
      obj.facilityName = obj?.facility?.facilityName
        ? obj.facility.facilityName
        : '';
      eaArray.add(obj.eaInd ? obj.eaInd : '');
      gpArray.add(obj.gpInd ? obj.gpInd : '');
      dueDate.add(obj.dueDate ? obj.dueDate : '')
      if (obj.permitType) {
        let permits = obj.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      dartStatus.add(obj.dartStatus ? obj.dartStatus : '');
      projectId.add(obj.projectId ? obj.projectId : '');
      facilityName.add(
        obj?.facility?.facilityName ? obj.facility.facilityName : ''
      );
      locationDirections.add(
        obj.formattedAddress.trim()
      );

      trackedIdFormatted.add(
        obj.trackedIdFormatted ? obj.trackedIdFormatted : ''
      );
      programStaff.add(obj.programStaff ? obj.programStaff : '');

      this.suspendedHeaders.forEach((header: any) => {
        if (header.columnTitle === 'facilityName') {
          obj[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl +
            (obj?.edbDistrictId ? obj?.edbDistrictId : null);
        }
        if (header.columnTitle === 'projectId') {
          obj[header.columnTitle + 'linkToNavigate'] =
            '/virtual-workspace/'+obj.projectId+'?from=suspended';
        }
        if (header.columnTitle === 'eaInd') {
          if (obj.eaInd === 'Yes')
            obj[header.columnTitle + 'isCheckbox'] = true;
        }
        if (header.columnTitle === 'gpInd') {
          if (obj.gpInd === 'Yes')
            obj[header.columnTitle + 'isCheckbox'] = true;
        }
      });
    });
    this.suspendedHeaders.forEach((header: any) => {
      switch (header.columnTitle) {
        case 'projectId':
          header.filtersList = Array.from(projectId)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'programStaff':
          header.filtersList = Array.from(programStaff)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'facilityName':
          header.filtersList = Array.from(facilityName)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'formattedAddress':
          header.filtersList = Array.from(locationDirections)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'trackedIdFormatted':
          header.filtersList = Array.from(trackedIdFormatted)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'permitType':
          header.filtersList = Array.from(permitTypeArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'dartStatus':
          header.filtersList = Array.from(dartStatus)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'eaInd':
          header.filtersList = Array.from(eaArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'gpInd':
          header.filtersList = Array.from(gpArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'dueDate':
          header.filtersList = Array.from(dueDate)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        default:
          header.filtersList = [];
          break;
      }
    });
    this.suspendedData = data ? [...cloneDeep(data)] : [...[]];
  }

  renderEmergencyAuthorization(data: any) {
    //TOOD: refractor the function to fit to emergency authorization
    let eaArray = new Set<any>();
    let permitTypeArray = new Set<any>();
    let dartStatus = new Set<any>();
    let projectId = new Set<any>();
    let gpArray = new Set<any>();
    let facilityName = new Set<any>();
    let locationDirections = new Set<any>();
    let trackedIdFormatted = new Set<any>();
    let programStaff = new Set<any>();
    let dueDate = new Set<any>();
    data?.sort((a: any, b: any) => {
      if (a.dueDate > b.dueDate) return -1;
      return 1;
    });
    data?.forEach((obj: any) => {
      if (obj.eaInd === 'Yes' || obj.eaInd == 'E') {
        obj['eaInd' + 'isCheckbox'] = true;
      }

      this.emergencyAuthorizationHeaders.forEach((header: any) => {
        if (header.columnTitle === 'facilityName') {
          obj[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl +
            (obj?.edbDistrictId ? obj?.edbDistrictId : null);
        }
        if (header.columnTitle === 'projectId') {
          obj[header.columnTitle + 'linkToNavigate'] = '/virtual-workspace/' + obj.projectId;
        }
        if (header.columnTitle === 'eaInd') {
          if (obj.eaInd === 'Yes')
            obj[header.columnTitle + 'isCheckbox'] = true;
        }
        if (header.columnTitle === 'gpInd') {
          if (obj.gpInd === 'Yes')
            obj[header.columnTitle + 'isCheckbox'] = true;
        }
      });
    });

    const emergencyAuthorizationData = data ? [...data] : [...[]];
    this.emergencyAuthorizationData = emergencyAuthorizationData.filter(
      (item: any) => {
        return item.eaIndisCheckbox;
      }
    );
    this.emergencyAuthorizationData.forEach((obj: any) => {
      obj.eaInd = obj.eaInd === 'E' ? 'Yes' : 'No';
      obj.gpInd = obj.gpInd === 1 ? 'Yes' : 'No';
      obj.formattedAddress = this.getFullAddress(obj); // obj?.facility?.formattedAddress
      // ? obj.facility.formattedAddress
      // : '';
      obj.facilityName = obj?.facility?.facilityName
        ? obj.facility.facilityName
        : '';
      eaArray.add(obj.eaInd ? obj.eaInd : '');
      gpArray.add(obj.gpInd ? obj.gpInd : '');

      if (obj.permitType) {
        let permits = obj.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      dartStatus.add(obj.dartStatus ? obj.dartStatus : '');
      projectId.add(obj.projectId ? obj.projectId : '');
      facilityName.add(
        obj?.facility?.facilityName ? obj.facility.facilityName : ''
      );
      locationDirections.add(
        obj?.formattedAddress ? obj.formattedAddress : ''
      );

      trackedIdFormatted.add(
        obj.trackedIdFormatted ? obj.trackedIdFormatted : ''
      );
      programStaff.add(obj.programStaff ? obj.programStaff : '');
      dueDate.add(obj.dueDate ? obj.dueDate : '');
    });
    this.emergencyAuthorizationHeaders.forEach((header: any) => {
      switch (header.columnTitle) {
        case 'projectId':
          header.filtersList = Array.from(projectId)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'programStaff':
          header.filtersList = Array.from(programStaff)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'dueDate':
          header.filtersList = Array.from(dueDate)
            .sort((a: any, b: any) => {
              if (new Date(b) < new Date(a)) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'facilityName':
          header.filtersList = Array.from(facilityName)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'formattedAddress':
          header.filtersList = Array.from(locationDirections)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'trackedIdFormatted':
          header.filtersList = Array.from(trackedIdFormatted)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'permitType':
          header.filtersList = Array.from(permitTypeArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'dartStatus':
          header.filtersList = Array.from(dartStatus)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'eaInd':
          header.filtersList = Array.from(eaArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        case 'gpInd':
          header.filtersList = Array.from(gpArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
            });
          break;
        default:
          header.filtersList = [];
          break;
      }
    });
  }

  // getApplications(){ //TODO: Delete function
  //     this.dashService.getUserDashboard().subscribe((response)=>{
  //       response?.sort((a:any,b:any)=>{
  //         if(a.createDate>b.createDate)return -1;
  //         if(a.createDate<b.createDate)return 1;
  //         return 0;
  //       });
  //       this.items=[...response];
  //       if(this.items?.length===0)this.activeTab='validate';
  //       //this.items.pop();
  //     },err=>{
  //       this.items=[]
  //     });

  // }

  // getApplicantResponseData(){ //TODO Delete function
  //   this.dashService.getPendingApplications().subscribe((response)=>{
  //     response?.sort((a:any,b:any)=>{
  //       if(a.createDate>b.createDate)return -1;
  //       if(a.createDate<b.createDate)return 1;
  //       return 0;
  //     });
  //     this.applicantResponseData=[...response];
  //     //this.items.pop();
  //   },err=>{
  //     this.items=[]
  //   })
  // }
  //   // getProductsSmall() {
  //   return this.http
  //     .get<any>('assets/products-small.json')
  //     .toPromise()
  //     .then(res => res.data)
  //     .then(data => {
  //       return data;
  //     });
  // }
  search(){    
    this.dashboardTableComponent?.search(this.searchItem)
  }
  ngOnDestroy() {
    this.commonService.addGreenBackground();
    this.commonService.isFixedFooter.next(false);

    this.unsubscriber.next();
    this.unsubscriber.complete();
    this.subs.unsubscribe();
  }
  getReviewerAddress(obj: any) {
    let address = '';
    if(obj.street1 && obj.state && obj.city) {
      address += obj.street1 + ', ';
      address += obj.street2 ? obj.street2 + ', ' : '';
      address += obj.city;
      address += obj.state + (obj.zip ? ', ' : '');
      address += obj.zip ? obj.zip : '';
    }
    return address;
  }
  getGIAddress(obj: any) {
    let address = '';
    address += obj.mailingAddressStreet1 ? obj.mailingAddressStreet1 + ', ' : '';
    address += obj.mailingAddressStreet2 ? obj.mailingAddressStreet2 + ', ' : '';
    address += obj.mailingAddressCity ? obj.mailingAddressCity + ', ' : '';
    address += obj.mailingAddressState
      ? obj.mailingAddressState + (obj.mailingAddressZip ? ', ' : '')
      : '';
    address += obj.mailingAddressZip ? obj.mailingAddressZip : '';
    return address.trim();
  }

}
