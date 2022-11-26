import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class ApiInterceptor implements HttpInterceptor {

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    let authenticatedRequest = request;
    if (
      request.url.startsWith('/api/') &&
      (!request.url.startsWith('/api/login') &&
        !request.url.startsWith('/api/signup')) &&
      localStorage.getItem('Authorization') != null
    ) {
      authenticatedRequest = request.clone({
        setHeaders: {
          Authorization: localStorage.getItem('Authorization') ?? '',
        },
      });
    }
    return next.handle(authenticatedRequest);
  }
}
