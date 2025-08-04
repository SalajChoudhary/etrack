import { GeoCodedAddress } from "./geo-coded-address";

export class IdResponse {
  facilityName!:string;
  locationDirections!: string;
  districtId!: number;
  decId!: number;
  decIdFormatted!:string;
  projectId!: number;
  rolePrimaryInd!: string;
  roleTypeDesc!: string;
  city!: string;
  muncipalities!:string;
  counties!:string;
  country: string="USA";
  state:string= "NY";
  zip!: string;
  zipExtension!: string;
  longLat!:string;
  phoneNumber!: string;
  contact!: string;
  lastKnownAppl!:string;
  geometry!: any;
  geometryType:number=0;
  codedAddress!: GeoCodedAddress;
}
