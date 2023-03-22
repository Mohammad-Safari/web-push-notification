import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
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
    if (typeof localStorage == 'undefined') {
      return new Observable();
    }
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
    if (typeof localStorage == 'undefined') {
      return new Observable();
    }
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
    if (typeof localStorage == 'undefined') {
      return false;
    }
    return (
      this._isAuthenticated ||
      (localStorage as any).getItem('Authorization') != null
    );
  }

  public getUsername(): string {
    if (!this.isAuthenticated()) {
      throw Error('user is not authenticated');
    }
    let token = localStorage.getItem('Authorization') ?? '';
    if (!token) {
      throw Error('token is missing');
    }
    token = token.substring('CUSTOM-AUTH '.length).split('.')[1];
    if (!token) {
      localStorage.removeItem('Authorization');
      document.cookie = '';
      throw Error(
        'token is in wrong format(token is now removed from all storages)'
      );
    }
    let username = undefined;
    try {
      username = JSON.parse(atob(token))?.sub;
    } catch (e) {
      localStorage.removeItem('Authorization');
      document.cookie = '';
      throw Error(
        'token is corrupted(token is now removed  from all storages)'
      );
    }
    if (username) {
      this.loginObservable.next(username);
      return username;
    }
    throw Error('token data is missing');
  }
}
