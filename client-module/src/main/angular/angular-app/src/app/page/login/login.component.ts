import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { LoginModel } from 'src/app/model/login-model';
import { LoginService } from 'src/app/service/login/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnDestroy {
  public loginModel: LoginModel = new LoginModel();
  private _loginUnsubscribed: Subject<never> = new Subject();

  constructor(private loginService: LoginService, private router: Router) {}

  onSubmit() {
    this.loginService
      .login(this.loginModel)
      .pipe(takeUntil(this._loginUnsubscribed))
      .subscribe({
        next: () => {
          this.router.navigate(['../']);
        },
      });
  }
  logout() {
    this.loginService
      .logout()
      .pipe(takeUntil(this._loginUnsubscribed))
      .subscribe({
        next: () => {
          this.router.navigate(['../']);
        },
      });
  }

  ngOnDestroy() {
    this._loginUnsubscribed.next();
    this._loginUnsubscribed.complete();
  }
}
