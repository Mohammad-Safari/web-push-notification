export class EventModel {
  sender: string;
  receiver: string;
  data: string;
  id: string;
  type: string = "server-notification";
}
