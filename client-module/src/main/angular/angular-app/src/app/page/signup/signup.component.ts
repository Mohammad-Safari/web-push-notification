import { Component, OnInit } from '@angular/core';
import { SignupModel } from 'src/app/model/signup-model';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
})
export class SignupComponent implements OnInit {
  singupModel: SignupModel = new SignupModel();
  constructor() {}

  ngOnInit(): void {}
  onSubmit() {}
}
