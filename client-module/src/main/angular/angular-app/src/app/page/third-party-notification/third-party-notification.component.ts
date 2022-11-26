import { Component } from '@angular/core';
import { NotificationModel } from 'src/app/model/notification';
import {
  EventPublisherService,
  PUBLSHER_ENDPOINT,
} from 'src/app/service/event-publisher/event-publisher.service';
import {
  ServerSentEventService,
  SUBSCRIBER_ENDPOINT,
} from 'src/app/service/server-sent-event/server-sent-event.service';
import {
  RESOLVER_ENDPOINT,
  UserUuidResolverService,
} from 'src/app/service/user-uuid-resolver/user-uuid-resolver.service';

@Component({
  selector: 'app-third-party-notification',
  templateUrl: './third-party-notification.component.html',
  styleUrls: ['./third-party-notification.component.scss'],
  viewProviders: [
    {
      provide: SUBSCRIBER_ENDPOINT,
      useValue: 'https://192.168.74.95:8443/sse/notification/',
    },
    {
      provide: PUBLSHER_ENDPOINT,
      useValue: 'https://192.168.74.95:8443/sse/notification/',
    },
    {
      provide: RESOLVER_ENDPOINT,
      useValue: '/api/thirdparty/getSubscriptionUrl',
    },
    {
      provide: ServerSentEventService,
      useClass: ServerSentEventService,
    },
    {
      provide: EventPublisherService,
      useClass: EventPublisherService,
    },
    {
      provide: UserUuidResolverService,
      useClass: UserUuidResolverService,
    },
  ],
})
export class ThirdPartyNotificationComponent {
  public publisherNotification: NotificationModel;

  // constructor(
  //   service1: EventPublisherService,
  //   service2: ServerSentEventService,
  //   service3: UserUuidResolverService,
  //   @Inject(PUBLSHER_ENDPOINT) url1: string,
  //   @Inject(SUBSCRIBER_ENDPOINT) url2: string,
  //   @Inject(RESOLVER_ENDPOINT) url3: string
  // ) {}

  public notify(notification: NotificationModel) {
    this.publisherNotification = notification;
  }
}
