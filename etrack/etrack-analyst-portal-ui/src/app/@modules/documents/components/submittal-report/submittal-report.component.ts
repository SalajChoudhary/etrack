import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormArray, FormControl, FormGroup, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { SearchReportService } from 'src/app/@shared/services/search-report.service';
import { ErrorService } from 'src/app/@shared/services/errorService';
import moment from 'moment';
import { ProjectService } from 'src/app/@shared/services/projectService';
import { DatePipe } from '@angular/common';
import FileSaver from 'file-saver';
@Component({
  selector: 'app-submittal-report',
  templateUrl: './submittal-report.component.html',
  styleUrls: ['./submittal-report.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SubmittalReportComponent implements OnInit {

  submittedProject: any[] = [];
  trackingArray: any[] = [];
  applicationFieldList : any [] =[];
  applicationOperators: any [] = [];
  applicationSearchQueriesList : any[]= [];
  searchForm! : FormGroup;
  serverErrorMessage!: string;
  reportQueryForm!: UntypedFormGroup;
  errorMsgObj: any = {};
  showServerError = false;
  payload: any= {
    "endDate": "01/17/2024",
    "permitTypes": ["TD"],
    "region": 0,
    "startDate": "01/17/2021",
    "transTypes": ["NEW"]
  };
  submitted: boolean = false;
  runClicked: boolean = false;
  regionList: any[]=[];
  region: any = '';
  availablePermitTypes: any[]= [];
  availableTransTypes: any[] = [];

 

  constructor(
    private errorService: ErrorService,
    private formBuilder: UntypedFormBuilder,
    private reportService: SearchReportService,
    private datePipe: DatePipe,
    private projectService: ProjectService,
    private cdr: ChangeDetectorRef
  ) { 
    this.createForm();
  }

  createForm() {
    this.reportQueryForm = this.formBuilder.group({
      startDate: new FormControl('', [Validators.required]),
      endDate: new FormControl(''),
      permitTypes: new FormArray([]),
      transTypes: new FormArray([]),
      region: new FormControl('')
    });
    this.addPermitAndTransType(true);
    this.addPermitAndTransType();
    this.submittedProject = [];
    this.runClicked = false;
  }

  ngOnInit(): void {
    this.getRegions();
    this.loadTransTypes();  
    this.getTransTypes();  
  }

  get permitTypes() {
    return this.reportQueryForm.controls["permitTypes"] as FormArray;
  }

  get transTypes() {
    return this.reportQueryForm.controls["transTypes"] as FormArray;
  }

  addPermitAndTransType(permit?: boolean) {
   if(permit) {
    const permitTypeForm = this.formBuilder.group({
      permitTypeCode: ['']
    });
    this.permitTypes.push(permitTypeForm);
   } else {
    const transTypeCode = this.formBuilder.group({
      transTypeCode: ['']
    });
    this.transTypes.push(transTypeCode);
   }
  }

  deletePermitAndTransType(index: number, permit?: boolean) {
    if (permit) {
      this.permitTypes.removeAt(index);
    } else {
      this.transTypes.removeAt(index);
    }
  }

  generateProjectReport(){
    this.runClicked = true;
    this.reportQueryForm.updateValueAndValidity();
    if (this.reportQueryForm.valid) {
      let apiData = this.reportQueryForm.value;
      apiData.startDate = this.datePipe.transform(
        apiData.startDate,
        'MM/dd/yyyy'
      );
      apiData.endDate = this.datePipe.transform(apiData.endDate, 'MM/dd/yyyy');
      apiData.permitTypes=apiData.permitTypes.map((e: any)=> e.permitTypeCode).filter((e: any) => e);
      apiData.transTypes=apiData.transTypes.map((e: any)=> e.transTypeCode).filter((e: any) => e);
      this.reportService
      .loadProjectReport(this.reportQueryForm.value)
      .subscribe((response: any) => {
        console.log("Project Report",response)
        let data: any[] = [];
        data.push({
          keywordText: 'Paper',
          count: response.body.paperProjects || 0,
          percentage: response.body.percentageOfPaperProjects || 0
        })
        data.push({
          keywordText: 'Email',
          count: response.body.emailedProjects || 0,
          percentage: response.body.percentageOfEmailedProjects || 0
        });
        data.push({
          keywordText: 'Total Submitted',
          count: response.body.totalProjects || 0,
          percentage: null
        })
        this.submittedProject=data;
        this.submitted = true;
        this.cdr.detectChanges();
      });
    }
  }

  getData() {
   this.submitted = true;
  }

  get dateError() {
    const startDate = this.reportQueryForm?.get('startDate')?.value;
    const endDate = this.reportQueryForm?.get('endDate')?.value;
    if (!startDate) {
      return (
        this.errorMsgObj?.START_DT_REQD ||
        'Error: Start Date is Required.'
      );
    }
    if(!endDate){
      return(this.errorMsgObj?.END_DT_REQD)
    }
    if (
      endDate &&
      moment(startDate, 'YYYY-MM-DD').isAfter(moment(endDate, 'YYYY-MM-DD'))
    ) {
      this.reportQueryForm.get('endDate')?.setErrors({ invalid: true });
      return (
        this.errorMsgObj?.END_DATE_GT_START_DATE ||
        'Error: End Date must be greater than Start Date.'
      );
    }
    if (this.reportQueryForm.get('endDate')?.errors?.invalid) {
      this.reportQueryForm.get('endDate')?.setErrors(null);
    }
    return null;
  }

  exportToExcel(){
   
    let dataList: any[] = [];

    const headers = ['keywordText','count','percentage'];
    
      dataList = this.submittedProject.map((data) => {
        let temp: any = {};
        headers.forEach((header) => {
          switch (header) {
            case 'keywordText':
              temp["Submittal Type"] = data[header];
              break;
              case 'count':
                temp["Count"] = data[header];
                break;
                case 'percentage':
                  temp[' % of Total'] = data[header];
                  break;
          }
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
      this.saveAsExcelFile(excelBuffer, 'Reports');
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

  getRegions() {
    this.projectService.getAllRegions().then(
      (response) => {
        this.regionList = response;
        console.log('regions', response);
      },
      (error: any) => {
        this.serverErrorMessage = this.errorService.getServerMessage(error);
        this.showServerError = true;
        throw error;
      }
    );
  }

  loadTransTypes() {
    this.reportService
      .getPermiTypes()
      .then((res: any) => {
        console.log("Permits",res)
        this.availablePermitTypes = res;
      })
      .catch((ex) => {
        this.serverErrorMessage = this.errorService.getServerMessage(ex);
        this.showServerError = true;
        throw ex;
      });
  }

  getTransTypes() {
    this.projectService.getTransTypes().then((res: any) => {
      console.log("Trans types", res)
      const omitTransTypes = ['DIM', 'DIR', 'DIS', 'DTN'];
      const allowedTransTypes = res.filter((item:any) => omitTransTypes.indexOf(item.transTypeCode) == -1); 
      this.availableTransTypes = res;
    });
  }

  onRegionChange(ev: any){

  }

}
