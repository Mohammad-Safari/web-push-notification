import { Component } from '@angular/core';
import { EventPublisherService } from 'src/app/service/event-publisher/event-publisher.service';
import {
  Publisher,
  PUBLSHER_ENDPOINT
} from 'src/app/service/interface/publisher';
import {
  Subscriber,
  SUBSCRIBER_ENDPOINT
} from 'src/app/service/interface/subscriber';
import { ServerSentEventService } from 'src/app/service/server-sent-event/server-sent-event.service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss'],
  providers: [
    { provide: SUBSCRIBER_ENDPOINT, useValue: '/event/notification/subscribe' },
    { provide: PUBLSHER_ENDPOINT, useValue: '/api/notification/publish' },
    {
      provide: Subscriber,
      useClass: ServerSentEventService,
    },
    {
      provide: Publisher,
      useClass: EventPublisherService,
    },
  ],
})
export class NotificationComponent {}
