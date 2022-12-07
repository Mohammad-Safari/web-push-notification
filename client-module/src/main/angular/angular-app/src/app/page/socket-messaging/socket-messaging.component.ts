import { Component, InjectionToken } from '@angular/core';
import { EventModel } from 'src/app/model/event-model';
import { NotificationModel } from 'src/app/model/notification-model';
import { Publisher } from 'src/app/service/interface/publisher';
import { Subscriber } from 'src/app/service/interface/subscriber';
import { SocketCommunicationService } from 'src/app/service/socket-communication/socket-communication.service';
const injectionToken = new InjectionToken<
  SocketCommunicationService<NotificationModel | EventModel>
>('', {
  factory: () => new SocketCommunicationService('wss://spring-mvc:8443/socket'),
});
@Component({
  selector: 'app-realtime-messaging',
  templateUrl: './socket-messaging.component.html',
  styleUrls: ['./socket-messaging.component.scss'],
  providers: [
    { provide: Subscriber, useExisting: injectionToken },
    { provide: Publisher, useExisting: injectionToken },
  ],
})
export class SocketMessagingComponent {}
