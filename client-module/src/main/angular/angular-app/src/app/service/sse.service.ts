import { Inject, Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';

type SseEmitter = {
  next: (messageEvent: MessageEvent<any>) => void;
  error: (messageEvent: Event) => void;
};

@Injectable({
  providedIn: 'root',
})
export class SseService {
  private _sseEventSource: EventSource;
  private _genericObservable: Observable<any>;
  private _observables: Map<string, Observable<MessageEvent>> = new Map();
  get genericObservable(): Observable<any> {
    return this._genericObservable;
  }

  constructor(
    private zone: NgZone,
    @Inject('EVENT_SERVER_URL') private eventServerUrl: string
  ) {
    this._sseEventSource = new EventSource(eventServerUrl);
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

  private initEventObserver(sseEventSource: EventSource): Observable<MessageEvent> {
    const onmessage = this.zoneRunnerFn;
    const onerror = this.zoneErrorFn;
    const emitter = (observer: SseEmitter) => {
      sseEventSource.onmessage = onmessage(observer);
      sseEventSource.onerror = onerror(observer);
    };
    return new Observable(emitter);
  }

  private _getEventObservable(eventType: string): Observable<MessageEvent> {
    const onevent = this.zoneRunnerFn;
    const emitter = (observer: SseEmitter) => {
      this._sseEventSource.addEventListener(eventType, onevent(observer));
    };
    return new Observable(emitter);
  }

  public getEventObservable(
    eventType: string,
    options: { ignoreExsiting: boolean } = { ignoreExsiting: false }
  ): Observable<MessageEvent> {
    if (options.ignoreExsiting === true && this._observables.has(eventType)) {
      return this._observables.get(eventType)!;
    }
    const observable = this._getEventObservable(eventType);
    this._observables.set(eventType, observable);
    return observable;
  }

  public closeEventService(){
    this._sseEventSource.close();
    this._observables.clear();
  }
}
