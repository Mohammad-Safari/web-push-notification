import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { LoginService } from './service/login/login.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Spring Boot - Angular Application';
  name: string | null;
  loginSubscription: Subscription;
  constructor(private loginService: LoginService) {}
  ngOnInit(): void {
    this.loginService.isAuthenticated()
      ? this.loginService.getUsername()
      : false;
    this.loginService.loginObservable.subscribe({
      next: (name) => {
        this.name = name;
      },
    });
  }

  ngOnDestroy(): void {
    this.loginSubscription.unsubscribe();
  }
}
