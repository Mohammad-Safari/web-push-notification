import { Component } from '@angular/core';
import { SignupModel } from 'src/app/model/signup-model';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
})
export class SignupComponent {
  singupModel: SignupModel = new SignupModel();

  onSubmit() {
    return undefined;
  }
}
