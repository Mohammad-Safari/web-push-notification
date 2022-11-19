import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { LoginModel } from '../../model/login-model';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  constructor(private httpClient: HttpClient) {}
  login(loginModel: LoginModel) {
    return this.httpClient
      .post('/api/login', loginModel, { observe: 'response' })
      .pipe(
        map((data: HttpResponse<any>) => {
          localStorage.setItem(
            'Authorization',
            data.headers.get('Authorization') ??
              data.body['Authorization'] ??
              ''
          );
        })
      );
  }
}
