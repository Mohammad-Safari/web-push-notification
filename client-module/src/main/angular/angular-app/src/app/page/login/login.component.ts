import { Component, OnInit } from '@angular/core';
import { LoginModel } from 'src/app/model/login-model';
import { LoginServiceService } from 'src/app/service/login-service.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  public loginModel: LoginModel = new LoginModel();
  constructor(private loginService: LoginServiceService) {}

  ngOnInit(): void {}
  onSubmit() {
    this.loginService.login(this.loginModel);
  }
}
