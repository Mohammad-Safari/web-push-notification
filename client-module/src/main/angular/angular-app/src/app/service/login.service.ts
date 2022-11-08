import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginModel } from '../model/login-model';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  constructor(private httpClient: HttpClient) {}
  login(loginModel: LoginModel) {
    this.httpClient
      .post('/api/login', loginModel, { observe: 'response' })
      .subscribe({
        next: (data) => {
          localStorage.setItem(
            'Authorization',
            data.headers.get('Authorization') ?? ''
          );
        },
      });
  }
}
