export class AddressResponse{
decIdFormatted!: string;
standardCode!: number;
facilityName!: string;
districtId!:number;
locationDirections!: string;
city!: string;
state!: string;
zip!: string;
zipExtension!: string;
long!: string;
lat!:string;
nytmx!:number;
nytmy!:number;
county!:string;
municipality!:string;
taxMapNumber!:string;
lastKnownAppl!:string;
geometry!: any;
wkid!: number;
isValidLocation!:number;
public get address() {
  return `${this.locationDirections} ${this.city} ${this.state} ${(this.zip !==null && this.zip !==undefined) ? this.zip:''}`;
}
}
