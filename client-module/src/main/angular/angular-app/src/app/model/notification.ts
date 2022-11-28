export class NotificationModel {
  data: string;
  name: string
  duration: number;
  constructor(data: string, name: string, duration?: number) {
    this.data = data;
    this.name = name;
    this.duration = duration ?? 3000;
  }
}
