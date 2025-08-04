import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { Router } from '@angular/router';
import { isEqual, remove, cloneDeep } from 'lodash';
import { Table } from 'primeng/table';
import { BehaviorSubject, fromEvent, Subject } from 'rxjs';
import { CommonService } from '../../services/commonService';
import { DashboardService } from '../../services/dashboard.service';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { takeUntil } from 'rxjs/operators';
import { FilterService, SortEvent } from 'primeng/api';
import { PTableHeader } from './table.model';
import FileSaver, { saveAs } from 'file-saver';
import { ErrorService } from '../../services/errorService';

@Component({
  selector: 'app-dashboard-table',
  templateUrl: './dashboard-table.component.html',
  styleUrls: ['./dashboard-table.component.scss'],
})
export class DashboardTableComponent implements OnInit {
  @Input() headers: PTableHeader[] = [];
  @Input() filename: string = '';
  @Input() totalRecords: number = 0;
  @Input() dataList: any[] = [];
  @Input() activeTab: string = '';
  @Input() isPagination: boolean = false;
  @Input() isRecords: boolean = false;
  @Input() isMaxHeight: boolean = false;
  @Input() scrollHeight: string = '45rem';
  @Input() isReadOnly: boolean = false;
  @Input() isArchive: boolean = false;
  @Input() disableRemoveAll: boolean = false;
  @Output() deleteClicked = new EventEmitter<string>();
  @Output() deleteDocClicked = new EventEmitter<any>();
  @Output() archiveDocClicked = new EventEmitter<any>();
  @Output() openModal = new EventEmitter<string>();
  @Output() openDocModal = new EventEmitter<any>();
  @Output() editClicked = new EventEmitter<string>();
  @Output() removePurgeArchiveDoc = new EventEmitter<any>();
  @Output() checkDocument = new EventEmitter<any>();
  @Output() checkAllDocuments = new EventEmitter<string>();
  @Output() deleteAllRows = new EventEmitter<boolean>();
  @Output() archiveAllRows = new EventEmitter<boolean>();
  @Output() removeAllRows = new EventEmitter<boolean>();
  @Output() emitFilteredRows = new BehaviorSubject<any[]>([]);
  @ViewChild('dataTable', { static: true }) dataTable!: Table;
  @Input() deleteCellWidth!: string;
  hiddenElem: any = 0;
  doctypeLabelFilteringList: any[] = [];
  docSubTypeFilteringList: any[] = [];
  filesCountFilteringList: any[] = [];
  trackedApplcationIdFilteringList: any[] = [];
  documentReleasableDescFilteringList: any[] = [];
  documentUploadDateTimeFilteringList: any[] = [];
  documentNameFilteringList: any[] = [];
  documentDescriptionFilteringList: any[] = [];
  UserRole = UserRole;
  userRoles: any = [];
  private unsubscriber: Subject<void> = new Subject<void>();
  showServerError = false;
  serverErrorMessage!: string;
  rejectReason: any = '';
  docDepRelDet: any = '';
  docAppId: any = '';
  docUploadTime: any = '';
  docFileCount: any = '';
  docDescription: any = '';
  docName: any = '';
  docSubTypeSelect: any = '';
  doctypeLabel: any = '';
  filterValues: string[] = [];
  currentTab: any = '';
  deleteProjectId: any = '';
  deleteIsClicked: Subject<boolean> = new Subject();
  deleteProject: Subject<boolean> = new Subject();
  deleteBodyText!: string;
  secondLineDeleteBodyText!: string;
  isValidate: boolean = false;
  initialData= [...this.dataList];
  keysToSearch:string[]=[];
  headerCheckbox: boolean = false;
  constructor(
    private router: Router,
    private commonService: CommonService,
    private dashboardSrv: DashboardService,
    private filterService: FilterService,
    private errorService: ErrorService
  ) {}

  editClickedFun(id: any) {
    this.editClicked.emit(id);
  }
  deleteClickedFun(e: any) {
    this.deleteClicked.emit(e);
  }
  filter(e: any) {}

  onFilter(ev: any) {
    this.emitFilteredRows.next(ev.filteredValue);
  }

  getFilterList(filterList: any[]) {
    // console.log('get filter list', filterList);
    
    if (filterList && filterList.length > 0) {
      return filterList.filter((obj: any) => obj.value && obj.label);
    }
    return [];
  }

  customSort(event: SortEvent) {
    console.log(event);
    event.data?.sort((data1, data2) => {
      const dateRegex: RegExp =
        /(0[1-9]|1[012])[- \/.](0[1-9]|[12][0-9]|3[01])[- \/.](19|20)\d\d/;

      let value1 = data1[event.field as string];
      let value2 = data2[event.field as string];
      let result = null;
      if (dateRegex.test(value1) && dateRegex.test(value2)) {
        value1 = new Date(value1);
        value2 = new Date(value2);
      }
      if (value1 == null && value2 != null) result = -1;
      else if (value1 != null && value2 == null) result = 1;
      else if (value1 == null && value2 == null) result = 0;
      else if (typeof value1 === 'string' && typeof value2 === 'string')
        result = value1.localeCompare(value2);
      else result = value1 < value2 ? -1 : value1 > value2 ? 1 : 0;

      return (event.order as number) * result;
    });
  }

  formattedDECId(decId: number) {
    let stringDec = decId ? decId.toString() : '';
    let formatted = '';
    if (stringDec) {
      for (var i = 0; i < stringDec.length; i++) {
        formatted =
          formatted +
          (i == 1 || i == 5
            ? '-' + stringDec[i]
            : i == 10
            ? '/' + stringDec[i]
            : stringDec[i]);
      }
    }
    return formatted;
  }

  isPastDate(date: string) {
    if (!date) return false;
    let currentDate = new Date();
    let fieldDate = new Date(date);
    if (currentDate > fieldDate) return true;
    return false;
  }
  resumeButtonClicked(item: any, button: HTMLButtonElement) {
    localStorage.setItem('projectId', item.projectId);
    localStorage.setItem(
      'edbPublicId',
      item.edbPublicId ? item.edbPublicId : ''
    );
    this.commonService.projectIdChanged.next(true);
    switch (button.innerText) {
      case 'Resume':
        this.commonService.activeMode.next('');
        localStorage.setItem('mode', '');
        localStorage.setItem('emergencyAuth', '');
        this.router.navigate(['/apply-for-permit-details']);
        break;
      case 'Rejected':
          this.commonService.activeMode.next('');
          localStorage.setItem('mode', '');
          localStorage.setItem('emergencyAuth', '');
          this.router.navigate(['/apply-for-permit-details']);
          break;
      case 'Validate':
        localStorage.setItem('mode', 'validate');
        this.commonService.activeMode.next('validate');
        localStorage.setItem('emergencyAuth', '');
        this.router.navigate(['/apply-for-permit-details']);
        break;
      case 'VALIDATE':
        localStorage.setItem('mode', 'validate');
        this.commonService.activeMode.next('validate');
        localStorage.setItem('emergencyAuth', '');
        this.router.navigate(['/apply-for-permit-details']);
        break;
      case 'ASSIGN':
        this.openModalFun(item);
        break;
    }
  }
  openModalFun(e: any) {
    this.openModal.emit(e);
  }

  compareInt(a: any, b: any) {
    const A = a.value;
    const B = b.value;

    let comparison = 0;
    if (A > B) {
      comparison = 1;
    } else if (A < B) {
      comparison = -1;
    }
    return comparison;
  }

  compareString(a: any, b: any) {
    const A = a.value.toUpperCase();
    const B = b.value.toUpperCase();

    let comparison = 0;
    if (A > B) {
      comparison = 1;
    } else if (A < B) {
      comparison = -1;
    }
    return comparison;
  }
  ngOnInit(): void {    
    this.initialData=this.dataList;
    console.log('initial data', this.initialData, 'data list', this.dataList);
    this.emitFilteredRows.next(this.initialData);
    
    if(this.filename == 'application-response-due'){
      console.log("this.headers", this.headers);
      console.log("this.dataList", this.dataList)
    }
    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
    this.filterService.register(
      'permitFilter',
      (value: string, filter: any[]) => {
        if (!filter || filter?.length === 0) return true;
        if (!value) return false;
        let isFoun = false;
        filter.forEach((selectedFilter: string) => {
          let i = value.indexOf(selectedFilter);
          if (i >= 0) isFoun = true;
        });
        return isFoun;
      }
    );
  }
  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  ngOnChanges(changes: any) {    
    this.initialData=this.dataList;
    this.keysToSearch = this.headers.map(x=>x.columnTitle).filter(y => y !== '');
    if (this.dataList.length) {
      setTimeout(() => {
        let elements = Array.from(
          document.getElementsByTagName('td') as HTMLCollection
        );
        elements.forEach((element: any) => {
          element.removeAttribute('aria-sort');
        });
      }, 0);
    }
  }

  rejectProject(){
    this.showServerError = false;
  }

  deleteProjects(e:any) {
    this.showServerError = false;
    console.log('Item delte current Tab',this.currentTab)
    console.log('Item delte current Tab',this.deleteProjectId)
    console.log("Item Delete", e)
    if(this.currentTab ==="Resume"|| this.currentTab ==="Rejected"){
    this.dashboardSrv.deleteApplications(this.deleteProjectId).subscribe(
      () => {
        remove(this.dataList, (item: any) =>
          isEqual(item.projectId, this.deleteProjectId)
        );
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }
  else if (this.currentTab ==="VALIDATE"){
        this.dashboardSrv.rejectApplications(this.deleteProjectId, e).subscribe(
            () => {
        remove(this.dataList, (item: any) =>
          isEqual(item.projectId, this.deleteProjectId)
        );
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );

  }
  }
  onDeleteApplication(item: any, header:PTableHeader) {
    if(header?.columnTitle === 'trashcanPurge') {
      this.deleteDocClicked.emit(item);
      return;
    }
    if(header?.columnTitle === 'archive') {
      this.archiveDocClicked.emit(item);
      return;
    }
   
      this.deleteBodyText = (header?.deleteBodyText? header.deleteBodyText : 'Are you sure you want to permanently delete project ')
      .concat(item.projectId)
      .concat('?');
    this.secondLineDeleteBodyText =
    (header?.secondLineDeleteBodyText? header.secondLineDeleteBodyText : 'All uploaded files will also be permanently deleted for this project.');
    this.deleteIsClicked.next(true);
    this.deleteProjectId = item.projectId;
    this.currentTab = item.isButton;
    if(item.isButton === "VALIDATE"){
      this.isValidate = true;
    }
    

   
    console.log('Item delte',item, "Header", header)
    
  }
  onDeleteAllRows() {
    this.deleteAllRows.emit(true);
  }
  onArchiveAllRows() {
    this.archiveAllRows.emit(true);
  }
  onRemoveAllClicked() {
    this.removeAllRows.emit(true);
  }
  resetFilters() {
    let blankArray: any = [];
    for (let i = 0; i < this.filterValues.length; i++) {
      blankArray.push('');
    }
    this.filterValues = [...blankArray];
    this.dataTable.reset();
  }
  setLocal(row: any, item: any) {
    let link: string = row[item.columnTitle + 'linkToNavigate'];
    if(link === '/documents' && this.activeTab === 'archive' && row.decId) {
      sessionStorage.setItem(
        'documentdecID',
        row.decId
      );      
      sessionStorage.setItem('documentProjectID', row.projectId);
      window.open(link, '_blank');
      return;
    }

    if (item.isSetLocal) {
      localStorage.setItem('projectId', row.projectId);
    }
    if (this.activeTab) {
      localStorage.setItem('vdActiveTab', this.activeTab);
    } else {
      localStorage.setItem('vdActiveTab', '');
    }
    if (item.isAssign) {
      this.openModalFun(row);
    } else {
      window.open(link, '_blank');
    }
  }
  openDocPopup(row: any) {
    this.openDocModal.emit(row);
  }
  exportToExcel() {
    let filteredVal = this.dataTable.filteredValue;
    let dataList: any[] = [];

    const headers = this.headers
      .filter((header) => header.exportToExcel)
      .map((item) => item.columnTitle);

    if (filteredVal) {
      dataList = filteredVal.map((data) => {
        let temp: any = {};
        headers.forEach((header) => {
          if(this.activeTab === 'archive' && (header === 'markForReview'
          || header === 'markedForReview')) {
            temp[this.isArchive ? "Marked for Archive" : "Marked for Delete"] = data[header];
          }
          else {
            temp[header] = data[header];
          }
        });
        return temp;
      });
    } else {
      dataList = this.dataList.map((data) => {
        let temp: any = {};
        headers.forEach((header) => {
          if(this.activeTab === 'archive' && (header === 'markForReview'
          || header === 'markedForReview')) {
            temp[this.isArchive ? "Marked for Archive" : "Marked for Delete"] = data[header];
          }
          else {
            temp[header] = data[header];
          }
        });
        return temp;
      });
    }
    import('xlsx').then((xlsx) => {
      const worksheet = xlsx.utils.json_to_sheet(dataList);
      const workbook = { Sheets: { data: worksheet }, SheetNames: ['data'] };
      const excelBuffer: any = xlsx.write(workbook, {
        bookType: 'xlsx',
        type: 'array',
      });
      this.saveAsExcelFile(excelBuffer, this.filename);
    });
  }

  saveAsExcelFile(buffer: any, fileName: string): void {
    let EXCEL_TYPE =
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
    let EXCEL_EXTENSION = '.xlsx';
    const data: Blob = new Blob([buffer], {
      type: EXCEL_TYPE,
    });
    FileSaver.saveAs(
      data,
      fileName + '_export_' + new Date().getTime() + EXCEL_EXTENSION
    );
  }
  search(input:any){
    if(input){
    this.dataList= this.initialData.reduce((result,obj)=>{
      for(const key of this.keysToSearch){
        if(Object.prototype.hasOwnProperty.call(obj,key)){
          const lowerCaseValue= String(obj[key]).toLowerCase();
          const lowercaseinput=input.toLowerCase();
          if(lowerCaseValue.includes(lowercaseinput)){
            result.push(obj);
            break;
          }
        }
      }
      return result;
    },[]);}
    else{
      this.dataList=[...this.initialData]
    }
  }
  removeButtonClicked(item: any) {
    this.removePurgeArchiveDoc.emit(item);
  }
  checkAllClicked(ev: any) {
    ev.target.checked ? this.checkAllDocuments.emit('Y') : 
      this.checkAllDocuments.emit('N');
  }

  onReviewCheckboxClicked() {
    this.checkDocument.emit(true);
  }
}
