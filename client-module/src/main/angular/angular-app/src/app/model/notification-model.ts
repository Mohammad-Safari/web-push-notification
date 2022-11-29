export class NotificationModel<T> {
  data: T;
  name: string;
  constructor(data: T, name: string) {
    this.data = data;
    this.name = name;
  }
}
export enum NOTIFICATION_DURATION {
  LONG_LASTING = 10000,
  REGULAR = 3000,
  SHORT_LASTING = 1000,
}
