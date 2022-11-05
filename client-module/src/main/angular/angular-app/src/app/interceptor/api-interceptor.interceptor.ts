import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class ApiInterceptorInterceptor implements HttpInterceptor {
  constructor() {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    if (
      request.url.startsWith('/api/') &&
      !request.url.startsWith('/api/login') &&
      !request.url.startsWith('/api/signup') &&
      localStorage.getItem('Authorization') != null
    ) {
      request = request.clone({
        setHeaders: {
          ...request.headers,
          Authorization: localStorage.getItem('Authorization') ?? '',
        },
      });
    }
    return next.handle(request);
  }
}
