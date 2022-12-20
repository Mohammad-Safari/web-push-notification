// subscription model according to sse protocol standards
export class NotificationModel {
  data: string;
  name: string;
  constructor(data: string, name: string) {
    this.data = data;
    this.name = name;
  }
}
export enum NOTIFICATION_DURATION {
  LONG_LASTING = 10000,
  REGULAR = 3000,
  SHORT_LASTING = 1000,
}
