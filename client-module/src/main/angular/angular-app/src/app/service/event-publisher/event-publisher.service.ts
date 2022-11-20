import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, InjectionToken, Optional } from '@angular/core';
import { Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { EventModel } from '../../model/event-model';
import {
  ResolverResponse,
  UserUuidResolverService,
} from '../user-uuid-resolver/user-uuid-resolver.service';

export const PUBLSHER_ENDPOINT = new InjectionToken<string>(
  'PUBLISHER_ENDPOINT'
);

@Injectable()
export class EventPublisherService {
  constructor(
    private httpClient: HttpClient,
    @Optional() private resolverService: UserUuidResolverService,
    @Inject(PUBLSHER_ENDPOINT) private publisherEndpoint: string
  ) {}

  publish(eventModel: EventModel): Observable<ResolverResponse> {
    if (this.resolverService) {
      return this.resolverService
        .getUserSubscriptionUuid(eventModel.receiver)
        .pipe(
          mergeMap((uuid) => {
            if (uuid === '') {
              throw Error(
                "Can't resolve user subscription token. if user exists, make sure user have active subscription"
              );
            }
            return this.httpClient.post(this.publisherEndpoint, {
              ...eventModel,
              receiver: uuid,
            }) as Observable<ResolverResponse>;
          })
        );
    } else {
      return this.httpClient.post(
        this.publisherEndpoint,
        eventModel
      ) as Observable<ResolverResponse>;
    }
  }
}
