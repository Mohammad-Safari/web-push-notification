import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { LoginService } from './service/login/login.service';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Spring Boot - Angular Application';
  name: string | null;
  loginSubscription: Subscription;
  mobileNavigation: boolean;

  constructor(
    private loginService: LoginService,
    private responsive: BreakpointObserver
  ) {}

  ngOnInit(): void {
    if (this.loginService.isAuthenticated()) {
      this.loginService.getUsername();
    }
    this.loginService.loginObservable.subscribe({
      next: (name) => {
        this.name = name;
      },
    });
    this.responsive
      .observe([
        Breakpoints.XSmall,
        Breakpoints.Small,
        Breakpoints.HandsetPortrait,
      ])
      .subscribe((result) => {
        this.mobileNavigation = false;
        if (result.matches) {
          this.mobileNavigation = true;
        }
      });
  }

  ngOnDestroy(): void {
    this.loginSubscription?.unsubscribe();
  }
}
