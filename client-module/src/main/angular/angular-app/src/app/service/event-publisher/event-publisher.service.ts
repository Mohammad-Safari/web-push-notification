import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EventModel } from '../../model/event-model';

@Injectable({
  providedIn: 'root',
})
export class EventPublisherService {
  constructor(private httpClient: HttpClient) {}

  publish(eventModel: EventModel): Observable<any> {
    return this.httpClient.post('/api/notification/publish', eventModel);
  }
}
