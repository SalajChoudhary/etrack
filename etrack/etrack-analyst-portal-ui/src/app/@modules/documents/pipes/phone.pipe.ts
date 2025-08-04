import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'phone'
})
export class PhonePipe implements PipeTransform {

  transform(value: any, ...args: unknown[]): unknown {
    if(!value){
      return null;
    }
    const valueSplitted = value.split('');
    valueSplitted.splice(0,0,'(');
    valueSplitted.splice(4,0,') ');
    valueSplitted.splice(8,0,'-');
    return valueSplitted.join('');
  }

}
