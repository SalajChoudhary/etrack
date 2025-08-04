import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';
import { ValidatorService } from '../../services/validator.service';
import { first,take } from 'rxjs/operators';
@Component({
  selector: 'app-validate-checkbox',
  templateUrl: './validate-checkbox.component.html',
  styleUrls: ['./validate-checkbox.component.scss'],
})
export class ValidateCheckboxComponent implements OnInit {
  @Input() validatedModel: boolean = false;
  @Input() isDisabled: boolean = false;
  @Output() validatedModelChange = new EventEmitter();
  validated = new UntypedFormControl(this.validatedModel);
  @Input() category = '';
  @Input() activityId = '';


  constructor(private validatorSrv: ValidatorService) {}

  ngOnChanges() {    
    console.log('changed', this.validatedModel);
    
    this.validated.setValue(this.validatedModel);
    this.validatedModelChange.emit(this.validatedModel);
    
    
  }

  ngOnInit(): void {
    this.validated.valueChanges.subscribe(() => {
      if (this.category && this.activityId) {
        const params = {
          category: this.category,
          activityId: this.activityId,
          indicator: this.validated.value ? 1 : 0,
        };
        this.validatorSrv.update(params).subscribe(() => {
            this.validatedModelChange.emit(this.validated.value);
        });
            // this.validatedModelChange.emit(this.validated.value);
      } else {
        this.validatedModelChange.emit(this.validated.value);
      }
    });
  }


  validatedClicked(){    
    this.validatedModelChange.emit(this.validated.value);
  }
}
