import { Inject, Injectable, InjectionToken, NgZone } from '@angular/core';
import { EventSourcePolyfill } from 'event-source-polyfill';
import { Observable } from 'rxjs';
import { NotificationModel } from 'src/app/model/notification';

type SseEmitter = {
  next: (messageEvent: MessageEvent<any>) => void;
  error: (messageEvent: Event) => void;
};

export const EVENT_SERVER_URL = new InjectionToken<string>('EVENT_SERVER_URL', {
  providedIn: 'root',
  factory: () => '/event/notification/subscribe',
});

@Injectable({
  providedIn: 'root',
})
export class ServerSentEventService {
  private _sseEventSource: EventSource;
  private _genericObservable: Observable<any>;
  private _observables: Map<string, Observable<MessageEvent>> = new Map();
  get genericObservable(): Observable<any> {
    return this._genericObservable;
  }

  constructor(
    private zone: NgZone,
    @Inject(EVENT_SERVER_URL) private eventServerUrl: string
  ) {
    this._sseEventSource = new EventSourcePolyfill(eventServerUrl, {
      headers: {
        Authorization: localStorage.getItem('Authorization') ?? '',
      },
    });
    this._genericObservable = this.initEventObserver(this._sseEventSource);
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
    sseEventSource: EventSource
  ): Observable<MessageEvent> {
    const onmessage = this.zoneRunnerFn;
    const onerror = this.zoneErrorFn;
    const emitter = (observer: SseEmitter) => {
      sseEventSource.onmessage = onmessage(observer);
      sseEventSource.onerror = onerror(observer);
    };
    return new Observable(emitter);
  }

  private getEventObservable(eventType: string): Observable<MessageEvent> {
    const onevent = this.zoneRunnerFn;
    const emitter = (observer: SseEmitter) => {
      const handler = onevent(observer);
      this._sseEventSource.addEventListener(eventType, handler as any);
    };
    return new Observable(emitter);
  }

  public get(
    eventType: string,
    options: { ignoreExsiting: boolean } = { ignoreExsiting: false }
  ): Observable<MessageEvent> {
    if (options.ignoreExsiting === true && this._observables.has(eventType)) {
      return this._observables.get(eventType)!;
    }
    const observable = this.getEventObservable(eventType);
    this._observables.set(eventType, observable);
    return observable;
  }

  public closeEventService() {
    this._sseEventSource.close();
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
  static async webPushApiTrigger(notification: NotificationModel) {
    var standardNotification = new Notification('Notification', {
      body: notification.data,
      icon: 'favicon.ico',
      dir: 'auto', // ltr/rtl
    });
    // optional - self destrcution
    setTimeout(function () {
      standardNotification.close();
    }, notification.duration);
  }
}
