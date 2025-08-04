import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  forwardRef,
  HostListener,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-searchable-dropdown',
  templateUrl: './searchable-dropdown.component.html',
  styleUrls: ['./searchable-dropdown.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SearchableDropdownComponent),
      multi: true,
    },
  ],
})
export class SearchableDropdownComponent
  implements OnInit, ControlValueAccessor
{
  _listItems: any[] = [];
  clonedListItems: any[] = [];
  @Input() set listItems(value: any[]) {    
    this._listItems = value;
    
    this.clonedListItems = Object.assign([], value);
    
  }
  @Input() set initialValue(value:string){
    if(value){
      this.selectedValue=value;
      this.onChange(value);
    }
  };
  @Input() labelKey: string = 'managerName';
  @Input() valueKey: string = 'userId';
  @Input() width: string = '15rem';
  @Input() disabled: boolean = false;
  @Input() maxLength : string = '';
  @Output() optionSelected=new EventEmitter();
  onChange: any = () => {};
  onTouched: any = () => {};
  value: string = '';
  isInputClicked: boolean = false;
  selectedValue: string = '';
  @ViewChild('dropdown', { static: false }) dropDownElem!: ElementRef;
  @HostListener('document:click', ['$event'])
  clickOut(event: any) {
    if (
      this.dropDownElem &&
      !this.dropDownElem.nativeElement.contains(event.target) &&
      this.isInputClicked
    ) {
      this.isInputClicked = false;
      if (!this.selectedValue) {
        setTimeout(() => {
          this.value = '';
          this.selectedValue = '';
          this.onChange('');
          (document.getElementById('ProjectManagerInput') as HTMLInputElement).value='';
          this._listItems = Object.assign([], this.clonedListItems);
        }, 0);
      }
    }
  }
  constructor(private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
  }

  onDropdownSelect(selectedObj: any) {
    this.value = selectedObj[this.labelKey];
    this.selectedValue = selectedObj[this.valueKey];
    this.isInputClicked = false;
    this.onChange(this.selectedValue);
    this.optionSelected.emit(selectedObj);
  }

  onDrpdownChange(event: any) {
    this.isInputClicked = true;
    this.selectedValue = '';
    this.onChange('');
    this._listItems = [
      ...(this._filter(event.target.value || '')
        ? this._filter(event.target.value || '')
        : []),
    ];
  }

  private _filter(value: any): string[] {
    const filterValue = this._normalizeValue(value);
    return this.clonedListItems.filter((street: any) =>
      this._normalizeValue(street[this.labelKey]).includes(filterValue)
    );
  }

  private _normalizeValue(value: string): string {
    return value ? value.toLowerCase().replace(/\s/g, '') : '';
  }

  writeValue(value: string): void {
    this.value = value;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

}
