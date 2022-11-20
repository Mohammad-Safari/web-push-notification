export class EventModel {
  sender: string;
  receiver: string;
  data: string;
  id: string;
  event: string;
  retry: number;
  constructor(event?: string) {
    this.event = event ?? 'server-notification';
  }
}
