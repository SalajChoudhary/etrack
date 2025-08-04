export class ProjectLocation {
  projectId!:number;
  mailInInd!: number;
  applicantTypeCode!: number;
  receivedDate!:Date;
  locDirections!:string;
  polygonId!:string;
  workAreaId!:string;
  polygonStatus!:string;
  facility!: ProjectFacility;
  polygon!:any;
  regions!:any;
  primaryRegion!:string;
  counties!:string;
  countySwis!:string;
  municipalities!:string;
  municipalitySwis!:string;
  primaryMunicipality!:string;
  taxmaps!:string;
  reason!:string;
  boundaryChangeReason!:string;
  lat!:number;
  long!:number;
  nytmx!:number;
  nytmy!:number
  validatedInd!:string;
  classifiedUnderSeqr!:string;
  ignoreDecIdMismatch:number=0;
  mode:number=0;
  printUrl:string='';
  hasSameGeometry:number=0;
  inquiries:string[]=[];
  //analystPrintUrl:string='';
}
export class ProjectFacility {
  facilityId!:number;
  facilityName!:string;
  edbDistrictId!:number;
  districtId!:number;
  decId!:number;
  decIdFormatted!:string;
  address!:FacilityAddress;
}
export class FacilityAddress{
  street1!:string;
  street2!:string;
  city!:string;
  state!:string;
  country!:string;
  zip!:string;
  zipExtension!:string;
  phoneNumber!:string;
}
