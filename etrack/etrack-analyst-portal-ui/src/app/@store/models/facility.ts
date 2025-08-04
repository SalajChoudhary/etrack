export class ApprovedFacility {
  SITE_ID!: number;
  SITE_TYPE!: string;
  SITE_NAME!: string;
  PRIMARY_ID!: string;
  PRIMARY_SWIS!: string;
  LOCATION_DIRECTIONS_1!: string;
  LOCATION_DIRECTIONS_2!: string;
  CITY!: string;
  STATE!: string;
  ZIP!: number;
  MUNICIPALITIES!: string;
  COUNTIES!: string;
  REGIONS!:string;
  geometry!: any;
  wkid!: number;
  taxMapNumber!:string;
  latitude!:number;
  longitude!:number;
  nytmx!:number;
  nytmy!:number;
  isValidLocation!:number;
  lastKnownAppl!:string;
  OWNER_NAME!:string;
  public get address() {
    return `${this.LOCATION_DIRECTIONS_1} ${this.CITY} ${this.STATE} ${this.ZIP !==null ? this.ZIP:''}`;
  }
}
