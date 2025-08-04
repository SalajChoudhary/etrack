import { EventEmitter, Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from '../../../environments/environment';



@Injectable()
export class RequiredDocsService {
  docSub = new BehaviorSubject<any>({});
  uploadIsSuccessfulSub = new BehaviorSubject<{}>('');
  otherUploadIsSuccessfulSub = new BehaviorSubject<{}>('');
  docName = '';
  listInd = new BehaviorSubject<string>('');
  existingDocumentNames = new BehaviorSubject<string []>([]);
  existingFiles = new BehaviorSubject<any []>([]);
  deleteListInd = new BehaviorSubject<string>('');
  step4Docs! : any;



  constructor(private http : HttpClient) {}

  setStepFourDocs(docs: any){
    this.step4Docs = docs;
  }

  setDocName(docName: string){
    this.docName = docName;
  }

  getDocName(){
    return this.docName;
  }

  setDeleteListInd(ind: string){
    this.deleteListInd.next(ind);
  }

  getDeleteListInd(){
    return this.deleteListInd.asObservable();
  }

  setExistingFilesArray(files: any []){
      this.existingFiles.next(files);
  }

  getExistingFilesArray(){
    return this.existingFiles.asObservable();
  }

  setExistingDocumentNames(names: any []){
    this.existingDocumentNames.next(names);
  }


  setDocument(doc : {}){    
      this.docSub.next(doc);
  }

  getDocument(){    
      return this.docSub.asObservable();
}

  uploadWasSuccessful(doc: {}){
        this.uploadIsSuccessfulSub.next(doc);
 }

 getUploadWasSuccessfulSub(){
    return this.uploadIsSuccessfulSub.asObservable();
 }

 otherUpLoadWasSuccessful(doc: {}){
      this.otherUploadIsSuccessfulSub.next(doc);
 }


 getOtherUploadWasSuccessful(){
  return this.otherUploadIsSuccessfulSub.asObservable();
 }

 setListInd(ind: string){
    this.listInd.next(ind);  
 }

 getListInd(){
  return this.listInd.asObservable();
 }

 getSupportDocumentFiles(documentId: string){
  let user = localStorage.getItem('loggedUserName');
   const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: localStorage.getItem('projectId'),
      }),
    };
    return this.http
      .get<any>(
        `${environment.apiBaseurl}/etrack-dcs/support-document/document/${documentId}`,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });
 }


 deleteSupportDocumentFile(documentId : string []){
  let user = localStorage.getItem('loggedUserName');
   const options = {
      headers: new HttpHeaders({
        // @ts-ignore
        userId: user.replace('SVC', '').substring(1),
        // @ts-ignore
        projectId: localStorage.getItem('projectId'),
      }),
    };
    return this.http
      .delete<any>(
        `${environment.apiBaseurl}/etrack-dcs/support-document/document/${documentId}`,
        options
      )
      .toPromise()
      .then((data) => {
        return data;
      });

 }


retrieveFileContent(fileNames: string, documentId : string,projectId:string){
  
  let user = localStorage.getItem('loggedUserName');
  let extension = fileNames.split('.');
  let payload ={
    fileName: fileNames
  }
  const options={
    headers : new HttpHeaders({
      'Content-Type': 'application/json',
      // @ts-ignore
      userId: user.replace('SVC', '').substring(1),
         // @ts-ignore
         projectId: ''+projectId ||'0',
    }),
    
  };

  return this.http
    .post(
      `${environment.apiBaseurl}/etrack-dcs/support-document/retrieveFileContent/${documentId}`,payload,
     { ...options,responseType: 'blob'}
      )
    .toPromise()
    .then((data: any) => {
      if (data) {
        return new Blob([data], { type: data.type });
      } else {
        return data;
      }
     
    });

}



// Rest of the details are same like Upload document
// like 1
// uploadButtonDocument(req: any, docClassName: string) {
//   let user = localStorage.getItem('loggedUserName');
//   let projectId = localStorage.getItem('projectId');
 
//   const options = {
//     headers: new HttpHeaders({
//       //'Content-Type': 'multipart/form-data',
//       // @ts-ignore
//       projectId: projectId,
// // @ts-ignore
//       userId: user.replace('SVC', '').substring(1),
//     // userId: "loadrunner",
//     //  docClass: docClassName,
//     }),
//   };

//   return this.http
//     .post<any>(
//       `${environment.apiBaseurl}/etrack-dcs/support-document/upload`,
//       req,
//       options
//     )
//     .toPromise()
//     .then((data) => {
//       return data;
//     });
// }


  replaceExistingDocument(req: any, docClassName: string){
    let user = localStorage.getItem('loggedUserName');
    let projectId = localStorage.getItem('projectId');
     
      const options = {
        headers: new HttpHeaders({
          //'Content-Type': 'multipart/form-data',
          // @ts-ignore
          projectId: projectId,
    // @ts-ignore
          userId: user.replace('SVC', '').substring(1),
        // userId: "loadrunner",
          //docClassification: docClassName,
        }),
      };
      return this.http
          .post<any>(
            `${environment.apiBaseurl}/etrack-dcs/support-document/replace-support-document`,
            req,
            options
          )
          .toPromise()
          .then((data) => {
            return data;
          });

      

  }



}
