import {
  Component,
  ElementRef,
  Input,
  OnDestroy,
  Renderer2,
  ViewChild
} from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EventModel } from 'src/app/model/event-model';
import { NotificationModel } from 'src/app/model/notification-model';
import { Publisher } from 'src/app/service/interface/publisher';
import { ResolverResponse } from 'src/app/service/user-uuid-resolver/user-uuid-resolver.service';

@Component({
  selector: 'app-publisher',
  templateUrl: './publisher.component.html',
  styleUrls: ['./publisher.component.scss'],
})
export class PublisherComponent implements OnDestroy {
  public eventModel = new EventModel();
  public pushSupport: boolean;
  public pushGranted: boolean;
  public pushEnabled = false;
  @ViewChild('notificationPublisher')
  publisherContainer: ElementRef<HTMLDivElement>;
  @Input()
  appInternalNotifier = new Subject<NotificationModel<string>>();
  private _unsubscribed = new Subject<never>();
  options = [
    'server-notification' /* info */,
    'server-warning',
    'server-action',
    'server-success',
  ].map((opt) => ({ label: opt }));

  constructor(
    private notificationService: Publisher<EventModel, ResolverResponse>,
    private renderer: Renderer2
  ) {}

  onPublish() {
    const next = () => {
      const div = this.renderer.createElement('div');
      const text = this.renderer.createText('Event is being send to Server');
      this.renderer.addClass(div, 'message-box');
      this.renderer.appendChild(div, text);
      this.renderer.appendChild(this.publisherContainer.nativeElement, div);
      setTimeout(() => {
        this.renderer.removeChild(this.publisherContainer.nativeElement, div);
      }, 1000);
    };
    const complete = () => {
      this.appInternalNotifier?.next(
        new NotificationModel(
          'Event Published By Server',
          'app-notification',
        )
      );
    };
    this.notificationService
      .publish(this.eventModel)
      .pipe(takeUntil(this._unsubscribed))
      .subscribe({
        next: next,
        complete: complete,
      });
  }
  ngOnDestroy() {
    this._unsubscribed.next();
    this._unsubscribed.complete();
  }
}
