import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';

export const PUBLSHER_ENDPOINT = new InjectionToken<string>(
  'PUBLISHER_ENDPOINT'
);
export abstract class Publisher<T, K> {
  abstract publish(eventModel: T): Observable<K>;
}
