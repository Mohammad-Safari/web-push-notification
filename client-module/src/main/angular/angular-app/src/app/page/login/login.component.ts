import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginModel } from 'src/app/model/login-model';
import { LoginService } from 'src/app/service/login/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  public loginModel: LoginModel = new LoginModel();

  constructor(private loginService: LoginService, private router: Router) {}

  ngOnInit(): void {}

  onSubmit() {
    this.loginService.login(this.loginModel).subscribe({
      next: () => {
        this.router.navigate(['../']);
      },
    });
  }
  logout() {
    this.loginService.logout().subscribe();
  }
}
