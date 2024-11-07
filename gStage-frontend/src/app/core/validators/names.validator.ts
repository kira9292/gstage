import { AbstractControl, ValidationErrors } from '@angular/forms';

export function noWhitespaceValidator(control: AbstractControl): ValidationErrors | null {
  const isValid = /^[A-Za-zÀ-ÖØ-öø-ÿ]+(?:[-'\s][A-Za-zÀ-ÖØ-öø-ÿ]+)*$/.test(control.value);
  return isValid ? null : { whitespace: true };
}
