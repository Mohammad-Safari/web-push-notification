import {
  Component,
  ElementRef,
  EventEmitter,
  OnInit,
  Output,
  Renderer2,
  ViewChild,
} from '@angular/core';
import { EventModel } from 'src/app/model/event-model';
import { NotificationModel } from 'src/app/model/notification';
import { EventPublisherService } from 'src/app/service/event-publisher/event-publisher.service';

@Component({
  selector: 'app-publisher',
  templateUrl: './publisher.component.html',
  styleUrls: ['./publisher.component.scss'],
})
export class PublisherComponent implements OnInit {
  public eventModel = new EventModel();
  public pushSupport: boolean;
  public pushGranted: boolean;
  public pushEnabled = false;
  @ViewChild('notificationPublisher')
  publisherContainer: ElementRef<HTMLDivElement>;
  @Output('publisherNotification')
  publishNotifier: EventEmitter<NotificationModel> = new EventEmitter();
  private _subscribed: boolean = true;

  constructor(
    private notificationService: EventPublisherService,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
  }

  onPublish() {
    this.notificationService.publish(this.eventModel).subscribe({
      next: () => {
        let div = this.renderer.createElement('div');
        this.renderer.addClass(div, 'message-box');
        let text = this.renderer.createText('Event is being send to Server');
        this.renderer.appendChild(div, text);
        this.renderer.appendChild(this.publisherContainer.nativeElement, div);
        setTimeout(() => {
          this.renderer.removeChild(this.publisherContainer.nativeElement, div);
        }, 1000);
      },
      complete: () => {
        this.publishNotifier.emit(
          new NotificationModel(
            'Event Published By Server',
            '0',
            'app-notification',
            700
          )
        );
      },
    });
  }
  ngOnDestroy() {
    this.publishNotifier.unsubscribe();
  }
}
