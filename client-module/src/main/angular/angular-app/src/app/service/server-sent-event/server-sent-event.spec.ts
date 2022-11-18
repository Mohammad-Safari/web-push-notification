import { TestBed } from '@angular/core/testing';
import { EventSourcePolyfill } from 'event-source-polyfill';
import { Observable } from 'rxjs';

import { ServerSentEventService } from './server-sent-event.service';

describe('ServerSentEventService', () => {
  let service: ServerSentEventService;
  let eventSourceSpy: jasmine.SpyObj<any>;

  beforeEach(() => {
    eventSourceSpy = jasmine.createSpyObj(
        (EventSource as any).__proto__ ,
        ["url" ]
    );
    // eventSourceSpy .and.callFake(() => {console.log('hi');});
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServerSentEventService);
  });

  it('should be created', () => {
    // expect(eventSourceSpy.addEventListener.calls.count())
    //   .withContext('initial listener')
    //   .toEqual(1);
    // expect(service.get('notification')).toBeInstanceOf(Observable);
    // expect(eventSourceSpy.addEventListener.calls.count())
    //   .withContext('secondary listener')
    //   .toEqual(2);
    // expect(eventSourceSpy.addEventListener.calls.mostRecent().args[0])
    //   .withContext('event type')
    //   .toEqual('notification');

    expect(service).toBeTruthy();
  });
});
