import {
  Component,
  ElementRef,
  HostListener,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import {
  FormControl,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import {
  ModalDismissReasons,
  NgbModal,
  NgbModalRef,
} from '@ng-bootstrap/ng-bootstrap';
import { CustomModalPopupComponent } from 'src/app/@shared/components/custom-modal-popup/custom-modal-popup.component';
import { ErrorService } from 'src/app/@shared/services/errorService';
import { KeywordMaintainService } from 'src/app/@shared/services/keyword-maintain.service';
import moment from 'moment';
import { filter } from 'rxjs/operators';
import { DatePipe } from '@angular/common';
import { FilterService } from 'primeng/api';
import FileSaver from 'file-saver';
import { isEmpty, uniqBy } from 'lodash';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { PendingChangesPopupComponent } from 'src/app/@shared/components/pending-changes-popup/pending-changes-popup.component';
import { CommonService } from 'src/app/@shared/services/commonService';
import { AuthService } from 'src/app/core/auth/auth.service';
export interface IKeywordCategoryList {
  keywordCategoryId: number;
  keywordCategory: string;
}
@Component({
  selector: 'app-keyword-maintenance',
  templateUrl: './keyword-maintenance.component.html',
  styleUrls: ['./keyword-maintenance.component.scss'],
})
export class KeywordMaintenanceComponent implements OnInit {
  disablepropety: boolean = false;
  searchForm!: UntypedFormGroup;
  keywordCategoryForm!: UntypedFormGroup;
  keyWordForm!: UntypedFormGroup;
  permitKeyWordForm: UntypedFormGroup;
  modalReference: any;
  showServerError = false;
  saveClicked = false;
  serverErrorMessage!: string;
  addCategoryClicked: boolean = false;
  addButtonDisabled: boolean = false;
  invalidCategoryForm: boolean = false;
  addKeywordClicked: boolean = false;
  addPermitClicked: boolean = false;
  showCategoryServerError: boolean = false;
  addCategoryConfig!: { title: string; showHeader: boolean };
  @ViewChild('addCategoryPopup')
  private addCategoryPopup!: CustomModalPopupComponent;
  addKeywordConfig!: { title: string; showHeader: boolean };
  @ViewChild('addKeywordPopup')
  private addKeywordPopup!: CustomModalPopupComponent;
  addpermitKeywordConfig!: { title: string; showHeader: boolean };
  @ViewChild('addPermitKeywordPopup')
  private addPermitKeywordPopup!: CustomModalPopupComponent;
  keywordDropdownList: IKeywordCategoryList[] = [];
  availableTransTypes: any[] = [];
  categoryList: any[] = [];
  categoryListSortedByKeyword: any[] = [];
  permitList: any[] = [];
  keywordList: any[] = [];
  keywordPermitList: any[] = [];
  filterValues: any[] = [];
  filtersList: any;
  permitFiltersList: any []=[];
  initialData= [...this.categoryList];
  @ViewChild('dataTable', { static: true }) dataTable!: Table;
  @ViewChild('permitTable', { static: true }) permitTable!: Table;
  subscriptions: Subscription[] = [];
  @ViewChild('pendingPopup', { static: true })
  confirmationModal!: PendingChangesPopupComponent;
  errorMsgObj: any = {};
  userRoles: any[] =[];
  selectedCategory: any;
  openedPermitForm: boolean = false;
  openedKeywordForm: boolean = false;
  keywordDataEmpty: boolean=false;
  isEditPopup: boolean = false;
  get dateError() {
    const startDate = this.keyWordForm?.get('startDate')?.value;
    const endDate = this.keyWordForm?.get('endDate')?.value;
    if (!startDate) {
      return (
        this.errorMsgObj?.START_DT_REQD ||
        'Error: Start Date is Required.'
      );
    }
    if (
      endDate &&
      moment(startDate, 'YYYY-MM-DD').isAfter(moment(endDate, 'YYYY-MM-DD'))
    ) {
      this.keyWordForm.get('endDate')?.setErrors({ invalid: true });
      return (
        this.errorMsgObj?.END_DATE_GT_START_DATE ||
        'Error: End Date must be greater than Start Date.'
      );
    }
    if (this.keyWordForm.get('endDate')?.errors?.invalid) {
      this.keyWordForm.get('endDate')?.setErrors(null);
    }
    return null;
  }

  get permitDateError() {
    const startDate = this.permitKeyWordForm?.get('startDate')?.value;
    const endDate = this.permitKeyWordForm?.get('endDate')?.value;
    if (!startDate) {
      return (
        this.errorMsgObj?.START_DT_REQD
      );
    }
    if (
      endDate &&
      moment(startDate, 'YYYY-MM-DD').isAfter(moment(endDate, 'YYYY-MM-DD'))
    ) {
      this.permitKeyWordForm.get('endDate')?.setErrors({ invalid: true });
      return (
        this.errorMsgObj?.END_DATE_GT_START_DATE ||   'Error: End Date must be greater than Start Date.'
      );
    }
    if (this.permitKeyWordForm.get('endDate')?.errors?.invalid) {
      this.permitKeyWordForm.get('endDate')?.setErrors(null);
    }
    return null;
  }

  constructor(
    private formBuilder: UntypedFormBuilder,
    private errorService: ErrorService,
    private datePipe: DatePipe,
    private filterService: FilterService,
    private modalService: NgbModal,
    private commonService: CommonService,
    private authService: AuthService,
    private keywordMaintenanceService: KeywordMaintainService
  ) {
    this.searchForm = this.formBuilder.group({
      searchItemText: new UntypedFormControl(' ', []),
      keywordCaegory: new UntypedFormControl(''),
    });

    this.keywordCategoryForm = this.formBuilder.group({
      keywordCategory: new FormControl('', [
        Validators.minLength(3),
        Validators.maxLength(30),
        Validators.required,
      ]),
      keywordCategoryId: new FormControl(null),
    });
    

    this.keyWordForm = this.formBuilder.group({
      keywordCategoryId: new FormControl('', [Validators.required]),
      keywordText: new FormControl('', [Validators.required]),
      startDate: new FormControl('', [Validators.required]),
      endDate: new FormControl(''),
      keywordId: new FormControl(''),
    });


    this.permitKeyWordForm = this.formBuilder.group({
      permitTypeCode: new FormControl('', [Validators.required]),
      keywordId: new FormControl('', [Validators.required]),
      keywordCategoryId: new FormControl('', [Validators.required]),
      startDate: new FormControl('', [Validators.required]),
      endDate: new FormControl(''),
      permitKeywordId: new FormControl(''),
    });
  }

  async getAllErrorMsgs() {
    this.commonService.emitErrorMessages.subscribe((val) => {
      if (val) this.errorMsgObj = this.commonService.getErrorMsgsObj();
    });
  }

  ngOnInit(): void {
    this.addCategoryConfig = {
      title: '',
      showHeader: false,
    };
    this.searchForm.controls['searchItemText'].valueChanges.subscribe((e: any) => {
         this.selectedCategory = '';
         if (e && e != ' ') {
          let index = this.keywordDropdownList.findIndex((f: any) => f.keywordCategoryId == e);
          if (index != -1) this.selectedCategory = this.keywordDropdownList[index].keywordCategory;
         }
    });
    this.addKeywordConfig = {
      title: '',
      showHeader: false,
    };

    this.addpermitKeywordConfig = {
      title: '',
      showHeader: false,
    };
    this.getAllErrorMsgs();
    this.loadKeywordCategoryDropdownList();
    this.loadKeywordData();
    this.loadKeywordPermitData();
    this.searchForm.controls['searchItemText'].valueChanges
      .pipe(filter((res: any) => res))
      .subscribe((res: any) => {
        console.log("Search text", res)
        this.loadKeywordData();
        this.loadKeywordPermitData(); 
             
      });
    this.loadTransTypes();
  }

  getCurrentUserRole() {
      this.authService.emitAuthInfo.subscribe((authInfo: any) => {
        if (authInfo === null) return;
        if (authInfo && !authInfo.isError) this.userRoles = authInfo.roles;
       
      })
  }

  get categoryIsAll() {

    if(this.searchForm.controls.searchItemText.value &&
      this.searchForm.controls.searchItemText.value !== ' ') {
        return false;
    }
    return true;
  }

  loadKeywordCategoryDropdownList(setValue?: any): void {
    this.addButtonDisabled = false;
    this.keywordMaintenanceService
      .loadKeywordDropDownList()
      .then((response: any) => {
        response.sort((a: any, b: any) => {
          return a.keywordCategory.toUpperCase() < b.keywordCategory.toUpperCase() ?
            -1 : 1;
        });
        this.keywordDropdownList = response;
        if(this.keywordDropdownList.length === 0){
          this.addButtonDisabled = true;
        }
        if(setValue) {
          // keywordCategory
          let index = this.keywordDropdownList.findIndex((e: any) => e.keywordCategory == setValue.keywordCategory);
          if (index != -1) this.searchForm.controls['searchItemText'].setValue(this.keywordDropdownList[index].keywordCategoryId);
        }
      })
      .catch((ex) => {
        this.serverErrorMessage = this.errorService.getServerMessage(ex);
        this.showCategoryServerError = true;
        throw ex;
      });
  }

  loadKeywordData() {
    this.keywordMaintenanceService
      .loadKeywordData(this.searchForm.controls['searchItemText'].value)
      .then((res: any) => {
        if (res) {
          console.log("Keyword table", res)
          if(isEmpty(res)){
            this.keywordDataEmpty = true;
          }else{
            this.keywordDataEmpty = false;
          }

          let categoryKeys = Array.from(Object.keys(res));
          categoryKeys.sort((a: any, b: any) => {
            return a.toUpperCase() < b.toUpperCase() ? -1 : 1;
          });
          
          let categories: any[] = [];
          let sortedCategories: any[] = [];
          categoryKeys.forEach((e: any) => {
            console.log(e);
            res[e].sort((a: any, b: any) => {
              if(a.keywordCategory.toUpperCase() > b.keywordCategory.toUpperCase()) {
                return 1;
              }
              else if((a.keywordCategory === b.keywordCategory) && 
                a.keywordText.toUpperCase() > b.keywordText.toUpperCase()) {
                  return 1;
              }
              return -1;
            });
            res[e][0].categoryText = e;
            categories = categories.concat(res[e]);
            sortedCategories = sortedCategories.concat(res[e]);
          });
          sortedCategories.sort((a: any, b: any) => {
            return a.keywordText.toUpperCase() < b.keywordText.toUpperCase() ? -1 : 1;
          });
          this.categoryList = categories;
          this.categoryListSortedByKeyword = sortedCategories;
          let filtersListValue = Array.from(categories)
          .sort((a: any, b: any) => {
            if (a.keywordCategory.toUpperCase() < b.keywordCategory.toUpperCase()) return -1;
            return 1;
          })
          .map((str: any) => {
            return { label: str.keywordCategory, value: str.keywordCategory };
          });              
          this.filtersList = uniqBy(
            filtersListValue,
            (value: any) => value.value
          );
        }
      })
      .catch((ex) => {
        this.serverErrorMessage = this.errorService.getServerMessage(ex);
        this.showServerError = true;
        throw ex;
      });
  }

  loadKeywordPermitData() {
    console.log('This method called ')
    this.keywordMaintenanceService
      .loadKeywordPermitType(this.searchForm.controls['searchItemText'].value)
      .then((res: any) => {
        res.sort((a: any, b: any) => {
          if(a.keywordText.toUpperCase() > b.keywordText.toUpperCase()) {
            return 1;
          }
          else if(a.keywordText === b.keywordText && a.permitTypeCode > b.permitTypeCode) {
            return 1;
          }
          return -1;
        });
        this.permitList = res;
          let permitFiltersListValue = Array.from(res)
          .sort((a: any, b: any) => {
            if (a.permitTypeCode < b.permitTypeCode) return -1;
            return 1;
          })
          .map((str: any) => {
            return { label: str.permitTypeCode, value: str.permitTypeCode };
          });              
          this.permitFiltersList = uniqBy(
            permitFiltersListValue,
            (value: any) => value.value
          );
      })
      .catch((ex) => {
        this.serverErrorMessage = this.errorService.getServerMessage(ex);
        this.showServerError = true;
        throw ex;
      });
  }

  onSearch() {}

  clearSearchForm() {}

  openAddCategory() {
    if(this.categoryIsAll) {
      this.isEditPopup = false;
      this.keywordCategoryForm.reset();
    }
    else {
      this.isEditPopup = true;
      this.addCategoryClicked = false;
      this.keywordCategoryForm.controls.keywordCategory.setValue(this.selectedCategory);
      this.keywordCategoryForm.controls.keywordCategoryId.setValue(this.searchForm.controls.searchItemText.value);
      this.keywordCategoryForm.updateValueAndValidity();
    }
    this.modalReference = this.addCategoryPopup.open('md');
  }

  onCategoryCancelClicked() {
    if(this.keywordCategoryForm.dirty) {
      this.confirmationModal.open();
    }
    else {
      this.closeModal();
    }
  }

  onKeywordTextCancelClicked() {
    if(this.keyWordForm.dirty) {
      this.confirmationModal.open()
    }
    else {
      this.closeModal();
      this.openedKeywordForm=false;
    }
  }

  onPermitTypeCancelClicked() {
    if(this.permitKeyWordForm.dirty) {
      this.confirmationModal.open();
    }
    else {
      this.closeModal();
      this.openedPermitForm=false;
    }
  }

  closeModal(e?: any) {
    this.modalService.dismissAll();
    this.showServerError=false;
  }

  addCategory() {
    this.addCategoryClicked = true;
    if (this.keywordCategoryForm.valid) {
      let apiCategoryData = this.keywordCategoryForm.getRawValue()
      apiCategoryData.keywordCategory=apiCategoryData.keywordCategory.trim();
      console.log("Category value",apiCategoryData)
      this.keywordMaintenanceService
        .createKeywordCategory(apiCategoryData)
        .subscribe(
          (response: any) => {
            console.log('Added Category', response)
            // handle success and reload grid data
            this.loadKeywordCategoryDropdownList(this.keywordCategoryForm.getRawValue());
            this.modalService.dismissAll();
            this.loadKeywordData();
            this.loadKeywordPermitData();
            this.addCategoryClicked = false;
            if(this.isEditPopup) {
              this.searchForm.controls.searchItemText.setValue(this.keywordCategoryForm.controls.keywordCategoryId.value);
              this.searchForm.updateValueAndValidity();
            }
            this.keywordCategoryForm.reset();
        },
          (error: any) => {
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          }
          );
    }
  }

  openAddKeyword(ev?: any) {
    if (ev) {
      this.isEditPopup = true;
      let data = JSON.parse(JSON.stringify(ev));
      data.startDate = this.datePipe.transform(
        ev.startDate,
        'yyyy-MM-dd'
      )
      data.endDate = this.datePipe.transform(
        ev.endDate,
        'yyyy-MM-dd'
      )
       this.keyWordForm.patchValue(data);
       this.keyWordForm.markAsPristine();
       this.keyWordForm.updateValueAndValidity();
      } 
    else {
      this.isEditPopup = false;
        let val = '' + this.searchForm.controls['searchItemText'].value;
        val = val.trim();
        this.keyWordForm = this.formBuilder.group({
        keywordCategoryId: new FormControl({value: val ? val : '', disabled: val? true : false}, [Validators.required]),
        keywordText: new FormControl('', [Validators.required]),
        startDate: new FormControl(this.datePipe.transform(new Date(), 'yyyy-MM-dd'), [Validators.required]),
        endDate: new FormControl(''),
        keywordId:new FormControl(''),
      });
     }
    this.addKeywordClicked = false;
    this.openedKeywordForm = true;
    this.modalReference = this.addKeywordPopup.open('md');
  }


  addKeyword() {
    this.addKeywordClicked = true;
    console.log("Click", this.addKeywordClicked, "valid", this.keyWordForm.valid)
    console.log(this.keyWordForm.value)
    console.log("Categroy",this.searchForm.controls['searchItemText'].value)
    console.log()
    // let val = this.searchForm.controls['searchItemText'].value;

   
    if (this.keyWordForm.valid) {

      let apiData = this.keyWordForm.getRawValue();

      apiData.startDate = this.datePipe.transform(
        apiData.startDate,
        'MM/dd/yyyy'
      );
      apiData.endDate = this.datePipe.transform(apiData.endDate, 'MM/dd/yyyy');
      apiData.keywordText=apiData.keywordText.trim();
      // if(!isEmpty(val)){
      //   apiData.keywordId =this.searchForm.controls['keywordCategoryId'].value;
      // }
      this.keywordMaintenanceService.addKeyword(apiData).then(
        (res: any) => {
          this.loadKeywordData();
          this.loadKeywordPermitData();
          this.modalService.dismissAll();
          this.openedKeywordForm = false;
        },
        (error: any) => {
          this.serverErrorMessage = this.errorService.getServerMessage(error);
          this.showServerError = true;
          throw error;
        }
      );
    }
  }

  openAddPermitKeyword(ev?: any) {
    if (ev) {
      this.isEditPopup = true;
      let data = JSON.parse(JSON.stringify(ev));
      data.startDate = this.datePipe.transform(
        ev.startDate,
        'yyyy-MM-dd'
      )
      data.endDate = this.datePipe.transform(
        ev.endDate,
        'yyyy-MM-dd'
      )
       this.permitKeyWordForm.patchValue(data);
       this.permitKeyWordForm.markAsPristine();
      this.permitKeyWordForm.updateValueAndValidity();
      } 
      else {
        this.isEditPopup = false;
        let val = '' + this.searchForm.controls['searchItemText'].value;
        val = val.trim();
        this.permitKeyWordForm = this.formBuilder.group({
          permitTypeCode: new FormControl('', [Validators.required]),
          //keywordText: new FormControl(''),
          keywordId: new FormControl('', [Validators.required]),
          keywordCategoryId: new FormControl({value: val ? val : '', disabled:  true}, [Validators.required]),
          startDate: new FormControl(this.datePipe.transform(new Date(), 'yyyy-MM-dd'), [Validators.required]),
          endDate: new FormControl(''),
          permitKeywordId:'',
        });
        this.permitKeyWordForm.controls['keywordId'].valueChanges.subscribe((res: any) => {
              if (!val) {
                if (res) {
                  const index = this.categoryList.findIndex((f: any) => f.keywordId == res);
                  if (index != -1) {
                    this.permitKeyWordForm.controls['keywordCategoryId'].setValue(this.categoryList[index].keywordCategoryId);
                  }
                } else {
                  this.permitKeyWordForm.controls['keywordCategoryId'].setValue('');
                }
              }
        });
    }
    this.addPermitClicked = false;
    this.openedPermitForm = true;
    this.modalReference = this.addPermitKeywordPopup.open('md');
  }

  loadTransTypes() {
    this.keywordMaintenanceService
      .getPermiTypes()
      .then((res: any) => {
        const omitTransTypes = ['DIM', 'DIR', 'DIS', 'DTN'];
        const allowedTransTypes = res.filter(
          (item: any) => omitTransTypes.indexOf(item.transTypeCode) == -1
        );
        this.availableTransTypes = res;
      })
      .catch((ex) => {
        this.serverErrorMessage = this.errorService.getServerMessage(ex);
        this.showServerError = true;
        throw ex;
      });
  }

  addPermitKeyword() {
    this.addPermitClicked = true;
    console.log("this.addPermitClicked", this.addPermitClicked ," valid", this.permitKeyWordForm.valid, this.permitKeyWordForm.value)
    if (this.permitKeyWordForm.valid) {
      console.log(this.permitKeyWordForm.value)
      let apiData = this.permitKeyWordForm.getRawValue();
      apiData.startDate = this.datePipe.transform(
        apiData.startDate,
        'MM/dd/yyyy'
      );
      apiData.endDate = this.datePipe.transform(apiData.endDate, 'MM/dd/yyyy');
      this.keywordMaintenanceService
        .addPermitType(apiData)
        .then(
          (res: any) => {
            this.loadKeywordPermitData();
            this.modalService.dismissAll();
            this.openedPermitForm = false;
          },
          (error: any) => {
            this.serverErrorMessage = this.errorService.getServerMessage(error);
            this.showServerError = true;
            throw error;
          }
        );
    }
  }

  getFormattedDate(dateString: any) {
    if (!dateString) {
      return;
    }
    return moment(dateString, 'YYYY-MM-DD').format('MM/DD/YYYY');
  }

  getDateFromText(text: string) {
    if (!text) return;
    return moment(text, 'MM-DD-YYYY').toDate();
  }

  resetFilters() {
    let blankArray: any = [];
    for (let i = 0; i < this.filterValues.length; i++) {
      blankArray.push('');
    }
    console.log("Filter list ", this.filterValues,"+",blankArray)
    this.filterValues = [];
    this.dataTable.reset();
    
  }

  resetPermitFilters() {
    let blankArray: any = [];
    for (let i = 0; i < this.filterValues.length; i++) {
      blankArray.push('');
    }
    this.filterValues = [];
    this.permitTable.reset();
    
  }


  filter(e: any) {}


  getFilterList(filterList: any[]) {
    if (filterList && filterList.length > 0) {
      return filterList.filter((obj: any) => obj.value && obj.label);
    }
    return [];
  }

  getPermitFilterList(filterList: any[]) {
    if (filterList && filterList.length > 0) {
      return filterList.filter((obj: any) => obj.value && obj.label);
    }
    return [];
  }
  

  // filterTable(e:any){
  //   console.log("filter table",e)
  //   if(e){
  //     this.categoryList = this.categoryList.filter((vt:any)=> vt.keywordCategory === e);
  //   }

  // }

  ngOnChanges(changes: any) {
    this.initialData=this.categoryList;
    if (this.categoryList.length) {
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

  exportToExcel() {
    let filteredVal = this.dataTable.filteredValue;
    let dataList: any[] = [];

    const headers = ['keywordCategory','keywordText','startDate','endDate'];
    if (filteredVal) {
      dataList = filteredVal.map((data) => {
        let temp: any = {};
        headers.forEach((header) => {
          temp[header] = data[header];
        });
        return temp;
      });
    } else {
      dataList = this.categoryList.map((data) => {
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
      this.saveAsExcelFile(excelBuffer, 'keyword table data');
    });
   }


   permitExportToExcel() {
    let filteredVal = this.permitTable.filteredValue;
    let dataList: any[] = [];

    const headers = ['permitTypeCode','keywordText','startDate','endDate'];
    if (filteredVal) {
      dataList = filteredVal.map((data) => {
        let temp: any = {};
        headers.forEach((header) => {
          temp[header] = data[header];
        });
        return temp;
      });
    } else {
      dataList = this.permitList.map((data) => {
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
      this.saveAsExcelFile(excelBuffer, 'permit keyword data table');
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
    // if(input){
    // this.dataList= this.initialData.reduce((result,obj)=>{
    //   for(const key of this.keysToSearch){
    //     if(Object.prototype.hasOwnProperty.call(obj,key)){
    //       const lowerCaseValue= String(obj[key]).toLowerCase();
    //       const lowercaseinput=input.toLowerCase();
    //       if(lowerCaseValue.includes(lowercaseinput)){
    //         result.push(obj);
    //         break;
    //       }
    //     }
    //   }
    //   return result;
    // },[]);}
    // else{
    //   this.dataList=[...this.initialData]
    // }
  }

  

  onOkclick(e: any) {
    
  }

  onPendingChangesOkclick(e: any) {
    this.confirmationModal.close();
    this.closeModal();
  }

  ngOnDestroy() {
    this.subscriptions.forEach((subscription: Subscription) => {
      if (subscription) subscription.unsubscribe();
    });
  }
}
