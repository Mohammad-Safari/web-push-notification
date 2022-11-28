import {
  Inject,
  Injectable,
  InjectionToken,
  NgZone,
  OnDestroy,
  Optional,
} from '@angular/core';
import { asapScheduler, merge, Observable, scheduled, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { NotificationModel } from 'src/app/model/notification';
import { UserUuidResolverService } from '../user-uuid-resolver/user-uuid-resolver.service';

type SseEmitter<T> = {
  next: (messageEvent: T) => void;
  error: (messageEvent: Event) => void;
};

export const SUBSCRIBER_ENDPOINT = new InjectionToken<string>(
  'SUBSCRIBER_ENDPOINT'
);

@Injectable()
export class ServerSentEventService<T> implements OnDestroy {
  private _sseEventSourceObservable: Observable<EventSource>; // is observable due to subscription is not available at the time of service creation
  private _genericObservable: Observable<MessageEvent | Event>;
  private _unsubscribed: Subject<never> = new Subject();
  private _observables: Map<string, Observable<T>> = new Map();
  private defaultConverter = (e: MessageEvent) => e as unknown as T;
  set converter(fn: (e: MessageEvent) => T) {
    this.defaultConverter = fn;
  }
  get converter(): (e: MessageEvent) => T {
    return this.defaultConverter;
  }
  get genericObservable(): Observable<MessageEvent | Event> {
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
          takeUntil(this._unsubscribed),
          map((subscription) => {
            const sseEventSource = new EventSource(
              subscription.userSubscriptionUrl
            );
            return sseEventSource;
          })
        );
    } else {
      // cookie as resolver token
      const sseEventSource = new EventSource(eventServerUrl, {
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

  private zoneRunnerGenericFn = (observer: SseEmitter<T>) => {
    return (e: MessageEvent) => {
      const t = this.converter(e);
      this.zone.run(() => {
        observer.next(t);
      });
    };
  };

  private zoneRunnerFn = (observer: SseEmitter<MessageEvent>) => {
    return (e: MessageEvent) => {
      this.zone.run(() => {
        observer.next(e);
      });
    };
  };

  private zoneErrorFn = (observer: SseEmitter<Event>) => {
    return (e: Event) => {
      this.zone.run(() => {
        observer.error(e);
      });
    };
  };

  private initEventObserver(
    sseEventSourceObservable: Observable<EventSource>
  ): Observable<MessageEvent | Event> {
    const onmessage = this.zoneRunnerFn;
    const onerror = this.zoneErrorFn;
    const emitter = (observer: SseEmitter<MessageEvent | Event>) => {
      this.zone.run(() => {
        sseEventSourceObservable
          .pipe(takeUntil(this._unsubscribed))
          .subscribe((eventSource) => {
            eventSource.onmessage = onmessage(observer);
            eventSource.onerror = onerror(observer);
          });
      });
    };
    return new Observable(emitter);
  }

  private getEventObservable(eventType: string): Observable<T> {
    const onevent = this.zoneRunnerGenericFn;
    const emitter = (observer: SseEmitter<T>) => {
      const handler = onevent(observer) as EventListener;
      this._sseEventSourceObservable
        .pipe(takeUntil(this._unsubscribed))
        .subscribe((eventSource) =>
          eventSource.addEventListener(eventType, handler)
        );
    };
    return new Observable(emitter);
  }

  public get(
    eventName: string,
    options: { ignoreExsiting: boolean } = { ignoreExsiting: false }
  ): Observable<T> {
    if (options.ignoreExsiting !== true && this._observables.has(eventName)) {
      return this._observables.get(eventName) as Observable<T>;
    }
    const observable = this.getEventObservable(eventName);
    this._observables.set(eventName, observable);
    return observable;
  }

  public closeEventService() {
    this._sseEventSourceObservable
      .pipe(takeUntil(this._unsubscribed))
      .subscribe({
        next: (eventSource) => {
          eventSource.close();
        },
      });
    this._observables.clear();
  }

  public getAll(...events: string[]): Observable<T> {
    events = events?.length ? events : Array.from(this._observables.keys());
    const obs = events.map((event) => this.get(event));
    return merge(...obs);
  }

  public add(event: string, observable: Observable<T>): void {
    this._observables.set(event, observable);
  }

  /**
   * Html5 Web Push API Permission Utility
   * @param notification
   */
  static async requestPermission(): Promise<boolean> {
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
    const standardNotification = new Notification('Notification', {
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

  ngOnDestroy() {
    this.closeEventService();
    this._unsubscribed.next();
    this._unsubscribed.complete();
  }
}
