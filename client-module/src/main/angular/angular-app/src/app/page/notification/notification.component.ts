import { Component, OnInit } from '@angular/core';
import { NotificationModel } from 'src/app/model/notification';
import {
  EventPublisherService,
  PUBLSHER_ENDPOINT
} from 'src/app/service/event-publisher/event-publisher.service';
import {
  ServerSentEventService,
  SUBSCRIBER_ENDPOINT
} from 'src/app/service/server-sent-event/server-sent-event.service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss'],
  providers: [
    { provide: SUBSCRIBER_ENDPOINT, useValue: '/event/notification/subscribe' },
    { provide: PUBLSHER_ENDPOINT, useValue: '/api/notification/publish' },
    {
      provide: ServerSentEventService,
      useClass: ServerSentEventService,
    },
    {
      provide: EventPublisherService,
      useClass: EventPublisherService,
    },
  ],
})
export class NotificationComponent implements OnInit {
  public publisherNotification: NotificationModel;

  ngOnInit(): void {}

  public notify(notification: NotificationModel) {
    this.publisherNotification = notification;
  }
}
