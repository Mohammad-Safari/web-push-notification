import { Inject, Injectable, NgZone, OnDestroy, Optional } from '@angular/core';
import { asapScheduler, merge, Observable, scheduled, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import {
  NotificationModel,
  NOTIFICATION_DURATION
} from 'src/app/model/notification-model';
import { Subscriber, SUBSCRIBER_ENDPOINT } from '../interface/subscriber';
import {
  PushSubscription,
  UserUuidResolverService
} from '../user-uuid-resolver/user-uuid-resolver.service';

type SseEmitter<T> = {
  next: (messageEvent: T) => void;
  error: (messageEvent: Event) => void;
};

@Injectable()
export class ServerSentEventService<T> implements Subscriber<T>, OnDestroy {
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

  constructor(
    private zone: NgZone,
    @Optional() resolverService: UserUuidResolverService,
    @Inject(SUBSCRIBER_ENDPOINT) private eventServerUrl: string
  ) {
    if (resolverService) {
      this.uuiBasedSubscriptionInit(resolverService);
    } else {
      this.cookieBasedSubscriptionInit(eventServerUrl);
    }
    this._genericObservable = this.initEventObserver(
      this._sseEventSourceObservable
    );
  }

  private cookieBasedSubscriptionInit(eventServerUrl: string) {
    const sseEventSource = new EventSource(eventServerUrl, {
      withCredentials: true,
    });
    this._sseEventSourceObservable = scheduled([sseEventSource], asapScheduler);
  }

  private uuiBasedSubscriptionInit(resolverService: UserUuidResolverService) {
    this._sseEventSourceObservable =
      resolverService.currentPushSubscription.pipe(
        takeUntil(this._unsubscribed),
        map((subscription: PushSubscription) => {
          return new EventSource(subscription.userSubscriptionUrl);
        })
      );
  }

  private initEventObserver(
    sseEventSourceObservable: Observable<EventSource>
  ): Observable<MessageEvent | Event> {
    const emitter = (observer: SseEmitter<MessageEvent | Event>) => {
      this.zone.run(() => {
        sseEventSourceObservable.pipe(takeUntil(this._unsubscribed)).subscribe({
          next: (eventSource: EventSource) => {
            eventSource.onmessage = this.zoneRunnerFn(observer);
            eventSource.onerror = this.zoneErrorFn(observer);
          },
        });
      });
    };
    return new Observable(emitter);
  }

  private createEventObservable(eventType: string): Observable<T> {
    const emitter = (observer: SseEmitter<T>) => {
      const handler = this.zoneRunnerGenericFn(observer) as EventListener;
      this._sseEventSourceObservable
        .pipe(takeUntil(this._unsubscribed))
        .subscribe({
          next: (eventSource) =>
            eventSource.addEventListener(eventType, handler),
        });
    };
    return new Observable(emitter);
  }

  public get(
    eventName: string,
    options?: { ignoreExsiting: boolean }
  ): Observable<T> {
    if (options?.ignoreExsiting !== true && this._observables.has(eventName)) {
      return this._observables.get(eventName) as Observable<T>;
    }
    const observable = this.createEventObservable(eventName);
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
    const eventsKeys = events?.length
      ? events
      : Array.from(this._observables.keys());
    const requiredObservable = eventsKeys.map((event) => this.get(event));
    return merge(...requiredObservable);
  }

  public add(event: string, observable: Observable<T>): void {
    this._observables.set(event, observable);
  }

  ngOnDestroy() {
    this.closeEventService();
    this._unsubscribed.next();
    this._unsubscribed.complete();
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
      body: notification.data.toString(),
      icon: 'favicon.ico',
      dir: 'auto', // ltr/rtl
    });
    // optional - self destrcution
    if (options?.selfDestroy === true) {
      asapScheduler.schedule(() => {
        standardNotification.close();
      }, NOTIFICATION_DURATION.REGULAR);
    }
  }
}
