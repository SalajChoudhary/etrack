import {
  Component,
  Input,
  OnInit,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  UntypedFormArray,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { CommonService } from 'src/app/@shared/services/commonService';
import { SearchToolService } from 'src/app/@shared/services/search-tool-service.service';
import { SearchRowQueryComponent } from 'src/app/@shared/components/search-row-query/search-row-query.component';
import { SearchFields } from 'src/app/@shared/constants/SearchFields';
import { SearchOperators } from 'src/app/@shared/constants/SearchOperators';
import { DocumentResultHeaders, ProjectResultHeaders } from './ResultHeaders';
import { AuthService } from 'src/app/core/auth/auth.service';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { BehaviorSubject, Subject } from 'rxjs';
import { SuccessPopupComponent } from 'src/app/@shared/components/success-popup/success-popup.component';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { environment } from 'src/environments/environment';
import { Utils } from 'src/app/@shared/services/utils';
import moment from 'moment';
import { PTableHeader } from 'src/app/@shared/components/dashboard-table/table.model';
import FileSaver from 'file-saver';
import { WindowRef } from 'src/app/@shared/services/windowRef';
import { RequiredDocsService } from 'src/app/@shared/services/required-docs.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss'],
})
export class SearchComponent implements OnInit {
  existingSearches: any[] = [];
  documentResults: any[] = [];
  projectResults: any[] = [];
  applicationSearchQueriesList: any[] = [];
  facilitySearchQueryList: any[] = [];
  publicsSearchQueryList: any[] = [];
  projectAttributesSearchQueryList: any[] = [];
  documentsSearchQueryList: any[] = [];
  keywordsSearchQueryList: any[] = [];
  isCreatingQuery: boolean = false;
  viewedQueryName!: string;
  isViewingQuery = false;
  userRoles: any = [];
  UserRole = UserRole;
  searchForm!: FormGroup;
  applicationFieldList: any[] = [];
  applicationOperators: any[] = [];
  genericOperators: any[] = [];
  facilityFieldList: any[] = [];
  publicsFieldList: any[] = [];
  projectAttrFieldList: any[] = [];
  activeHeaders: any[] = [];
  activeData: any[] = [];
  documentFieldList: any[] = [];
  keywordsFieldList: any[] = [];
  fieldList!: any[];
  showOr = false;
  viewedQuerySql!: string;
  @ViewChildren(SearchRowQueryComponent)
  queryRowsList!: SearchRowQueryComponent[];
  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;
  @ViewChild('successPopup', { static: true })
  successPopup!: SuccessPopupComponent;
  @ViewChild('confirmApplicant')
  confirmApplicant!: CustomModalPopupComponent;
  configObject: any = {};
  applicantTypeList: any = [];
  comments!: string;
  searchFields = SearchFields;
  searchOperators = SearchOperators;
  entityOperators: any[] = ['and', 'or'];
  queryForm!: FormGroup;
  queryData: any[] = [];
  trackingArray: any[] = [];
  isSubmitted = false;
  isFrom!: string;
  docResultGridHeaders = DocumentResultHeaders;
  projResultGridHeaders = ProjectResultHeaders;
  isSubmittedSuccessfully = false;
  deleteIsClicked: Subject<boolean> = new Subject();
  deleteQuery: Subject<boolean> = new Subject();
  deletePopupBodyText: string = '';
  successPopupMessage: string = 'Saved successfully';
  showDuplicateQueryNameError: boolean = false;
  uniqueApplications: any[] = [];
  serverErrorMessage: any;
  showServerError: boolean = false;
  activeSearchData: any[] = [];
  ownerId: any;
  runQuery: boolean = false;
  activeRunQueryItem: any;
  queryResults: any[] = [];
  queryId: any;
  showRun: boolean = false;
  @Input() headers: PTableHeader[] = [];
  tableHeaders:any;
  @Input() filename: string = '';
  fullData: any = [];
  viewButtonIsClicked: Subject<string> = new Subject();
  projectId: any;
  showResultTableServerError: boolean = false;
  filterValues: string[] = [];
  dataTable: any = [];
  lastLoadDate: any = '';
  createDeleteRow: boolean = false;
  confirmConfig: { title: string; showHeader: boolean };
  isOpenConfirmPopUp = new BehaviorSubject<boolean>(false);
  saveAsClicked: boolean = false;
  errorMsgObj: any = '';

  constructor(
    private router: Router,
    private commonService: CommonService,
    public utils: Utils,
    private winRef: WindowRef,
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private searchToolService: SearchToolService,
    private requiredDocsService: RequiredDocsService,
    private errorService: ErrorService
  ) {
    this.confirmConfig = {
      title: '',
      showHeader: false,
    };
  }

  ngOnInit(): void {
    this.getCurrentUserRole();
    this.getAttributeTypes();
    this.getAvailableSearches();
    this.getAllErrorMsgs();
    setTimeout(() => {
      // this.initArrays();
    }, 200);
    this.existingSearches = [];

    this.initForm();
    this.ownerId = (localStorage.getItem('loggedUserName') || '')
      .replace('SVC', '')
      .substring(1);
    this.formControls.pending.valueChanges.subscribe((val: any) => {
      if (val == false) {
        this.formControls.pending.setValue(null);
      }
    });
    this.formControls.historic.valueChanges.subscribe((val: any) => {
      if (val == false) {
        this.formControls.historic.setValue(null);
      }
    });

    //this.documentResults =this.renderDocumentResultsData(this.documentResults);
    //this. projectResults  = this.renderProjectResultsData(this.projectResults);

    this.deleteQuery.subscribe((val: any) => {
      this.deleteQueryFromGrid();
    });
  }

  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMsgObj = this.commonService.getErrorMsgsObj();
    });
  }

  initForm() {
    this.searchForm = this.formBuilder.group({
      resultDetails: ['P', Validators.required],
      pending: [true],
      historic: [true],
      queryId: [null],
      queryOwner: [null],
      persistenceDataType: ['1'],
      queryName: ['', [Validators.required, this.utils.documentNameValidator]],
      originalQueryName: [''],
      comments: ['', Validators.required],
      queries: this.formBuilder.array([]),
      searchQuery: this.formBuilder.array([]),
      isView: [false],
    });
    this.createDeleteRow = false;
  }

  disableSave(): boolean {
    let fval: any = this.searchForm.value;
    if (
      this.userRoles.includes('System Admin') &&
      fval.queryOwner == 'GLOBAL'
    ) {
      return false;
    }
    // if (fval.queryId && fval.queryOwner != this.ownerId) {
    //   return true;
    // }
    return false;
  }

  isView() {
    return this.searchForm.getRawValue()?.isView;
  }

  getAvailableSearches() {
    this.utils.emitLoadingEmitter(true);
    this.searchToolService.getAvailableSearches().subscribe(
      (res: any) => {
        this.utils.emitLoadingEmitter(false);
        this.existingSearches =
          res.map((e: any) => {
            return {
              ...e,
              modifiedDate: moment(e.modifiedDate, 'YYYY-MM-DD').format(
                'MM/DD/YYYY'
              ),
            };
          }) || [];
        if (!this.userRoles.includes('System Admin')) {
          this.existingSearches = this.existingSearches.filter(
            (vt: any) =>
              vt.queryOwner == 'GLOBAL' || vt.queryOwner === this.ownerId
          );
        }
      },
      (error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  getActiveSearchDetails(id: any, view: boolean) {
    this.showServerError = false;
    this.serverErrorMessage = '';
    console.log('View', view, this.createDeleteRow);
    this.searchToolService.getParticularSearch(id).subscribe(
      (res: any) => {
        this.frameSearchObj(res);
        if (view == true) {
          this.searchForm.disable();
          this.searchForm.controls['queryName'].enable();
          this.createDeleteRow = view;
        }
        this.searchForm.controls['isView'].setValue(view);
        this.searchForm.controls['isView'].enable();
      },
      (error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(error);

        this.showServerError = true;

        throw error;
      }
    );
  }

  okClicked() {
    this.isOpenConfirmPopUp.next(false);
    this.confirmApplicant.close();
  }

  /**
   * Get RUN Query by Query ID
   */
  selectedRun(item: any) {
    this.activeData = [];
    this.serverErrorMessage = '';
    this.utils.emitLoadingEmitter(true);
    this.runQuery = true;
    this.activeRunQueryItem = item;
    this.activeHeaders =
      item.resultDetails == 'P' ? ProjectResultHeaders : DocumentResultHeaders;

    this.searchToolService.getRunQuery(item.queryId).subscribe(
      (res: any) => {
        if (res) {
          this.filename = item.queryName;
          //TODO: Once confirmed remove custom name for file download
          //item.resultDetails == 'P' ? 'Project_Result' : 'Document_Results';
          this.queryResults = [res] || [];
          this.activeData = this.queryResults[0].map((e: any) => {
            return { ...e, rcvdDate: moment(e.rcvdDate).format('MM/DD/YYYY') };
          });
          this.utils.emitLoadingEmitter(false);
        } else {
          this.utils.emitLoadingEmitter(false);
          this.confirmApplicant.open('sm');
          this.isOpenConfirmPopUp.next(true);
        }
      },
      (error: any) => {
        this.utils.emitLoadingEmitter(false);
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showResultTableServerError = true;
        throw error;
      }
    );
  }

  /**
   * Get Search Data Condition
   */
  frameSearchObj(data: any) {
    for (let index = 0; index < this.uniqueApplications.length; index++) {
      this.searchQueryControls(index).clear();
    }
    this.activeSearchData = data || {};
    this.searchForm.patchValue({
      ...data,
      originalQueryName: data.queryName,
      pending: data.documentSearchType
        ? data.documentSearchType.includes('P')
        : false,
      historic: data.documentSearchType
        ? data.documentSearchType.includes('H')
        : false,
      projects: data.resultDetails.includes('P'),
      documents: data.resultDetails.includes('D'),
    });
    let filteredRecords: any[] = [];
    this.uniqueApplications.forEach((e: any) => {
      console.log('frame', e);
      let applicationRecords: any[] = data.searchQueryConditions
        .filter((f: any) => f.searchEntityCode == e.searchEntityCode)
        .sort(
          (a: any, b: any) => a.searchAttributeOrder - b.searchAttributeOrder
        );
      if (applicationRecords.length > 0)
        filteredRecords.push(applicationRecords);
    });
    filteredRecords.forEach((sortedApps: any[]) => {
      let count = 0;
      sortedApps.forEach((e: any) => {
        let index = this.uniqueApplications.findIndex(
          (f: any) => f.searchEntityCode == e.searchEntityCode
        );
        if (index != -1) {
          count  = count + 1;
          let atIndex = this.uniqueApplications[index].children.findIndex(
            (f: any) => f.searchAttributeId == e.searchAttributeId
          );
          let dataType =
            this.uniqueApplications[index].children[atIndex].attributeDataType;
          let value: any[] = [];
          if (dataType == 2) {
            console.log("Comp value", e.comparisonValue)
            e.comparisonValue.split(',').forEach((e: any) => {
              let i = this.uniqueApplications[index].children[
                atIndex
              ].attributes.findIndex((f: any) => f.cd == e);
              if (i != -1) {
                value.push(
                  this.uniqueApplications[index].children[atIndex].attributes[i]
                    .cd
                );
              }
            });
          }

          let bt1, bt2;
          if (dataType == 3 && e.comparisonOperator == 'BETWEEN') {
            bt1 = e.comparisonValue.split(',')[0];
            bt2 = e.comparisonValue.split(',')[1];
          } else if (dataType == 3) {
          }
          let control = this.formBuilder.group({
            section: ['APPL'],
            inputField: [
              dataType == 1 || dataType == 3 || dataType == 5 || dataType == 6
                ? dataType == 3
                  ? this.formatDate(e.comparisonValue)
                  : e.comparisonValue
                : '',
            ],
            andOr: [e.conditionOperator],
            field: [
              this.uniqueApplications[index].children[atIndex]
                .searchAttributeName,
              Validators.required,
            ],
            operator: [e.comparisonOperator, Validators.required],
            betweenInput1: [bt1 ? this.formatDate(bt1) : ''],
            betweenInput2: [bt2 ? this.formatDate(bt2) : ''],
            multicheckboxes: [
              dataType == 2
                ? dataType == 2
                  ? e.comparisonValue == '1'
                    ? '1'
                    : '0'
                  : e.comparisonValue
                : '',
            ],
            checkbox: [
              dataType == 4
                ? dataType == 4
                  ? e.comparisonValue == '1'
                    ? '1'
                    : '0'
                  : e.comparisonValue
                : '',
            ],
            multipleCheckboxes: [dataType == 2 ? value : ''],
            searchQueryConditionId: [e.searchQueryConditionId],
            maskedInput: [
              dataType == 5 ? this.formatDecId(e.comparisonValue) : '',
            ],
            dataType: [dataType],
          });
          this.searchQueryControls(index).push(control);
          if (e.conditionOperator && count == 1) {
            this.uniqueApplications[index].selectedAndOr = e.conditionOperator;
          }
        }
      });
    });
  }

  /**
   * Format Date Functionality
   */

  formatDate(dt: any) {
    if (dt) {
      return moment(dt, 'MM-DD-YYYY').format('YYYY-MM-DD');
    }
    return dt;
  }

  /**
   * Navigate to Add New or Edit screen
   */
  navigate(item: any, header: any, fieldType: any) {
    if (fieldType == 'projectId') {
      window.open('/virtual-workspace/' + item.projectId, '_blank');
    } else if (fieldType == 'decId') {
      window.open(environment.facilityNameUrl + '' + item.districtId, '_blank');
    } else if (fieldType == 'documentNm') {
      this.projectId = item.projectId;
      this.showResultTableServerError = false;
      this.searchToolService
        .getSupportDocumentFiles(item.documentId, this.projectId)
        .then(
          (files: any[]) => {
            this.fullData = files;
            let docName = item.documentTitleId;
            this.viewButtonIsClicked.next(docName);
          },
          (error: any) => {
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showResultTableServerError = true;
            throw error;
          }
        );
    }
  }

  viewDocumentClick() {}

  /**
   * Format DEC ID Functionality
   */
  formatDecId(val: string) {
    val = val.replace(/\D/g, '');
    const length = val.length;
    if (length > 0) {
      if (length > 1 && length < 5) {
        val = val.substring(0, 1) + '-' + val.substring(1, 5);
      } else if (length > 5 && length < 11) {
        val =
          val.substring(0, 1) +
          '-' +
          val.substring(1, 5) +
          '-' +
          val.substring(5, 10);
      } else if (length > 10) {
        let rtns = length / 10;
        rtns = rtns % 1 !== 0 ? rtns + 1 : rtns;
        rtns = ('' + rtns).includes('.') ? +('' + rtns).split('.')[0] : rtns;
        let substr: any[] = [];
        for (let index = 0; index < rtns; index++) {
          substr.push(val.substring(index * 10, index * 10 + 10));
        }
        for (let index = 0; index < substr.length; index++) {
          substr[index] = this.formatDecId(substr[index]);
        }
        val = substr.join(',');
      }
      return val;
    }
    return val;
  }

  setLastRunDate(item: any) {}

  /**
   * Get Available Attributes from DB
   * Application, Facility, Publics, Project Attributes, Documents, Keyword
   */
  getAttributeTypes() {
    this.searchToolService.getSearchByAttributes().subscribe(
      (res: any) => {
        this.lastLoadDate = res.plastLoadDate;
        let uniqueApps: any[] = res.searchModels || [];
        uniqueApps.forEach((e: any) => {
          e.selectedAndOr= 'and',
          e.children = e.attributeModels;
          e.children.forEach((f: any) => {
            f.field = f.searchAttributeName;
            // added dropdown to multiselect f.attributeDataType == 2 ||
            f.isCheckbox = f.attributeDataType == 4 ? 'Y' : 'N';

            f.isMultiSelect = f.attributeDataType == 2 ? 'Y' : 'N';
            // f.isSelect = f.attributeDataType == 6?(f.attributes ||[]).map((str:any)=>{
            //   return { label: str.cdDesc ? str.cdDesc : str.cd, value: str.cd  };
            // }) : [];
            f.isMasked = f.attributeDataType == 5;
            f.isDate = f.attributeDataType == 3;
            f.checkboxesValue =
              f.attributeDataType == 2
                ? (f.attributes || []).map((str: any) => {
                    return {
                      label: str.cdDesc ? str.cdDesc : str.cd,
                      value: str.cd,
                    };
                  })
                : [];
            f.operators = this.getOperators(f.attributeDataType);
          });
          this.createSearchQuery(e.searchEntityCode, e.searchEntityDesc);
        });
        this.uniqueApplications = uniqueApps;
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  initArrays() {
    let uniqueApps: any[] = [];
    [].forEach((e: any) => {
      let index = uniqueApps.findIndex(
        (f: any) => f.searchEntityCode == e.searchEntityCode
      );
      if (index == -1) {
        uniqueApps.push({
          searchEntityCode: e.searchEntityCode,
          searchEntityDesc: e.searchEntityDesc,
          children: [
            {
              ...e,
              selectedAndOr: 'and',
              field: e.searchAttributeName,
              isMultiSelect: e.attributedatatype == 2 ? '1' : '0',
              isCheckbox: e.attributedatatype == 4 ? '1' : '0',
              isDate: e.attributedatatype == 3,
              checkboxesValue:
                e.attributedatatype == 2 ? e.attributes || [] : [],
              operators: this.getOperators(e.attributedatatype),
            },
          ],
        });
        this.createSearchQuery(e.searchEntityCode, e.searchEntityDesc);
      } else {
        uniqueApps[index].children.push({
          ...e,
          selectedAndOr: 'and',
          field: e.searchAttributeName,
          isCheckbox: e.attributedatatype == 4 ? '1' : '0',
          isMultiSelect: e.attributedatatype == 2 ? '1' : '0',
          isDate: e.attributedatatype == 3,
          checkboxesValue: e.attributedatatype == 2 ? e.attributes || [] : [],
          operators: this.getOperators(e.attributedatatype),
        });
      }
    });
    this.uniqueApplications = uniqueApps;
  }

  /*
   *Operators Based on Data Types
   *Multiple choice, Radio Button has no operator
   *Multiple choice will have drop down with Check box
   *Radio Button will have Indicator field Yes/No
   *Free text will have EQUAL TO, NOT EQUAL TO,  IN, STARTS WITH, CONTAINS, LIKE, NOT
   *Date Fields will have  =, >, >=, <= and BETWEEN
   */

  getOperators(id: number) {
    switch (id) {
      case 1:
        return [
          'EQUAL TO',
          'NOT EQUAL TO',
          'EXCLUDE PROJECT',
          'IN',
          'STARTS WITH',
          'CONTAINS',
          'LIKE',
        ];
      case 2:
        return ['IN', 'NOT IN', 'EXCLUDE PROJECT'];
      case 3:
        return ['=', '>', '>=', '<', '<=', 'BETWEEN'];
      case 4:
        return [];
      case 5:
        return [];
      case 6:
        return ['STARTS WITH', 'CONTAINS', 'LIKE'];
    }
  }

  ngOnDestroy() {
    this.commonService.isFixedFooter.next(false);
  }

  get formControls() {
    return this.searchForm.controls;
  }

  get queries() {
    return this.searchForm?.get('queries') as UntypedFormArray;
  }

  get searchQueries() {
    return this.searchForm?.get('searchQuery') as UntypedFormArray;
  }

  searchQueryControls(i: number) {
    let fg = this.searchQueries.at(i) as UntypedFormGroup;
    return fg.get('controls') as UntypedFormArray;
  }

  createSearchQuery(id: number, name: string) {
    let fg = new FormGroup({
      searchEntityCode: new FormControl(id),
      searchEntityDesc: new FormControl(name),
      controls: this.formBuilder.array([]),
      entityAndOr: new FormControl('and'),
    });
    this.searchQueries.push(fg);
  }

  createControl(i: number) {
    if (!this.createDeleteRow) {
      let control = this.formBuilder.group({
        section: ['APPL'],
        inputField: [''],
        maskedInput: [''],
        andOr: ['and'],
        field: [null, Validators.required],
        operator: [null, Validators.required],
        betweenInput1: [''],
        betweenInput2: [''],
        checkbox: [''],
        multipleCheckboxes: [],
        dataType: [],
      });
      this.searchQueryControls(i).push(control);
      this.trackingArray.push(this.uniqueApplications[i]);
      this.isSubmitted = false;
    }
  }

  deleteControl(i: number, j: number, listInd: string) {
    if (!this.createDeleteRow) {
      this.searchQueryControls(i).removeAt(j);
      this.trackingArray.splice(j, 1);
    }
  }

  getFormattedDate(value: any) {
    if (value) {
      return moment(value, 'YYYY-MM-DD').format('MM-DD-YYYY');
    }
    return value;
    // if (value) {
    //   const val = new Date(value);
    //   value = ((''+val.getMonth()).length > 1 ? val.getMonth() + 1 : '0'+ (val.getMonth() + 1)) + "-" +
    //           (('' +val.getDate()).length > 1 ? val.getDate() : '0' + val.getDate()) + '-' +
    //           val.getFullYear();
    // }
    // return value;
  }

  /**
   * Persist Search Query Condition
   */
  duplicateQueryName: boolean = false;
  sentValue: any;
  saveQuery(saveas?: boolean, run?: boolean) {
    let valuetosend = JSON.parse(JSON.stringify(this.searchForm.getRawValue()));
    console.log('Value to send', valuetosend, valuetosend.entityAndOr);
    delete valuetosend.queries;
    this.showServerError = false;
    this.serverErrorMessage = '';
    if (!saveas) {
      this.showRun = true;
      if (!valuetosend.queryId)
        valuetosend.queryOwner =
          this.userRoles[0] == 'System Admin' ? 'GLOBAL' : this.ownerId;
    } else {
      valuetosend.queryOwner =
        this.userRoles[0] == 'System Admin' ? 'GLOBAL' : this.ownerId;
    }
    valuetosend.documentSearchType =
      valuetosend.pending && valuetosend.historic
        ? 'P,H'
        : valuetosend.pending
        ? 'P'
        : valuetosend.historic
        ? 'H'
        : '';
    if (!valuetosend.documentSearchType) {
      this.isSubmitted = true;
      return;
    }
    delete valuetosend.pending;
    delete valuetosend.historic;
    if (saveas && valuetosend.queryName == valuetosend.originalQueryName) {
      this.duplicateQueryName = true;
      this.scrollToInvalid();
      return false;
    } else {
      this.duplicateQueryName = false;
    }
    if (!valuetosend.queryId || saveas) valuetosend.queryId = null;
    let conditions: any[] = [];
    valuetosend.searchQuery.forEach((query: any, i: number) => {
      if (query.controls.length > 0)
        query.controls.forEach((control: any, j: number) => {
          if (control.field) {
            const index = this.uniqueApplications[i].children.findIndex(
              (e: any) => e.field == control.field
            );
            let value;
            if (
              control.multipleCheckboxes &&
              control.multipleCheckboxes.length > 0
            ) {
              if (
                this.uniqueApplications[i].children[index].searchAttributeId ==
                16
              ) {
                value = control.multipleCheckboxes.join(',');
              } else {
                value = control.multipleCheckboxes
                  .map((e: any) => e.split(')')[0])
                  .join(',');
                console.log(control.multipleCheckboxes);
                console.log(
                  control.multipleCheckboxes.map((e: any) => e.split(')')[0])
                );
              }
            } else if (
              control.operator == 'BETWEEN' &&
              this.uniqueApplications[i].children[index].attributeDataType == 3
            ) {
              value =
                this.getFormattedDate(control.betweenInput1) +
                ',' +
                this.getFormattedDate(control.betweenInput2);
            } else if (
              this.uniqueApplications[i].children[index].attributeDataType == 3
            ) {
              let val: Date;
              value = this.getFormattedDate(control.inputField);
            } else if (
              this.uniqueApplications[i].children[index].isCheckbox == 'N'
            ) {
              value = control.inputField;
            } else {
              value = control.checkbox;
            }
            console.log(query.entityAndOr, '776');
            console.log( (j == 0 && i == 0) || conditions.length == 0
            ? '1'
            : j == 0 && i > 0 && conditions.length != 0
            ? query.entityAndOr + '2'
            : control.andOr + '3')
            conditions.push({
              conditionOperator:
                (j == 0 && i == 0) || conditions.length == 0
                  ? ''
                  : j == 0 && i > 0 && conditions.length != 0
                  ? this.uniqueApplications[i].selectedAndOr
                  : control.andOr,
              searchAttributeId:
                this.uniqueApplications[i].children[index].searchAttributeId,
              comparisonOperator: control.operator,
              comparisonValue: value,
              searchEntityCode: this.uniqueApplications[i].searchEntityCode,
              searchQueryConditionId:
                saveas || this.disableSave()
                  ? undefined
                  : control?.searchQueryConditionId,
              searchAttributeOrder: j,
            });
            console.log('Conditions log', conditions, conditions.indexOf(0));
          }
        });
    });
    delete valuetosend.searchQuery;
    delete valuetosend.originalQueryName;
    delete valuetosend.isView;
    if (conditions.length == 0) {
      this.showServerError = true;
      this.serverErrorMessage =
        'Error: At least one selection criteria is Required.';
      this.scrollToInvalid();
      return;
    }
    valuetosend.searchQueryConditions = conditions;
    this.searchToolService.saveQuery(valuetosend).subscribe(
      (res: any) => {
        if (run) {
          this.selectedRun(valuetosend);
        } else {
          this.successPopup.open();
          this.sentValue = res;
          // this.isCreatingQuery = false;
          // this.getAvailableSearches();
          // this.uniqueApplications = [];
          // this.getAttributeTypes();
          // this.initForm();
          // this.showServerError = false;
          // this.serverErrorMessage = null;
          // this.queries.clear();
        }
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        this.scrollToInvalid();
        throw error;
      }
    );
  }

  scrollToInvalid() {
    setTimeout(() => {
      const invalidInputs = Array.from(
        document.querySelectorAll('.addr-error-msg')
      ); // set up so you can use any custom invalid classes you're adding to your elements, as well
      invalidInputs.sort(
        (a, b) => a.getBoundingClientRect().top - b.getBoundingClientRect().top
      ); // sort inputs by offset from top of viewport (handles issues with multi-column layouts, where the first element in the markup isn't necessarily the highest on the page)
      invalidInputs[0].scrollIntoView({ block: 'center', behavior: 'smooth' }); // scroll first (top) input into center of view, using smooth animation
    }, 100);
  }

  /**
   * Run Query
   * @returns Result Set based on search Query Criteria
   */
  onRunClicked() {
    // this.isSubmitted = true;
    // if(this.queries.length == 0){
    //   this.formControls?.queries.setErrors({required: true});
    //   return;
    // }
    // this.isSubmitted = true;
    // if(this.searchForm.valid){
    //   this.isSubmittedSuccessfully = true;
    // }
    // this.setFormData(this.searchForm, this.queryData);
    if (!this.disableSave()) {
      this.onSaveClicked(true);
    } else {
      this.selectedRun(this.searchForm.value);
    }
  }

  /**
   * Success Popup Run Click
   */

  onRunClick() {
    this.isSubmitted = false;
    this.selectedRun(this.sentValue);
    this.isCreatingQuery = false;
    this.frameSearchObj(this.sentValue);
    this.isCreatingQuery = true;
  }

  /**
   * On Save Success Pop Up
   */

  onSaveClicked(run?: boolean) {
    this.isSubmitted = true;
    this.showServerError = false;
    if (this.searchForm.valid) {
      this.saveQuery(false, run);
    } else {
      this.searchForm.markAllAsTouched();
      console.log(this.searchForm.errors);
    }
    this.runQuery = false;
  }

  /**
   * On SaveAs Success Pop Up
   */

  onSaveAsClicked(saveAsClick?: boolean, run?: boolean) {
    if (this.searchForm.valid) {
      this.saveQuery(true, run);
    } else {
      this.searchForm.markAllAsTouched();
      console.log(this.searchForm.errors);
    }
    this.isSubmitted = true;
    this.runQuery = false;
    this.saveAsClicked = true;
  }

  /**
   * Get User Role
   */
  getCurrentUserRole() {
    let userInfo = this.authService.getUserInfo();
    this.commonService.getUsersRoleAndPermissions(userInfo.ppid).then(
      (response) => {
        this.userRoles = response.roles;
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  getConfig() {
    this.commonService.getAllConfigurations().then(
      (response) => {
        if (response) {
          this.configObject = response;
          this.applicantTypeList = response.publicTypes;
        }
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  initiateQueryForm(sectionInd: any) {
    let queryForm = this.formBuilder.group({
      section: [sectionInd],
      inputField: [''],
      andOr: ['and'],
      field: [null, Validators.required],
      operator: [null, Validators.required],
      betweenInput1: [''],
      betweenInput2: [''],
      checkbox: [''],
      multipleCheckboxes: [],
    });
    return queryForm;
  }

  createRow(sectionInd: string) {
    this.genericOperators = this.searchOperators.generic;
    this.trackingArray.push(sectionInd);
    this.queries.push(this.initiateQueryForm(sectionInd));

    switch (sectionInd) {
      case 'APPL':
        this.applicationSearchQueriesList.push('Query');
        this.applicationFieldList = this.searchFields.application;
        this.applicationOperators = this.searchOperators.application;
        break;
      case 'FAC':
        this.facilitySearchQueryList.push('Query');
        this.facilityFieldList = this.searchFields.facility;
        break;
      case 'PUB':
        this.publicsSearchQueryList.push('Query');
        this.publicsFieldList = this.searchFields.publics;
        break;
      case 'PROJ ATTR':
        this.projectAttributesSearchQueryList.push('Query');
        this.projectAttrFieldList = this.searchFields.projectAttributes;
        break;
      case 'DOCS':
        this.documentsSearchQueryList.push('Query');
        this.documentFieldList = this.searchFields.documents;
        break;
      case 'KEY':
        this.keywordsSearchQueryList.push('Query');
        this.keywordsFieldList = this.searchFields.keywords;
        break;
    }
  }

  /**
   * Delete Row
   */
  deleteRow(index: number, listInd: string) {
    this.queries.removeAt(index);
    this.trackingArray.splice(index, 1);
    switch (listInd) {
      case 'APPL':
        this.applicationSearchQueriesList.splice(0, 1);
        break;
      case 'FAC':
        this.facilitySearchQueryList.splice(0, 1);
        break;
      case 'PUB':
        this.publicsSearchQueryList.splice(0, 1);
        break;
      case 'PROJ ATTR':
        this.projectAttributesSearchQueryList.splice(0, 1);
        break;
      case 'DOCS':
        this.documentsSearchQueryList.splice(0, 1);
        break;
      case 'KEY':
        this.keywordsSearchQueryList.splice(0, 1);
        break;
    }
  }

  deleteQueryFromGrid() {}
  onSuccessPopupOkClicked() {
    this.isCreatingQuery = false;
    this.getAvailableSearches();
    this.uniqueApplications = [];
    this.getAttributeTypes();
    this.initForm();
    this.showServerError = false;
    this.serverErrorMessage = null;
    this.queries.clear();
    this.saveAsClicked = false;
  }

  /**
   * Delete Query Confirmation pop up
   */

  onDeleteQueryClicked(obj: any) {
    this.queryId = obj.queryId;
    this.deletePopupBodyText =
      'Are you sure you want to permanently delete query '
        .concat(obj.queryName)
        .concat('?');
    this.deleteIsClicked.next(true);
  }

  /**
   * DELETE Query based on Query Id
   */
  deleteQueryId() {
    this.searchToolService
      .deleteQuery(this.queryId)
      .then((response) => {
        this.getAvailableSearches();
      })
      .catch((error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      });
  }

  /**
   * Navigate to Query Page based on Query Id
   */
  navigateToQueryPage(indicator: string) {
    this.showRun = false;
    this.isFrom = indicator;
    this.isCreatingQuery = true;
  }

  onCloseClicked() {
    this.saveAsClicked = false;
    this.showServerError = false;
    if (this.searchForm.dirty) {
      this.pendingPopup.open();
      console.log(' closed');
      this.initForm();
    } else if (this.isCreatingQuery) {
      this.isCreatingQuery = false;
      this.uniqueApplications = [];
      this.getAttributeTypes();
      this.getAvailableSearches();
      this.initForm();
    } else [this.router.navigate(['/dashboard'])];
  }

  onSearchCloseClicked() {
    this.showServerError = false;
    if (this.searchForm.dirty) {
      this.pendingPopup.open();
      //this.initForm();
    } else if (this.isCreatingQuery) {
      this.isCreatingQuery = false;
      this.uniqueApplications = [];
      this.getAttributeTypes();
      this.getAvailableSearches();
      this.initForm();
      this.activeData = [];
    }
  }

  goToDash() {
    this.isCreatingQuery = false;
    this.uniqueApplications = [];
    this.getAttributeTypes();
    this.getAvailableSearches();
    this.initForm();
    this.activeData = [];
    //this.router.navigate(['/dashboard']);
  }

  onViewClicked(query: any) {
    this.isViewingQuery = true;
    this.viewedQueryName = query.name;
    this.viewedQuerySql = query.searchQuery;
  }

  /**
   * @param form
   * @param queryData
   * Configure Form DATA
   */
  setFormData(form: any, queryData: any) {
    let apiData = this.searchForm.getRawValue();
    apiData.queries = this.queryData;
  }

  onCommentsChange(ev: any) {
    this.searchForm.get('comments')?.setValue(ev);
  }

  /**
   * Clear All Fields in Search Conditions
   */
  onClearClicked() {
    this.frameSearchObj([]);
    this.initForm();
  }

  saveAsExcelFile(buffer: any, fileName: string): void {
    let EXCEL_TYPE =
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
    let EXCEL_EXTENSION = '.xlsx';
    const data: Blob = new Blob([buffer], {
      type: EXCEL_TYPE,
    });
    //TODO: Once confirmed remove custom name for file download
    // '_export_' + new Date().getTime()
    FileSaver.saveAs(data, fileName + '' + EXCEL_EXTENSION);
  }

  exportToExcel() {
    let dataList: any[] = [];
    dataList = this.activeData.map((data) => {
      let temp: any = {};
      this.activeHeaders.forEach((header: any) => {
        temp[header.columnLabel]  = data[header.columnTitle];
      });
      return temp;
    });
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

  onViewDocumentPopupFileClicked(event?: any) {
    console.log(event), console.log(this.projectId);
    let win = this.winRef.nativeWindow;
    let downloadable = [
      'accdb',
      'docx',
      'xlsx',
      'vsd',
      'vsdx',
      'rtf',
      'xls',
      'mdb',
      'doc',
      'eml',
      'mbox',
      'msg',
      'ppt',
      'pptx',
      'rtf',
      'shp',
      'tif',
      'zip',
    ];
    const file = event.fileName;
    let newTab = !downloadable.includes(
      file.split('.')[file.split('.').length - 1]
    )
      ? win.open()
      : null;
    if (!downloadable.includes(file.split('.')[file.split('.').length - 1]))
      newTab.document.write(`<html>
      <head><title>${file}</title></head>
      <body style="margin: 0; padding: 0"> <span id="sm1">Retrieving file content...</span>`);
    //newTab.document.write('Retrieving file content...');
    this.utils.emitLoadingEmitter(true);
    this.requiredDocsService
      .retrieveFileContent(event.fileName, event.documentId, this.projectId)
      .then(
        (res: any) => {
          this.utils.emitLoadingEmitter(false);
          if (!res) {
            //this.modalReference.close('no_data');
            return;
          }

          if (
            !downloadable.includes(file.split('.')[file.split('.').length - 1])
          ) {
            let docUrl = win.URL.createObjectURL(res);
            newTab.document.write(`
            <iframe src="${docUrl}" style="width: 100%; height: 100%; margin: 0; padding: 0; border: none;">
            </iframe></body>
            <script>
              document.getElementById("sm1").innerHTML = "";
            </script>
            </html>`);
          }
          //newTab.location.href = win.URL.createObjectURL(res);
          else this.saveFiles(file, res);
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showResultTableServerError = true;
          throw error;
        }
      );
  }

  private async saveFiles(fileName: string, blob: Blob) {
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = fileName;
    link.click();
  }

  resetFilters() {
    let blankArray: any = [];
    for (let i = 0; i < this.filterValues.length; i++) {
      blankArray.push('');
    }
    this.filterValues = [...blankArray];
    this.dataTable.reset();
  }
  filter(e: any) {}

  onFilter(ev: any) {
    // this.emitFilteredRows.next(ev.filteredValue);
  }

  getFilterList(filterList: any[]) {
    // console.log('get filter list', filterList);

    if (filterList && filterList.length > 0) {
      return filterList.filter((obj: any) => obj.value && obj.label);
    }
    return [];
  }
  renderProjectResultsData(data: any): any {
    let projectIdSet = new Set<any>();
    let applicantSet = new Set<any>();
    let facilityIdSet = new Set<any>();
    let facilityNameSet = new Set<any>();
    let addressSet = new Set<any>();
    let municipalitySet = new Set<any>();
    let permitTypeSet = new Set<any>();
    let applicationTypeSet = new Set<any>();
    let recvDateSet = new Set<any>();
    data?.sort((a: any, b: any) => {
      if (a.projectId && b.projectId) {
        if (a.projectId > b.projectId) return -1;
      }

      return 1;
    });
    data.forEach((obj: any) => {
      projectIdSet.add(obj.projectId ? obj.projectId : '');
      applicantSet.add(obj.applicant ? obj.applicant : '');
      facilityIdSet.add(obj.facId ? obj.facId : '');
      facilityNameSet.add(obj.facName ? obj.facName : '');
      addressSet.add(obj.address ? obj.address : '');
      municipalitySet.add(obj.muni ? obj.muni : '');
      permitTypeSet.add(obj.permitType ? obj.permitType : '');
      applicationTypeSet.add(obj.applType ? obj.applType : '');
      recvDateSet.add(obj.recvDate ? obj.recvDate : '');
      this.projResultGridHeaders.forEach((header: any) => {
        switch (header.columnTitle) {
          case 'projectId':
            header.filtersList = Array.from(projectIdSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'applicant':
            header.filtersList = Array.from(applicantSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'facId':
            header.filtersList = Array.from(facilityIdSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'facName':
            header.filtersList = Array.from(facilityNameSet)
              .sort((a: any, b: any) => {
                if (new Date(b) < new Date(a)) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'address':
            header.filtersList = Array.from(addressSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'muni':
            header.filtersList = Array.from(municipalitySet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'permitType':
            header.filtersList = Array.from(permitTypeSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'applType':
            header.filtersList = Array.from(applicationTypeSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'rcvdDate':
            header.filtersList = Array.from(recvDateSet)
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
    return [...data];
  }

  /**
   * Render result set Data
   */
  renderDocumentResultsData(data: any): any {
    let projectIdSet = new Set<any>();
    let documentTypeSet = new Set<any>();
    let documentSubTypeSet = new Set<any>();
    let facilityIdSet = new Set<any>();
    let facilityNameSet = new Set<any>();
    let addressSet = new Set<any>();
    let municipalitySet = new Set<any>();
    let permitTypeSet = new Set<any>();
    let applicationTypeSet = new Set<any>();
    let recvDateSet = new Set<any>();
    data?.sort((a: any, b: any) => {
      if (a.projectId && b.projectId) {
        if (a.projectId > b.projectId) return -1;
      }
      return 1;
    });
    data.forEach((obj: any) => {
      projectIdSet.add(obj.projectId ? obj.projectId : '');
      documentTypeSet.add(obj.docType ? obj.docType : '');
      documentSubTypeSet.add(obj.docSubType ? obj.docSubType : '');
      facilityIdSet.add(obj.facId ? obj.facId : '');
      facilityNameSet.add(obj.facName ? obj.facName : '');
      addressSet.add(obj.address ? obj.address : '');
      municipalitySet.add(obj.muni ? obj.muni : '');
      permitTypeSet.add(obj.permitType ? obj.permitType : '');
      applicationTypeSet.add(obj.applType ? obj.applType : '');
      recvDateSet.add(obj.recvDate ? obj.recvDate : '');
      this.docResultGridHeaders.forEach((header: any) => {
        switch (header.columnTitle) {
          case 'projectId':
            header.filtersList = Array.from(projectIdSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'docType':
            header.filtersList = Array.from(documentTypeSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'docSubType':
            header.filtersList = Array.from(documentSubTypeSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'facId':
            header.filtersList = Array.from(facilityIdSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'facName':
            header.filtersList = Array.from(facilityNameSet)
              .sort((a: any, b: any) => {
                if (new Date(b) < new Date(a)) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'address':
            header.filtersList = Array.from(addressSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'muni':
            header.filtersList = Array.from(municipalitySet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'permitType':
            header.filtersList = Array.from(permitTypeSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'applType':
            header.filtersList = Array.from(applicationTypeSet)
              .sort((a: any, b: any) => {
                if (a < b) return -1;
                return 1;
              })
              .map((str: string) => {
                return { label: str, value: str };
              });
            break;
          case 'recvDate':
            header.filtersList = Array.from(recvDateSet)
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
    return [...data];
  }
}
