import {
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Renderer2,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { animationFrameScheduler, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
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
  appInternalNotifier = new Subject<NotificationModel>();
  @Input()
  subscribedEvents: { [key: string]: boolean } = {
    'app-notification': false,
    'server-notification': true,
    'server-success': true,
    'server-action': true,
    'server-warning': true,
    custom: false,
  };
  private _unsubscribed = new Subject<never>();
  customEvent = 'custom-event';

  constructor(
    private serverSentEventService: ServerSentEventService<NotificationModel>,
    private renderer: Renderer2
  ) {}

  collapse(element: HTMLDivElement) {
    element.classList.toggle('collapsed');
  }

  ngOnInit(): void {
    this.pushSupport = 'Notification' in window;
    this.serverSentEventService.add(
      'app-notification',
      this.appInternalNotifier
    );
    this.applySubscription();
  }

  private applySubscription() {
    this.serverSentEventService.converter = (event) =>
      new NotificationModel(event.data, event.type ?? 'server-notification');
    this.serverSentEventService
      .getAll(
        ...Object.keys(this.subscribedEvents)
          .filter((k) => this.subscribedEvents[k])
          .map((e) => (e === 'custom' ? this.customEvent : e))
      )
      .pipe(takeUntil(this._unsubscribed))
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
    if (simpleChanges?.subscribedEvents?.currentValue) {
      this._unsubscribed.next();
      this.applySubscription();
    }
  }

  private renderNotification(notification: NotificationModel) {
    const notifData = this.renderer.createText(notification.data);
    const notifType = notification.name ?? 'server-notification';
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
    this._unsubscribed.next();
    this._unsubscribed.complete();
    this.serverSentEventService.closeEventService();
  }

  toggleWebPush() {
    if (!this.pushEnabled && !this.pushGranted) {
      ServerSentEventService.requestPermission().then((permission) => {
        this.pushGranted = this.pushEnabled = permission;
      });
    } else {
      this.pushEnabled = !this.pushEnabled;
    }
  }
  subscribedEventsChange() {
    this._unsubscribed.next();
    this.applySubscription();
  }
}
