import { Inject, Injectable } from '@angular/core';
import { asyncScheduler, Observable, scheduled } from 'rxjs';
import { concatAll, map } from 'rxjs/operators';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Publisher } from '../interface/publisher';
import { Subscriber, SUBSCRIBER_ENDPOINT } from '../interface/subscriber';
@Injectable()
export class SocketCommunicationService<T extends { name: string }>
  implements Subscriber<T>, Publisher<T, unknown>
{
  private socket: WebSocketSubject<T>;
  converter: (event: unknown) => T = e => e as T;
  private topicLock: Observable<T>;

  constructor(@Inject(SUBSCRIBER_ENDPOINT) private socketEndpoint: string) {
    this.socket = webSocket(socketEndpoint);
    this.topicLock = this.initSubscription();
  }


  private initSubscription() {
    return this.socket.multiplex(
      () => ({ event: 'subscription' }),
      () => ({ event: 'unsubscription' }),
      () => true // every event
    ).pipe(map(this.converter)) as Observable<T>;
  }

  add(): void {
    // 'Method does not have implementation'
  }

  get(eventName: string, options?: any): Observable<T> {
    const observable = this.socket.multiplex(
      () => ({ event: eventName, Subscribed: true }),
      () => ({ event: eventName, Subscribed: false }),
      (event) => event.name === eventName
    ) as Observable<T>;
    return scheduled([this.topicLock, observable], asyncScheduler).pipe(
      concatAll()
    );
  }

  getAll(...events: string[]): Observable<T> {
    return scheduled([this.topicLock], asyncScheduler).pipe(
      concatAll()
    );
  }

  publish(eventModel: T): Observable<unknown> {
    const a = () =>
      this.socket.multiplex(
        () => eventModel,
        () => undefined,
        () => true // every event
      ) as Observable<unknown>;
    return scheduled([this.topicLock, a()], asyncScheduler).pipe(concatAll());
  }
}
