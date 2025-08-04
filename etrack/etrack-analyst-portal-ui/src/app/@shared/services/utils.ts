// import 'rxjs/add/operator/map';
import { Injectable, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { FileSystemFileEntry } from 'ngx-file-drop';
import { UntypedFormControl } from '@angular/forms';

@Injectable()
export class Utils {

  version:string='3.3.12';

  loadingEmitter: EventEmitter<any> = new EventEmitter();
  totalFileSize: number=0;
  constructor(public router: Router) { }

  navigateTo(path: string) {
    this.router.navigate([path]);
  }

  emitLoadingEmitter(value:boolean){
  this.loadingEmitter.emit(value.toString());
  }

  getLoadingEmitter(){
    return this.loadingEmitter;
  }

  /**
   * Checks if a DecId is having current format.
   * @constructor
   * @param {any} test - The DECID text.
   * @returns {Boolean} True
   */
  checValidDecId(text: any) {
    return (text.length > 0 && text.length < 13 && text[1] == "-" && text[6] == "-") ? true : false;
  }

  /**
   * Validates the document name.
   * @constructor
   * @param {FormControl} control - The documentName control.
   * @returns {Boolean || null} null
   */
  documentNameValidator(control: UntypedFormControl) {
    if(control.value){
    let text = control.value.trim();
    var reg = new RegExp('^[ A-Za-z0-9_,./-]*$');
    if (!text.length || (text && !text.match(reg))) {
      return {
        documentNameInvalid: {
          text: 'Error: Invalid document name'
        }
      };
    }
    }
    return null;
  }

  /**
   * Validates the Facility name.
   * @constructor
   * @param {FormControl} control - The facilityName control.
   * @returns {Boolean || null} null
   */
   facilityNameValidator(control: UntypedFormControl) {
    if(control.value){
    let text = control.value.trim();
    var reg = new RegExp('^[ A-Za-z0-9!-\/:-@[-`{-~]*$');
    if (!text.length || (text && !text.match(reg))) {
      return {
        facilityNameInvalid: {
          text: 'Error: Invalid document name'
        }
      };
    }
    }
    return null;
  }

  /**
   * Validates if a text is a valid DEC-ID
   * @constructor
   * @param {FormControl} control - The searchText control.
   * @returns {Boolean || null} null
   */
  searchTextValidatorDecId(control: UntypedFormControl) {
    let text = control.value;
    var reg = new RegExp('^[0-9-]*$');
    if (text && text.length < 5) {
      return {
        minThree: {
          text: 'Minimum three characters required'
        }
      };
    }
    if (
      !text.match(reg) ||
      !(text[1] == '-' && text[6] == '-')
    ) {
      return {
        invalidDecSearch: {
          text: 'Invalid DECId'
        }
      };
    }
    if (
      !(text.length > 0 && text.length < 13)
    ) {
      return {
        invalidDecSearchLength: {
          text: 'Invalid DECId'
        }
      };
    }
    return null;
  }

  /**
     * Validates if a tracked application Id is valid
     * @constructor
     * @param {FormControl} control - The trackApplicationId control.
     * @returns {Boolean || null} null
     */

  trackedAppIdValidator(control: UntypedFormControl) {
    let text = control.value;
    var reg = new RegExp('^[0-9]*$');
    if (text && ((text.length != 5 && text.length != 0) || !text.match(reg))) {
      return {
        maxFiveAllowed: {
          text: 'Maximum five numbers allowed'
        }
      };
    }
    return null;
  }



  /**
   * Validates if search text is validate facility.
   * @constructor
   * @param {FormControl} control - The searchText control.
   * @returns {Boolean || null} null
   */
  searchTextValidatorfacility(control: UntypedFormControl) {
    let text = control.value;
    var reg = new RegExp("^[0-9 a-z A-Z ,.&_'-:]*$");
    if (text && (text.length < 5 || text.length > 100 || !text.trim().length)) {
      return {
        invalidFacilitySearch: {
          text: 'Invalid facility name'
        }
      };
    }
    if (text && (!reg.test(text) || text.indexOf("*")!=-1 || text.indexOf("/")!=-1)) {
      return {
        invalidFacilityCharSearch: {
          text: 'Invalid facility name'
        }
      };
    }
    return null;
  }

/**
  * Reduce totla file size on deletion of files.
  * @constructor
  * @param {any} fileSize - Size of the file
  */

decrementTotalFileSize(fileSize:number){
   this.totalFileSize= this.totalFileSize-fileSize;
   return this.totalFileSize;
}

  /**
  * Validates if a files is amongst the allowed formats and with the allowed file size limit.
  * @constructor
  * @param {any} files - List of files added
  */

  fileValidate(
    files: any
  ): {
    sizeExceededError: boolean;
    invalidFilesError: boolean;
    sameFileNameErr:boolean;
    totalSizeError: boolean;
    invalidSizeFiles: any[];
    invalidFiles: any[];
    isInvalid: boolean;
  } {
    var fileTypes = ['application/pdf', 'image/jpeg'
    , 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-excel','image/png', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'image/tiff', 'image/gif', 'application/vnd.ms-visio.viewer' , 'text/xml', 'text/plain' , 'application/vnd.ms-outlook',
    'application/msaccess', 'message/rfc822' ,'application/msword' , 'application/msword'
  ];

    var fileTypesExtensionExceptions=["msg", "mbox", "pdfa", "vsd", "docx", "mdb", "ppt", "pptx", "rtf", "vsdx", "doc", "zip"]

    // Individual file limit 50 MB
    const fileLimit = 50*1024*1024;

    // Total file limit 50 MB
    const totalFileLimit = 50*1024*1024;
    //return (file.size<50001) && (fileTypes.includes(file.type));
    var sizeErrors: any[] = [];
    var typeErros: any[] = [];
    this.totalFileSize=0;
    let filenameArr:any[]=[];
    files.forEach((v:any,i:any)=>{
      if (v.size > fileLimit) {
        sizeErrors.push(v.name);
      }
    if(!filenameArr.length)
     filenameArr= files.filter((item:any,index:number)=>item.name==v.name && i!=index);
      if (!fileTypes.includes(v.type) && !fileTypesExtensionExceptions.includes(v.name.split(".")[v.name.split(".").length-1]))
       {
        typeErros.push(v.name);
      }
      this.totalFileSize = this.totalFileSize + v.size;
    })

    return {
      sizeExceededError: sizeErrors.length > 0,
      invalidFilesError: typeErros.length > 0,
      sameFileNameErr: filenameArr.length?true:false,
      totalSizeError: this.totalFileSize > totalFileLimit,
      invalidSizeFiles: sizeErrors,
      invalidFiles: typeErros,
      isInvalid:
        sizeErrors.length > 0 ||
        typeErros.length > 0 ||
        this.totalFileSize > totalFileLimit ||
        filenameArr.length > 0
    };
  }
}
