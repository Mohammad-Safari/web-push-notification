import { Component } from '@angular/core';
import { LoginService } from './service/login/login.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  title = 'Spring Boot - Angular Application';
  name: string | null;
  constructor(private loginService: LoginService) {}
  ngOnInit(): void {
    this.loginService.isAuthenticated()
      ? this.loginService.getUsername()
      : false;
    this.loginService.loginObservable.subscribe((name) => {
      this.name = name;
    });
  }
}
