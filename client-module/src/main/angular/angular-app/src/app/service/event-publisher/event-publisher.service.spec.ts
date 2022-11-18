import { TestBed } from '@angular/core/testing';

import { EventPublisherService } from './event-publisher.service';

describe('EventPublisherService', () => {
  let service: EventPublisherService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EventPublisherService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
