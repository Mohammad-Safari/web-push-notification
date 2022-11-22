import {
  Inject,
  Injectable,
  InjectionToken,
  NgZone,
  Optional,
} from '@angular/core';
import { asapScheduler, Observable, scheduled } from 'rxjs';
import { map } from 'rxjs/operators';
import { NotificationModel } from 'src/app/model/notification';
import { UserUuidResolverService } from '../user-uuid-resolver/user-uuid-resolver.service';

type SseEmitter = {
  next: (messageEvent: MessageEvent<any>) => void;
  error: (messageEvent: Event) => void;
};

export const SUBSCRIBER_ENDPOINT = new InjectionToken<string>(
  'SUBSCRIBER_ENDPOINT'
);

@Injectable()
export class ServerSentEventService {
  private _sseEventSourceObservable: Observable<EventSource>; // is observable due to subscription is not available at the time of service creation
  private _genericObservable: Observable<any>;
  private _observables: Map<string, Observable<MessageEvent>> = new Map();
  get genericObservable(): Observable<any> {
    return this._genericObservable;
  }

  constructor(
    private zone: NgZone,
    @Optional() resolverService: UserUuidResolverService,
    @Inject(SUBSCRIBER_ENDPOINT) private eventServerUrl: string
  ) {
    if (resolverService) {
      // uuid as resolver token
      this._sseEventSourceObservable =
        resolverService.currentPushSubscription.pipe(
          map((subscription) => {
            let sseEventSource = new EventSource(
              subscription.userSubscriptionUrl
            );
            return sseEventSource;
          })
        );
    } else {
      // cookie as resolver token
      let sseEventSource = new EventSource(eventServerUrl, {
        withCredentials: true,
      });
      this._sseEventSourceObservable = scheduled(
        [sseEventSource],
        asapScheduler
      );
    }
    this._genericObservable = this.initEventObserver(
      this._sseEventSourceObservable
    );
  }

  private zoneRunnerFn = (observer: SseEmitter) => {
    return (e: MessageEvent<any>) => {
      this.zone.run(() => {
        observer.next(e);
      });
    };
  };

  private zoneErrorFn = (observer: SseEmitter) => {
    return (e: Event) => {
      this.zone.run(() => {
        observer.error(e);
      });
    };
  };

  private initEventObserver(
    sseEventSourceObservable: Observable<EventSource>
  ): Observable<MessageEvent> {
    const onmessage = this.zoneRunnerFn;
    const onerror = this.zoneErrorFn;
    const emitter = (observer: SseEmitter) => {
      this.zone.run(() => {
        sseEventSourceObservable.subscribe((eventSource) => {
          eventSource.onmessage = onmessage(observer);
          eventSource.onerror = onerror(observer);
        });
      });
    };
    return new Observable(emitter);
  }

  private getEventObservable(eventType: string): Observable<MessageEvent> {
    const onevent = this.zoneRunnerFn;
    const emitter = (observer: SseEmitter) => {
      const handler = onevent(observer);
      this._sseEventSourceObservable.subscribe((eventSource) =>
        eventSource!.addEventListener(eventType, handler as any)
      );
    };
    return new Observable(emitter);
  }

  public get(
    eventName: string,
    options: { ignoreExsiting: boolean } = { ignoreExsiting: false }
  ): Observable<MessageEvent> {
    if (options.ignoreExsiting === true && this._observables.has(eventName)) {
      return this._observables.get(eventName)!;
    }
    const observable = this.getEventObservable(eventName);
    this._observables.set(eventName, observable);
    return observable;
  }

  public closeEventService() {
    this._sseEventSourceObservable.subscribe({
      next: (eventSource) => {
        eventSource.close();
      },
    });
    this._observables.clear();
  }

  /**
   * Html5 Web Push API Permission Utility
   * @param notification
   */
  static async reauestPermission(): Promise<boolean> {
    let permitted = false;
    permitted = await Notification.requestPermission().then((permission) => {
      return permission === 'granted' ? true : false;
    });
    return permitted;
  }

  /**
   * Html5 Web Push API publisher Utility
   * @param notification
   */
  static async webPushApiTrigger(
    notification: NotificationModel,
    options?: { selfDestroy: boolean }
  ) {
    var standardNotification = new Notification('Notification', {
      body: notification.data,
      icon: 'favicon.ico',
      dir: 'auto', // ltr/rtl
    });
    // optional - self destrcution
    if (options?.selfDestroy === true) {
      asapScheduler.schedule(() => {
        standardNotification.close();
      }, notification.duration);
    }
  }
}
