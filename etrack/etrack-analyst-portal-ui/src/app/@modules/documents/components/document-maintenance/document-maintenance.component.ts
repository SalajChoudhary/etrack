import { Component, OnInit, ViewChild } from '@angular/core';
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
  PermitTypeURLHeaders,
  DocumentSubTypeTitleTableHeaders,
  DocumentTitleHeaders,
  DocumentTitleTableHeaders,
  DocumentTypeHeaders,
  GenericDocumentTableHeaders,
  MessagesHeaders,
  PermitCategoryHeaders,
  SWFacilitySubTypeHeaders,
  SWFacilityTypeHeaders,
  SystemParameterHeaders,
  TransTypeCodeHeaders,
  GISLayerHeaders,
  ReqdDocFacilityTypeHeaders,
  ReqdDocNaturalResourcesHeaders,
  ReqdDocPermitTypeHeaders,
  InvoiceFeeHeaders,
  ReqdDocSeqrHeaders,
  ReqdDocShpaHeaders,
  ReqdDocGiHeaders,
  TransTypeRuleHeaders
} from './MaintenanceTableHeaders';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { SuccessPopupComponent } from 'src/app/@shared/components/success-popup/success-popup.component';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { Utils } from 'src/app/@shared/services/utils';
import { BehaviorSubject } from 'rxjs';
import FileSaver from 'file-saver';

@Component({
  selector: 'app-document-maintenance',
  templateUrl: './document-maintenance.component.html',
  styleUrls: ['./document-maintenance.component.scss'],
})
export class DocumentMaintenanceComponent implements OnInit {
  maintenanceForm!: UntypedFormGroup;
  addNewForm!: UntypedFormGroup;
  editForm!: UntypedFormGroup;
  reqdDocsForm!: UntypedFormGroup;
  addNewHeaders: any[] = [];
  editHeaders: any[] = [];
  tableNames: any = [];
  codeTableData: any = CodeTables;
  categories: any[] = [
    'System Parameter',
    'Documents',
    'Required Documents',
    'Error Message',
    'SW Facility Types',
    'GIS Layers',
    'Permit Type Fees',
    'Transaction Types',
    'Links',
  ];
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
  @ViewChild('editPopup') editPopup!: CustomModalPopupComponent;
  @ViewChild('warningPopup') warningPopup!: CustomModalPopupComponent;
  editPopupOpen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  warningPopupOpen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  currentData: any[] = [];
  sysParameters: any;
  sysParameterNames: any[] = [];
  categoriesList: any[] = [];
  categoryWiseData: any[] = [];
  tableWiseData: any[] = [];
  formSubmitted: boolean = false;
  onPopupOkClicked: boolean = false;
  showRegulationCodeError: boolean = false;
  showServerError: boolean =false;
  showPopupServerError: boolean = false;
  popupServerErrorMessage!: string;
  serverErrorMessage!: string;
  modalReference!: any;
  @ViewChild('successPopup', { static: true }) successPopup!: SuccessPopupComponent;
  @ViewChild('pendingPopup', { static: true })
  pendingPopup!: PendingChangesPopupComponent;
  onTableClicked: boolean = false;
  getAllTableData: boolean = false;
  allowAddNew: boolean = true;
  showSaveButton: boolean = true;
  sendAsArray: boolean = true;
  tableData: any[] = [];
  convertedTableNames: any;
  editRow: any;
  swRegulationCode: string = '';
  prevTableVal: string = '';
  prevCategoryVal: string = '';
  prevSwFacilityType: string = '';
  prevSwFacilitySubType: string = '';
  prevPermitTypeCode: string = '';
  prevPermitTypeGrouping: string = '';
  pendingChangesAction: string = '';
  docSubTypesData: any = [];
  radioHeaders = [
    {name: 'NEW', columnTitle: 'reqdNew'},
    {name: 'MOD', columnTitle: 'reqdMod'},
    {name: 'EXT', columnTitle: 'reqdExt'},
    {name: 'MNM', columnTitle: 'reqdMnm'},
    {name: 'MTN', columnTitle: 'reqdMtn'},
    {name: 'REN', columnTitle: 'reqdRen'},
    {name: 'RTN', columnTitle: 'reqdRtn'},
    {name: 'XFER', columnTitle: 'reqdXfer'}
  ];
  radioValues = ['n/a', 'Yes', 'C-Y', 'No'];
  reqdSwFacilityTypes: any[] = [];
  reqdSwFacilitySubTypes: any[] = [];
  reqdPermitTypes: any[] = [];
  retrieveClicked: boolean = false;
  facilitySubTypesData: any[] = [];
  reqdDocsDropdowns: boolean = false;
  permitTypeGroupings: any[] = [
    {name: "Operating", val: 'O'},
    {name: "Construction", val: 'C'},
    {name: "General Permits", val: 'GP'}
  ];
  filterValues: any;
  errorMsgObj: any={};

  constructor(
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private codeTableService: CodeTablesService,
    private commonService: CommonService,
    private utils: Utils,
    private errorService: ErrorService
  ) {}

  ngOnInit(): void {
    this.initiateForm();
    this.createRow();
    this.getCategoryWiseData();
    this.setData(this.codeTableData);
    this.getSystemParameters();
    this.getCategories();
    this.getAllErrorMsgs();
  }

  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMsgObj = this.commonService.getErrorMsgsObj();
    });
  }

  getCategories() {
    this.codeTableService.getCategoryTablesData().subscribe((res: any) => {
      this.showServerError = false;
      this.categoriesObj = {};
      res.forEach((category: any) => {
        this.categoriesObj[category.categoryName] = category.tableNames;
      });
      this.categoriesList = Object.keys(this.categoriesObj);
      this.getUserFriendlyTableNames();
    }, (err: any) => {
      this.showServerError = true;
      this.serverErrorMessage = err.error;
      throw err;
    });
  }

  getReqdSwFacilities() {
    this.codeTableService.getSwFacTypeAssociate().then((res: any) => {
      res.sort((a: any, b: any) => {
        return a.facilityType.toUpperCase() > b.facilityType.toUpperCase() ? 1 : -1;
      });
      this.reqdSwFacilityTypes = [...res];
    });
    this.codeTableService.getSelectedTableData('E_SW_FACILITY_SUB_TYPE').subscribe((res: any) => {
      this.facilitySubTypesData = [...res];
    });
  }

  getReqdPermitTypes() {
    let payload = {
      tableName: 'E_PERMIT_TYPE_CODE',
      keyValues: {
        permitSubCategory: null,
        swFacTypeId: null,
        swFacSubTypeId: null
      }
    }
    this.utils.emitLoadingEmitter(true);
    this.codeTableService.getReqdDocumentsTableData(payload).then((res: any) => {
      this.utils.emitLoadingEmitter(false);
      this.reqdPermitTypes = [...res];
    }).catch((err: any) => {
      this.utils.emitLoadingEmitter(false);
      console.log(err);
    });
  }

  getUserFriendlyTableNames() {
    this.convertedTableNames = {};
    this.categoriesList.forEach((category: any) => {
      this.categoriesObj[category].forEach((tableName: string) => {
        let tableNameWords = tableName.slice(2).split('_');
        let newTableName = '';
        for(let i = 0; i < tableNameWords.length; i++) {
          newTableName += (tableNameWords[i].charAt(0) + tableNameWords[i].toLowerCase().slice(1));
          if(i < tableNameWords.length - 1) newTableName += ' ';
        }
        switch(newTableName) {

          case 'Message':
            newTableName += ' Text';
            break;
          case 'System Parameter':
            newTableName = 'URL Configurations';
            break;
          case 'Sw Facility Type':
            newTableName = 'Solid Waste Facility Types';
            break;
          case 'Sw Facility Sub Type':
            newTableName = 'Solid Waste Facility Sub Types';
            break;
          case 'Gis Layer Config':
            newTableName = 'GIS Layers';
            break;
          case 'Required Doc For Fac Type':
            newTableName = 'Documents for Solid Waste Facilities';
            break;
          case 'Required Doc For Nat Gp':
            newTableName = 'Documents for Natural Resource GPs';
            break;
          case 'Required Doc For Permit Type':
            newTableName = 'Documents for Permit Types';
            break;
          case 'Required Doc For Seqr':
            newTableName = 'Documents for SEQR';
            break;
          case 'Required Doc For Shpa':
            newTableName = 'Documents for SHPA';
            break;
          case 'Required Doc For Spatial Inq':
            newTableName = 'Documents for Geographical Inquiries';
            break;
          case 'Invoice Fee Type':
            newTableName = 'Permit Application Fees';
            break;
          case 'Transaction Type Rule':
            newTableName += 's';
            break;
          case 'Permit Type Code':
            newTableName = 'URL Reference Links';
            break;
          default:
            break;
        }
        this.convertedTableNames[tableName] = newTableName;
      })
    });
  }

  getSystemParameters() {
    this.commonService.getSystemParameters().subscribe((res: any) => {
      this.sysParameters = res || {};
      // this.sysParameterNames = Object.keys(this.sysParameters);
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

  getCategoryWiseData() {
    this.codeTableService.getCategoryTablesData().subscribe((res: any) => {
      this.categoryWiseData = res || [];
    }, (err: any) => {

    });
  }

  setSelectedTableData(type: any) {
    let index = this.categoryWiseData.findIndex(
      (e: any) => e.categoryName == type
    );
    this.tableWiseData = this.categoryWiseData[index].tableNames;
    this.maintenanceForm.controls['tableName'].enable();
  }

  changesMade() {
    if(!this.maintenanceForm.controls['tableName'].value) {
      return false;
    }

    if(!this.getAllTableData) {
      return this.maintenanceForm.controls.columns.dirty;
    }

    let res : boolean = false;
    this.tableData.forEach((row: any) => {
      if(this.rowChanged(row)) {
          res = true;
      }
    });

    return res;
  }

  rowChanged(row: any) {
    return (row.availToDepInd?.toString() &&
    ((row.availToDepInd === "1" && !row.depOnly) ||
    (row.availToDepInd === "0" && row.depOnly) )) ||
    (row.urlId && row.updatedValue.length) ||
    (row.refLink?.length !==row.updatedURLLinkValue?.length) ||
    (row.layerName &&
    ((row.activeInd.toString() === "1" && !row.active) ||
    (row.activeInd.toString() === "0" && row.active)) ||
    (row.layerType !== row.layerTypeVal) ||
    (row.layerUrl !== row.updatedLayerUrl) ||
    (row.order.toString() !== row.orderInd.toString())) ||
    (row.activeInd?.toString() && (
    (row.activeInd.toString() === "1" && !row.active) ||
    (row.activeInd.toString() === "0" && row.active))
    ) ||
    (row.reqdDocInd?.toString() && (
    (row.reqdDocInd.toString() === "1" && !row.reqdDoc) ||
    (row.reqdDocInd.toString() === "0" && row.reqdDoc))) ||
    (
      (row.transactionTypeRuleId) && (
        (row.userSelNewInd.toString() === "1" && !row.userSelNew) ||
        (row.userSelNewInd.toString() === "0" && row.userSelNew) ||
        (row.userSelModInd.toString() === "1" && !row.userSelMod) ||
        (row.userSelModInd.toString() === "0" && row.userSelMod) ||
        (row.userSelExtInd.toString() === "1" && !row.userSelExt) ||
        (row.userSelExtInd.toString() === "0" && row.userSelExt) ||
        (row.userSelTransferInd.toString() === "1" && !row.userSelTransfer) ||
        (row.userSelTransferInd.toString() === "0" && row.userSelTransfer) ||
        (row.userSelRenInd.toString() === "1" && !row.userSelRen) ||
        (row.userSelRenInd.toString() === "0" && row.userSelRen) ||
        (row.chgOriginalProjectInd.toString() === "1" && !row.chgOriginalProject) ||
        (row.chgOriginalProjectInd.toString() === "0" && row.chgOriginalProject) ||
        (row.modExtFormInd.toString() === "1" && !row.modExtForm) ||
        (row.modExtFormInd.toString() === "0" && row.modExtForm)
      )
    );
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
      category: [null, Validators.required],
      tableName: [{ value: null, disabled: true }, Validators.required],
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

  get disableSaveButton() {
    return !this.changesMade();
  }

  onTextboxChange(textinput: HTMLInputElement, item: any, columnTitle: any) {
    if(columnTitle === 'order') {
      textinput.value = textinput.value.replace(/[^0-9]/g, '');
      item.order = item.order.replace(/[^0-9]/g, '');
    }
  }

  onPopupTextboxChange(popupType: any, columnTitle: any) {
    if(columnTitle === 'invoiceFee') {
      let oldVal = '';
      if(popupType === 'edit') {
        oldVal = this.editForm.controls.invoiceFee.value;
        this.editForm.controls.invoiceFee.setValue(oldVal.replace(/[^0-9]/g, ''));
        this.editForm.controls.invoiceFee.updateValueAndValidity();
      }
      else {
        oldVal = this.addNewForm.controls.invoiceFee.value;
        this.addNewForm.controls.invoiceFee.setValue(oldVal.replace(/[^0-9]/g, ''));
        this.addNewForm.controls.invoiceFee.updateValueAndValidity();
      }
    }
  }

  onAddDropdownChange(columnTitle: any) {
    if(this.maintenanceForm.getRawValue().tableName === 'E_SW_FACILITY_SUB_TYPE') {
      if(!this.addNewForm.controls.facilityType.value) {
        this.swRegulationCode = '';
      }
      this.addNewHeaders[0].tableData.forEach((swFacilityType: any) => {
        if(swFacilityType.swFacilityTypeId.toString() === this.addNewForm.controls.facilityType.value) {
          this.swRegulationCode = swFacilityType.regulationCode;
        }
      });
    }
    else if(this.maintenanceForm.getRawValue().tableName === 'E_DOCUMENT_SUB_TYPE_TITLE' &&
      columnTitle === 'documentTypeId') {
        this.codeTableService.getSelectedTableData(`E_DOCUMENT_SUB_TYPE/${this.addNewForm.controls.documentTypeId.value}`)
        .subscribe((res: any) => {
          res.sort((a: any, b: any) => {
            if(a.description == null) return -1;
            if(b.description == null) return 1;
            return a.description.toUpperCase() > b.description.toUpperCase() ? 1 : -1;
          });
          this.addNewHeaders[1].tableData = [...res];
          if(res.length == 0) {
            this.addNewForm.controls.documentSubTypeId.setValue('');
            this.addNewForm.controls.documentSubTypeId.disable();
          }
          else if(res.length == 1 && res[0].description == null) {
            this.addNewForm.controls.documentSubTypeId.setValue(res[0].documentSubTypeId);
            this.addNewForm.controls.documentSubTypeId.disable();
          }
          else if(res.length > 1) {
            this.addNewHeaders[1].tableData[0].description = '--Select--';
            this.addNewForm.controls.documentSubTypeId.setValue(res[0].documentSubTypeId);
            this.addNewForm.controls.documentSubTypeId.enable();
          }
          this.addNewForm.controls.documentSubTypeId.updateValueAndValidity();
        });
    }

    else if(this.maintenanceForm.getRawValue().tableName === 'E_REQUIRED_DOC_FOR_FAC_TYPE' &&
      columnTitle === 'facilityType') {
        this.codeTableService.getSelectedTableData(`E_SW_FACILITY_SUB_TYPE/${this.addNewForm.controls.facilityType.value}`)
        .subscribe((res: any) => {
          this.addNewHeaders[1].tableData = [...res];
          if(res.length == 0) {
            this.addNewForm.controls.facilitySubType.setValue('');
            this.addNewForm.controls.facilitySubType.clearValidators();
            this.addNewForm.controls.facilitySubType.disable();
            this.addNewHeaders[1].optional = true;
          }
          else {
            this.addNewForm.controls.facilitySubType.enable();
            this.addNewForm.controls.facilitySubType.setValidators(Validators.required);
            this.addNewHeaders[1].tableData.sort((a: any, b: any) => {
              return a.facilitySubType.toUpperCase() > b.facilitySubType.toUpperCase() ? 1 : -1;
            });
            this.addNewHeaders[1].optional = false;
          }
          this.addNewForm.controls.facilitySubType.updateValueAndValidity();
        });
    }

  }

  onEditDropdownChange(columnTitle: any) {
    if(this.maintenanceForm.getRawValue().tableName === 'E_SW_FACILITY_SUB_TYPE') {

      if(!this.editForm.controls.facilityType.value) {
        this.swRegulationCode = '';
      }
      this.editHeaders[0].tableData.forEach((swFacilityType: any) => {
        if(swFacilityType.swFacilityTypeId.toString() === this.editForm.controls.facilityType.value) {
          this.swRegulationCode = swFacilityType.regulationCode;
        }
      });
    }

    else if(this.maintenanceForm.getRawValue().tableName === 'E_DOCUMENT_SUB_TYPE_TITLE' &&
      columnTitle === 'docTypeId') {
        console.log(this.editForm.controls.docTypeId.value);
    }
  }

  onModClicked() {
    if(this.addNewForm.controls.userSelMod.value) {
      this.addNewForm.controls.chgOriginalProjectInd.setValue('');
      this.addNewForm.controls.chgOriginalProjectInd.addValidators(Validators.required);
    }
    else {
      this.addNewForm.controls.chgOriginalProjectInd.setValue('0');
      this.addNewForm.controls.chgOriginalProjectInd.clearValidators();
    }
    this.addNewForm.controls.chgOriginalProjectInd.updateValueAndValidity();
  }

  getAppropriateTableDataforAdd(tableNmae: any,i: any) {
    this.codeTableService
      .getSelectedTableData(tableNmae)
      .subscribe((res: any) => {
        this.showServerError = false;
        if(tableNmae === 'e_sw_facility_type') {
          res.sort((a: any, b: any) => {
            return a.facilityType.toUpperCase() > b.facilityType.toUpperCase() ? 1 : -1;
          })
        }else if(tableNmae === 'e_gis_layer_config') {
          let layerTypes = new Set<any>();
          res.forEach((layer: any) => {
            layerTypes.add(layer.layerType);
          });
          res = [];
          layerTypes.forEach((layerType: any) => {
            res.push({'layerType': layerType});
          });
          res.sort((a: any, b: any) => {
            return a.layerType.toUpperCase() > b.layerType.toUpperCase() ? 1 : -1;
          });
        }
        if(this.maintenanceForm.getRawValue().tableName === 'E_INVOICE_FEE_TYPE') {
          res = res.filter((permitType: any) =>
            ['FW', 'TW', 'LG'].includes(permitType.permitTypeCode)
          );
          console.log(res);
        }
        if(!(tableNmae === 'e_document_sub_type'
          && this.maintenanceForm.getRawValue().tableName === 'E_DOCUMENT_SUB_TYPE_TITLE'))
          this.addNewHeaders[i].tableData = [...res] || [];
        else {
          this.docSubTypesData = [...res];
        }
        if(this.editHeaders[i]) this.editHeaders[i].tableData = [...res] || [];

      }, (err: any) => {
        this.showServerError = true;
        this.serverErrorMessage = err.error;
        throw err;
      });
  }

  getTableDropdownList(tableName: any, i: any) {

    this.codeTableService
      .getSelectedTableData(tableName)
      .subscribe((res: any) => {
        this.showServerError = false;
        if(tableName === 'e_gis_layer_config') {
          let layerTypes = new Set<any>();
          res.forEach((layer: any) => {
            layerTypes.add(layer.layerType);
          });
          res = [];
          layerTypes.forEach((layerType: any) => {
            res.push({'layerType': layerType});
          });
          res.sort((a: any, b: any) => {
            return a.layerType.toUpperCase() > b.layerType.toUpperCase() ? 1 : -1;
          });
        }
        this.headers[i].tableData = [...res] || [];
      }, (err: any) => {
        this.showServerError = true;
        this.serverErrorMessage = err.error;
        throw err;
      });
  }

  createColumn() {
    if (this.columns.length > 0) {
      // if (this.maintenanceForm.invalid) return;
    }
    let obj: any = { originalObj: [''] };
    if (this.maintenanceForm.controls['tableName'].value) {
      this.headers.forEach((e: any) => {
        obj[e.columnTitle] = this.formBuilder.control(
          e.isDisabled ? { disabled: true, value: '' } : '',
          e.columnTitle == 'updatedValue' ? [Validators.required, Validators.maxLength(e.maxCharacter)] : []
        );
      });
    }
    console.log(obj);
    const fb = this.formBuilder.group(obj);
    this.columns.push(fb);
    this.formSubmitted = false;
    this.showServerError=false;
    console.log(this.columns);
  }

  removeColumn(index: number) {
    if (this.columns.length > 1) this.columns.removeAt(index);
    else if (index != 0) this.columns.removeAt(index);
    this.checkduplicateError();
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
    if(this.changesMade()) {
      this.pendingChangesAction = 'category';
      this.modalReference = this.pendingPopup.open();
    }
    else {
      this.changeCategory(category);
    }
  }

  changeCategory(category: any) {
    this.showServerError = false;
    this.retrieveClicked = false;
    this.prevCategoryVal = category;
    this.formControls?.tableName.enable();
    this.formControls?.tableName.setValue(null);
    this.tableData = [];
    this.editHeaders = [];
    this.rows.clear();
    this.columns.clear();
    this.columns.reset();
    this.setTableNameList(category);
    this.allowAddNew = category === "System Parameter" ? false : true;
    this.showSaveButton = (category === "Messages")
      ? false : true;
    if(category === 'Supporting Documents') {
      this.tableNames.sort((a: any, b: any) => {
        return this.convertedTableNames[a] > this.convertedTableNames[b] ? 1 : -1;
      })
    }
  }

  onTableNameChange(val: any) {
    if(this.changesMade()) {
      this.pendingChangesAction = 'table';
      this.modalReference = this.pendingPopup.open();
    }
    else {
      this.changeTableName(val);
    }
  }

  changeTableName(val: any) {
    this.prevTableVal = val;
    this.getAllTableData = false;
    this.sendAsArray = true;
    this.retrieveClicked = false;
    this.reqdDocsDropdowns = false;
    this.editHeaders = [];
    switch (val) {
      case 'E_SYSTEM_PARAMETER':
        this.headers = SystemParameterHeaders;
        this.getAllTableData = true;
        break;
      case 'E_PERMIT_TYPE_CODE':
        this.headers = PermitTypeURLHeaders;
        this.allowAddNew=false;
        this.getAllTableData = true;
        break;
      case 'E_DOCUMENT_TYPE':
        this.headers = DocumentTypeHeaders;
        this.getAllTableData = true;
        break;
      case 'E_DOCUMENT_SUB_TYPE':
        this.headers = DocumentSubTypeHeaders;
        this.getAllTableData = true;
        break;
      case 'E_DOCUMENT_TITLE':
        this.headers = DocumentTitleHeaders;
        break;
      case 'E_DOCUMENT_SUB_TYPE_TITLE':
        this.headers = DocumentSubTypeTitleTableHeaders;
        this.getAllTableData = true;
        break;
      case 'E_MESSAGE':
        this.headers = MessagesHeaders;
        this.getAllTableData = true;
        break;
      case 'E_PERMIT_CATEGORY':
        this.headers = PermitCategoryHeaders;
        break;
      case 'E_SW_FACILITY_TYPE':
        this.headers = SWFacilityTypeHeaders;
        this.getAllTableData = true;
        break;
      case 'E_SW_FACILITY_SUB_TYPE':
        this.headers = SWFacilitySubTypeHeaders;
        this.getAllTableData = true;
        break;
      case 'E_GIS_LAYER_CONFIG':
        this.headers = GISLayerHeaders;
        this.getAllTableData = true;
        break;
      case 'E_INVOICE_FEE_TYPE':
        this.headers = InvoiceFeeHeaders;
        this.getAllTableData = true;
        break;
      case 'E_REQUIRED_DOC_FOR_FAC_TYPE':
        this.headers = ReqdDocFacilityTypeHeaders;
        this.getAllTableData = true;
        this.reqdDocsDropdowns = true;
        this.getReqdSwFacilities();
        break;
      case 'E_REQUIRED_DOC_FOR_NAT_GP':
        this.headers = ReqdDocNaturalResourcesHeaders;
        this.getAllTableData = true;
        break;
      case 'E_REQUIRED_DOC_FOR_PERMIT_TYPE':
        this.headers = ReqdDocPermitTypeHeaders;
        this.getAllTableData = true;
        this.reqdDocsDropdowns = true;
        this.getReqdPermitTypes();
        break;
      case 'E_REQUIRED_DOC_FOR_SEQR':
        this.headers = ReqdDocSeqrHeaders;
        this.getAllTableData = true;
        break;
      case 'E_REQUIRED_DOC_FOR_SHPA':
        this.headers = ReqdDocShpaHeaders;
        this.getAllTableData = true;
        break;
      case 'E_REQUIRED_DOC_FOR_SPATIAL_INQ':
        this.headers = ReqdDocGiHeaders;
        this.getAllTableData = true;
        break;
      case 'E_TRANSACTION_TYPE_RULE':
        this.headers = TransTypeRuleHeaders;
        this.getAllTableData = true;
        this.reqdDocsDropdowns = true;
        this.getReqdPermitTypes();
        break;
    }
    console.log(this.headers);
    this.tableData = [];
    this.columns.clear();
    this.columns.reset();
    this.onPopupOkClicked = false;
    if(!this.reqdDocsDropdowns)
      this.getAppropriateTableData(val);
    else
      this.initReqdDocsForm();
    let addNObj: any = {};
    this.headers[0].addNewHeaders.forEach((e: any, i: number) => {
      if(e.columnType !== 'text') {

        if(e.columnTitle === 'invoiceFee') {
          addNObj[e.columnTitle] = this.formBuilder.control('', [Validators.required,
             Validators.pattern(/[^0]+/)]);
        }

        else if(e.optional) {
          if(e.columnType === 'checkbox') {
            addNObj[e.columnTitle] = this.formBuilder.control(false);
          }
          else if(e.columnTitle === 'chgOriginalProjectInd') {
            addNObj[e.columnTitle] = this.formBuilder.control('0');
          }
          else {
            addNObj[e.columnTitle] = this.formBuilder.control({value: '', disabled: true});
          }
        }
        else {
          addNObj[e.columnTitle] = this.formBuilder.control('', [Validators.required]);
        }
      }

      if (e.tableName) this.getAppropriateTableDataforAdd(e.tableName, i);
    });
    this.headers.forEach((e: any, i: number) => {
      if(e.tableName) this.getTableDropdownList(e.tableName, i);
    })
    this.addNewHeaders = this.headers[0].addNewHeaders;
    if(this.headers[0].hasRadioButtons) {
      this.radioHeaders.forEach((radioLabel: any) => {
        addNObj[radioLabel.columnTitle] = this.formBuilder.control('', [Validators.required]);
      })
    }
    console.log(addNObj, 'columnTitle');
    this.addNewForm = this.formBuilder.group(addNObj);
    if(!this.getAllTableData)
      this.createColumn();
  }

  onSwFacTypeChange() {
    if(!this.changesMade()) {
      this.changeSwFacType();
    }
    else {
      this.pendingChangesAction = 'swFacilityType';
      this.modalReference = this.pendingPopup.open();
    }
  }

  changeSwFacType() {
    this.tableData = [];
    this.prevSwFacilityType = this.reqdDocsForm.controls.swFacilityType.value;
    this.reqdSwFacilityTypes.forEach((facilityType: any) => {
      this.reqdDocsForm.controls.swFacilitySubType.setValue(null);
      if(facilityType.swFacilityTypeId.toString() === this.reqdDocsForm.controls.swFacilityType.value) {
        this.reqdSwFacilitySubTypes = [...facilityType.facilitySubTypes];
      }
      if(this.reqdSwFacilitySubTypes.length) {
        this.reqdDocsForm.controls.swFacilitySubType.setValidators(Validators.required);
        this.reqdDocsForm.controls.swFacilitySubType.enable();
      }
      else {
        this.reqdDocsForm.controls.swFacilitySubType.clearValidators();
        this.reqdDocsForm.controls.swFacilitySubType.disable();
      }
      this.reqdDocsForm.controls.swFacilitySubType.updateValueAndValidity();
    });

  }

  onSwFacSubTypeChange() {
    if(!this.changesMade()) {
      this.changeSwFacSubType();
    }
    else {
      this.pendingChangesAction = 'swFacilitySubType';
      this.modalReference = this.pendingPopup.open();
    }
  }

  changeSwFacSubType() {
    this.tableData = [];
    this.prevSwFacilitySubType = this.reqdDocsForm.controls.swFacilitySubType.value;
  }

  onPermitTypeCodeChange() {
    if(!this.changesMade()) {
      this.changePermitTypeCode();
    }
    else {
      this.pendingChangesAction = 'permitTypeCode';
      this.modalReference = this.pendingPopup.open();
    }
  }

  changePermitTypeCode() {
    this.tableData = [];
    this.prevPermitTypeCode = this.reqdDocsForm.controls.permitTypeCode.value;
  }

  onPermitTypeGroupingChange() {
    if(!this.changesMade()) {
      this.changePermitTypeGrouping();
    }
    else {
      this.pendingChangesAction = 'permitTypeGrouping';
      this.modalReference = this.pendingPopup.open();
    }
  }

  changePermitTypeGrouping() {
    this.tableData = [];
    this.prevPermitTypeGrouping = this.reqdDocsForm.controls.permitTypeGrouping.value;
  }

  initReqdDocsForm() {
    let reqdDocsObj: any = {};
    if(this.maintenanceForm.getRawValue().tableName === 'E_REQUIRED_DOC_FOR_PERMIT_TYPE') {
      reqdDocsObj['permitTypeCode'] = this.formBuilder.control(null, [Validators.required]);
    }
    else if(this.maintenanceForm.getRawValue().tableName === 'E_TRANSACTION_TYPE_RULE') {
      reqdDocsObj['permitTypeGrouping'] = this.formBuilder.control(null, [Validators.required]);
    }
    else {
      reqdDocsObj['swFacilityType'] = this.formBuilder.control(null, [Validators.required]);
      reqdDocsObj['swFacilitySubType'] = this.formBuilder.control({value: null, disabled: true});
    }
    this.reqdDocsForm = this.formBuilder.group(reqdDocsObj);
  }

  retrieveReqdDocs() {
    this.retrieveClicked = true;
    if(this.reqdDocsForm.invalid) {
      return;
    }
    let formVal = this.reqdDocsForm.getRawValue();
    if(this.maintenanceForm.getRawValue().tableName === 'E_TRANSACTION_TYPE_RULE') {
      let payload = {
        tableName: this.maintenanceForm.getRawValue().tableName,
        keyValues: {
          permitSubCategory: formVal.permitTypeGrouping
        }
      }
      this.utils.emitLoadingEmitter(true);
      this.codeTableService.getTransactionTableData(payload).then((res: any) => {
        console.log(res);
        this.retrieveClicked = false;
        this.renderTableData(res);
        this.utils.emitLoadingEmitter(false);
      }).catch((err: any) => {
        console.log(err);
        this.utils.emitLoadingEmitter(false);
      });
      return;
    }
    let payload = {
      tableName: this.maintenanceForm.getRawValue().tableName,
      keyValues: {
        permitTypeCode: formVal.permitTypeCode ? formVal.permitTypeCode : null,
        swFacTypeId: formVal.swFacilityType ? formVal.swFacilityType : null,
        swFacSubTypeId: formVal.swFacilitySubType ? formVal.swFacilitySubType : null
      }
    }
    this.utils.emitLoadingEmitter(true);
    this.codeTableService.getReqdDocumentsTableData(payload).then((res: any) => {
      console.log(res);
      this.retrieveClicked = false;
      this.renderTableData(res);
      this.utils.emitLoadingEmitter(false);
    }).catch((err: any) => {
      console.log(err);
      this.utils.emitLoadingEmitter(false);
    });
  }

  getAppropriateTableData(tableName: any) {
    if(this.maintenanceForm.controls.category.value === 'Supporting Documents') {
      let payload = {
        tableName: this.maintenanceForm.getRawValue().tableName,
        keyValues: {
          permitSubCategory: null,
          swFacTypeId: null,
          swFacSubTypeId: null
        }
      }
      this.utils.emitLoadingEmitter(true);
      this.codeTableService.getReqdDocumentsTableData(payload).then((res: any) => {
        this.utils.emitLoadingEmitter(false);
        console.log(res);
        this.retrieveClicked = false;
        this.renderTableData(res);
      }).catch((err: any) => {
        this.utils.emitLoadingEmitter(false);
        console.log(err);
      });
      return;
    }
    this.utils.emitLoadingEmitter(true);
    this.codeTableService
      .getSelectedTableData(tableName)
      .subscribe((res: any) => {
        this.utils.emitLoadingEmitter(false);
        this.showServerError = false;
        console.log(res);
        if(!this.getAllTableData)
          this.sysParameterNames = res || [];
        else
          this.renderTableData(res);
      }, (err: any) => {
        this.showServerError = true;
        this.serverErrorMessage = err.error;
        this.utils.emitLoadingEmitter(false);
        throw err;
      });
  }

  renderTableData(rows: any) {
    let permitTypeCodes = new Set<any>();
    if(this.maintenanceForm.getRawValue().tableName !== 'E_GIS_LAYER_CONFIG' &&
    this.maintenanceForm.getRawValue().tableName !== 'E_TRANSACTION_TYPE_RULE' &&
    this.maintenanceForm.getRawValue().category !== 'Supporting Documents'&&
    this.maintenanceForm.getRawValue().tableName!=='E_PERMIT_TYPE_CODE') {

      rows.sort((a: any, b: any) => {
        if(a.documentSubTypeTitleId) {
          if(a.documentTypeDesc === b.documentTypeDesc) {
            if(!a.documentSubTypeDesc && b.documentSubTypeDesc) return -1;
            if(a.documentSubTypeDesc && !b.documentSubTypeDesc) return 1;
            if((!a.documentSubTypeDesc && !b.documentSubTypeDesc) ||
              (a.documentSubTypeDesc === b.documentSubTypeDesc)) {
                return a.documentTitle.toUpperCase() > b.documentTitle.toUpperCase() ? 1 : -1;
            }
            return a.documentSubTypeDesc.toUpperCase() > b.documentSubTypeDesc.toUpperCase() ? 1 : -1;
          }
          return a.documentTypeDesc.toUpperCase() > b.documentTypeDesc.toUpperCase() ? 1 : -1;
        }
        if(a.messageCode) {
          return a.messageCode.toUpperCase() > b.messageCode.toUpperCase() ? 1 : -1;
        }
        if(a.urlId) {
          return a.urlId.toUpperCase() > b.urlId.toUpperCase() ? 1 : -1;
        }
        if(a.facilityType) {
          if(a.facilityType === b.facilityType && a.facilitySubType) {
            return a.facilitySubType.toUpperCase() > b.facilitySubType.toUpperCase() ? 1 : -1;
          }
          return a.facilityType.toUpperCase() > b.facilityType.toUpperCase() ? 1 : -1;
        }
        if(a.invoiceFeeType) {
          if(a.permitTypeCode === b.permitTypeCode) {
            return a.invoiceFeeType.toUpperCase() > b.invoiceFeeType.toUpperCase() ? 1 : -1;
          }
          return a.permitTypeCode.toUpperCase() > b.permitTypeCode.toUpperCase() ? 1 : -1;
        }
        return a.description.toUpperCase() > b.description.toUpperCase() ? 1 : -1;
      });
    }

    rows.forEach((obj: any) => {
      if(this.maintenanceForm.getRawValue().tableName === 'E_REQUIRED_DOC_FOR_FAC_TYPE') {
        obj.facilityTypeId = this.reqdDocsForm.controls.swFacilityType.value;
        obj.facilitySubTypeId = this.reqdDocsForm.controls.swFacilitySubType.value ?
          this.reqdDocsForm.controls.swFacilitySubType.value : null;
      }
      if(this.maintenanceForm.getRawValue().tableName === 'E_REQUIRED_DOC_FOR_PERMIT_TYPE') {
        obj.permitTypeCode = this.reqdDocsForm.controls.permitTypeCode.value;
      }
      if(this.maintenanceForm.getRawValue().tableName === 'E_TRANSACTION_TYPE_RULE') {
        permitTypeCodes.add(obj.permitTypeCode ? obj.permitTypeCode : '');
      }
      if(obj.activeInd == null) {
        obj.activeInd = '0';
      }
      if(obj.activeInd) {
        obj.active = obj.activeInd.toString() === '1' ? true : false;
      }
      if(obj.reqdDocInd) {
        obj.reqdDoc = obj.reqdDocInd.toString() === '1' ? true : false;
      }
      if(obj.availToDepInd) {
        obj.depOnly = obj.availToDepInd === '1' ? true : false;
      }
      if(obj.messageCode || obj.urlId) {
        obj.updatedValue = '';
      }
      if(obj.orderInd == null) {
        obj.orderInd = '';
        obj.order = '';
      }
      else {
        obj.order = obj.orderInd;
      }
      if(obj.refLink){
        obj.updatedURLLinkValue = obj.refLink
      }
      if(obj.layerUrl) {
        obj.updatedLayerUrl = obj.layerUrl;
      }
      if(obj.layerType) {
        obj.layerTypeVal = obj.layerType;
      }
      if(obj.facilitySubType == null) {
        obj.facilitySubType = '';
      }
      if(obj.userSelNewInd) {
        obj.userSelNew = obj.userSelNewInd.toString() === '1' ? true : false;
      }
      if(obj.userSelModInd) {
        obj.userSelMod = obj.userSelModInd.toString() === '1' ? true : false;
      }
      if(obj.userSelExtInd) {
        obj.userSelExt = obj.userSelExtInd.toString() === '1' ? true : false;
      }
      if(obj.userSelTransferInd) {
        obj.userSelTransfer = obj.userSelTransferInd.toString() === '1' ? true : false;
      }
      if(obj.userSelRenInd) {
        obj.userSelRen = obj.userSelRenInd.toString() === '1' ? true : false;
      }
      if(obj.chgOriginalProjectInd) {
        obj.chgOriginalProject = obj.chgOriginalProjectInd.toString() === '1' ? true : false;
      }
      if(obj.modExtFormInd) {
        obj.modExtForm = obj.modExtFormInd.toString() === '1' ? true : false;
      }
    });
    if(permitTypeCodes.size) {
      this.headers[0].filtersList = Array.from(permitTypeCodes)
            .sort((a: any, b: any) => {
              if (a < b) return -1;
              return 1;
            })
            .map((str: string) => {
              return { label: str, value: str };
      });
    }
    this.tableData = [...rows];
    console.log(this.tableData);
  }

  getFilterList(filterList: any[]) {
    // console.log('get filter list', filterList);

    if (filterList && filterList.length > 0) {
      return filterList.filter((obj: any) => obj.value && obj.label);
    }
    return [];
  }

  resetFilters() {
    this.filterValues = [];
    this.dataTable.reset();
  }

  editClicked(row: any) {
    this.editRow = row;
    if(this.changesMade()) {
      this.pendingChangesAction = 'edit';
      this.modalReference = this.pendingPopup.open();
    }
    else {
      this.openEditPopup();
    }
  }

  openEditPopup() {
    this.showPopupServerError=false;
    this.showRegulationCodeError = false;
    this.swRegulationCode = '';
    const row = this.editRow;
    let editObj = this.headers[0].editObj;
    let editNObj: any = {};
    console.log(this.addNewHeaders);
    this.headers[0].editHeaders.forEach((e: any, i: number) => {
      if(this.addNewHeaders[i].tableData) {
        if(e.columnTitle === 'documentSubTypeId'
          && this.maintenanceForm.getRawValue().tableName === 'E_DOCUMENT_SUB_TYPE_TITLE') {
            e.tableData = [...this.docSubTypesData];
        }
        else if(e.columnTitle === 'facilitySubType' &&
          this.maintenanceForm.getRawValue().tableName === 'E_REQUIRED_DOC_FOR_FAC_TYPE') {
            e.tableData = [...this.facilitySubTypesData];
          }
        else {

          e.tableData = [...this.addNewHeaders[i].tableData];
        }
      }
      if(e.columnType !== 'text') {
        if(!e.disableOnEdit) {
          if(e.columnTitle === 'invoiceFee') {
            editNObj[e.columnTitle] = new FormControl(row[editObj[e.columnTitle]], [Validators.required,
              Validators.pattern(/[^0]+/)]);
          }
          else if(e.optional) {
            if(e.columnTitle === 'chgOriginalProjectInd') {
              editNObj[e.columnTitle] =  new FormControl(row[editObj[e.columnTitle]].toString());
            }
            else {
              editNObj[e.columnTitle] =  new FormControl(row[editObj[e.columnTitle]]);
            }
          }
          else {
            editNObj[e.columnTitle] =  new FormControl(row[editObj[e.columnTitle]], [Validators.required]);
          }
        }
        else {
          editNObj[e.columnTitle] =  new FormControl({value: row[editObj[e.columnTitle]], disabled: true },
            [Validators.required]);
        }
      }
    });
    if(this.headers[0].hasRadioButtons) {
      this.radioHeaders.forEach((radioLabel: any) => {
        editNObj[radioLabel.columnTitle] = this.formBuilder.control(row[radioLabel.columnTitle], [Validators.required]);
      })
    }
    this.editHeaders = this.headers[0].editHeaders;
    console.log(this.editHeaders);
    this.editForm = this.formBuilder.group(editNObj);
    this.editPopup.open('medium');
    this.editPopupOpen.next(true);
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

  // onTableNameChanged(val: any) {
  //   this.rows.clear();
  //   this.showServerError=false;
  //   let objArray = this.codeTableData[this.currentCategory].filter(
  //     (obj: any) => obj.tableName == val
  //   );
  //   this.selectedObject = objArray[0];
  //   console.log('here', val, this.tableNames);
  //   console.log(this.currentCategory);
  //   if (this.currentCategory == this.documentsString) {
  //     switch (val) {
  //       case 'System Parameter':
  //         this.headers = GenericDocumentTableHeaders;
  //         break;
  //       case 'Document Type':
  //         this.headers = GenericDocumentTableHeaders;
  //         break;
  //       case 'Document Sub Type':
  //         this.headers = DocumentSubTypeTableHeaders;
  //         break;
  //       case 'Document Title':
  //         this.headers = DocumentTitleTableHeaders;
  //         break;
  //       case 'Document Sub Type Title':
  //         this.headers = DocumentSubTypeTitleTableHeaders;
  //     }
  //   } else if (this.currentCategory == this.swFacilityTypesString) {
  //     switch (val) {
  //       case 'SW Facility Type':
  //         this.headers = SWFacilityTypeHeaders;
  //         break;
  //       case 'SW Facility Sub Type':
  //         this.headers = SWFacilitySubTypeHeaders;
  //         break;
  //     }
  //   } else if (this.currentCategory == this.errorMessageString) {
  //     this.headers = MessageHeaders;
  //   }
  //   this.createRow();
  // }

  setTableNameList(category: string) {
    this.tableNames = this.categoriesObj[category];
    switch (category) {
      case 'System Parameters':
        this.currentCategory = this.systemParamterString;
        break;
      case 'Documents':
        this.currentCategory = this.documentsString;
        break;
      case 'Required Documents':
        this.currentCategory = this.reqdDocsString;
        break;
      case 'Error Message':
        this.currentCategory = this.errorMessageString;
        break;
      case 'SW Facility Types':
        this.currentCategory = this.swFacilityTypesString;
        break;
      case 'GIS Layers':
        this.currentCategory = this.gisString;
        break;
      case 'Permit Type Fees':
        this.currentCategory = this.permitTypeFeesString;
        break;
      case 'Transaction Types':
        this.currentCategory = this.transactionTypeString;
        break;
      case 'Links':
        this.currentCategory = this.linksString;
        break;
    }
  }
 duplicateError: boolean = false;

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

 checkSWRegulationCodeError(typeRegulationCode: any, subTypeRegulationCode: any) {
    if(this.maintenanceForm.getRawValue().tableName !== 'E_SW_FACILITY_SUB_TYPE') {
      return false;
    }
    if(typeRegulationCode.length >= subTypeRegulationCode.length) {
      this.showRegulationCodeError = true;
      return true;
    }
    for(let i = 0; i < typeRegulationCode.length; i++) {
      if(typeRegulationCode[i] !== subTypeRegulationCode[i]) {
        this.showRegulationCodeError = true;
        return true;
      }
    }
    this.showRegulationCodeError = false;
    return false;
 }

 onSaveClicked() {
    if(this.maintenanceForm.valid &&
      this.maintenanceForm.getRawValue().tableName === 'E_PERMIT_CATEGORY') {
        let changedValue = false;
        this.maintenanceForm.getRawValue().columns.forEach((row: any) => {
          if(row.existingValue !== row.updatedValue) {
            changedValue = true;
          }
        });
      if(changedValue) {
        this.modalReference = this.warningPopup.open('alert');
        this.warningPopupOpen.next(true);
      }
      else {
        this.saveTable();
      }
    }
    else {
      this.saveTable();
    }
 }

  saveTable() {
    console.log('here is the form', this.maintenanceForm);
    let sendObj = this.headers[0].sendObj;
    let checkboxes: any[] = this.headers[0].checkboxes || [];
    this.formSubmitted = true;
    if(this.getAllTableData) {
      // console.log(this.tableData);
      if(this.maintenanceForm.getRawValue().tableName === 'E_GIS_LAYER_CONFIG') {
        let rows: any[] = [];
        this.tableData.forEach((row: any) => {
          let obj: any = {};
          Object.keys(obj).forEach((e: any) => {
            if (typeof obj[e] == 'string')
            obj[e] = obj[e].trim();
          });
          Object.keys(sendObj).forEach((f: any) => {
            if (checkboxes.includes(sendObj[f])) {
              obj[f] = row[sendObj[f]];
              obj[f] = obj[f] == true ? '1' : '0';
            } else {
              obj[f] = row[sendObj[f]];
            }
            });
            rows.push(obj);
        });
        let payload = {
          tableName: this.maintenanceForm.getRawValue().tableName,
          keyValues: rows
        };
        console.log("Save Table",payload);
        this.utils.emitLoadingEmitter(true);
        this.codeTableService.updateTable(payload).then((res: any) => {
          this.showServerError = false;
          this.utils.emitLoadingEmitter(false);
          this.changeTableName(payload.tableName);
          this.modalReference = this.successPopup.open();
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = error.error;
          this.showServerError = true;
          throw error;
        });
        return;
      }
      let updatedValues: any[] = [];
      this.tableData.forEach((row: any) => {
        if(this.rowChanged(row))
            {
            let obj: any = {};
            Object.keys(obj).forEach((e: any) => {
              if (typeof obj[e] == 'string')
              obj[e] = obj[e].trim();
            });
          Object.keys(sendObj).forEach((f: any) => {
            if (checkboxes.includes(sendObj[f])) {
              obj[f] = row[sendObj[f]];
              obj[f] = obj[f] == true ? '1' : '0';
            } else if (row[sendObj[f]] || (f === 'swFacilitySubTypeId')) {
              obj[f] = row[sendObj[f].trim()];
            } else {
              obj[f] = {...row['originalObj'],a:'b'}[sendObj[f]] || '';
            }
            });
            updatedValues.push(obj);
          }
        });

      if(updatedValues.length) {
        let payload = {
          tableName: this.maintenanceForm.getRawValue().tableName !== 'E_PERMIT_TYPE_CODE' ? this.maintenanceForm.getRawValue().tableName : this.maintenanceForm.getRawValue().tableName + '_reflink',
          keyValues: updatedValues
        }
        console.log("Payloads",payload.keyValues);
        this.utils.emitLoadingEmitter(true);
        if(this.maintenanceForm.getRawValue().category === 'Supporting Documents') {
          this.codeTableService.updateReqdDocumentsTable(payload).then((res: any) => {
            this.showServerError = false;
            this.utils.emitLoadingEmitter(false);
            if(this.reqdDocsDropdowns) {
              this.retrieveReqdDocs();
            }
            else {
              this.changeTableName(payload.tableName);
            }
            this.modalReference = this.successPopup.open();
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = error.error;
            this.showServerError = true;
            throw error;
          });
          return;
        }
        if(payload.tableName === 'E_TRANSACTION_TYPE_RULE') {
          this.codeTableService.updateTransactionTable(payload).then((res: any) => {
            this.showServerError = false;
            this.utils.emitLoadingEmitter(false);
            this.retrieveReqdDocs();
            this.modalReference = this.successPopup.open();
          },
          (error: any) => {
            this.utils.emitLoadingEmitter(false);
            this.serverErrorMessage = error.error;
            this.showServerError = true;
            throw error;
          });
          return;
        }
        this.codeTableService.updateTable(payload).then((res: any) => {
          this.showServerError = false;
          this.utils.emitLoadingEmitter(false);
          this.changeTableName(payload.tableName == 'E_PERMIT_TYPE_CODE_reflink' ? 'E_PERMIT_TYPE_CODE' : payload.tableName);
          this.modalReference = this.successPopup.open();
        },
        (error: any) => {
          this.utils.emitLoadingEmitter(false);
          this.serverErrorMessage = error.error;
          this.showServerError = true;
          throw error;
        });
      }
      return;
    }

    if (this.maintenanceForm.valid) {
      // this.duplicateError =false;
      // let columns: any[] = this.maintenanceForm.getRawValue().columns;
      // for (let index = 0; index < columns.length; index++) {
      //    let ele = columns[index];
      //    let searchColumns = JSON.parse(JSON.stringify(columns));
      //    searchColumns.splice(index, 1);
      //    const i = searchColumns.findIndex((e: any) => e[this.headers[0].columnTitle] == ele[this.headers[0].columnTitle]);
      //    if (i != -1) {
      //     this.duplicateError =true;
      //     // break;
      //     let group: any = this.columns.at(index);
      //     group.controls[this.headers[0].columnTitle].setErrors({duplicate: true});
      //    }
      // }
      this.duplicateError = this.checkduplicateError();
      if (this.duplicateError == true) {

        return;
      }

      let body = {
        tableName: this.maintenanceForm.getRawValue().tableName,
        keyValues: this.maintenanceForm.getRawValue().columns.map((e: any) => {
          let obj: any = {};
          Object.keys(obj).forEach((e: any) => {
            if (typeof obj[e] == 'string')
            obj[e] = obj[e].trim();
          });
          Object.keys(sendObj).forEach((f: any) => {
            if (checkboxes.includes(sendObj[f])) {
              obj[f] = e[sendObj[f]];
              obj[f] = obj[f] == true ? '1' : '0';
            } else if (e[sendObj[f]]) {
              obj[f] = e[sendObj[f]].trim();
            } else {
              obj[f] = e['originalObj'][sendObj[f]];
            }
            // if (typeof obj[f] == "boolean") {
            //   obj[f] = obj[f] == true ? '1' : '0';
            // }
          });
          return obj;
        }),
      };
      console.log("Save body", body)
      this.codeTableService.updateTable(body).then((res: any) => {
        this.changeTableName(body.tableName);
        this.modalReference = this.successPopup.open();
      },
      (error: any) => {
        this.serverErrorMessage = error.error;
        this.showServerError = true;
        throw error;
      });
    } else {
      this.maintenanceForm.markAllAsTouched();
      this.showServerError = true;
    }
  }

  onTableDropdownChange(ev: any, item: any) {
    if(this.maintenanceForm.getRawValue().tableName === 'E_GIS_LAYER_CONFIG') {
      item.layerTypeVal = ev.target.value;
    }
  }

  onTableCheckboxChange(row: any) {
    if(this.maintenanceForm.getRawValue().tableName === 'E_GIS_LAYER_CONFIG') {
      if(!row.active) {
        row.order = '';
      }
    }
  }

  onAddClicked() {
    if(this.changesMade()) {
      this.pendingChangesAction = 'add';
      this.modalReference = this.pendingPopup.open();
    }
    else {
      this.openAddPopup();
    }
  }

  openAddPopup() {
    this.showPopupServerError=false;
    this.showRegulationCodeError = false;
    this.swRegulationCode = '';
    this.addPopup.open('medium');
  }


  onAddOkClicked() {
    console.log('here is the form', this.maintenanceForm);
    console.log(this.addNewForm);
    let sendObj = this.addNewHeaders[0].sendObj;
    let formValue = this.addNewForm.getRawValue();
    let checkboxes: any[] = this.headers[0].checkboxes || [];
    this.onPopupOkClicked = true;
    let regulationCodeError = false;
    if (!this.addNewForm.valid) {
      return;
    }
    if(this.maintenanceForm.getRawValue().tableName === 'E_TRANSACTION_TYPE_RULE' &&
      !formValue.userSelNew && !formValue.userSelMod && !formValue.userSelExt &&
      !formValue.userSelTransfer && !formValue.userSelRen) {
      return;
    }
    if(this.maintenanceForm.getRawValue().tableName === 'E_SW_FACILITY_SUB_TYPE') {
      regulationCodeError = this.checkSWRegulationCodeError(
        this.swRegulationCode,
        this.addNewForm.controls.subTypeRegulationCode.value);
    }
    if(regulationCodeError) {
      return;
    }
    let obj: any = {};
    Object.keys(sendObj).forEach((f: any) => {
      if (formValue[sendObj[f]] || checkboxes.includes(sendObj[f])) {
        obj[f] = formValue[sendObj[f]];
        if(checkboxes.includes(sendObj[f])) {
          obj[f] = obj[f] == true ? '1' : '0';
        }
      }
      if(f === 'newMessageCode' ||
        f === 'newInvoiceFee' ||
        (this.maintenanceForm.getRawValue().tableName === 'E_DOCUMENT_SUB_TYPE_TITLE'
        && f === 'activeInd')) {
        obj[f] = sendObj[f];
      }
      if (!obj[f]) obj[f] = null;
    });
    Object.keys(obj).forEach((e: any) => {
      if (typeof obj[e] == 'string')
      obj[e] = obj[e].trim();
    });
    console.log(obj);
    let body = {
      tableName: this.maintenanceForm.getRawValue().tableName,
      keyValues: this.sendAsArray ? [obj] : obj,
    };

    if(body.tableName === 'E_GIS_LAYER_CONFIG') {
      obj.activeInd = 1;
      let newTableData = [...this.tableData, obj];
      this.renderTableData(newTableData);
      this.closeAddPopup();
      return;
    }

    if(this.maintenanceForm.getRawValue().category === 'Supporting Documents') {
      this.codeTableService.updateReqdDocumentsTable(body).then((res: any) => {
        this.addPopup.close();
        this.changeTableName(body.tableName);
        this.modalReference = this.successPopup.open();
      },
      (err: any) => {
        this.showPopupServerError = true;
        this.popupServerErrorMessage = err.error;
        throw err;
      });
      return;
    }
    if(body.tableName === 'E_TRANSACTION_TYPE_RULE') {
      this.codeTableService.updateTransactionTable(body).then((res: any) => {
        this.addPopup.close();
        this.changeTableName(body.tableName);
        this.modalReference = this.successPopup.open();
      }, (err: any) => {
        this.showPopupServerError = true;
        this.popupServerErrorMessage = err.error;
        throw err;
      });
      return;
    }
    console.log("Add Save",body);
    this.codeTableService.updateTable(body).then((res: any) => {
      this.addPopup.close();
      this.changeTableName(body.tableName);
      this.modalReference = this.successPopup.open();
    }, (err: any) => {
      this.showPopupServerError = true;
      this.popupServerErrorMessage = err.error;
      throw err;
    });
  }

  onEditSaveClicked() {
    let sendObj = this.editHeaders[0].sendObj;
    this.editForm.updateValueAndValidity();
    let formValue = this.editForm.getRawValue();
    let checkboxes = this.headers[0].checkboxes;

    this.onPopupOkClicked = true;
    let regulationCodeError = false;

    console.log(this.editForm);
    if (!this.editForm.valid) {
      return;
    }

    if(this.maintenanceForm.getRawValue().tableName === 'E_TRANSACTION_TYPE_RULE' &&
      !formValue.userSelNew && !formValue.userSelMod && !formValue.userSelExt &&
      !formValue.userSelTransfer && !formValue.userSelRen) {
      return;
    }

    if(this.maintenanceForm.getRawValue().tableName === 'E_SW_FACILITY_SUB_TYPE') {
      regulationCodeError = this.checkSWRegulationCodeError(
        this.editRow.facilityTypeRegulationCode,
        this.editForm.controls.subTypeRegulationCode.value);
    }
    if(regulationCodeError) {
      return;
    }

    let obj: any = {};

    Object.keys(sendObj).forEach((f: any) => {
      if (formValue[sendObj[f]] || checkboxes.includes(sendObj[f])) {
        obj[f] = formValue[sendObj[f]];
        if(checkboxes && checkboxes.includes(sendObj[f])) {
          obj[f] = obj[f] == true ? '1' : '0';
        }
      }
      else {
        obj[f] = this.editRow[sendObj[f]];
      }
    });
    Object.keys(obj).forEach((e: any) => {
      if (typeof obj[e] == 'string')
      obj[e] = obj[e].trim();
    });
    let body = {
      tableName: this.maintenanceForm.getRawValue().tableName,
      keyValues: this.sendAsArray ? [obj] : obj,
    };
    console.log(body);
    if(this.maintenanceForm.getRawValue().category === 'Supporting Documents') {
      this.codeTableService.updateReqdDocumentsTable(body).then((res: any) => {
        this.showPopupServerError = false;
        this.closeEditPopup();
        if(this.reqdDocsDropdowns) {
          this.retrieveReqdDocs();
        }
        else {
          this.changeTableName(body.tableName);
        }
        this.modalReference = this.successPopup.open();
      },
      (err: any) => {
        this.showPopupServerError = true;
        this.popupServerErrorMessage = err.error;
        throw err;
      });
      return;
    }
    if(body.tableName === 'E_TRANSACTION_TYPE_RULE') {
      this.codeTableService.updateTransactionTable(body).then((res: any) => {
        this.showPopupServerError = false;
        this.closeEditPopup();
        this.retrieveReqdDocs();
        this.modalReference = this.successPopup.open();
      }, (err: any) => {
        this.showPopupServerError = true;
        this.popupServerErrorMessage = err.error;
        throw err;
      });
      return;
    }
    console.log("Edit Save",body )
    this.codeTableService.updateTable(body).then((res: any) => {
      this.showPopupServerError = false;
      this.closeEditPopup();
      this.changeTableName(body.tableName);
      this.modalReference = this.successPopup.open();
    }, (err: any) => {
      this.showPopupServerError = true;
      this.popupServerErrorMessage = err.error;
      throw err;
    });
  }

  closeAddPopup() {
    this.addPopup.close();
    this.onPopupOkClicked = false;
    let formObj = this.addNewForm.getRawValue();
    Object.keys(formObj).forEach((controlKey: any) => {
      formObj[controlKey] = '';
      if(controlKey === 'chgOriginalProjectInd') {
        formObj[controlKey] = '0';
      }
    });
    this.addNewForm.reset(formObj);
    if(this.maintenanceForm.getRawValue().tableName === 'E_DOCUMENT_SUB_TYPE_TITLE') {
      this.addNewForm.controls.documentSubTypeId.disable();
    }
  }

  closeEditPopup() {
    this.editPopup.close();
    this.editPopupOpen.next(false);
    this.onPopupOkClicked = false;
    this.editForm.reset();
  }

  onAddCancelClicked() {
    if (this.addNewForm.dirty) {
      this.pendingChangesAction = 'addCancel';
      this.modalReference = this.pendingPopup.open();
    } else {
      this.closeAddPopup();
    }
  }

  onEditCancelClicked() {
    if (this.editForm.dirty) {
      this.pendingChangesAction = 'editCancel';
      this.modalReference = this.pendingPopup.open();
    } else {
      this.closeEditPopup();
    }
  }

  goBack() {
    if(!this.onTableClicked){
    this.pendingPopup.close();
    this.onPopupOkClicked = false;
      setTimeout(() => {
          this.onAddCancelClicked();
      }, 500);
    }else if (this.onTableClicked) {
      this.rows.clear();
      this.addPopup.open('medium');
      this.onTableClicked=false;
      }


  }

  pendingOkClicked() {
    switch(this.pendingChangesAction) {
      case 'table':
        this.changeTableName(this.maintenanceForm.controls.tableName.value);
        break;
      case 'category':
        this.changeCategory(this.maintenanceForm.controls.category.value);
        break;
      case 'add':
        this.openAddPopup();
        break;
      case 'edit':
        this.openEditPopup();
        break;
      case 'addCancel':
        this.closeAddPopup();
        break;
      case 'editCancel':
        this.closeEditPopup();
        break;
      case 'swFacilityType':
        this.changeSwFacType();
        break;
      case 'swFacilitySubType':
        this.changeSwFacSubType();
        break;
      case 'permitTypeCode':
        this.changePermitTypeCode();
        break;
      case 'permitTypeGrouping':
        this.changePermitTypeGrouping();
        break;
      default:
        break;
    }
  }

  pendingCancelClicked() {
    switch(this.pendingChangesAction) {
      case 'table':
        this.maintenanceForm.controls.tableName.setValue(this.prevTableVal);
        this.maintenanceForm.controls.tableName.updateValueAndValidity();
        break;
      case 'category':
        this.maintenanceForm.controls.category.setValue(this.prevCategoryVal);
        this.maintenanceForm.controls.category.updateValueAndValidity();
        break;
      case 'swFacilityType':
        this.reqdDocsForm.controls.swFacilityType.setValue(this.prevSwFacilityType);
        this.reqdDocsForm.controls.swFacilityType.updateValueAndValidity();
        break;
      case 'swFacilitySubType':
        this.reqdDocsForm.controls.swFacilitySubType.setValue(this.prevSwFacilitySubType);
        this.reqdDocsForm.controls.swFacilitySubType.updateValueAndValidity();
        break;
      case 'permitTypeCode':
        this.reqdDocsForm.controls.permitTypeCode.setValue(this.prevPermitTypeCode);
        this.reqdDocsForm.controls.permitTypeCode.updateValueAndValidity();
        break;
      case 'permitTypeGrouping':
        this.reqdDocsForm.controls.permitTypeGrouping.setValue(this.prevPermitTypeGrouping);
        this.reqdDocsForm.controls.permitTypeGrouping.updateValueAndValidity();
        break;
      default:
        break;
    }
  }

  warningOkClicked() {
    this.warningCancelClicked();
    this.saveTable();
  }

  warningCancelClicked() {
    this.warningPopup.close();
    this.warningPopupOpen.next(false);
  }

  onCloseClicked() {
    this.router.navigate(['/dashboard']);
  }

  exportToExcel(selectedHeader:any) {
    let dataList: any[] = [];
    dataList = this.tableData.map((data) => {
      let temp: any = {};
      this.headers.forEach((header: any) => {
        console.log(header);
        temp[header.columnLabel] = header.columnType == "checkbox" ? data[header.columnTitle] ? data[header.columnTitle] : false : data[header.columnTitle];
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
      let excelFileName ='';
      if(this.prevTableVal =='E_TRANSACTION_TYPE_RULE'){
        excelFileName= this.convertedTableNames[this.prevTableVal] ,''+'',this.permitTypeGroupings;
        console.log("Excel File name in",excelFileName)
      }
      else{
        excelFileName= this.convertedTableNames[this.prevTableVal]
        console.log("Excel File name out",excelFileName)
      }

      this.saveAsExcelFile(excelBuffer, excelFileName);
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
      fileName + ' Export ' + new Date().getTime() + EXCEL_EXTENSION
    );
  }
}
