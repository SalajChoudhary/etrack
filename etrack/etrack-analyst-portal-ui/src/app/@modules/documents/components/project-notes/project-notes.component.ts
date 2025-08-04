import { DatePipe } from '@angular/common';
import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { SortEvent } from 'primeng/api';
import { CommonService } from 'src/app/@shared/services/commonService';
import { VirtualDesktopService } from 'src/app/@shared/services/virtual-desktop.service';
import { UserRole } from 'src/app/@shared/constants/UserRole';
import { takeUntil } from 'rxjs/operators';
import { fromEvent, Subject } from 'rxjs';
import { findIndex, isEmpty, isEqual, toString } from 'lodash';
import { ErrorService } from 'src/app/@shared/services/errorService';
import FileSaver from 'file-saver';

@Component({
  selector: 'app-project-notes',
  templateUrl: './project-notes.component.html',
  styleUrls: ['./project-notes.component.scss'],
})
export class ProjectNotesComponent implements OnInit {
  @ViewChild('pTable', { static: true }) pTable: any;
  @Input()  projectNotes: any = [];
  @Input() configObject: any;
  @Input() userRoles: any;
  @Input() isReadOnly: any;
  @Input() projectId:any;
  @Input() inquiryId: any;
  @Input() isGi: boolean = false;
  @Output() onEdit = new EventEmitter();
  // @Input() notes : any;
  popOverNote: string = '';
  popUpNoteHeader: string = 'Notes';
  popUpCommentHeader: string = 'Comments';
  popOverComment: string = '';
  innerWidth: number = 0;
  dateFilterArray: any = [];
  typeFilterArray: any = [];
  noteFilterArray: any = [];
  commentFilterArray: any = [];
  actionDate = '';
  actionType = '';
  actionNote = '';
  comments = '';
  scrollHeight = '190px';
  userRole = UserRole;
  exportHeaders: any = [];
  exportData: any = [];
  showServerError = false;
  serverErrorMessage!: string;
  private unsubscriber: Subject<void> = new Subject<void>();

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.innerWidth = window.innerWidth;
    this.setScrollHeight();
  }
  setScrollHeight() {
    if (this.innerWidth > 1400 && this.innerWidth < 1600) {
      this.scrollHeight = '180px';
    } else if (this.innerWidth > 1600 && this.innerWidth < 1800) {
      this.scrollHeight = '175px';
    } else if (this.innerWidth > 1800) {
      this.scrollHeight = '165px';
    } else if (this.innerWidth < 1400 && this.innerWidth > 1200) {
      this.scrollHeight = '190px';
    } else if (this.innerWidth < 1200) {
      this.scrollHeight = '255px';
    }
  }
  constructor(
    private commonService: CommonService,
    private virtualDesktopService: VirtualDesktopService,
    private datePipe: DatePipe,
    private errorService: ErrorService
  ) {}

  getActionDescription(actionTypeId: any) {
    if (this.configObject) {
      let fArray = this.configObject?.actionTypes?.filter(
        (x: any) => x.actionTypeCode == actionTypeId
      );
      if (fArray?.length > 0) return fArray[0].actionTypeDesc;
      else return '';
    } else {
      return '';
    }
  }
  customSort(event: SortEvent) {
    console.log(event);
    if (event && event.data && event.field) {
      this.projectNotes.sort((data1: any, data2: any) => {
        let value1 = data1[event.field || ''];
        let value2 = data2[event.field || ''];
        let result = null;

        if (value1 == null && value2 != null) result = -1;
        else if (value1 != null && value2 == null) result = 1;
        else if (value1 == null && value2 == null) result = 0;
        else if (typeof value1 === 'string' && typeof value2 === 'string') {
          if (event.field === 'actionDate') {
            result =
              new Date(value1) > new Date(value2)
                ? -1
                : new Date(value1) < new Date(value2)
                ? 1
                : 0;
          } else {
            result = value1.localeCompare(value2);
          }
        } else result = value1 < value2 ? -1 : value1 > value2 ? 1 : 0;
        if (event.order) {
          return event.order * result;
        } else {
          return result;
        }
      });
    }
  }

  exportToExcel() {
    let filteredVal = this.pTable.filteredValue;
    let dataList: any[] = [];

    const headers:any = ['actionDate','actionTypeDesc','actionNote','comments']

    if(filteredVal){
       dataList = filteredVal.map((data:any) => {
        let temp: any = {};
        headers.forEach((header:any) => {
          temp[header] = data[header];
        });
        return temp;
      });
    }else{
       dataList = this.projectNotes.map((data:any) => {
        let temp: any = {};
        headers.forEach((header:any) => {
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
      this.saveAsExcelFile(excelBuffer);
    });
  }
  saveAsExcelFile(buffer: any): void {
    let EXCEL_TYPE =
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
    let EXCEL_EXTENSION = '.xlsx';
    const data: Blob = new Blob([buffer], {
      type: EXCEL_TYPE,
    });
    FileSaver.saveAs(data,
       'project-notes_export_' + new Date().getTime() + EXCEL_EXTENSION)
   

}
  resetFilters() {
    console.log(this.pTable);
    this.actionType = '';
    this.actionDate = '';
    // this.getNotes();
    this.pTable.reset();
  }
  ngOnChanges(changes: any) {    
   if(this.projectNotes && this.projectNotes?.length){
    this.getFilterArrays();
    this.setExportData();
   }
  
   this.projectNotes.forEach((note: any)=>{
      if(note.actionTypeCode =='19'){
        note.actionNote =  note.actionNote.replace('|', ', ');
        let splitArray =    note.actionNote.toString().split(",");
        let litHoldStartString = splitArray[0];
        let litHoldEndString = splitArray[1];
        let hasEndDate =  litHoldEndString.split('=');        
      // note.actionNote =  note.actionNote.replace('|', ', ');
        if(hasEndDate[1].trim().length < 1){
          hasEndDate = "none";
          note.actionNote = litHoldStartString + ", " + litHoldEndString + " "+ hasEndDate;    
        }
      }
    });    
  }
  
  editNote(item: any) {
    this.onEdit.emit(item);
  }
  onNoteHover(item: any) {
    this.popOverNote = item?.actionNote;
  }
  onCommentHover(item: any) {
    this.popOverComment = item?.comments;
  }
  deleteRow(item: any) {
    if(this.isGi) {
      this.virtualDesktopService.deleteGiNote(item?.inquiryNoteId, this.inquiryId).subscribe(
        (response) => {
        // let i=this.projectNotes.findIndex((x:any)=>x.projectNoteId==item.projectNotedId); // TODO: always returns -1; so moved to lodash. 
        let i = findIndex(this.projectNotes, (x:any)=> {
          return isEqual(toString(x.inquiryNoteId), toString(item.inquiryNoteId));
        })
        this.projectNotes.splice(i,1);
        },
        (err) => {
          console.log(err);
        }
      );
    }

    else {
      this.virtualDesktopService.deleteNote(item?.projectNoteId, this.projectId).subscribe(
        (response) => {
        // let i=this.projectNotes.findIndex((x:any)=>x.projectNoteId==item.projectNotedId); // TODO: always returns -1; so moved to lodash. 
        let i = findIndex(this.projectNotes, (x:any)=> {
          return isEqual(toString(x.projectNoteId), toString(item.projectNoteId));
        })
        this.projectNotes.splice(i,1);
        },
        (err) => {
          console.log(err);
        }
      );
    }

  }
  ngOnInit(): void {
    this.innerWidth = window.innerWidth;
  //  console.log(this.innerWidth);
    this.setScrollHeight();
    //diables browswers back button
    history.pushState(null, '');
    fromEvent(window, 'popstate')
      .pipe(takeUntil(this.unsubscriber))
      .subscribe((_) => {
        history.pushState(null, '');
      });
        this.projectNotes.forEach((note: any)=>{
          if(note.actionTypeCode =='19'){
          note.actionNote =  note.actionNote.replace('|', ', ');
          }
        });
        
  }
  getFilterArrays() {
    let dateArray: any = [];
    let typeArray: any = [];
    let noteArray: any = [];
    let commentArray: any = [];
    this.dateFilterArray = [];
    this.noteFilterArray = [];
    this.commentFilterArray = [];
    this.typeFilterArray = [];
    this.projectNotes.sort((a: any, b: any) => {
      let aa: any = new Date(a.actionDate);
      let bb: any = new Date(b.actionDate);
      return bb - aa;
    });
    this.projectNotes.forEach((element: any) => {
      if (element?.actionDate) dateArray.push(element.actionDate);
      if (element?.actionTypeDesc) typeArray.push(element.actionTypeDesc);
      if (element?.actionNote) noteArray.push(element.actionNote);
      if (element?.comments) commentArray.push(element.comments);
    });
    typeArray = [...new Set(typeArray)];
    dateArray = [...new Set(dateArray)];
    noteArray = [...new Set(noteArray)];
    commentArray = [...new Set(commentArray)];
    typeArray.forEach((item: any) => {
     
        this.typeFilterArray.push({
          label: item,
          value: item,
        });
    });
   // console.log(dateArray,'datearray',typeArray)
    dateArray.forEach((item: any) => {
      this.dateFilterArray.push({ label: item, value: item });
    });
    noteArray.forEach((item: any) => {
      this.noteFilterArray.push({ label: item, value: item });
    });
    commentArray.forEach((item: any) => {
      this.commentFilterArray.push({ label: item, value: item });
    });
    this.typeFilterArray.sort((a: any, b: any) => {
      if (a.label < b.label) return -1;
      if (a.label > b.label) return 1;
      return 0;
    });
    this.dateFilterArray.sort((a: any, b: any) => {
      if (a.label < b.label) return -1;
      if (a.label > b.label) return 1;
      return 0;
    });
    this.noteFilterArray.sort((a: any, b: any) => {
      if (a.label.toLowerCase() < b.label.toLowerCase()) return -1;
      if (a.label.toLowerCase() > b.label.toLowerCase()) return 1;
      return 0;
    });
    this.commentFilterArray.sort((a: any, b: any) => {
      if (a.label.toLowerCase() < b.label.toLowerCase()) return -1;
      if (a.label.toLowerCase() > b.label.toLowerCase()) return 1;
      return 0;
    });
  }
  getNotes() {
    console.log('get notes called', this.projectNotes);
    this.projectNotes.sort((a: any, b: any) => {
      let aa: any = new Date(a.actionDate);
      let bb: any = new Date(b.actionDate);
      return bb - aa;
    });
    this.getFilterArrays();
    this.setExportData();
    
    // this.virtualDesktopService
    //   .getNotesList()
    //   .then((response) => {
    //     if (response) {
    //       this.projectNotes = response;
    //       this.projectNotes.sort((a: any, b: any) => {
    //         let aa: any = new Date(a.actionDate);
    //         let bb: any = new Date(b.actionDate);
    //         return bb - aa;
    //       });
    //       // this.projectNotes.sort((a: any, b: any) => {
    //       //   let aa: any = new Date(a.actionDate);
    //       //   let bb: any = new Date(b.actionDate);
    //       //   return bb - aa;
    //       // });
    //       // this.getFilterArrays();
    //       // this.projectNotes.forEach((notes:any)=>{
    //       //  notes.actionTypeDesc=this.getActionDescription(notes.actionType);
    //       // })
    //     }
    //     this.setExportData();
    //   })
    //   .catch((err) => {
    //     this.projectNotes = [];
    //     this.setExportData();
    //     setTimeout(()=>{
    //       this.serverErrorMessage = this.errorService.getServerMessage(err);
    //       this.showServerError = true;
    //       throw err;  
    //     }, 10)
    //   });
  }
  ngAfterViewInit(){
   // isEmpty(this.projectNotes);
  }

  isDeleteToBeShown(isSystemGenerated: 'N' | 'Y') {
    if (isSystemGenerated === 'Y') {
      if (this.userRoles?.includes(UserRole.System_Admin)) return true;
      return false;
    }
    return true;
  }

  setExportData() {
    this.exportHeaders = [
      'actionDate',
      'actionTypeDesc',
      'actionNote',
      'comments',
    ];
    this.exportData = this.projectNotes.map((data: any) => {
      let temp: any = {};
      this.exportHeaders.forEach((header: any) => {
        temp[header] = data[header];
      });
      return temp;
    });
  }

  ngOnDestroy(): void {
    this.unsubscriber.next();
    this.unsubscriber.complete();
  }

  // isEmpty(projectNotes: any){ 
  //   setTimeout(()=>{ 
  //     return isEmpty(projectNotes);
  //   },5000)
    
  // }
}
