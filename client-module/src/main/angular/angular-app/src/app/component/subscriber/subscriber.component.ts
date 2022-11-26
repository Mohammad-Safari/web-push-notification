import {
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Renderer2,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { animationFrameScheduler } from 'rxjs';
import { map, takeWhile } from 'rxjs/operators';
import { EventModel } from 'src/app/model/event-model';
import { NotificationModel } from 'src/app/model/notification';
import { ServerSentEventService } from 'src/app/service/server-sent-event/server-sent-event.service';

@Component({
  selector: 'app-subscriber',
  templateUrl: './subscriber.component.html',
  styleUrls: ['./subscriber.component.scss'],
})
export class SubscriberComponent implements OnInit, OnChanges, OnDestroy {
  public eventModel = new EventModel();
  public pushSupport: boolean;
  public pushGranted: boolean;
  public pushEnabled = false;
  @ViewChild('notificationSubscriber')
  subscriberContainer: ElementRef<HTMLDivElement>;
  @Input()
  appNotification: NotificationModel;
  private _subscribed = true;

  constructor(
    private serverSentEventService: ServerSentEventService<NotificationModel>,
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
              message.id,
              'server-notification'
            )
        )
      )
      .subscribe((notification: NotificationModel) => {
        if (notification) {
          this.renderNotification(notification);
          if (this.pushEnabled) {
            ServerSentEventService.webPushApiTrigger(notification);
          }
        }
      });
  }

  ngOnChanges(simpleChanges: SimpleChanges) {
    if (simpleChanges?.appNotification?.currentValue) {
      this.renderNotification(simpleChanges.appNotification.currentValue);
    }
  }

  private renderNotification(notification: NotificationModel) {
    const notifData = this.renderer.createText(notification.data);
    const notifType = notification.event ?? 'server-notification';
    const notifContainer = this.subscriberContainer.nativeElement;
    const notifBox = this.renderer.createElement('div');
    const contentBox = this.renderer.createElement('div');
    const notifheader = this.renderer.createElement('b');
    const headerText = this.renderer.createText(notifType);
    const classesToAdd = ['message-box', notifType];
    const stylesToAdd = {
      transition: `ease-out margin-left 300ms`,
      'margin-left': '-110%',
      opacity: '1',
    };
    classesToAdd.forEach((c) => this.renderer.addClass(notifBox, c));
    Object.entries(stylesToAdd).forEach(([k, v]) =>
      this.renderer.setStyle(notifBox, k, v)
    );
    this.renderer.appendChild(notifContainer, notifBox);
    this.renderer.appendChild(notifBox, notifheader);
    this.renderer.appendChild(notifheader, headerText);
    this.renderer.appendChild(notifBox, contentBox);
    this.renderer.appendChild(contentBox, notifData);
    this.schedEntryEntrance(notifBox);
    this.schedEntryRemove(notifBox, notification);
  }

  private schedEntryEntrance(notifBox: HTMLDivElement) {
    animationFrameScheduler.schedule(() => {
      this.renderer.setStyle(notifBox, 'margin-left', '0');
      // to make sure timing will not change as fast as setting
      animationFrameScheduler.schedule(() => {
        this.renderer.setStyle(notifBox, 'transition', `ease-in opacity 300ms`);
      }, 300);
    }, 300);
  }

  private schedEntryRemove(
    notificationBox: HTMLDivElement,
    notification: NotificationModel
  ) {
    animationFrameScheduler.schedule(() => {
      this.renderer.setStyle(notificationBox, 'opacity', '0');
      // to make sure transition hapen before deletion
      animationFrameScheduler.schedule(() => {
        this.renderer.removeChild(
          this.subscriberContainer.nativeElement,
          notificationBox
        );
      }, 300);
      // enough time for opacity transition(margin transition didn't need!)
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
