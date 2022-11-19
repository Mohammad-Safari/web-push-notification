export class NotificationModel {
  data: string;
  id: string;
  event: string;
  duration: number;
  constructor(data: string, id: string, type: string, duration?: number) {
    this.data = data;
    this.id = id;
    this.event = type;
    this.duration = duration ?? 3000;
  }
}
