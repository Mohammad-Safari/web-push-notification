import { Component, InjectionToken } from '@angular/core';
import { NotificationModel } from 'src/app/model/notification-model';
import { Publisher } from 'src/app/service/interface/publisher';
import { Subscriber } from 'src/app/service/interface/subscriber';
import { SocketCommunicationService } from 'src/app/service/socket-communication/socket-communication.service';
const injectionToken = new InjectionToken<SocketCommunicationService<NotificationModel<string>>>('', {
  factory: () => new SocketCommunicationService('wss://127.0.0.1:8443/socket'),
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
