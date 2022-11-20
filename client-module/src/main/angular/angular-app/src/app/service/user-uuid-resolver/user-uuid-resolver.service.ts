import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, InjectionToken } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export const RESOLVER_ENDPOINT = new InjectionToken<string>(
  'PUBLISHER_ENDPOINT'
);

export type ResolverResponse = {
  uuid: string;
  url: string;
};

type PushSubscription = {
  userUuid: string;
  userSubscriptionUrl: string;
};

@Injectable()
export class UserUuidResolverService {
  recieverObservable = new BehaviorSubject<string | null>(null);
  currentPushSubscription: Observable<PushSubscription>;
  constructor(
    private client: HttpClient,
    @Inject(RESOLVER_ENDPOINT) private resolverEndpoint: string
  ) {
    this.currentPushSubscription = (
      this.client.get(resolverEndpoint) as Observable<ResolverResponse>
    ).pipe<PushSubscription>(
      map((data: ResolverResponse) => ({
        userUuid: data.uuid,
        userSubscriptionUrl: data.url,
      }))
    );
  }

  getUserSubscriptionUuid(
    username: string,
    options?: { getUrl: boolean }
  ): Observable<string> {
    return (
      this.client.post(this.resolverEndpoint, {
        username: username,
      }) as Observable<ResolverResponse>
    ).pipe(
      map((data: ResolverResponse) => (options?.getUrl ? data.url : data.uuid))
    );
  }
}
