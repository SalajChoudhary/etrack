export class CivilDivision {
  name!: string;
  swis!:string;
  ctType!:number;
  constructor(name:string,swis:string,ctType?:number){
    this.name=name;
    this.swis=swis;
    if(typeof ctType !== 'undefined'){
      this.ctType=ctType;
    }
  }
  get fullDetails(){
    return this.name+"-"+this.swis;
  }
  get fullMunicipalityDetails(){
    return this.name+"-"+this.swis+"-"+this.ctType;
  }

  get fullDescription(){
   switch(this.ctType){
    case 1: return this.name+' City'+' ('+this.swis +')';
    case 2: return this.name+' Town'+' ('+this.swis +')';
    default: return this.name+' ('+this.swis +')';
   }
  }


}
