import { Inject, Injectable } from '@angular/core';
import { asyncScheduler, merge, Observable, scheduled, zip } from 'rxjs';
import { concatAll, filter, map, mergeAll } from 'rxjs/operators';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Publisher } from '../interface/publisher';
import { Subscriber, SUBSCRIBER_ENDPOINT } from '../interface/subscriber';
@Injectable()
export class SocketCommunicationService<
  T extends { name: string } | { event: string }
> implements Subscriber<T>, Publisher<T, unknown>
{
  private socket: WebSocketSubject<T>;
  private topicObservable: Observable<T>;
  // interoperability between notification and event
  fieldUnion = <K>(
    field: K,
    key1: keyof K,
    key2: keyof K,
    newValue?: K[keyof K]
  ) => {
    const key = key1 in field ? key1 : key2;
    const value = field[key];
    if (newValue !== undefined) {
      field[key] = newValue;
    }
    return field[key];
  };
  // converts indefinite model to certain supporting model for components usage
  converter: (event: unknown) => T = (e) => {
    const t = e as T;
    const name = 'name' as keyof T;
    const event = 'event' as keyof T;
    const value = this.fieldUnion<T>(t, name, event);
    t[name] = value;
    t[event] = value;
    return t;
  };

  constructor(@Inject(SUBSCRIBER_ENDPOINT) socketEndpoint: string) {
    this.socket = webSocket(socketEndpoint);
    this.topicObservable = this.initSubscription();
  }

  private initSubscription() {
    return this.socket
      .multiplex(
        () => ({ event: 'subscription' }),
        () => ({ event: 'unsubscription' }),
        () => true // every event
      )
      .pipe(map(this.converter));
  }

  add(): void {
    // 'Method does not have implementation'
  }

  get(eventName: string, options?: any): Observable<T> {
    const name = 'name' as keyof T;
    const event = 'event' as keyof T;
    return this.topicObservable.pipe(
      filter(
        (eventModel) => this.fieldUnion(eventModel, name, event) === eventName
      )
    );
  }

  getAll(...events: string[]): Observable<T> {
    const name = 'name' as keyof T;
    const event = 'event' as keyof T;
    return this.topicObservable.pipe(
      filter((eventModel) =>
        events.includes(this.fieldUnion(eventModel, name, event) as string)
      )
    );
  }

  publish(eventModel: T): Observable<unknown> {
    return scheduled([this.socket.next(eventModel)], asyncScheduler);
  }
}
