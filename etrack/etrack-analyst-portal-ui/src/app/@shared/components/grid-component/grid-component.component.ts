import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter,
  ViewChild,
} from '@angular/core';
import moment from 'moment';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { Table } from 'primeng/table';
import { MultiSelect } from 'primeng/multiselect';
import { FilterMetadata, SelectItem } from 'primeng/api';
import { fromEvent, Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { filter, isEmpty, isEqual, keys } from 'lodash';
import FileSaver, { saveAs } from 'file-saver';
import { CommonService } from '../../services/commonService';

@Component({
  selector: 'app-grid-component',
  templateUrl: './grid-component.component.html',
  styleUrls: ['./grid-component.component.scss'],
})
export class GridComponentComponent implements OnInit {
  @Input() rowData: any[] = [];
  @Input() userRole!: string;
  @Input() componentType!: string;
  @Input() headers: any[] = [];
  @Input() filename: string = '';
  @Input() isFromDMS!: boolean;
  @Output() deleteClicked: any = new EventEmitter<string>();
  @Output() openModal: any = new EventEmitter<string>();
  @Output() editClicked: any = new EventEmitter<string>();
  @ViewChild('docTable', { static: true }) docTable!: Table;
  hiddenElem: any = 0;

  doctypeLabelFilteringList: any[] = [];
  docSubTypeFilteringList: any[] = [];
  filesCountFilteringList: any[] = [];
  trackedApplcationIdFilteringList: any[] = [];
  documentReleasableDescFilteringList: any[] = [];
  documentUploadDateTimeFilteringList: any[] = [];
  documentNameFilteringList: any[] = [];
  documentDescriptionFilteringList: any[] = [];
  projectIdFilteringList: any[] = [];

  docDepRelDet: any = '';
  docAppId: any = '';
  docUploadTime: any = '';
  docFileCount: any = '';
  docDescription: any = '';
  docName: any = '';
  docSubTypeSelect: any = '';
  doctypeLabel: any = '';
  projectId: any = '';
  UserRole: any = UserRole;
  private subs = new Subscription();
  private unsubscriber: Subject<void> = new Subject<void>();

  constructor(private commonService: CommonService) {}

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
    this.subs.unsubscribe();
  }

  setLocal(row: any,event:any) {
    event.preventDefault();
    localStorage.setItem('projectId', row.projectId);
    window.open('/virtual-workspace/' + row.projectId, '_blank');
  }

  editClickedFun(id: any) {
    this.editClicked.emit(id);
  }
  deleteClickedFun(e: any) {
    console.log('JJ', e);
    if(!(e.deleteDisabled || e.reviewedInd))
      this.deleteClicked.emit(e);
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

  get canShowProjectId() {
    return (
      this.componentType === 'documents' &&
      isEqual(this.userRole, UserRole.DEC_Program_Staff)
    );
  }

  filters!: { [key in keyof any]: FilterMetadata[] };
  ngOnInit(): void {
    this.rowData;
    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
    this.filters = {
      projectId: [{ value: null, matchMode: 'in' }],
    };

    const documentProjectID = sessionStorage.getItem('documentProjectID');
    if (documentProjectID != null) {
      let Projectids = [Number(documentProjectID)];
      this.filters = {
        projectId: [{ value: Projectids, matchMode: 'in' }],
      };
      sessionStorage.removeItem('documentProjectID');
    }
    this.subs.add(
      this.commonService.emitClearTableFilters.subscribe((val: boolean) => {
        if (val) {
          this.resetFilters();
          this.doctypeLabelFilteringList = [];
          this.docSubTypeFilteringList = [];
          this.documentNameFilteringList = [];
          this.documentDescriptionFilteringList = [];
          this.filesCountFilteringList = [];
          this.documentUploadDateTimeFilteringList = [];
          this.trackedApplcationIdFilteringList = [];
          this.documentReleasableDescFilteringList = [];
          this.projectIdFilteringList = [];
        }
      })
    );
  }
  ngOnChanges(changes: any) {
    this.hiddenElem = Math.random();
    if (this.rowData.length) {
      this.documentNameFilteringList = [];
      this.rowData.forEach((v, i) => {
        v.fileCounts = v.files?.length;
        let cleanDate = v.uploadDateTime.split(' ');
        cleanDate.length = 2;
        v.uploadDateTime = cleanDate.join(' ');
        v.docSubTypeLabel = v.otherDocSubCategory
          ? v.otherDocSubCategory
          : v.docSubTypeLabel;
        let dateTime = moment(new Date(v.uploadDateTime)).format('MM/DD/yyyy');
        v.uploadDate = dateTime;

        if (
          v.doctypeLabel &&
          this.doctypeLabelFilteringList.findIndex(
            (i) => i.label == v.doctypeLabel
          ) == -1
        )
          this.doctypeLabelFilteringList.push({
            label: v.doctypeLabel,
            value: v.doctypeLabel,
          });

        if (
          v.docSubTypeLabel &&
          this.docSubTypeFilteringList.findIndex(
            (i) => i.label == v.docSubTypeLabel
          ) == -1
        )
          this.docSubTypeFilteringList.push({
            label: v.docSubTypeLabel,
            value: v.docSubTypeLabel,
          });

        if (
          v.documentName &&
          this.documentNameFilteringList.findIndex(
            (i) => i.label == v.doucmentName
          ) == -1
        )
          this.documentNameFilteringList.push({
            label: v.documentName,
            value: v.documentName,
          });

        if (
          v.description &&
          this.documentDescriptionFilteringList.findIndex(
            (i) => i.label == v.description
          ) == -1
        )
          this.documentDescriptionFilteringList.push({
            label: v.description,
            value: v.description,
          });

        if (
          v.files &&
          v.files.length &&
          this.filesCountFilteringList.findIndex(
            (i) => i.label == v.fileCounts
          ) == -1
        )
          this.filesCountFilteringList.push({
            label: v.fileCounts,
            value: v.fileCounts,
          });

        if (
          v.uploadDateTime &&
          this.documentUploadDateTimeFilteringList.findIndex(
            (i) => i.label == dateTime
          ) == -1
        )
          this.documentUploadDateTimeFilteringList.push({
            label: dateTime,
            value: v.uploadDate,
          });

        if (
          v.trackedApplicationId &&
          this.trackedApplcationIdFilteringList.findIndex(
            (i) => i.label == v.trackedApplicationId
          ) == -1
        )
          this.trackedApplcationIdFilteringList.push({
            label: v.trackedApplicationId,
            value: v.trackedApplicationId,
          });

        if (
          v.documentReleasableDesc &&
          this.documentReleasableDescFilteringList.findIndex(
            (i) => i.label == v.documentReleasableDesc
          ) == -1
        )
          this.documentReleasableDescFilteringList.push({
            label: v.documentReleasableDesc,
            value: v.documentReleasableDesc,
          });

        if (
          v.projectId &&
          this.projectIdFilteringList.findIndex(
            (i) => i.label == v.projectId
          ) == -1
        )
          this.projectIdFilteringList.push({
            label: v.projectId,
            value: v.projectId,
          });
      });
    }
    this.doctypeLabelFilteringList.sort(this.compareString);
    this.docSubTypeFilteringList.sort(this.compareString);
    this.documentNameFilteringList.sort(this.compareString);
    this.documentDescriptionFilteringList.sort(this.compareString);
    this.filesCountFilteringList.sort(this.compareInt);
    this.documentUploadDateTimeFilteringList.sort(this.compareInt);
    this.trackedApplcationIdFilteringList.sort((a, b) => a.label - b.label);
    this.documentReleasableDescFilteringList.sort(this.compareString);
    this.projectIdFilteringList.sort(this.compareInt);
    console.log(this.documentDescriptionFilteringList, 'doc filterlist');
  }

  resetFilters() {
    this.docDepRelDet = '';
    this.docAppId = '';
    this.docUploadTime = '';
    this.docFileCount = '';
    this.docDescription = '';
    this.docName = '';
    this.docSubTypeSelect = '';
    this.doctypeLabel = '';
    this.docTable.reset();
  }

  exportToExcel() {
    console.log('row data', this.rowData);

    if (isEmpty(this.rowData)) {
      return;
    }
    let filteredVal = this.docTable.filteredValue;
    let dataList: any[] = [];
    if (this.isFromDMS && !filteredVal) {
      dataList = this.rowData.map((data) => {
        let temp: any = {};
        temp['Type'] = data.doctypeLabel;
        temp['Sub-Type'] = data.docSubTypeLabel;
        temp['Document Name'] = data.documentName;
        temp['Description'] = data.description;
        temp['# Files'] = data.fileCounts;
        temp['Upload Date | Time'] = data.uploadDateTime;
        temp['Project ID'] = data.projectId;
        temp['APP ID'] = data.trackedApplicationId;
        temp['DEP Rel Det'] = data.documentReleasableDesc;
        return temp;
      });
    } else if (this.isFromDMS && filteredVal) {
      dataList = filteredVal.map((data) => {
        let temp: any = {};
        temp['Type'] = data.doctypeLabel;
        temp['Sub-Type'] = data.docSubTypeLabel;
        temp['Document Name'] = data.documentName;
        temp['Description'] = data.description;
        temp['# Files'] = data.fileCounts;
        temp['Upload Date | Time'] = data.uploadDateTime;
        temp['Project ID'] = data.projectId;
        temp['APP ID'] = data.trackedApplicationId;
        temp['DEP Rel Det'] = data.documentReleasableDesc;
        return temp;
      });
    }
    if (!this.isFromDMS && !filteredVal) {
      const headers = this.headers
        .filter((header) => header.exportToExcel)
        .map((item) => item.columnTitle);
      dataList = this.rowData.map((data) => {
        let temp: any = {};
        headers.forEach((header) => {
          temp[header] = data[header];
        });
        return temp;
      });
    } else if (!this.isFromDMS && filteredVal) {
      const headers = this.headers
        .filter((header) => header.exportToExcel)
        .map((item) => item.columnTitle);
      dataList = filteredVal.map((data) => {
        let temp: any = {};
        headers.forEach((header) => {
          temp[header] = data[header];
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
}
