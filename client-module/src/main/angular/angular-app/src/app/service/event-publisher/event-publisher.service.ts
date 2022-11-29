import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, Optional } from '@angular/core';
import { Observable } from 'rxjs';
import { concatMap } from 'rxjs/operators';
import { EventModel } from '../../model/event-model';
import { Publisher, PUBLSHER_ENDPOINT } from '../interface/publisher';
import {
  ResolverResponse,
  UserUuidResolverService
} from '../user-uuid-resolver/user-uuid-resolver.service';

@Injectable()
export class EventPublisherService
  implements Publisher<EventModel, ResolverResponse>
{
  constructor(
    private httpClient: HttpClient,
    @Optional() private resolverService: UserUuidResolverService,
    @Inject(PUBLSHER_ENDPOINT) private publisherEndpoint: string
  ) {}

  publish(eventModel: EventModel): Observable<ResolverResponse> {
    if (this.resolverService) {
      return this.uuidBasedPublisher(eventModel);
    } else {
      return this.cookieBasedPublihser(eventModel);
    }
  }

  private uuidBasedPublisher(
    eventModel: EventModel
  ): Observable<ResolverResponse> {
    return this.resolverService
      .getUserSubscriptionUuid(eventModel.receiver)
      .pipe(
        concatMap((uuid) => {
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
  }

  private cookieBasedPublihser(
    eventModel: EventModel
  ): Observable<ResolverResponse> {
    return this.httpClient.post(
      this.publisherEndpoint,
      eventModel
    ) as Observable<ResolverResponse>;
  }
}
