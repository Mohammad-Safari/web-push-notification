import {
  Component,
  ContentChild,
  ContentChildren,
  QueryList,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { merge } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AutocompleteContentDirective } from 'src/app/directive/autocomplete-content.directive';
import { OptionComponent } from '../option/option.component';

@Component({
  selector: 'app-autocomplete',
  template: `
    <ng-template #root>
      <div class="autocomplete">
        <ng-container *ngTemplateOutlet="content.tpl"></ng-container>
      </div>
    </ng-template>
  `,
  styleUrls: ['./autocomplete.component.scss'],
  exportAs: 'appAutocomplete',
})
export class AutocompleteComponent {
  @ViewChild('root') rootTemplate: TemplateRef<never>;

  @ContentChild(AutocompleteContentDirective)
  content: AutocompleteContentDirective;

  @ContentChildren(OptionComponent) options: QueryList<OptionComponent>;

  optionsClick() {
    return this.options.changes.pipe(
      switchMap((options: QueryList<OptionComponent>) => {
        const clicks$ = options.map((option) => option.click$);
        return merge(...clicks$);
      })
    );
  }
}
