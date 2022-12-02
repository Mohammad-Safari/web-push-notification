import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';

export const SUBSCRIBER_ENDPOINT = new InjectionToken<string>(
  'SUBSCRIBER_ENDPOINT'
);

export abstract class Subscriber<T> {
  converter: (event: any) => T;
  abstract add(event: string, observable: Observable<T>): void
  abstract get(eventName: string, options?: any): Observable<T>;
  abstract getAll(...events: string[]): Observable<T>
}
