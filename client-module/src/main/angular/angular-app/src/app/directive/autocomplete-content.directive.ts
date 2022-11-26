import { Directive, TemplateRef } from '@angular/core';

/**
 * The whole purpose of this directive is to expose a reference 
 * to host TemplateRef so its parent, the AutocompleteComponent,
 * can query and render it on demand.
 */
@Directive({
  selector: '[appAutocompleteContent]',
})
export class AutocompleteContentDirective {
  constructor(public tpl: TemplateRef<unknown>) {}
  
}
