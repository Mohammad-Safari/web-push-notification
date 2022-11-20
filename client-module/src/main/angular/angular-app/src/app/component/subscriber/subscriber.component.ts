import {
  Component,
  ElementRef,
  Input,
  OnInit,
  Renderer2,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { map, takeWhile } from 'rxjs/operators';
import { EventModel } from 'src/app/model/event-model';
import { NotificationModel } from 'src/app/model/notification';
import { ServerSentEventService } from 'src/app/service/server-sent-event/server-sent-event.service';

@Component({
  selector: 'app-subscriber',
  templateUrl: './subscriber.component.html',
  styleUrls: ['./subscriber.component.scss'],
})
export class SubscriberComponent implements OnInit {
  public eventModel = new EventModel();
  public pushSupport: boolean;
  public pushGranted: boolean;
  public pushEnabled = false;
  @ViewChild('notificationSubscriber')
  subscriberContainer: ElementRef<HTMLDivElement>;
  @Input('appNotification')
  publishNotifier: NotificationModel;
  private _subscribed: boolean = true;

  constructor(
    private serverSentEventService: ServerSentEventService,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.pushSupport = 'Notification' in window;
    this.serverSentEventService
      .get('server-notification')
      .pipe(
        takeWhile(() => this._subscribed),
        map(
          (message) =>
            new NotificationModel(
              message.data,
              (message as any).id,
              'server-notification'
            )
        )
      )
      .subscribe((notification) => {
        if (notification) {
          this.renderNotification(notification);
          if (this.pushEnabled) {
            ServerSentEventService.webPushApiTrigger(notification);
          }
        }
      });
  }

  ngOnChanges(simpleChanges: SimpleChanges) {
    if (simpleChanges?.publishNotifier?.currentValue) {
      this.renderNotification(simpleChanges.publishNotifier.currentValue);
    }
  }

  private renderNotification(notification: NotificationModel) {
    let notificationBox = this.renderer.createElement('div');
    let header = this.renderer.createElement('b');
    let content = this.renderer.createElement('div');
    const NOTIFICATION_TYPE = notification.event ?? 'server-notification';
    const boxColorClass = NOTIFICATION_TYPE;
    let classesToAdd = ['message-box', boxColorClass];
    const headerText = this.renderer.createText(NOTIFICATION_TYPE);
    const text = this.renderer.createText(notification.data);
    classesToAdd.forEach((x) => this.renderer.addClass(notificationBox, x));
    this.renderer.setStyle(
      notificationBox,
      'transition',
      `opacity ${notification.duration}ms`
    );
    this.renderer.setStyle(notificationBox, 'opacity', '1');
    this.renderer.appendChild(header, headerText);
    this.renderer.appendChild(content, text);
    this.renderer.appendChild(
      this.subscriberContainer.nativeElement,
      notificationBox
    );
    this.renderer.appendChild(notificationBox, header);
    this.renderer.appendChild(notificationBox, content);
    this.schedEntryRemove(notificationBox, notification);
  }

  private schedEntryRemove(
    notificationBox: any,
    notification: NotificationModel
  ) {
    setTimeout(() => {
      this.renderer.setStyle(notificationBox, 'opacity', '0');
      setTimeout(() => {
        this.renderer.removeChild(
          this.subscriberContainer.nativeElement,
          notificationBox
        );
      }, notification.duration);
    }, notification.duration);
  }

  ngOnDestroy() {
    this._subscribed = false;
    this.serverSentEventService.closeEventService();
  }

  toggleWebPush() {
    if (!this.pushEnabled && !this.pushGranted) {
      ServerSentEventService.reauestPermission().then((permission) => {
        this.pushGranted = this.pushEnabled = permission;
      });
    } else {
      this.pushEnabled = !this.pushEnabled;
    }
  }
}
