import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { LoginModel } from '../../model/login-model';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  public loginObservable = new BehaviorSubject<string | null>(null);
  private _isAuthenticated = false;
  constructor(private httpClient: HttpClient) {}
  login(loginModel: LoginModel) {
    return this.httpClient
      .post<{ [key: string]: string }>('/api/login', loginModel, {
        observe: 'response',
      })
      .pipe(
        map((data: HttpResponse<{ [key: string]: string }>) => {
          localStorage.setItem(
            'Authorization',
            data.headers.get('Authorization') ?? data.body?.Authorization ?? ''
          );
        }),
        tap(() => {
          this._isAuthenticated = true;
          this.getUsername();
        })
      );
  }

  logout() {
    return this.httpClient.get('/api/logout', { observe: 'response' }).pipe(
      map(() => {
        localStorage.removeItem('Authorization');
      }),
      tap(() => {
        this._isAuthenticated = false;
        this.loginObservable.next(null);
      })
    );
  }

  public isAuthenticated(): boolean {
    return (
      this._isAuthenticated || localStorage.getItem('Authorization') != null
    );
  }

  public getUsername(): string {
    if (!this.isAuthenticated()) {
      throw Error('user is not authenticated');
    }
    let token = localStorage.getItem('Authorization') ?? '';
    token = token?.substring('CUSTOM-AUTH '.length);
    if (!token || !JSON.parse(atob(token))) {
      throw Error('token is corrupted');
    }
    const username = JSON.parse(atob(token))['username'];
    this.loginObservable.next(username);
    return username;
  }
}
