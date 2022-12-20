import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { LoginService } from '../service/login/login.service';

@Injectable({
  providedIn: 'root',
})
export class IsAuthorizedGuard implements CanActivate {
  constructor(private loginService: LoginService, private router: Router) {}
  canActivate():
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    if (this.loginService.isAuthenticated()) {
      return true;
    }
    return this.router.createUrlTree(['/login']);
  }
}
