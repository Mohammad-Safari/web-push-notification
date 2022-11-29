import { Component } from '@angular/core';
import { NotificationModel } from 'src/app/model/notification-model';
import { EventPublisherService } from 'src/app/service/event-publisher/event-publisher.service';
import { Publisher, PUBLSHER_ENDPOINT } from 'src/app/service/interface/publisher';
import { Subscriber, SUBSCRIBER_ENDPOINT } from 'src/app/service/interface/subscriber';
import { ServerSentEventService } from 'src/app/service/server-sent-event/server-sent-event.service';
import { RESOLVER_ENDPOINT, UserUuidResolverService } from 'src/app/service/user-uuid-resolver/user-uuid-resolver.service';

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
      provide: Subscriber,
      useClass: ServerSentEventService,
    },
    {
      provide: Publisher,
      useClass: EventPublisherService,
    },
    {
      provide: UserUuidResolverService,
      useClass: UserUuidResolverService,
    },
  ],
})
export class ThirdPartyNotificationComponent {
  public publisherNotification: NotificationModel<string>;

  // constructor(
  //   service1: Subscriber,
  //   service2: Publisher,
  //   service3: UserUuidResolverService,
  //   @Inject(PUBLSHER_ENDPOINT) url1: string,
  //   @Inject(SUBSCRIBER_ENDPOINT) url2: string,
  //   @Inject(RESOLVER_ENDPOINT) url3: string
  // ) {}

  public notify(notification: NotificationModel<string>) {
    this.publisherNotification = notification;
  }
}
