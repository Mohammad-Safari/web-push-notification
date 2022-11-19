export class EventModel {
  sender: string;
  receiver: string;
  data: string;
  id: string;
  name: string;
  retry: number;
  constructor(name?: string) {
    this.name = name ?? 'server-notification';
  }
}
