import { AbstractControl, UntypedFormGroup } from '@angular/forms';

export const checkIfSpecialCharOrNumber = (control: AbstractControl) => {
  if (!control.value || control.value.length == 0) return null;
  if (control.value.length > 0) {
    if (
      /[~`!@#$%^&()_={}[\]:;,.<>+\/?-]/.test(control.value) ||
      /\d/.test(control.value)
    ) {
      return { isSpecialChar: true };
    } else return null;
  } else {
    return null;
  }
};
export function validateEmail(control: AbstractControl) {
  if (control.value == '' || control.value == null) return null;
  let val = control.value ? control.value : window.event;

  var filter =
    /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

  if (!filter.test(val)) {
    return { isEmailValidated: true };
  }

  return null;
}

export function validatePastDate(control: AbstractControl) {
  if (control.value == '' || control.value == null) return null;
  let inputDate = control.value ? new Date(control.value) : window.event;
  let currentDate = new Date();
  if (inputDate && inputDate <= currentDate) {
    return { isPastDate: true };
  }

  return null;
}

export function whiteSpaceValidator(control: AbstractControl) {
  if (
    control?.value &&
    control?.value?.length > 0 &&
    control?.value?.trim()?.length === 0
  ) {
    return { isAllSpaces: true };
  }
  return null;
}

export function atleastTwoCharReqd(control: AbstractControl) {
  if (
    control.value &&
    control.value.length > 0 &&
    control.value.trim().length !== 0
  ) {
    if (control.value.length < 2) {
      return { atleastTwoChar: true };
    }
  }
  return null;
}
export const validateGreatThanStartDate = (
  start: string,
  end: string,
  constructionType: string,
  isConstTobeBeDisplayed: boolean
) => {
  return (formGroup: UntypedFormGroup) => {
    let constControl = formGroup.controls[constructionType];
    let startField = formGroup.controls[start];
    let endField = formGroup.controls[end];
    if (!startField.value) {
      if (isConstTobeBeDisplayed) {
        startField.setErrors({ isRequired: true });
      } else {
        startField.setErrors(null);
      }
    }

    if (
      isConstTobeBeDisplayed &&
      (constControl?.value == 'null' || !constControl?.value)
    ) {
      constControl.setErrors({ isRequired: true });
    } else {
      constControl.setErrors(null);
    }
    if (!endField.value) {
      if (isConstTobeBeDisplayed) {
        endField.setErrors({ isRequired: true });
      } else {
        endField.setErrors(null);
      }
      return;
    }
    let startDate = new Date(startField.value);
    let endDate = new Date(endField.value);
    if (startDate >= endDate) {
      endField.setErrors({ isGreaterThanStart: true });
    } else {
      endField.setErrors(null);
    }
    return;
  };
};
export const validateConstrnType = (
  constructionType: string,
  isConstTobeBeDisplayed: boolean
) => {
  return (formGroup: UntypedFormGroup) => {
    let constControl = formGroup.controls[constructionType];

    if (
      isConstTobeBeDisplayed &&
      (constControl?.value == 'null' || !constControl?.value)
    ) {
      constControl.setErrors({ isRequired: true });
    } else {
      constControl.setErrors(null);
    }
    return;
  };
};
const setSameTime=(targetDate:Date,sourceDate:Date)=>{
  targetDate.setHours(sourceDate.getHours());
  targetDate.setMinutes(sourceDate.getMinutes());
  targetDate.setSeconds(sourceDate.getSeconds());
  targetDate.setMilliseconds(sourceDate.getMilliseconds());
}

export const validateDueDate = (
  dueDate: string,
  dateAssigned: string,
  reviewer: string
) => {
  
  return (formGroup: UntypedFormGroup) => {
    let dueDateField = formGroup.controls[dueDate];
    let dateAssignedField = formGroup.controls[dateAssigned];
    let reviewerField = formGroup.controls[reviewer];
    if (
      !dueDateField.value ||
      !dateAssignedField.value ||
      !reviewerField.value
    ) {
      if (!dueDateField.value) {
        dueDateField.setErrors({ required: true });
      }
      if (!dateAssignedField.value) {
        dateAssignedField.setErrors({ required: true });
      }
      if (!reviewerField.value) {
        reviewerField.setErrors({ required: true });
      }
    }

    if (dateAssignedField.value) {
      let startDate = new Date(dateAssignedField.value + ' 00:00:00');

      let currentDate = new Date();
     setSameTime(currentDate,startDate);
      if (currentDate.getTime() > startDate.getTime()) {
        dateAssignedField.setErrors({ isGreaterThanStart: true });
        return;
      } else {
        dateAssignedField.setErrors(null);
      }
    }
    if (dateAssignedField.value && dueDateField.value) {
      let startDate = new Date(dateAssignedField.value + ' 00:00:00');
      let endDate = new Date(dueDateField.value + ' 00:00:00');
      //let twoWeekDate = new Date();
      //twoWeekDate.setDate(startDate.getDate() + 14);
      //setSameTime(twoWeekDate,endDate);
      if (endDate <= startDate) {
        dueDateField.setErrors({ isGreaterThanStart: true });
        return;
      } else {
        dueDateField.setErrors(null);
      }
    }

    return;
  };
};
export const atleastOneNumberValidator = (
  cellNumber: string,
  workNumber: string,
  homeNumber: string
) => {
  return (formGroup: UntypedFormGroup) => {
    let contactFormGroup: any = formGroup.controls.contactDetailsFormGroup;

    let cellNumberCtrl = contactFormGroup['controls'][cellNumber];
    let workNumberCtrl = contactFormGroup['controls'][workNumber];
    let homeNumberCtrl = contactFormGroup['controls'][homeNumber];

    if (
      !cellNumberCtrl.value &&
      !workNumberCtrl.value &&
      !homeNumberCtrl.value
    ) {
      cellNumberCtrl.setErrors({ atleastOneRequired: true });
      workNumberCtrl.setErrors({ atleastOneRequired: true });
      homeNumberCtrl.setErrors({ atleastOneRequired: true });
    } else {
      cellNumberCtrl?.errors?.minlength || cellNumberCtrl?.errors?.maxlength
        ? null
        : cellNumberCtrl.setErrors(null);
      workNumberCtrl?.errors?.minlength || workNumberCtrl?.errors?.maxlength
        ? null
        : workNumberCtrl.setErrors(null);
      homeNumberCtrl?.errors?.minlength || homeNumberCtrl?.errors?.maxlength
        ? null
        : homeNumberCtrl.setErrors(null);

      return;
    }

    return;
  };
};
export const checkBoxValidation = (
  owner: string,
  operator: string,
  lessee: string,
  tab: number
) => {
  return (formGroup: UntypedFormGroup) => {
    let ownerCtrl = formGroup.controls[owner];
    let operatorCtrl = formGroup.controls[operator];
    let lesseeCtrl = formGroup.controls[lessee];
    if (
      !ownerCtrl.value &&
      !operatorCtrl.value &&
      !lesseeCtrl.value &&
      tab === 0
    ) {
      ownerCtrl.setErrors({ checkboxRequired: true });
      operatorCtrl.setErrors({ checkboxRequired: true });
      lesseeCtrl.setErrors({ checkboxRequired: true });
    } else {
      ownerCtrl.setErrors(null);
      operatorCtrl.setErrors(null);
      lesseeCtrl.setErrors(null);
      return;
    }

    return;
  };
};

export const validateGreaterThanEqRecvdDate = (
  recvdDate: string
) => {
  return (control: AbstractControl) => {
    const dateCtrl = control.value;
    if(!dateCtrl) {
      return null;
    }
    const date = Date.parse(dateCtrl);
    const receivedDate = Date.parse(recvdDate);
    if(date >= receivedDate) {
      return null;
    }

    return {lessThanRecvdDate: true};
  }
}