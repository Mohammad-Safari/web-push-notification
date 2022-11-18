import { HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { LoginModel } from 'src/app/model/login-model';

import { LoginService } from './login.service';

describe('LoginService', () => {
  const headers = jasmine.createSpyObj('HttpHeaders', ['get']);
  headers.get.and.returnValue('Scheme Token');
  const mockResponseData = { headers: headers };
  const mockCredentials: LoginModel = {
    username: 'mockuser',
    password: 'mockpassword',
  };
  let service: LoginService;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;
  let storageSpy: jasmine.SpyObj<Storage>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('HttpClient', ['post']);
    TestBed.configureTestingModule({
      providers: [LoginService, { provide: HttpClient, useValue: spy }],
    });
    service = TestBed.inject(LoginService);
    httpClientSpy = TestBed.inject(HttpClient) as jasmine.SpyObj<HttpClient>;
    storageSpy = setupStorage() as jasmine.SpyObj<Storage>;
  });

  it('should interact correctly with http client', () => {
    const stubResponse = of(mockResponseData);
    httpClientSpy.post.and.returnValue(stubResponse);
    expect(service.login(mockCredentials)).toBeInstanceOf(Observable);
    expect(httpClientSpy.post.calls.count()).withContext('one call').toEqual(1);
    expect(httpClientSpy.post.calls.mostRecent().args[0])
      .withContext('url')
      .toBe('/api/login');
    expect(httpClientSpy.post.calls.mostRecent().args[1])
      .withContext('credentials')
      .toBe(mockCredentials as any);
  });

  it('should have extracted token successfully', () => {
    const stubResponse = of(mockResponseData);
    httpClientSpy.post.and.returnValue(stubResponse);
    service.login(mockCredentials).subscribe({
      next: () => {
        expect(storageSpy.setItem.calls.count())
          .withContext('tokenized now')
          .toEqual(1);
          expect(storageSpy.setItem.calls.mostRecent().args[0])
            .withContext('tokenized correctly')
            .toBe('Authorization');
        expect(storageSpy.setItem.calls.mostRecent().args[1])
          .withContext('tokenized correctly')
          .toBe(mockResponseData.headers.get());
      },
    });
  });
});

function setupStorage() {
  let store: { [name: string]: any } = {};
  let storageSpy = spyOnAllFunctions(Object.getPrototypeOf(localStorage));
  storageSpy.getItem.and.callFake((key: string): string => {
    return store[key] ?? null;
  });
  storageSpy.setItem.and.callFake((key: string, value: string): string => {
    return (store[key] = <string>value);
  });
  storageSpy.removeItem.and.callFake((key: string): void => {
    delete store[key];
  });
  storageSpy.clear.and.callFake(() => {
    store = {};
  });
  return storageSpy;
}
