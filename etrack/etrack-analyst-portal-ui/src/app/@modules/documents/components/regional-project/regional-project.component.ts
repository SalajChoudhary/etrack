import {
  Component,
  ElementRef,
  HostListener,
  OnInit,
  ViewChild,
} from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { NgbDateNativeUTCAdapter, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { get, isEmpty } from 'lodash';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { CommonService } from 'src/app/@shared/services/commonService';
import { DashboardService } from 'src/app/@shared/services/dashboard.service';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { Utils } from 'src/app/@shared/services/utils';
import { AuthService } from 'src/app/core/auth/auth.service';
import { environment } from 'src/environments/environment';
import { whiteSpaceValidator } from 'src/app/@shared/applicationInformation.validator';
import { DocumentService } from 'src/app/@shared/services/documentService';
import { take } from 'rxjs/operators';
import {
  AllActiveHeaders,
  UnValidatedHeaders,
  DisposedHeaders,
  BBLHeaders,
  EnergyProjectHeaders,
  LeadAgencyRequestHeaders,
  PermitScreeningHeaders,
  PreAppMeetingRequestHeaders,
  SanitorySewageHeaders,
  SerpCertificationHeaders,
  MGMTCompHeaders,
  AllActiveInquiriesHeaders,
} from './UnValidatedHeaders';
import { ProgramReviewHeaders, GIProgramReviewHeaders } from './ProgramReviewHeaders';
import { PTableHeader } from '../../../../@shared/components/dashboard-table/table.model';
import { ValidatorService } from 'src/app/@shared/services/validator.service';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { Subscription } from 'rxjs';
import { DashboardTableComponent } from '../../../../@shared/components/dashboard-table/dashboard-table.component';
import { InquiryService } from 'src/app/@shared/services/inquiryService';
@Component({
  selector: 'app-regional-project',
  templateUrl: './regional-project.component.html',
  styleUrls: ['./regional-project.component.scss'],
})
export class RegionalProjectComponent implements OnInit {
  // @ViewChild('dropdown',{static:false}) dropDownElem!:ElementRef;
  isInputClicked: boolean = false;
  projectManagersList: any = [];
  selectedManager: any = null;
  @ViewChild('assignModal', { static: true })
  assignModal!: any;
  notesFormGroup!: UntypedFormGroup;
  userRoles: any = [];
  isEligibleToDisplay: boolean = false;
  activeTab: string = 'program-review';
  unValidatedData: any = [];
  errorMsgObj: any = {};
  unValidatedHeaders: PTableHeader[] = UnValidatedHeaders;
  programReviewHeaders: PTableHeader[] = ProgramReviewHeaders;
  GIProgramReviewHeaders: PTableHeader[] = GIProgramReviewHeaders;
  allActiveHeaders: PTableHeader[] = AllActiveHeaders;
  disposedHeaders: PTableHeader[] = DisposedHeaders;
  leadAgencyRequestHeaders: PTableHeader[] = LeadAgencyRequestHeaders;
  preAppMeetingRequestHeaders: PTableHeader[] = PreAppMeetingRequestHeaders;
  bblDeterminationHeaders: PTableHeader[] = BBLHeaders;
  mgmtCompHeaders: PTableHeader[] = MGMTCompHeaders;
  sanitorySewageHeaders: PTableHeader[] = SanitorySewageHeaders;
  energyProjectHeaders: PTableHeader[] = EnergyProjectHeaders;
  permitScreeningHeaders: PTableHeader[] = PermitScreeningHeaders;
  serpCertificationHeaders: PTableHeader[] = SerpCertificationHeaders;
  allActiveInquiriesHeaders: PTableHeader[] = AllActiveInquiriesHeaders;
  disposedData: any = [];
  modalReference!: NgbModalRef;
  note: string = '';
  maxNote: any = 300;
  projectManagers: any = [];
  region: any = '';
  regionList: any = [];
  submitted: boolean = false;
  currentProjectId: string = '';
  assignDetails: any = {};
  reviewerData: any[] = [];
  allActiveData: any[] = [];
  isEdit: boolean = false;
  currentRow: any = {};
  currentInquiryId: any;
  currentRowIsInquiry: boolean = false;
  serverErrorMessage!: string;
  showServerError = false;
  subscription = new Subscription();
  isDisplayGrids: boolean = true;
  searchTitle: string = '';
  searchItem: any = '';
  currentSelectedAnalystObj: any;
  permitScreeningData: any[] = [];
  energyProjectData: any[] = [];
  sanitorySewageData: any[] = [];
  mgmtCompData: any[] = [];
  bblDeterminationData: any[] = [];
  preAppMeetingRequestData: any[] = [];
  leadAgencyRequestData: any[] = [];
  serpCertificationData: any[] = [];
  allActiveInquiriesData: any[] = [];
  GIReviewerData: any[] = [];
  assignedRegion: any = '';
  isAssignedRegion: boolean = true;

  @ViewChild('dashboardTableComponent', { static: false })
  dashboardTableComponent!: DashboardTableComponent;
  constructor(
    public commonService: CommonService,
    public authService: AuthService,
    public utils: Utils,
    public translate: TranslateService,
    private modalService: NgbModal,
    private formBuilder: UntypedFormBuilder,
    private dashboardService: DashboardService,
    private projectService: ProjectService,
    private docService: DocumentService,
    private util: ValidatorService,
    private inquiryService: InquiryService,
    private errorService: ErrorService
  ) {}

  ngOnInit(): void {
    this.initiateForm();
    this.getCurrentUserRole();
    // if(!this.regionList || !this.regionList.length){
    //this.getRegions();
    // }
    //
    this.getAllErrorMsgs();
    this.commonService.removeGreenBackground();
    // this.projectService.getProjectAlerts().then(
    //   (data: any[]) => {
    //     data.forEach((alert) => {
    //       if (alert.msgRead === 'N') {
    //         //this.showAlertNotification = true;
    //         this.commonService.showAlertNotification.next(true);
    //         return;
    //       }
    //     });
    //     console.log('All alerts are read');

    //     // this.commonService.showAlertNotification.next(false);
    //   },
    //   (error: any) => {
    //     this.serverErrorMessage = this.errorService.getServerMessage(error);
    //     this.showServerError = true;
    //     throw error;
    //   }
    // );
  }

  getAnalystsList() {
    this.dashboardService.getAnalystsByRegion(this.region).subscribe(
      (response) => {
        response?.sort((a: any, b: any) => {
          if (b.managerName > a.managerName) return -1;
          return 1;
        });
        this.projectManagersList = response;
        this.projectManagers = Object.assign([], response);
      },
      (error: any) => {
        this.projectManagersList = [];
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }
  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMsgObj = this.commonService.getErrorMsgsObj();
    });
  }

  getRegions() {
    this.utils.emitLoadingEmitter(true);
    this.projectService.getAllRegions().then(
      (response) => {
        this.regionList = response;
        if (this.isEligibleToDisplay) {
          this.dashboardService.getAnalystRegion().subscribe(res => {
            this.utils.emitLoadingEmitter(false);
            if(res == null) {
              this.isAssignedRegion = false;
            }
            else {
              this.assignedRegion = res;
              this.innerTabChange(this.activeTab, false);
            }
          }, (err: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(err);
            this.showServerError = true;
            throw err;
          })
          
        } else {
          this.utils.emitLoadingEmitter(false);
          this.getProgramReveiwerData();
        }

        console.log('regions', response);
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }
  onRegionChange(e: any) {
    console.log('Region Changed ' + e.target.value);
    this.region = e.target.value;
    if (this.isEligibleToDisplay) {
      //this.getUnValidatedData(true);
      this.innerTabChange(this.activeTab, true);
    } else {
      this.getProgramReveiwerData();
    }

    this.getAnalystsList();
  }

  getProgramReveiwerData() {
    console.log("Act tab", this.activeTab)
    console.log("this region", this.region)
    if (this.region === null || this.region === undefined) return;
    this.utils.emitLoadingEmitter(true);  
    if(this.region === ''){this.region ='-1';}	
    this.dashboardService.getReviewerRegionalDashboard(this.region).subscribe(
      (response) => {
        this.utils.emitLoadingEmitter(false);
        this.isDisplayGrids = true;
        if (response.length) {          
          this.commonService.isFixedFooter.next(false);
        } else {
          this.commonService.isFixedFooter.next(true);
        }
        this.reviewerData = this.renderReviewerData(
          this.util.replaceNullWithEmptyString(response)
        );
      },
      (error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  getUnValidatedData(isRegionChange = false) {
    console.log('Region response ' + this.region);
    //if (this.region === null || this.region === undefined) return;
    this.utils.emitLoadingEmitter(true);
    this.dashboardService.getRegionalUnvalidatedRecords(this.region).subscribe(
      (response) => {
        console.log(response);
        this.unValidatedData = this.renderUnValidated(response);
        console.log('Unvalidated List');
        console.log(this.unValidatedData);
        console.log(response, 'activeregion');
        this.utils.emitLoadingEmitter(false);
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  innerTabChange(input: any, regionChange: any) {
    this.searchItem = '';
    this.activeTab = input;
    if(!regionChange){
        this.region= this.assignedRegion;   
    }
    switch (input) {
      case 'unvalidated':
        this.unValidatedData = [];
        console.log("UNValidated Region", this.region)
        this.utils.emitLoadingEmitter(true)
        this.dashboardService.getRegionalUnvalidatedRecords(this.region).subscribe(response => {
          this.unValidatedData = this.renderUnValidated(this.util.replaceNullWithEmptyString(response))
          this.utils.emitLoadingEmitter(false);
          this.searchTitle = "Search in Unvalidated";
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        })
        break; 
      case 'all-active-regional':
        
        this.allActiveData = [];
        this.utils.emitLoadingEmitter(true)
        if(regionChange) {
          if(this.region === ''){
           this.region = '-1';
          }
        } 
        this.dashboardService.getRegionalDashboard(this.region).subscribe(response => {
          this.allActiveData = this.renderAllActive(this.util.replaceNullWithEmptyString(response))
          this.utils.emitLoadingEmitter(false)
          this.searchTitle = "Search in All Active";
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        })
        break;

      case 'program-review':
        
        this.reviewerData = [];
        this.utils.emitLoadingEmitter(true)
        this.dashboardService.getRegionalProgramreviewRecords(this.region).subscribe(response => {
          this.reviewerData = this.renderReviewerData(this.util.replaceNullWithEmptyString(response))
          console.log("Program Review",this.reviewerData  )
          this.utils.emitLoadingEmitter(false)
          this.searchTitle="Search in Program Review";
        })
        break;
      case 'gi-program-review':
        
        this.GIReviewerData = [];
        // this.utils.emitLoadingEmitter(true)
        // this.dashboardService.getRegionalAllActiveInquiriesRecords(this.region).subscribe(response => {
        //   this.renderProgramReviewAllActiveInquiries(this.util.replaceNullWithEmptyString(response))
        //   this.utils.emitLoadingEmitter(false)
        //   this.searchTitle="Search in All Active Inquiries";
        // })
        this.searchTitle="Search in All Active Inquiries";
        break;
      case 'disposed':
        this.disposedData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashboardService
          .getRegionalDisposedRecords(this.region)
          .subscribe((response) => {
            this.disposedData = this.renderDisposed(
              this.util.replaceNullWithEmptyString(response)
            );
            this.utils.emitLoadingEmitter(false);
            this.searchTitle = 'Search in Disposed';
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          });
        break;
      case 'permitScreenings':
        this.permitScreeningData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashboardService
          .getRegionalPermitScreeningRecords(this.region)
          .subscribe((response) => {
            this.renderPermitScreening(
              this.util.replaceNullWithEmptyString(response)
            );
            this.searchTitle = 'Search in Permit Screenings';
            this.utils.emitLoadingEmitter(false);
          });

        break;
      case 'energyProject':
        this.energyProjectData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashboardService
          .getRegionalEnergyProjectRecords(this.region)
          .subscribe((response) => {
            this.renderEnergyProject(
              this.util.replaceNullWithEmptyString(response)
              );
              this.utils.emitLoadingEmitter(false);
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          });
        this.searchTitle = 'Search in Energy Projects';
        break;
      case 'sanitorySewage':
        this.sanitorySewageData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashboardService
          .getRegionalSanitorySewageRecords(this.region)
          .subscribe((response) => {
            this.renderSanitorySewage(
              this.util.replaceNullWithEmptyString(response)
            );
            this.utils.emitLoadingEmitter(false);
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          });
        this.searchTitle = 'Search in Sanitary Sewage Extension';
        break;
      case 'mgmtComp':
        this.mgmtCompData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashboardService
          .getRegionalMgmtCompRecords(this.region)
          .subscribe((response) => {
            this.renderMgmtComp(this.util.replaceNullWithEmptyString(response));
            this.utils.emitLoadingEmitter(false);
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          });
        this.searchTitle = 'Search in MGMT/Comp Plan';
        break;
      case 'bblDetermination':
        this.bblDeterminationData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashboardService
          .getRegionalBblDeterminationRecords(this.region)
          .subscribe((response) => {
            this.renderBblDetermination(
              this.util.replaceNullWithEmptyString(response)
              );
            this.utils.emitLoadingEmitter(false);
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          });
        this.searchTitle = 'Search in Borough/Block/Lot';
        break;
      case 'preAppMtgRequest':
        this.preAppMeetingRequestData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashboardService
          .getRegionalPreAppMtgRecords(this.region)
          .subscribe((response) => {
            this.renderPreAppMtg(
              this.util.replaceNullWithEmptyString(response)
            );
            this.utils.emitLoadingEmitter(false);
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          });
        this.searchTitle = 'Search in Pre-App Mtg Req';
        break;
      case 'leadAgencyRequest':
        this.leadAgencyRequestData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashboardService
          .getRegionalLeadAgencyRecords(this.region)
          .subscribe((response) => {
            this.renderLeadAgency(
              this.util.replaceNullWithEmptyString(response)
            );
            this.utils.emitLoadingEmitter(false);
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          });
        this.searchTitle = 'Search in SEQR Lead Agency Req';
        break;
      case 'serpCertification':
        this.serpCertificationData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashboardService
          .getRegionalSerpcertificationRecords(this.region)
          .subscribe((response) => {
            this.renderSerpCertification(
              this.util.replaceNullWithEmptyString(response)
            );
            this.utils.emitLoadingEmitter(false);
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          });
        this.searchTitle = 'Search in SERP Certification';
        break;
      case 'allActiveInquiries':
        this.allActiveInquiriesData = [];
        this.utils.emitLoadingEmitter(true);
        this.dashboardService
          .getRegionalAllActiveInquiriesRecords(this.region)
          .subscribe((response) => {
            this.renderAllActiveInquiries(
              this.util.replaceNullWithEmptyString(response)
            );
            this.utils.emitLoadingEmitter(false);
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          });
        this.searchTitle = 'Search in All Active Inquiries';
        break;

      default:
        break;
    }
  }
  renderDisposed(data: any[]) {
    data?.sort((a: any, b: any) => {
      if (a.createDate > b.createDate) return -1;
      return 1;
    });
    let municipalityArray = new Set<any>();
    let permitTypeArray = new Set<any>();
    let appTypeArray = new Set<any>();
    let county = new Set<any>();
    let projectId = new Set<any>();
    let applicant = new Set<any>();
    let facilityName = new Set<any>();
    let effectiveDate = new Set<any>();
    let pmArray = new Set<any>();
    let eaArray = new Set<any>();
    let gpArray = new Set<any>();
    let dartStatus = new Set<any>();
    const dataAlteredForSorting = data.map((item: any, i: number) => {
      item.facilityName = get(item, 'facility.facilityName', '');
      // item.locationDirections = get(item, 'facility.locationDirections', '');
      item.locationDirections = this.getFullAddress(item);
      //  obj.locationDirections=this.getFullAddress(obj);
      item.municipality = get(item, 'facility.municipality', '');
      item.county = get(item, 'facility.county', '');
      if (item.permitType) {
        let permits = item.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      item.eaInd = item.eaInd === 'E' ? 'Yes' : 'No';
      item.gpInd = item.gpInd === 1 ? 'Yes' : 'No';
      eaArray.add(item.eaInd ? item.eaInd : '');
      gpArray.add(item.gpInd ? item.gpInd : '');
      if (item.permitType) {
        let permits = item.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      if (item.appType) {
        appTypeArray.add(item.appType ? item.appType : '');
      }
      if (item.municipality) {
        municipalityArray.add(item.municipality ? item.municipality : '');
      }
      if (item.county) {
        county.add(item.county ? item.county : '');
      }

      projectId.add(item.projectId ? item.projectId : '');
      if (item.applicant) applicant.add(item.applicant);
      facilityName.add(item.facilityName ? item.facilityName : '');
      effectiveDate.add(item.effectiveDate ? item.effectiveDate : '');
      dartStatus.add(item.dartStatus ? item.dartStatus : '');
      eaArray.add(item.eaInd ? item.eaInd : '');
      gpArray.add(item.gpInd ? item.gpInd : '');
      pmArray.add(item.analystName ? item.analystName : 'UNASSIGNED');

      this.disposedHeaders.forEach((header: any) => {
        if (header.columnTitle === 'analystName') {
          item.analystName = item.analystName ? item.analystName : 'UNASSIGNED';
        }

        if (header.columnTitle === 'applicant') {
          item[header.columnTitle + 'linkToNavigate'] =
            environment.lrpUrl + item.edbPublicId;
        }
        if (header.columnTitle === 'facilityName') {
          item[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl + item.facility?.districtId;
        }
        if (header.columnTitle === 'projectId') {
          item[header.columnTitle + 'linkToNavigate'] =
            '/virtual-workspace/' + item.projectId + '?from=disposed';
          // '/virtual-workspace?from=disposed/'+item.projectId;
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
    this.disposedHeaders.forEach((header: any) => {
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

        case 'effectiveDate':
          header.filtersList = Array.from(effectiveDate)
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
        case 'analystName':
          header.filtersList = Array.from(pmArray)
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
    console.log('j here', dataAlteredForSorting);

    return [...dataAlteredForSorting];
  }
  renderAllActive(data: any[]) {
    data?.sort((a: any, b: any) => {
      if (a.createDate > b.createDate) return -1;
      return 1;
    });
    let municipalityArray = new Set<any>();
    let permitTypeArray = new Set<any>();
    let appTypeArray = new Set<any>();
    let county = new Set<any>();
    let projectId = new Set<any>();
    let applicant = new Set<any>();
    let facilityName = new Set<any>();
    let rcvdDate = new Set<any>();
    let dueDate = new Set<any>();
    let dartStatus = new Set<any>();
    let pmArray = new Set<any>();
    let eaArray = new Set<any>();
    let gpArray = new Set<any>();
    const dataAlteredForSorting = data.map((item: any, i: number) => {
      item.facilityName = get(item, 'facility.facilityName', '');
      // item.locationDirections = get(item, 'facility.locationDirections', '');
      item.locationDirections = this.getFullAddress(item);
      //  obj.locationDirections=this.getFullAddress(obj);
      item.municipality = get(item, 'facility.municipality', '');
      item.county = get(item, 'facility.county', '');
      item.dueDate = get(item, 'dueDate','');
      item.dartStatus = get(item, 'dartStatus', '')
      item.eaInd = item.eaInd === 'E' ? 'Yes' : 'No';
      item.gpInd = item.gpInd === 1 ? 'Yes' : 'No';
      eaArray.add(item.eaInd ? item.eaInd : '');
      gpArray.add(item.gpInd ? item.gpInd : '');
      if (item.permitType) {
        let permits = item.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      if (item.appType) {
        appTypeArray.add(item.appType ? item.appType : '');
      }
      if (item.municipality) {
        municipalityArray.add(item.municipality ? item.municipality : '');
      }
      if (item.facility.county) {
        county.add(item.facility.county ? item.facility.county : '');
      }

      projectId.add(item.projectId ? item.projectId : '');
      if (item.applicant) applicant.add(item.applicant);
      facilityName.add(item.facilityName ? item.facilityName : '');
      rcvdDate.add(item.rcvdDate ? item.rcvdDate : '');
      dartStatus.add(item.dartStatus? item.dartStatus:'');
      dueDate.add(item.dueDate? item.dueDate: '');
      eaArray.add(item.eaInd ? item.eaInd : '');
      gpArray.add(item.gpInd ? item.gpInd : '');
      pmArray.add(item.analystName ? item.analystName : 'UNASSIGNED');
      this.allActiveHeaders.forEach((header: any) => {
        if (header.columnTitle === 'analystName') {
          item.analystName = item.analystName ? item.analystName : 'UNASSIGNED';
        }
        if (header.columnTitle === 'applicant') {
          item[header.columnTitle + 'linkToNavigate'] =
            environment.lrpUrl + item.edbPublicId;
        }
        if (header.columnTitle === 'facilityName') {
          item[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl + item.facility?.districtId;
        }
        if (header.columnTitle === 'projectId') {
          item[header.columnTitle + 'linkToNavigate'] =
            '/virtual-workspace/' + item?.projectId;
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
        case 'analystName':
          header.filtersList = Array.from(pmArray)
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
    console.log(this.allActiveHeaders, 'activehead');
    return [...dataAlteredForSorting];
  }
  renderUnValidated(data: any) {
    let municipalityArray = new Set<any>();
    let permitTypeArray = new Set<any>();
    let appTypeArray = new Set<any>();
    let county = new Set<any>();
    let projectId = new Set<any>();
    let applicant = new Set<any>();
    let facilityName = new Set<any>();
    let locationDirections = new Set<any>();
    let rcvdDate = new Set<any>();
    let pmArray = new Set<any>();
    let status = new Set<any>();
    let programStaff = new Set<any>();
    let eaArray = new Set<any>();
    let gpArray = new Set<any>();
    data?.sort((a: any, b: any) => {
      if (a.projectId && b.projectId) {
        if (a.projectId > b.projectId) return -1;
      }

      return 1;
    });
    data.forEach((obj: any) => {
      obj.facilityName = obj?.facility?.facilityName;
      obj.locationDirections = this.getFullAddress(obj);
      //obj.locationDirections = obj?.facility?.locationDirections;
      obj.municipality = obj?.facility?.municipality;
      obj.county = obj?.facility?.county;
      obj.eaInd = obj.eaInd === 'E' ? 'Yes' : 'No';
      obj.gpInd = obj.gpInd === 1 ? 'Yes' : 'No';
      if (obj.permitType) {
        let permits = obj.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      if (obj.appType) {
        appTypeArray.add(obj.appType ? obj.appType : '');
      }
      if (obj.municipality) {
        municipalityArray.add(obj.municipality ? obj.municipality : '');
      }
      county.add(obj.county ? obj.county : '');
      projectId.add(obj.projectId ? obj.projectId : '');
      if (obj.applicant) applicant.add(obj.applicant);
      facilityName.add(obj.facilityName ? obj.facilityName : '');
      status.add(obj.dartStatus ? obj.dartStatus : '');
      programStaff.add(obj.programStaff ? obj.programStaff : '');
      locationDirections.add(
        obj.locationDirections ? obj.locationDirections : ''
      );
      rcvdDate.add(obj.rcvdDate ? obj.rcvdDate : '');
      eaArray.add(obj.eaInd ? obj.eaInd : '');
      gpArray.add(obj.gpInd ? obj.gpInd : '');
      pmArray.add(obj.analystName ? obj.analystName : 'UNASSIGNED');
      this.unValidatedHeaders.forEach((header: any) => {
        if (header.columnTitle === 'applicant') {
          obj[header.columnTitle + 'linkToNavigate'] =
            environment.lrpUrl + obj.edbPublicId;
        }
        if (header.columnTitle === '' && header.isButton) {
          obj.isButton = obj.analystName ? 'VALIDATE' : 'ASSIGN';
        }
        if (header.columnTitle === 'analystName') {
          obj.analystName = obj.analystName ? obj.analystName : 'UNASSIGNED';
        }
        if (header.columnTitle === 'facilityName') {
          obj[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl +
            (obj.facility?.districtId ? obj.facility?.districtId : null);
        }
        if (header.columnTitle === 'projectId') {
          obj[header.columnTitle + 'linkToNavigate'] = '/virtual-workspace';
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

      // this.programReviewHeaders.forEach((header: any) => {
      //   if (header.columnTitle === 'applicant') {
      //     obj[header.columnTitle + 'linkToNavigate'] =
      //       environment.lrpUrl + obj.edbPublicId;
      //   }

      //   if (header.columnTitle === 'analystName') {
      //     obj.analystName = obj.analystName ? obj.analystName : 'UNASSIGNED';
      //   }
      //   if (header.columnTitle === 'facilityName') {
      //     obj[header.columnTitle + 'linkToNavigate'] =
      //       environment.facilityNameUrl +
      //       (obj.facility?.districtId ? obj.facility?.districtId : null);
      //   }
      //   if (header.columnTitle === 'projectId') {
      //     obj[header.columnTitle + 'linkToNavigate'] = '/virtual-workspace';
      //   }
      // });
    });
    this.unValidatedHeaders.forEach((header: any) => {
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
        case 'analystName':
          header.filtersList = Array.from(pmArray)
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

    return [...data];
  }

  renderReviewerData(data: any[]) {
    let permitTypeArray = new Set<any>();
    let county = new Set<any>();
    let pmArray = new Set<any>();
    let eaArray = new Set<any>();
    let gpArray = new Set<any>();
    let status = new Set<any>();
    let programStaff = new Set<any>();
    let projectId = new Set<any>();
    let dueDate = new Set<any>();
    let dateAssigned = new Set<any>();
    let decId = new Set<any>();
    let facilityName = new Set<any>();
    data?.sort((a: any, b: any) => {
      if (b.projectId < a.projectId) return -1;
      return 1;
    });
    data.forEach((obj: any) => {
      obj.facilityName = obj?.facility?.facilityName;
      obj.locationDirections = this.getFullAddress(obj);
      obj.decId = obj?.facility?.decId;
      obj.eaInd = obj.eaInd === 'E' ? 'Yes' : 'No';
      obj.gpInd = obj.gpInd === 1 ? 'Yes' : 'No';

      obj.municipality = obj?.facility?.municipality;
      obj.county = obj?.facility?.county;

      if (obj.permitType) {
        let permits = obj.permitType.split(',');
        permits.forEach((permit: string) => {
          permitTypeArray.add(permit.trim());
        });
      }
      county.add(obj.county ? obj.county : '');
      status.add(obj.dartStatus ? obj.dartStatus : '');
      programStaff.add(obj.programStaff ? obj.programStaff : '');
      projectId.add(obj.projectId ? obj.projectId : '');
      dueDate.add(obj.dueDate);
      dateAssigned.add(obj.dateAssigned ? obj.dateAssigned : '');
      eaArray.add(obj.eaInd ? obj.eaInd : '');
      gpArray.add(obj.gpInd ? obj.gpInd : '');
      if (decId) decId.add(obj.decId);
      facilityName.add(obj.facilityName ? obj.facilityName : '');

      console.log(obj.appType ? obj.appType : '', 'EAP is apptype');
      pmArray.add(obj.analystName ? obj.analystName : 'UNASSIGNED');
      //obj.dueDate = obj?.dueDate ? new Date(obj.dueDate) : '';
      this.programReviewHeaders.forEach((header: any) => {
        if (header.columnTitle === 'applicant') {
          obj[header.columnTitle + 'linkToNavigate'] =
            environment.lrpUrl + obj.edbPublicId;
        }

        if (header.columnTitle === 'analystName') {
          obj.analystName = obj.analystName ? obj.analystName : 'UNASSIGNED';
        }
        if (header.columnTitle === 'facilityName') {
          obj[header.columnTitle + 'linkToNavigate'] =
            environment.facilityNameUrl +
            (obj.facility?.districtId ? obj.facility?.districtId : null);
        }
        if (header.columnTitle === 'projectId') {
          obj[header.columnTitle + 'linkToNavigate'] =
            '/virtual-workspace/' + obj.projectId;
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

    this.programReviewHeaders.forEach((header: any) => {
      switch (header.columnTitle) {
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
        case 'dartStatus':
          header.filtersList = Array.from(status)
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
        case 'analystName':
          header.filtersList = Array.from(pmArray)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
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
        default:
          header.filtersList = [];
          break;
      }
    });
    return [...data];
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
  getCurrentUserRole() {
    this.utils.emitLoadingEmitter(true);
    this.subscription.add(
      this.authService.emitAuthInfo.pipe().subscribe((authInfo: any) => {
        this.utils.emitLoadingEmitter(false);
        if (authInfo && !authInfo.isError) {
          //this.userRoles = ['DEC_Program_Staff'];
          this.userRoles = authInfo.roles;
        } else if (authInfo && authInfo.isError) {
          this.serverErrorMessage = this.errorService.getServerMessage(
            authInfo.error
          );
          this.showServerError = true;
          return;
        } else {
          return;
        }

        if (
          (authInfo &&
            authInfo.roles &&
            this.userRoles.includes(UserRole.Analyst)) ||
          this.userRoles.includes(UserRole.System_Analyst) ||
          this.userRoles.includes(UserRole.Override_Analyst) ||
          this.userRoles.includes(UserRole.System_Admin) ||
          this.userRoles.includes(UserRole.Override_Admin)
        ) {
          this.isEligibleToDisplay = true;
          this.activeTab = 'unvalidated';
          this.searchTitle ="Search in Unvalidated";
          this.region = ' ';
          
          if (!this.regionList?.length) this.getRegions();
          if (!this.projectManagers?.length) this.getAnalystsList();
        } else {
          this.isEligibleToDisplay = false;
          this.activeTab = 'program-review';
          this.searchTitle = 'Search in Program Review';
          if (!this.regionList?.length) this.getRegions();
          // if (!this.reviewerData?.length) this.getProgramReveiwerData();
        }
      })
    );
  }

  submitComment() {
    this.submitted = true;
    if(this.currentSelectedAnalystObj?.userId) {
      this.notesFormGroup.controls.managerId.setValue(this.currentSelectedAnalystObj.userId);
      this.notesFormGroup.controls.managerId.updateValueAndValidity();
    }
    const formData = this.notesFormGroup.value;
    const pmName =
      this.projectManagers.find(
      (x: any) =>(x.userId == this.notesFormGroup.value?.managerId)
      );
    console.log(this.notesFormGroup.value);
    if (this.notesFormGroup.valid) {
      this.currentSelectedAnalystObj = {};
      const payload = {
        analystId: pmName.userId,
        analystName: pmName.managerName,
        comments: formData.comments,
        analystRoleId: pmName.analystRoleId
      };
      if(this.currentRowIsInquiry) {
        this.dashboardService
          .assignInquiry(this.currentInquiryId, payload)
          .subscribe(
            (response) => {
              this.modalReference.close();
              this.currentRow.analystName = pmName.managerName;
              this.currentRow.analystAssignedId = pmName.userId;
              this.currentRow.isButton = '';
              console.log(this.currentRow);
            }
          );
        return;
      }
      this.dashboardService
        .assignProject(this.currentProjectId, payload)
        .subscribe(
          (response) => {
            this.modalReference.close();
            this.getUnValidatedData();
            this.currentRow.analystName = pmName.managerName;
            this.currentRow.isButton = 'VALIDATE';
            console.log(this.currentRow);
          },
          (error: any) => {
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          }
        );
    }
  }
  renderPermitScreening(data: any) {
    let giArray = new Set<any>();
    let requestor = new Set<any>();
    let address = new Set<any>();
    let municipality = new Set<any>();
    let recvdDate = new Set<any>();
    let projectManager = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      item.analystName=item.assignedAnalystName? item.assignedAnalystName :'UNASSIGNED';
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
       }
      item.address = this.getGIAddress(item);

      giArray.add(item.inquiryId ? item.inquiryId : '');
      requestor.add(item.requestorName ? item.requestorName : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');
      projectManager.add(item.analystName);

      this.permitScreeningHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId' && item.assignedAnalystName) {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
            + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
        if (header.columnTitle === '' && header.isButton ) {
          item.isButton = item.assignedAnalystName ?  '':'ASSIGN';
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
          case 'analystName':
            header.filtersList = Array.from(projectManager)
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

  renderEnergyProject(data: any) {

    let giArray = new Set<any>();
    let projectName = new Set<any>();
    let developer = new Set<any>();
    let owner = new Set<any>();
    let pscDocket = new Set<any>();
    let address = new Set<any>();
    let municipality = new Set<any>();
    let recvdDate = new Set<any>();
    let projectManager = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      // map the data
      item.analystName=item.assignedAnalystName? item.assignedAnalystName :'UNASSIGNED';
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
       }
      item.address = this.getGIAddress(item);
      

      giArray.add(item.inquiryId ? item.inquiryId : '');
      projectName.add(item.projectName ? item.projectName : '');
      developer.add(item.developer ? item.developer : '');
      owner.add(item.owner ? item.owner : '');
      pscDocket.add(item.pscDocketNum ? item.pscDocketNum : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');
      projectManager.add(item.analystName);

      this.energyProjectHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId' && item.assignedAnalystName) {
          
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
        if (header.columnTitle === '' && header.isButton ) {
          item.isButton = item.assignedAnalystName ?  '':'ASSIGN';
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
          case 'analystName':
            header.filtersList = Array.from(projectManager)
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
    let projectManager = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      item.analystName=item.assignedAnalystName? item.assignedAnalystName :'UNASSIGNED';
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
       }
      item.address = this.getGIAddress(item);
      giArray.add(item.inquiryId ? item.inquiryId : '');
      extender.add(item.extenderName ? item.extenderName : '');
      dowContact.add(item.dowContact ? item.dowContact : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');
      projectManager.add(item.analystName);

      this.sanitorySewageHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId' && item.assignedAnalystName) {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
        
        if (header.columnTitle === '' && header.isButton ) {
          item.isButton = item.assignedAnalystName ?  '':'ASSIGN';
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
          case 'analystName':
            header.filtersList = Array.from(projectManager)
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
    let projectManager = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      // map the data
      item.analystName=item.assignedAnalystName? item.assignedAnalystName :'UNASSIGNED';
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
       }
      item.address = this.getGIAddress(item);

      giArray.add(item.inquiryId ? item.inquiryId : '');
      planName.add(item.planName ? item.planName : '');
      requestor.add(item.requestorName ? item.requestorName : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');
      projectManager.add(item.analystName);

      this.mgmtCompHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId' && item.assignedAnalystName) {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
        if (header.columnTitle === '' && header.isButton ) {
          item.isButton = item.assignedAnalystName ?  '':'ASSIGN';
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
          case 'analystName':
            header.filtersList = Array.from(projectManager)
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
    let projectManager = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      // map the datalet 
     item.analystName=item.assignedAnalystName? item.assignedAnalystName :'UNASSIGNED';
     if(item.inquiryId) {
      item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
     }
      
      this.bblDeterminationHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId' && item.assignedAnalystName) {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' + 
            this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
        if (header.columnTitle === '' && header.isButton ) {
          item.isButton = item.assignedAnalystName ?  '':'ASSIGN';
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
      projectManager.add(item.analystName);

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
          case 'analystName':
            header.filtersList = Array.from(projectManager)
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
    let projectManager = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      // map the data   
      item.analystName=item.assignedAnalystName? item.assignedAnalystName :'UNASSIGNED';
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
       }

      this.preAppMeetingRequestHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId' && item.assignedAnalystName) {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
        if (header.columnTitle === '' && header.isButton ) {
          item.isButton = item.assignedAnalystName ?  '':'ASSIGN';
        }
      })

      item.address = this.getGIAddress(item);
      giArray.add(item.inquiryId ? item.inquiryId : '');
      projectName.add(item.projectName ? item.projectName : '');
      projectSponsor.add(item.projectSponsor ? item.projectSponsor : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');
      projectManager.add(item.analystName);

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
          case 'analystName':
            header.filtersList = Array.from(projectManager)
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
    let projectManager = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });
    
    data.forEach((item: any) => {
      // map the data
      item.analystName=item.assignedAnalystName? item.assignedAnalystName :'UNASSIGNED';
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
       }
      this.leadAgencyRequestHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId' && item.assignedAnalystName) {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
        if (header.columnTitle === '' && header.isButton ) {
          item.isButton = item.assignedAnalystName ?  '':'ASSIGN';
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
      projectManager.add(item.analystName);

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
          case 'analystName':
            header.filtersList = Array.from(projectManager)
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
    let projectManager = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      // map the data
      item.analystName=item.assignedAnalystName? item.assignedAnalystName :'UNASSIGNED';
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
       }

      this.serpCertificationHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId' && item.assignedAnalystName) {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' 
          + this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
        if (header.columnTitle === '' && header.isButton ) {
          item.isButton = item.assignedAnalystName ?  '':'ASSIGN';
        }
      })
      item.address = this.getGIAddress(item);

      giArray.add(item.inquiryId ? item.inquiryId : '');
      projectName.add(item.projectName ? item.projectName : '');
      efcContact.add(item.efcContact ? item.efcContact : '');
      address.add(item.address ? item.address : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');
      projectManager.add(item.analystName);

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
          case 'analystName':
            header.filtersList = Array.from(projectManager)
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

  renderAllActiveInquiries(data: any) {
    let giArray = new Set<any>();
    let inquiryType = new Set<any>();
    let requestor = new Set<any>();
    let nameArray = new Set<any>();
    let municipality = new Set<any>();
    let projectManager = new Set<any>();
    let recvdDate = new Set<any>();
    let status = new Set<any>();
    let completeDate = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      // map the data
      item.analystName=item.assignedAnalystName? item.assignedAnalystName :'UNASSIGNED'
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
       }

      item.inquiryTypeDesc = this.getInquiryType(item);

      this.allActiveInquiriesHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId' && item.assignedAnalystName) {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' + 
            this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
        if (header.columnTitle === '' && header.isButton ) {
          item.isButton = item.assignedAnalystName ?  '':'ASSIGN';
        }
      })

      item.statusInd = item.completedDate ? 'Complete' : 'Pending';

      giArray.add(item.inquiryId ? item.inquiryId : '');
      inquiryType.add(item.inquiryTypeDesc ? item.inquiryTypeDesc : '');
      requestor.add(item.requestor ? item.requestor : '');
      nameArray.add(item.requestIdentifier ? item.requestIdentifier : '');
      municipality.add(item.municipality ? item.municipality : '');
      recvdDate.add(item.rcvdDate ? item.rcvdDate : '');
      status.add(item.statusInd);
      completeDate.add(item.completedDate ? item.completedDate : '');
      projectManager.add(item.analystName);


      this.allActiveInquiriesHeaders.forEach((header: any) => {
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
          case 'requestor':
            header.filtersList = Array.from(requestor)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'requestIdentifier':
            header.filtersList = Array.from(nameArray)
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
          case 'analystName':
            header.filtersList = Array.from(projectManager)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'statusInd':
              header.filtersList = Array.from(status)
                .sort((a: any, b: any) => {
                  if (a < b) return -1;
                  return 1;
                })
                .map((str: string) => {
                  return { label: str, value: str };
                });
              break;
          case 'completedDate':
              header.filtersList = Array.from(completeDate)
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
    this.allActiveInquiriesData = [...data];
  }

  renderProgramReviewAllActiveInquiries(data: any) {
    let giArray = new Set<any>();
    let inquiryType = new Set<any>();
    let requestor = new Set<any>();
    let reqIdentifierArray = new Set<any>();
    let municipalityCounty = new Set<any>();
    let projectManager = new Set<any>();
    let completeDate = new Set<any>();
    let revDue = new Set<any>();
    let assignedDate = new Set<any>();
    let programStaff = new Set<any>();

    data?.sort((a: any, b: any) => {
      if (b.inquiryId < a.inquiryId) return -1;
      return 1;
    });

    data.forEach((item: any) => {
      // map the data
      item.analystName=item.assignedAnalystName? item.assignedAnalystName :'UNASSIGNED';
      let municicounty = '';
      if(item.municipality) {
        municicounty += item.municipality;
        if(item.county) {
          municicounty += "/";
        }
      }
      if(item.county) {
        municicounty += item.county;
      }
      if(item.inquiryId) {
        item.inquiryId = this.inquiryService.formatInquiryId(item.inquiryId);
       }

      item.municicounty = municicounty;

      item.inquiryTypeDesc = this.getInquiryType(item);

      this.allActiveInquiriesHeaders.forEach((header: any) => {
        if (header.columnTitle === 'inquiryId' && item.assignedAnalystName) {
          item[header.columnTitle + 'linkToNavigate'] = '/gi-virtual-workspace/' + 
            this.inquiryService.decodeInquiryId(item?.inquiryId);
        }
      })

      giArray.add(item.inquiryId ? item.inquiryId : '');
      inquiryType.add(item.inquiryTypeDesc ? item.inquiryTypeDesc : '');
      requestor.add(item.requestor ? item.requestor : '');
      reqIdentifierArray.add(item.requestIdentifier ? item.requestIdentifier : '');
      completeDate.add(item.completedDate ? item.completedDate : '');
      revDue.add(item.dueDate ? item.dueDate : '');
      assignedDate.add(item.dateAssigned ? item.dateAssigned : '');
      programStaff.add(item.programStaff ? item.programStaff : '');
      municipalityCounty.add(item.municicounty ? item.municicounty : '');
      projectManager.add(item.analystName);

      this.GIProgramReviewHeaders.forEach((header: any) => {
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
          case 'requestor':
            header.filtersList = Array.from(requestor)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'requestIdentifier':
            header.filtersList = Array.from(reqIdentifierArray)
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
          case 'dueDate':
              header.filtersList = Array.from(revDue)
                .sort((a: any, b: any) => {
                  if (a < b) return -1;
                  return 1;
                })
                .map((str: string) => {
                  return { label: str, value: str };
                });
            break;
          case 'analystName':
            header.filtersList = Array.from(projectManager)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'completedDate':
              header.filtersList = Array.from(completeDate)
                .sort((a: any, b: any) => {
                  if (a < b) return -1;
                  return 1;
                })
                .map((str: string) => {
                  return { label: str, value: str };
                });
              break;
          case 'dateAssigned':
                header.filtersList = Array.from(assignedDate)
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
          default:
            header.filtersList = [];
            break;
        }
      })
    });
    this.GIReviewerData = [...data];
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
  getInquiryType(inquiry: any) {
    let res = ''
    switch(inquiry.reason) {
      case 'BOROUGH_DETERMINATION':
        let bbl = '';
        bbl += inquiry.borough ? inquiry.borough + '/' : '';
        bbl += inquiry.block ? inquiry.block + (inquiry.lot ? '/' : '') : '';
        bbl += inquiry.lot ? inquiry.lot : '';
        inquiry.requestIdentifier = bbl;
        inquiry.requestor = inquiry.requestorName ? inquiry.requestorName : '';
        res = 'Borough/Block/Lot';
        break;
      case 'ENERGY_PROJ':
        inquiry.requestor = inquiry.developer ? inquiry.developer : '';
        inquiry.requestIdentifier = inquiry.projectName ? inquiry.projectName : '';
        res = 'Energy Projects';
        break;
      case 'MGMT_COMPRE_PLAN':
        inquiry.requestor = inquiry.requestorName ? inquiry.requestorName : '';
        inquiry.requestIdentifier = inquiry.planName ? inquiry.planName : '';
        res = 'Mgmt/Comp Plan';
        break;
      case 'JURISDICTION_DETERMINATION':
        inquiry.requestor = inquiry.requestorName ? inquiry.requestorName : '';
        inquiry.requestIdentifier = inquiry.projectName ? inquiry.projectName : '';
        res = 'Permit Screenings';
        break;
      case 'PRE_APPLN_REQ':
        inquiry.requestor = inquiry.requestorName ? inquiry.requestorName : '';
        inquiry.requestIdentifier = inquiry.projectName ? inquiry.projectName : '';
        res = 'Pre-App Mtg Req';
        break;
      case 'SANITARY_SEWER_EXT':
        inquiry.requestor = inquiry.dowContact ? inquiry.dowContact : '';
        inquiry.requestIdentifier = inquiry.extenderName ? inquiry.extenderName : '';
        res = 'Sanitary Sewage Extension';
        break;
      case 'SEQR_LA_REQ':
        inquiry.requestor = inquiry.leadAgencyName ? inquiry.leadAgencyName : '';
        inquiry.requestIdentifier = inquiry.projectName ? inquiry.projectName : '';
        res = 'SEQR Lead Agency Req';
        break;
      case 'SERP_CERT':
        inquiry.requestor = inquiry.efcContact ? inquiry.efcContact : '';
        inquiry.requestIdentifier = inquiry.projectName ? inquiry.projectName : '';
        res = 'SERP Certification';
        break;
      default:
        break;
    }
    return res;
  }

  openAssignPopup(row: any) {
    if(row.projectId) {
      this.currentProjectId = row.projectId;
    }
    this.initiateForm();
    this.currentRow = row;
    console.log(row);
    
    if(row.inquiryId) {
      this.currentInquiryId = this.inquiryService.decodeInquiryId(row.inquiryId);
      this.currentRowIsInquiry= true;
    }
    else {
      this.currentRowIsInquiry=false;
    }
    
    if (row.analystName && row.analystName !== 'UNASSIGNED') {
      if(row.inquiryId) {
          this.assignDetails = {
            comments: null,
            analystName: row.analystName,
            userAssigned: row.analystAssignedId
          }
          this.setFormData(this.assignDetails);
          console.log(this.notesFormGroup);
      }
      else { 
        this.dashboardService
          .getAssignNotesDetails(this.currentProjectId)
          .subscribe(
            (response) => {
              this.assignDetails = response;
              this.setFormData(response);
            },
            (err) => {
              this.isEdit = false;
              this.assignDetails = {};
              this.serverErrorMessage = this.errorService.getServerMessage(err);
              this.showServerError = true;
              throw err;
            }
          );
      }
    } 
    
    else {
      this.isEdit = false;
    }
    this.modalReference = this.modalService.open(this.assignModal, {
      ariaLabelledBy: 'modal-basic-title',
      size: 'medium-big',
    });
    this.modalReference.result.then(
      (result: any) => {
        this.submitted = false;
        console.log(result);
      },
      (reason: any) => {
        this.submitted = false;
        console.log(reason);
      }
    );
  }

  setFormData(notesData: any) {
    this.isEdit = true;
    console.log('notesData',notesData);
    this.selectedManager = notesData.userAssigned;
    // this.notesFormGroup.get('comments')?.setValue(notesData.comments);
    this.onInputChange(notesData.comments);
    this.maxNote = 301;
    setTimeout(() => {
      this.maxNote = 300;
    });
    this.notesFormGroup.get('projectManager')?.setValue(notesData.analystName);
    this.notesFormGroup.get('managerId')?.setValue(notesData.userAssigned);
    this.notesFormGroup.controls.projectManager.updateValueAndValidity();
    this.notesFormGroup.controls.managerId.updateValueAndValidity();
  }

  onInputChange(event: string) {
    this.note = event;
    this.notesFormGroup.patchValue({ comments: event });
    this.notesFormGroup.updateValueAndValidity();
    console.log(this.notesFormGroup);
  }

  onAnalystSelect(ev: any) {
    this.currentSelectedAnalystObj = ev;
    this.notesFormGroup.get('projectManager')?.setValue(ev.managerName);
    this.notesFormGroup.get('managerId')?.setValue(ev.userId);
    this.notesFormGroup.controls.projectManager.updateValueAndValidity();
    this.notesFormGroup.controls.managerId.updateValueAndValidity();
  }

  initiateForm() {
    this.notesFormGroup = this.formBuilder.group({
      projectManager: ['', Validators.required],
      comments: [''],
      managerId: ['']
    });
  }
  closeModal() {
    this.modalReference.close();
  }
  search(){
    this.dashboardTableComponent?.search(this.searchItem)
  }
  ngOnDestroy() {
    this.commonService.showAlertNotification.next(false);
    this.subscription.unsubscribe();
    this.commonService.isFixedFooter.next(false);
  }
}
