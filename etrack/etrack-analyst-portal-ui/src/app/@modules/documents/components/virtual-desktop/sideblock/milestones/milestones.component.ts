import { Component, Input, OnInit } from '@angular/core';
import { isEmpty, get, result, flatten} from 'lodash';
import csvDownload from 'json-to-csv-export'
import FileSaver from 'file-saver';
import * as XLSX from 'xlsx';

@Component({
  selector: 'app-milestones',
  templateUrl: './milestones.component.html',
  styleUrls: ['./milestones.component.scss']
})
export class MilestonesComponent implements OnInit {

  @Input() milestone:any;
  panelOpenState: boolean = false;

  get batchDetails(){
    return get(this.milestone, 'batchDetails', [])
  }

  constructor() { }

  ngOnInit(): void {
  }

  exportMilestones(event:any, milestones:any){

console.log(milestones);

    event.preventDefault();
    event.stopPropagation();
    if(isEmpty(milestones)){
      return;
    }
    // const dataToConvert = {
    //   data: milestones,
    //   filename: 'project-milestones',
    //   delimiter: ',',
    //   // headers: ['IP', "Full Name", "IP Address"]
    // }
   // csvDownload(dataToConvert)
  
   let data = [milestones];
   console.log("Batch Details",data);
     let filename : string = 'project-milestones';
     let worksheet1 = XLSX.utils.json_to_sheet(milestones);
     let workbook = XLSX.utils.book_new();
     XLSX.utils.book_append_sheet(workbook,worksheet1,'Data') ;
     XLSX.writeFile(workbook,filename + "_"+ new Date().getTime() +'.xlsx');
   
  }

  splitByComma(permitTransTypeApplId:any){

    if(isEmpty(permitTransTypeApplId)){
      return [];
    }
    const result = permitTransTypeApplId.map((item:any)=>{
      return item.split(',');
    }).filter((v:any) => !isEmpty(v));
    return flatten(result);
  }
}
