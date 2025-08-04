import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import {
  FormControl,
  FormGroup,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { CodeTables } from '../../../../@shared/constants/CodeTableMaintenance';
import { CodeTablesService } from '../../../../@shared/services/code-tables-service';
import {
  DocumentSubTypeHeaders,
  DocumentSubTypeTableHeaders,
  DocumentSubTypeTitleTableHeaders,
  DocumentTitleHeaders,
  DocumentTitleTableHeaders,
  DocumentTypeHeaders,
  GenericDocumentTableHeaders,
  MessagesHeaders,
  PermitCategoryHeaders,
  PermitTypeCodeHeaders,
  SWFacilitySubTypeHeaders,
  SWFacilityTypeHeaders,
  SystemParameterHeaders,
  TransTypeCodeHeaders,
} from '../document-maintenance/MaintenanceTableHeaders';
import { CustomModalPopupComponent } from '../../../../@shared/components/custom-modal-popup/custom-modal-popup.component';
import { CommonService } from '../../../../@shared/services/commonService';
import { SuccessPopupComponent } from '../../../../@shared/components/success-popup/success-popup.component';
import { PendingChangesPopupComponent } from '../../../../@shared/components/pending-changes-popup/pending-changes-popup.component';
import { ErrorService } from '../../../../@shared/services/errorService';
import { Utils } from 'src/app/@shared/services/utils';

@Component({
  selector: 'app-maintenance-permit-type',
  templateUrl: './maintenance-permit-type.component.html',
  styleUrls: ['./maintenance-permit-type.component.scss','../document-maintenance/document-maintenance.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MaintenancePermitTypeComponent implements OnInit {
  maintenanceForm!: UntypedFormGroup;
  addNewForm!: UntypedFormGroup;
  addNewHeaders: any[] = [];
  tableNames: any = [];
  codeTableData: any = CodeTables;
  
  categoriesObj: any;
  documentTables: any[] = [];
  requiredDocumentsTables: any[] = [];
  messageTables: any[] = [];
  swFacTypeTables: any[] = [];
  gisTables: any[] = [];
  permitTypeFeesTables: any[] = [];
  transactionTypesTables: any[] = [];
  linksTables: any[] = [];
  systemParamterString: string = 'systemParamter';
  documentsString: string = 'documents';
  reqdDocsString: string = 'requiredDocuments';
  errorMessageString: string = 'errorMessage';
  swFacilityTypesString: string = 'swFacilityTypes';
  gisString: string = 'gisLayers';
  permitTypeFeesString: string = 'permitTypeFees';
  transactionTypeString: string = 'transactionTypes';
  linksString: string = 'links';
  currentCategory: string = '';
  selectedObject: any = {};
  headers!: any;
  isMaxHeight = '34px';
  addPopupLabel = 'Add Document Type Description*:';
  addInput: any;
  addPopupInputMaxlength = 75;
  @ViewChild('dataTable') dataTable!: any;
  @ViewChild('addPopup') addPopup!: CustomModalPopupComponent;
  currentData: any[] = [];
  sysParameters: any;
  sysParameterNames: any[] = [];
  categoriesList: any[] = [];
  categoryWiseData: any[] = [];
  tableWiseData: any[] = [];
  formSubmitted: boolean = false;
  onPopupOkClicked: boolean = false;
  showServerError: boolean =false;
  serverErrorMessage!: string;
  modalReference!: any;
  @ViewChild('successPopup', { static: true }) successPopup!: SuccessPopupComponent;
  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;
  sameOption: boolean = false;
  constructor(
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private codeTableService: CodeTablesService,
    public utils: Utils,
    private commonService: CommonService,
    private errorService: ErrorService,
    public cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initiateForm();
    this.createRow();
    this.setData(this.codeTableData);
    this.getCategories();
    this.onTableNameChange('E_PERMIT_TYPE_CODE');
    this.getSubAppropriateTableData();
    this.maintenanceForm.valueChanges.subscribe(() => {
       this.duplicateError = false;
       this.sameOption = false;
    });
  }

  getCategories() {
    this.codeTableService.getCategoryTablesData().subscribe((res: any) => {
      this.categoriesObj = {};
      res.forEach((category: any) => {
        this.categoriesObj[category.categoryName] = category.tableNames;
      });
      this.categoriesList = Object.keys(this.categoriesObj);
    });
  }


  setCurrentValue(data: any, index: number) {
    console.log(data);
    let fg: any = this.columns.at(index);
    // if (data.target.value) {
    //   fg.controls['currentValue'].setValue(this.sysParameters[data.target.value]);
    // } else {
    //   fg.controls['currentValue'].setValue("");
    // }
  }

 
  setSelectedTableData(type: any) {
    let index = this.categoryWiseData.findIndex(
      (e: any) => e.categoryName == type
    );
    this.tableWiseData = this.categoryWiseData[index].tableNames;
    this.maintenanceForm.controls['tableName'].enable();
  }

  private setData(data: any) {
    data[this.documentsString].forEach((docs: any) => {
      this.documentTables.push(docs.tableName);
    });
    data[this.reqdDocsString].forEach((docs: any) => {
      this.requiredDocumentsTables.push(docs.tableName);
    });
    data[this.errorMessageString].forEach((docs: any) => {
      this.messageTables.push(docs.tableName);
    });
    data[this.swFacilityTypesString].forEach((docs: any) => {
      this.swFacTypeTables.push(docs.tableName);
    });
    data[this.gisString].forEach((docs: any) => {
      this.gisTables.push(docs.tableName);
    });
    data[this.permitTypeFeesString].forEach((docs: any) => {
      this.permitTypeFeesTables.push(docs.tableName);
    });
  }

  private initiateForm() {
    this.maintenanceForm = this.formBuilder.group({
      category: [],
      tableName: [{ value: "E_PERMIT_TYPE_CODE", disabled: true }, Validators.required],
      rows: this.formBuilder.array([]),
      columns: this.formBuilder.array([]),
    });
  }

  get rows() {
    return this.maintenanceForm?.controls['rows'] as UntypedFormArray;
  }

  get columns() {
    return this.maintenanceForm?.controls['columns'] as UntypedFormArray;
  }

  get formControls() {
    return this.maintenanceForm?.controls;
  }

  getAppropriateTableDataforAdd(tableNmae: any,i: any) {
    this.codeTableService
      .getSelectedTableData(tableNmae)
      .subscribe((res: any) => {
        console.log(res);
        this.addNewHeaders[i].tableData = res || [];
      });
  }

  createColumn() {
    if (this.columns.length > 0) {
     // if (this.maintenanceForm.invalid) return;
    }
    let obj: any = { originalObj: [''] };
    if (this.maintenanceForm.controls['tableName'].value) {
      this.headers.forEach((e: any, i: number) => {
        obj[e.columnTitle] = this.formBuilder.control(
          e.isDisabled || e.columnTitle == 'updatedValue' ? { disabled: true, value: '' } : '',
          e.columnTitle == 'updatedValue' || i == 0 ? [Validators.required] : []
        );
      });
    }
    console.log(obj);
    const fb = this.formBuilder.group(obj);
    this.columns.push(fb);
    let addNObj: any = {};
    this.headers[0].addNewHeaders.forEach((e: any, i: number) => {
      addNObj[e.columnTitle] = this.formBuilder.control('', [Validators.required]);
      if (e.tableName) this.getAppropriateTableDataforAdd(e.tableName, i);
    });
    this.addNewHeaders = this.headers[0].addNewHeaders;
    console.log(addNObj, 'columnTitle');
    this.addNewForm = this.formBuilder.group(addNObj);
    this.formSubmitted = false;
  }

  removeColumn(index: number) {
    if (this.columns.length > 1) this.columns.removeAt(index);
    else if (index != 0) this.columns.removeAt(index);
    this.checkduplicateError();
    //this.sameOptionError();
  }

  createRow(addInd?: any) {
    const formGroup = new FormGroup({});
    if (addInd) {
      console.log('we here', this.headers);
      this.headers?.forEach((header: any) => {
        console.log('header title', header.columnLabel);

        if (header.columnTitle != 'addBtn') {
          if (header.columnLabel == 'Updated Value') {
            console.log('in the if');
          } else if (header.columnTitle == 'active') {
            formGroup.addControl(header.columnTitle, new FormControl(true));
          } else {
            formGroup.addControl(header.columnTitle, new FormControl(null));
          }
        } else {
          console.log('updated value skipped');
        }
      });
      this.rows.push(formGroup);
    } else {
      this.headers?.forEach((header: any) => {
        if (header.columnTitle != 'addBtn') {
          if (header.columnTitle == 'active') {
            formGroup.addControl(header.columnTitle, new FormControl(true));
          }
          formGroup.addControl(header.columnTitle, new FormControl(null));
        }
      });
      this.rows.push(formGroup);
    }
  }

  onCategoryChanged(category: any) {
    this.formControls?.tableName.enable();
    this.formControls?.tableName.setValue(null);
    this.rows.clear();
  }

  onTableNameChange(val: any) {
    this.headers = PermitTypeCodeHeaders;
    this.getAppropriateTableData(val);
    this.columns.clear();
    this.createColumn();
    this.onPopupOkClicked = false;
  }

  getAppropriateTableData(tableName: any) {
    this.codeTableService
      .getSelectedTableData(tableName)
      .subscribe((res: any) => {
        console.log(res);
        this.sysParameterNames = res || [];
        this.cdr.detectChanges();
        setTimeout(() => {
        this.utils.emitLoadingEmitter(false);
        }, 200);
      });
  }

  getSubAppropriateTableData() {
    this.codeTableService
      .getSelectedTableData("E_PERMIT_CATEGORY")
      .subscribe((res: any) => {
        console.log(res);
        this.headers[2].dropdownValues = res || [];
      });
  }

  bindOtherValuesOnChange(data: any, index: number, formKey: string) {
    let fg: any = this.columns.at(index);
    console.log(data.target.value);
    console.log(fg.controls[formKey].value);
    let val = data.target.value;
    if (val) {
      let mappingObj: any = this.headers[0].mapperBinding;
      let index = this.sysParameterNames.findIndex(
        (e: any) => e[this.headers[0].dropdownKey] == val
      );
      Object.keys(mappingObj).forEach((e: any) => {
        let i = this.headers.findIndex((g: any) => g.columnTitle == e);
        if (i != -1) {
          let columnType = this.headers[i].columnType;
          if (columnType == 'checkbox') {
            fg.controls[e].setValue(
              this.sysParameterNames[index][mappingObj[e]] == 1 ? true : false
            );
          } else {
            fg.controls[e].setValue(
              this.sysParameterNames[index][mappingObj[e]]
            );
          }
        }
      });
      fg.controls['originalObj'].setValue(this.sysParameterNames[index]);
      fg.controls['updatedValue'].enable();
    } else {
      fg.controls['updatedValue'].disabled();
    }
  }

  checkBoxValidation(
    data: any,
    index: number,
    formKey: string,
    oppTitle: string
  ) {
    // let fg: any = this.columns.at(index);
    // let val = data.target.value;
    // if (val) {
    //   if (fg.controls[oppTitle].value) fg.controls[oppTitle].setValue(false);
    // }
  }

  onTableNameChanged(val: any) {
    this.rows.clear();
    this.showServerError=false;
    let objArray = this.codeTableData[this.currentCategory].filter(
      (obj: any) => obj.tableName == val
    );
    this.selectedObject = objArray[0];
    console.log('here', val, this.tableNames);
    console.log(this.currentCategory);
    if (this.currentCategory == this.documentsString) {
      switch (val) {
        case 'System Parameter':
          this.headers = GenericDocumentTableHeaders;
          break;
        case 'Document Type':
          this.headers = GenericDocumentTableHeaders;
          break;
        case 'Document Sub Type':
          this.headers = DocumentSubTypeTableHeaders;
          break;
        case 'Document Title':
          this.headers = DocumentTitleTableHeaders;
          break;
        case 'Document Sub Type Title':
          this.headers = DocumentSubTypeTitleTableHeaders;
      }
    } else if (this.currentCategory == this.swFacilityTypesString) {
      switch (val) {
        case 'SW Facility Type':
          this.headers = SWFacilityTypeHeaders;
          break;
        case 'SW Facility Sub Type':
          this.headers = SWFacilitySubTypeHeaders;
          break;
      }
    }
    this.createRow();
  }

  checkduplicateError() {
    let duplicateError =false;
    let columns: any[] = this.maintenanceForm.getRawValue().columns;
    for (let index = 0; index < columns.length; index++) {
       let ele = columns[index];
       let searchColumns = JSON.parse(JSON.stringify(columns));
       searchColumns.splice(index, 1);
       const i = searchColumns.findIndex((e: any) => e[this.headers[0].columnTitle] == ele[this.headers[0].columnTitle]);
       let group: any = this.columns.at(index);
       if (i != -1) {
        duplicateError =true;
        // break;
       
        group.controls[this.headers[0].columnTitle].setErrors({duplicate: true});
       } else {
        group.controls[this.headers[0].columnTitle].setErrors(null);
       }
    }
    return duplicateError
   }

  //  sameOptionError(){
  //   let sameOptionError =false;
  //   let columns: any[] = this.maintenanceForm.getRawValue().columns;
  //   for (let index = 0; index < columns.length; index++) {
  //     let ele = columns[index];
  //     let header = this.headers[2];
  //     let i = header.dropdownValues.findIndex((e: any) => ele.permitCategory == e.permitCategoryDescription);
  //     if (i != -1) {
  //       let group: any = this.columns.at(index);
  //       if (ele.updatedValue == header.dropdownValues[i].permitCategoryId) {
  //          sameOptionError =true;           
  //          group.controls['updatedValue'].setErrors({sameOption: true});
  //       }
  //       else{
  //         group.controls['updatedValue'].setErrors(null);
  //       }
  //     }
  //   }
  //   return sameOptionError
  //  }

 duplicateError: boolean = false;
  onSaveClicked() {
    console.log('here is the form', this.maintenanceForm.value);
    let sendObj = this.headers[0].sendObj;
    let checkboxes: any[] = this.headers[0].checkboxes || [];
    this.formSubmitted = true;
    if (this.maintenanceForm.valid) {
      this.duplicateError = this.checkduplicateError(); 
      //this.sameOption = this.sameOptionError();
      if (this.duplicateError == true || this.sameOption == true) {

        return;
      }
      let body = {
        tableName: this.maintenanceForm.getRawValue().tableName,
        keyValues: this.maintenanceForm.getRawValue().columns.map((e: any) => {
          let obj: any = {};
          Object.keys(sendObj).forEach((f: any) => {
            if (checkboxes.includes(sendObj[f])) {
              obj[f] = e[sendObj[f]];
              obj[f] = obj[f] == true ? '1' : false ? '0' :null;
            } else if (e[sendObj[f]]) {
              obj[f] = e[sendObj[f]];
            } else {
              obj[f] = e['originalObj'][sendObj[f]];
            }
          });
          return obj;
        }),
      };
      this.codeTableService.updateTable(body).then((res: any) => {
        this.onTableNameChange('E_PERMIT_TYPE_CODE'); 
        this.modalReference = this.successPopup.open();
      },
      (error: any) => {
        this.serverErrorMessage = error.error;
        this.showServerError = true;
        throw error;
      });
    } else {
      this.maintenanceForm.markAllAsTouched();
      this.sameOption =false;
      let columns: any[] = this.maintenanceForm.getRawValue().columns;
      for (let index = 0; index < columns.length; index++) {
        let ele = columns[index];
        let header = this.headers[2];
        let i = header.dropdownValues.findIndex((e: any) => ele.permitCategory == e.permitCategoryDescription);
        if (i != -1) {
          if (ele.updatedValue == header.dropdownValues[i].permitCategoryId) {
             this.sameOption =true;
            //  break;
             let group: any = this.columns.at(index);
             group.controls['updatedValue'].setErrors({sameOption: true});
          }
        }
      }
    }
  }

  onAddClicked() {
    this.addPopup.open('medium');
  }


  

  async openConfirmModal() {
    if (this.addNewForm.dirty) {
      this.modalReference = await this.pendingPopup.open();
    } else {
      //this.onAddCancelClicked();
    }
  }

  goBack() {
    this.pendingPopup.close();
    this.onPopupOkClicked = false;
    setTimeout(() => {
     
      //this.addPopup.close();
      //this.onAddCancelClicked();
    }, 500);
  }

  onCloseClicked() {
    this.router.navigate(['/dashboard']);
  }
}
