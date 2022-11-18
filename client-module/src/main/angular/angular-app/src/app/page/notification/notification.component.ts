import {
  Component,
  ElementRef,
  OnInit,
  Renderer2,
  ViewChild,
} from '@angular/core';
import { map, takeWhile } from 'rxjs/operators';
import { EventModel } from 'src/app/model/event-model';
import { NotificationModel } from 'src/app/model/notification';
import { EventPublisherService } from 'src/app/service/event-publisher/event-publisher.service';
import { ServerSentEventService } from 'src/app/service/server-sent-event/server-sent-event.service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss'],
})
export class NotificationComponent implements OnInit {
  public eventModel = new EventModel();
  @ViewChild('notificationSubscriber')
  subscriberContainer: ElementRef<HTMLDivElement>;
  @ViewChild('notificationSpublisher')
  publisherContainer: ElementRef<HTMLDivElement>;
  private _subscribed: boolean = true;

  constructor(
    private serverSentEventService: ServerSentEventService,
    private notificationService: EventPublisherService,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.serverSentEventService
      .get('notification')
      .pipe(
        takeWhile(() => this._subscribed),
        map(
          (message) =>
            new NotificationModel(
              message.data,
              (message as any).id,
              'notification'
            )
        )
      )
      .subscribe((notification) => {
        if (notification) {
          this.renderNotification(notification);
          }
      });
  }

  onPublish() {
    this.notificationService.publish(this.eventModel).subscribe({
      next: () => {
        let div = this.renderer.createElement('div');
        let text = this.renderer.createText('Published');
        this.renderer.appendChild(this.publisherContainer.nativeElement, text);
        this.renderer.appendChild(this.publisherContainer.nativeElement, div);
        setTimeout(() => {
          this.renderer.removeChild(
            this.publisherContainer.nativeElement,
            text
          );
        }, 1000);
      },
    });
  }

  private renderNotification(notification: NotificationModel) {
    let notificationBox = this.renderer.createElement('div');
    let header = this.renderer.createElement('b');
    let content = this.renderer.createElement('div');
    const NOTIFICATION_TYPE = 'notification';
    const boxColorClass = NOTIFICATION_TYPE;
    let classesToAdd = ['message-box', boxColorClass];
    classesToAdd.forEach((x) => this.renderer.addClass(notificationBox, x));
    this.renderer.setStyle(
      notificationBox,
      'transition',
      `opacity ${notification.duration}ms`
    );
    this.renderer.setStyle(notificationBox, 'opacity', '1');
    const headerText = this.renderer.createText(NOTIFICATION_TYPE);
    this.renderer.appendChild(header, headerText);
    const text = this.renderer.createText(notification.data);
    this.renderer.appendChild(content, text);
    this.renderer.appendChild(
      this.subscriberContainer.nativeElement,
      notificationBox
    );
    this.renderer.appendChild(notificationBox, header);
    this.renderer.appendChild(notificationBox, content);
    this.removeEntry(notificationBox, notification);
  }

  private removeEntry(notificationBox: any, notification: NotificationModel) {
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

  }
